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

import java.util.*;
import java.io.*;
import org.h3270.regex.*;

import org.h3270.render.*;

/**
 * An implementation of the Screen interface that is fed by the
 * output of s3270.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class S3270Screen extends AbstractScreen {

  private static final byte FIELD_ATTR_PROTECTED = 0x20;
  private static final byte FIELD_ATTR_NUMERIC   = 0x10;
  private static final byte FIELD_ATTR_DISP_1    = 0x08;
  private static final byte FIELD_ATTR_DISP_2    = 0x04;

  private List bufferData = null;
  private String status = null;
  
  public S3270Screen() {
    width  = 0;
    height = 0;
    buffer = null;
    isFormatted = true; 
  }
  
  public S3270Screen (String filename) {
    try {
      BufferedReader input =
        new BufferedReader 
          (new InputStreamReader(new FileInputStream(filename),
                                 "ISO-8859-1"));
      List lines = new ArrayList();
      String status = null;
      while (true) {
        String line = input.readLine();
        if (line == null) break;
        if (line.startsWith ("data:"))
          lines.add (line);
        else if (Pattern.matches ("[ULE] [UF] [UC] .*", line))
          status = line;
      }
      update (status, lines);
    } catch (IOException ex) {
      throw new RuntimeException ("error: " + ex);
    } 
  }

  /**
   * Pattern that matches a status line from s3270.
   * Example:   U F U C(hostname) I 3 32 80 22 15 0x0 -
   */
  private static Pattern statusPattern =
    Pattern.compile (  "^[ULE] "             // Keyboard State
                     + "[FU] "               // Formatted / Unformatted
                     + "[PU] "               // Protected / Unprotected (at cursor)
                     + "(?:C\\([^)]*\\)|N) " // Connected / Not Connected
                     + "[ILCN] "             // Emulator Mode
                     + "[2-5] "              // Model Number
                     + "[0-9]+ "             // Number of Rows
                     + "[0-9]+ "             // Number of Columns
                     + "([0-9]+) "           // Cursor Row
                     + "([0-9]+) "           // Cursor Column
                     + "0x0 "                // Window ID (always 0x0)
                     + "(?:[0-9]+|-)$"       // Time for last command
                    );

  /**
   * Updates this screen with output from "readbuffer ascii".
   * @param status the status line that was returned by s3270
   * @param bufferData the actual screen data, as a list of strings
   */
  public void update (String status, List bufferData) {
    this.status = status;
    if (status.charAt(2) == 'F') {
      isFormatted = true;
      updateBuffer (bufferData);
    } else {
      isFormatted = false;
      updateBuffer (bufferData);
    }
    Matcher m = statusPattern.matcher(status);
    if (m.find()) {
      cursorX = Integer.parseInt (m.group(2));
      cursorY = Integer.parseInt (m.group(1));
      Field f = getFieldAt (cursorX, cursorY);
      if (f != null) f.setFocused (true);
    } else {
      cursorX = 0;
      cursorY = 0;
    }
  }
  
  private void updateBuffer (List bufferData) {
    this.bufferData = new ArrayList (bufferData);
    height = bufferData.size();
    width = 0;
    buffer = new char[height][];
    fields = new ArrayList();
    for (int y=0; y<height; y++) {
      char[] line = decode ((String)bufferData.get(y), y, fields);
      if (line.length > width) width = line.length;
      buffer[y] = line;
    }     
  }
  
  public List getBufferData() {
    return Collections.unmodifiableList (bufferData);
  }
  
  public void dump (String filename) {
    try {
      PrintWriter out = new PrintWriter (new FileWriter (filename));
      for (Iterator i = bufferData.iterator(); i.hasNext();) {
        out.println (i.next());
      }
      out.println (status);
      out.println ("ok");
      out.close();
    } catch (IOException ex) {
      throw new RuntimeException ("error: " + ex);
    } 
  }

  private static final Pattern formattedCharPattern = 
    Pattern.compile ("SF\\((..)=(..)(,.*?)?\\)|[0-9a-fA-F]{2}");

  /**
   * Decodes a single line from the raw screen buffer dump.
   */
  private char[] decode (String line, int y, List fields) {
    if (line.startsWith ("data: ")) line = line.substring(6);
    StringBuffer result = new StringBuffer();
    byte[] bytes = new byte[1];
    int index = 0;
    int fieldStart = -1;
    byte fieldStartCode = 0x00;
    Matcher m = formattedCharPattern.matcher (line);
    while (m.find()) {
      String code = m.group();
      if (code.startsWith ("SF")) {
        if (!isFormatted)
          throw new RuntimeException 
            ("format information in unformatted screen");
        result.append (' ');
        if (m.group(1).equals("c0")) {
          if (fieldStart != -1) {
            // if we've been in an open field, close it now
            fields.add (createField (fieldStartCode, fieldStart, index, y,
                                     result.substring (fieldStart, index)));
            fieldStart = -1;
            fieldStartCode = 0x00;
          }            
          byte fieldCode = (byte)Integer.parseInt (m.group(2), 16);
          if ((fieldCode & FIELD_ATTR_PROTECTED) == 0) {
            // unprotected: a new field begins
            fieldStart = index + 1;
            fieldStartCode = fieldCode;
          }
        }
      } else
        result.append ((char)(Integer.parseInt (code, 16)));
      index++;
    }
    // a field that extends past the end of the line --
    // we'll consider it to end with the end of this line
    if (fieldStart != -1) {
      fields.add (createField (fieldStartCode, fieldStart, index, y,
                               result.substring (fieldStart, index)));
      fieldStart = -1;
      fieldStartCode = 0x00;
    }  
    return result.toString().toCharArray(); 
  }
  
  private Field createField (byte startCode,
                             int start, int end, int y,
                             String value) {
    return new Field (this, start, y, end - start, value,
                      (startCode & FIELD_ATTR_NUMERIC) != 0,
                          (startCode & FIELD_ATTR_DISP_1) != 0
                       && (startCode & FIELD_ATTR_DISP_2) != 0,
                      false);
  }
                              
  public static void main (String[] args) throws IOException {
    BufferedReader in = new BufferedReader 
                          (new FileReader ("src/org/h3270/test/advantis.dump"));
    List lines = new ArrayList();
    while (true) {
      String line = in.readLine();
      if (line == null || !line.startsWith ("data: ")) break;
      lines.add (line.substring(6));
    }
    S3270Screen s = new S3270Screen();
    s.update ("U F U", lines);
    System.out.println (new TextRenderer().render(s));
  }

}
