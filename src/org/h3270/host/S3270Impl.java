package org.h3270.host;

/*
 * Copyright (C) 2003 it-frameworksolutions
 *
 * This file is part of h3270.
 *
 * h3270 is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * h3270 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with h3270; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307 USA
 */

import java.io.*;
import java.util.*;

import org.h3270.render.*;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class S3270Impl implements S3270 {

  private Process s3270   = null;
  private String hostname = null;

  private PrintWriter    out = null;
  private BufferedReader in  = null;
  private BufferedReader err = null;

  private List log = null;

  private S3270Screen screen = null;

  public S3270Impl (String hostname, String path_to_s3270_binary) {
    try {
      File s3270_binary = new File (path_to_s3270_binary, "s3270");
      s3270 = Runtime.getRuntime().exec 
                  (s3270_binary.toString()
                 + " -model 3 "
                 // uncomment the following to support different charsets
                 // (codepages) -- see s3270 docs for supported charsets
                 // + " -charset german "
                 + hostname);
      out = new PrintWriter (new OutputStreamWriter (s3270.getOutputStream(),
                                                     "ISO-8859-1"));
      in  = new BufferedReader (new InputStreamReader (s3270.getInputStream(),
                                                       "ISO-8859-1"));
      this.hostname = hostname;
      screen = new S3270Screen();
      waitFormat();
    } catch (IOException ex) {
      throw new RuntimeException ("IO Exception when starting s3270: " + ex);
    }
  }

  /**
   * Represents the result of an s3270 command.
   */
  private class Result {
    public List data;
    public String status;
    public Result (List data, String status) {
      this.data = data;
      this.status = status; 
    } 
  }

  /**
   * Perform an s3270 command.  All communication with s3270 should
   * go via this method.
   */
  private Result doCommand (String command) {
    try {
      out.println (command);
      out.flush();
      if (log != null) log.add ("---> " + command);
      List lines = new ArrayList();
      while (true) {
        String line = in.readLine();
        if (line == null) throw new EOFException ("premature end of data");
        if (log != null) log.add ("<--- " + line);
        if (line.equals ("ok")) break;
        lines.add (line);
      }
      int size = lines.size();
      return new Result (lines.subList (0, size-1), 
                         (String)lines.get (size-1));
    } catch (IOException ex) {
      throw new RuntimeException ("IOException during command: " + command 
                                + ", " + ex);
    }
  }

  /**
   * waits for a formatted screen
   */
  private void waitFormat() {
    try {
      for (int i=0; i<50; i++) {
        Result r = doCommand ("");
        if (r.status.startsWith ("U F")) return;
        Thread.sleep (100);
      }
    } catch (Exception e) {
      // ignored
    }
  }

  public void disconnect() {
    out.println ("quit");
    out.flush();

    new Thread (new Runnable() {
      public void run() {
        try {
          Thread.sleep (1000);
          if (s3270 != null) s3270.destroy();
        } catch (InterruptedException ex) {
          if (s3270 != null) s3270.destroy();
        }
      }
    }).start();
    
    try { s3270.waitFor(); } catch (InterruptedException ex) { /* ignore */ }
    try { in.close();      } catch (IOException ex) { /* ignore */ }
    out.close();
    in = null;
    out = null;
    s3270 = null;
  }
 

  public String getHostname() {
    return hostname;
  }
  
  public void dumpScreen(String filename) {
    screen.dump(filename); 
  }
  
  public void startLogging() {
    log = new ArrayList();
  }

  public List getLog() {
    return log;
  }

  public void stopLogging() {
    log = null;
  }

  /**
   * Updates the screen object with s3270's buffer data.
   */
  public void updateScreen() {
    while (true) {
      Result r = doCommand ("readbuffer ascii");
      if (r.data.size() > 0) {
        String firstLine = (String)r.data.get(0);
        if (firstLine.startsWith ("data: Keyboard locked"))
          continue;
      }
      screen.update (r.status, r.data);
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
      Field f = (Field)i.next();
      if (f.isChanged()) {
        doCommand ("movecursor (" + f.getY() + ", " + f.getX() + ")");
        doCommand ("eraseeof");
        String value = f.getValue();
        for (int j=0; j < value.length(); j++) {
          char ch = value.charAt(j);
          doCommand ("key (0x" + Integer.toHexString (ch) + ")");
        }
      }
    }     
  }

  public void submitUnformatted (String data) {
    int index = 0;
    for (int y = 0; y < screen.getHeight(); y++) {
      for (int x = 0; x < screen.getWidth(); x++) {
        char newCh = data.charAt (index);
        if (newCh != screen.charAt(x, y)) {
          doCommand ("movecursor (" + y + ", " + x + ")");
          doCommand ("key (0x" + Integer.toHexString (newCh) + ")");
        }
        index++;
      }
      index++; // skip newline
    }
  }

  // s3270 actions below this line

  public void clear() {
    doCommand ("clear");
  }

  public void enter() {
    doCommand ("enter");
    waitFormat();
  }

  public void eraseEOF() {
    doCommand ("eraseEOF");
  }

  public void pa (int number) {
    doCommand ("pa(" + number + ")");
    waitFormat(); 
  }
  
  public void pf (int number) {
    doCommand ("pf(" + number + ")");
    waitFormat();
  }
  
  public void reset() {
    doCommand ("reset");
  }
  
  public void sysReq() {
    doCommand ("sysReq");
  }
  
  public void attn() {
    doCommand ("attn");
  }
  
  public static void main (String[] args) {
    S3270 s3270 = new S3270Impl ("locis.loc.gov", "/home/spiegel/bin");
    s3270.updateScreen();
    Screen s = s3270.getScreen();
    System.out.println (new TextRenderer().render(s)); 
  }
  
}
