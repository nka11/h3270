package org.h3270.test;

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

import junit.framework.TestCase;

import org.h3270.host.*;
import org.h3270.render.*;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class S3270ScreenTest extends TestCase {

  public S3270ScreenTest (String name) {
    super (name);
  }

  private Screen createScreenFromDump (String filename) {
    try {
      BufferedReader in = 
        new BufferedReader (new FileReader (filename));
      List lines = new ArrayList();
      String status = null;
      while (true) {
        String line = in.readLine();
        if (line == null)
          break;
        else if (line.startsWith ("data: "))
          lines.add (line.substring (6));
        else if (line.startsWith ("U F U"))
          status = line;
      }
      S3270Screen screen = new S3270Screen();
      screen.update (status, lines);
      return screen;
    } catch (IOException ex) {
      throw new RuntimeException ("IOException while reading dump: " + ex);
    }
  }

  private String readTextScreen (String filename) {
    StringBuffer result = new StringBuffer();
    try {
      BufferedReader in = 
        new BufferedReader 
         (new InputStreamReader
            (new FileInputStream (filename),
             "ISO-8859-1"));
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        result.append (line);
        result.append ('\n');      
      }
      return result.toString();
    } catch (IOException ex) {
      throw new RuntimeException ("IOException while reading text screen: " + ex);
    }
  }

  private void screenTest (String filename) {
    Screen s = createScreenFromDump (filename + ".dump");
    String result   = new TextRenderer().render (s);
    String expected = readTextScreen (filename + ".txt");
    assertEquals (expected, result);
  }

  public void test_screen_3() {
    screenTest ("src/org/h3270/test/screen3");
  }

  public void test_screen_4() {
    screenTest ("src/org/h3270/test/screen4");
  }

  public void test_screen_5() {
    screenTest ("src/org/h3270/test/screen5");
  }

  public static void main (String[] args) {
    junit.textui.TestRunner.run (S3270ScreenTest.class);
  }

}
