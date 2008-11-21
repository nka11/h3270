package org.h3270.host;

/*
 * Copyright (C) 2003-2008 akquinet tech@spree
 *
 * This file is part of h3270.
 *
 * h3270 is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * h3270 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with h3270; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h3270.render.H3270Configuration;

/**
 * A Terminal that connects to the host via s3270.
 *
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class S3270 implements Terminal {

  private final static Log logger = LogFactory.getLog(S3270.class);

  private String hostname = null;
  private String logicalUnit = null;
  private S3270Screen screen = null;

  /**
   * The subprocess that does the actual communication with the host.
   */
  private Process s3270 = null;

  /**
   * Used to send commands to the s3270 process.
   */
  private PrintWriter out = null;

  /**
   * Used for reading input from the s3270 process.
   */
  private BufferedReader in = null;

  /**
   * A thread that does a blocking read on the error stream
   * from the s3270 process.
   */
  private ErrorReader errorReader = null;

  /**
   * Constructs a new S3270 object.  The s3270 subprocess (which does the
   * communication with the host) is immediately started and connected to
   * the target host.  If this fails, the constructor will throw an
   * appropriate exception.
   * @param hostname the name of the host to connect to
   * @param configuration the h3270 configuration, derived from h3270-config.xml
   * @throws org.h3270.host.UnknownHostException if <code>hostname</code>
   * cannot be resolved
   * @throws org.h3270.host.HostUnreachableException if the host cannot be reached
   * @throws org.h3270.host.S3270Exception for any other error not matched by
   * the above
   */
  public S3270 (String logicalUnit, String hostname, Configuration configuration) {

    this.logicalUnit = logicalUnit;
    this.hostname = hostname;
    this.screen = new S3270Screen();

    String commandLine = buildCommandLine (logicalUnit, hostname, configuration);
    try {
      logger.info("Starting s3270: " + commandLine);
      s3270 = Runtime.getRuntime().exec(commandLine);

      out = new PrintWriter (
        new OutputStreamWriter (s3270.getOutputStream(), "ISO-8859-1")
      );
      in = new BufferedReader (
        new InputStreamReader (s3270.getInputStream(), "ISO-8859-1")
      );
      errorReader = new ErrorReader();
      errorReader.start();

      waitFormat();
    } catch (IOException ex) {
      throw new RuntimeException("IO Exception while starting s3270", ex);
    }
  }

  /**
   * Builds the command line for starting the s3270 process.
   * @param hostname the name of the host to connect to.
   * @param configuration the configuration for h3270
   * @return a command line, ready to be executed by Runtime.exec()
   */
  private String buildCommandLine (String logicalUnit,
                                   String hostname,
                                   Configuration configuration) {
    String execPath = configuration.getChild("exec-path").getValue("/usr/local/bin");
    Configuration s3270_options = configuration.getChild("s3270-options");
    String charset    = s3270_options.getChild("charset").getValue("bracket");
    String model      = s3270_options.getChild("model").getValue("3");
    String additional = s3270_options.getChild("additional").getValue("");
    File s3270_binary = new File(execPath, "s3270");
    StringBuffer cmd = new StringBuffer(s3270_binary.toString());
    cmd.append(" -model " + model);
    if (!charset.equals("bracket")) cmd.append(" -charset " + charset);
    if (additional.length() > 0)    cmd.append(" " + additional);
    cmd.append(" ");
    if (logicalUnit != null){
      cmd.append(logicalUnit).append('@');
    }
    cmd.append(hostname);
    return cmd.toString();
  }

  /**
   * Represents the result of an s3270 command.
   */
  private class Result {
    public final List data;
    public final String status;

    public Result (List data, String status) {
      this.data = data;
      this.status = status;
    }
  }

  /**
   * Perform an s3270 command. All communication with s3270 should go via this
   * method.
   */
  private Result doCommand (String command) {
    try {
      out.println (command);
      out.flush();
      if (logger.isDebugEnabled()) {
        logger.debug("---> " + command);
      }

      List lines = new ArrayList();
      while (true) {
        String line = in.readLine();
        if (line == null) {
          checkS3270Process(); // will throw appropriate exception
          // if we get here, it's a more obscure error
          throw new RuntimeException("s3270 process not responding");
        }

        if (logger.isDebugEnabled()) {
          logger.debug("<--- " + line);
        }

        if (line.equals("ok")) {
          break;
        }
        lines.add(line);
      }
      int size = lines.size();
      if (size > 0) {
        return new Result (lines.subList(0, size - 1),
                           (String) lines.get(size - 1));
      } else {
        throw new RuntimeException("no status received in command: " + command);
      }
    } catch (IOException ex) {
      throw new RuntimeException("IOException during command: " + command, ex);
    }
  }

  /**
   * Performs a blocking read on the s3270 error stream.  We do this
   * asynchronously, because otherwise the error message might already
   * be lost when we get a chance to look for it.  The message is kept
   * in the instance variable <code>message</code> for later retrieval.
   */
  private class ErrorReader extends Thread {
    public String message = null;
    public void run() {
      BufferedReader err = new BufferedReader (
        new InputStreamReader (s3270.getErrorStream())
      );
      try {
        while (true) {
          String msg = err.readLine();
          if (msg == null) break;
          message = msg;
        }
      } catch (IOException ex) {
        // ignore
      }
    }
  }

  private static final Pattern unknownHostPattern = Pattern.compile (
    // This message is hard-coded in s3270 as of version 3.3.5,
    // so we can rely on it not being localized.
    "Unknown host: (.*)"
  );
  private static final Pattern unreachablePattern = Pattern.compile (
    // This is the hard-coded part of the error message in s3270 version 3.3.5.
    "Connect to ([^,]+), port ([0-9]+): (.*)"
  );

  /**
   * Checks whether the s3270 process is still running, and if it isn't,
   * tries to determine the cause why it failed.  This method throws
   * an exception of appropriate type to indicate what went wrong.
   */
  private void checkS3270Process() {
    // Ideally, we'd like to call Process.waitFor() with a timeout,
    // but that is so complicated to implement that we take a
    // second-rate approach: wait a little while, and then check if
    // the process is already terminated.
    try { Thread.sleep(100); } catch (InterruptedException ex) {}
    try {
      int exitValue = s3270.exitValue();
      String message = errorReader.message;
      if (exitValue == 1 && message != null) {
        Matcher m = unknownHostPattern.matcher (message);
        if (m.matches()) {
          throw new UnknownHostException (m.group(1));
        } else {
          m = unreachablePattern.matcher (message);
          if (m.matches()) {
            throw new HostUnreachableException (m.group(1), m.group(3));
          }
        }
        throw new S3270Exception ("s3270 terminated with code " + exitValue
                                    + ", message: " + errorReader.message);
      }
    } catch (IllegalThreadStateException ex) {
      // we get here if the process has still been running in the
      // call to s3270.exitValue() above
      throw new S3270Exception ("s3270 not terminated, error: "
                                + errorReader.message);
    }
  }

  /**
   * waits for a formatted screen
   */
  private void waitFormat() {
    for (int i = 0; i < 50; i++) {
      Result r = doCommand("");
      if (r.status.startsWith("U F")) {
        return;
      }
      try { Thread.sleep(100); } catch (InterruptedException ex) {}
    }
  }

  public void disconnect() {
    out.println("quit");
    out.flush();

    new Thread(new Runnable() {
      public void run() {
        try {
          Thread.sleep(1000);
          if (s3270 != null) {
            s3270.destroy();
          }
        } catch (InterruptedException ex) {
          if (s3270 != null) {
            s3270.destroy();
          }
        }
      }
    }).start();

    try {
      s3270.waitFor();
    } catch (InterruptedException ex) { /* ignore */
    }
    try {
      in.close();
    } catch (IOException ex) { /* ignore */
    }
    out.close();
    in = null;
    out = null;
    s3270 = null;
  }

  public boolean isConnected() {
    if (s3270 == null || in == null || out == null)
      return false;
    else {
      Result r = doCommand("");
      if (r.status.matches(". . . C.*"))
        return true;
      else {
        out.println("quit");
        out.flush();
        s3270.destroy();
        s3270 = null;
        in = null;
        out = null;
        return false;
      }
    }
  }

  public String getHostname() {
    return hostname;
  }

  public String getLogicalUnit() {
    return logicalUnit;
  }

  public void dumpScreen (String filename) {
    screen.dump(filename);
  }

  /**
   * Updates the screen object with s3270's buffer data.
   */
  public void updateScreen() {
    while (true) {
      Result r = doCommand ("readbuffer ascii");
      if (r.data.size() > 0) {
        String firstLine = (String) r.data.get(0);
        if (firstLine.startsWith("data: Keyboard locked")) {
          continue;
        }
      }
      screen.update(r.status, r.data);
      break;
    }
  }

  public Screen getScreen() {
    return screen;
  }

  /**
   * Writes all changed fields back to s3270.
   */
  public void submitScreen() {
    for (Iterator i = screen.getFields().iterator(); i.hasNext();) {
      Field f = (Field) i.next();
      if ((f instanceof InputField) && ((InputField) f).isChanged()) {
        doCommand("movecursor (" + f.getStartY() + ", " + f.getStartX() + ")");
        doCommand("eraseeof");
        String value = f.getValue();
        for (int j = 0; j < value.length(); j++) {
          char ch = value.charAt(j);
          if (ch == '\n') {
            doCommand("newline");
          } else if (!Integer.toHexString(ch).equals("0")) {
            doCommand("key (0x" + Integer.toHexString(ch) + ")");
          }
        }
      }
    }
  }

  public void submitUnformatted (String data) {
    int index = 0;
    for (int y = 0; y < screen.getHeight() && index < data.length(); y++) {
      for (int x = 0; x < screen.getWidth() && index < data.length(); x++) {
        char newCh = data.charAt(index);
        if (newCh != screen.charAt(x, y)) {
          doCommand ("movecursor (" + y + ", " + x + ")");
          if (!Integer.toHexString(newCh).equals("0")) {
            doCommand ("key (0x" + Integer.toHexString(newCh) + ")");
          }
        }
        index++;
      }
      index++; // skip newline
    }
  }

  // s3270 actions below this line

  public void clear() {
    doCommand("clear");
  }

  public void enter() {
    doCommand("enter");
    waitFormat();
  }

  public void newline() {
    doCommand("newline");
    waitFormat();
  }

  public void eraseEOF() {
    doCommand("eraseEOF");
  }

  public void pa (int number) {
    doCommand("pa(" + number + ")");
    waitFormat();
  }

  public void pf (int number) {
    doCommand("pf(" + number + ")");
    waitFormat();
  }

  public void reset() {
    doCommand("reset");
  }

  public void sysReq() {
    doCommand("sysReq");
  }

  public void attn() {
    doCommand("attn");
  }

  private static final Pattern FUNCTION_KEY_PATTERN =
    Pattern.compile("p(f|a)([0-9]{1,2})");

  public void doKey (String key) {
    Matcher m = FUNCTION_KEY_PATTERN.matcher(key);
    if (m.matches()) { // function key
      int number = Integer.parseInt(m.group(2));
      if (m.group(1).equals("f"))
        this.pf(number);
      else
        this.pa(number);
    } else if (key.equals("")) {
      // use ENTER as a default action if the actual key got lost
      this.enter();
    } else { // other key: find a parameterless method of the same name
      try {
        Class c = this.getClass();
        Method method = c.getMethod(key, new Class[] {});
        method.invoke(this, new Object[] {});
      } catch (NoSuchMethodException ex) {
        throw new IllegalArgumentException("no such key: " + key);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException("illegal s3270 method access for key: "
            + key);
      } catch (InvocationTargetException ex) {
        throw new RuntimeException("error invoking s3270 for key: " + key
            + ", exception: " + ex.getTargetException());
      }
    }
  }

  public String toString() {
    return "s3270 " + super.toString();
  }

  public static void main(String[] args) throws Exception {
    Configuration configuration = H3270Configuration.create("/home/spiegel/projects/h3270/cvs/webapp/WEB-INF/h3270-config.xml");
    S3270 s3270 = new S3270(null, "locis.loc.gov", configuration);
    System.out.println(s3270.isConnected());
  }
}