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

import junit.framework.TestCase;

import org.h3270.host.*;
import org.h3270.render.*;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class FileScreenTest extends TestCase {

  public FileScreenTest (String name) {
    super (name);
  }

  private void screenTest (String filename) {
    FileScreen f = new FileScreen (filename);
    String result = new TextRenderer().render (f);

    StringBuffer expected = new StringBuffer();
    try {
      BufferedReader in = 
        new BufferedReader 
         (new InputStreamReader
            (new FileInputStream (filename),
             "ISO-8859-1"));
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        expected.append (line);
        expected.append ('\n');      
      }
      String re$ult = expected.toString();
      assertEquals (expected.toString(), result);
    } catch (IOException ex) {
      fail ("IOException during test case: " + ex);
    }
  }

  public void test_screen_1() {
    screenTest ("src/org/h3270/test/screen1.txt");
  }

  public void test_screen_2() {
    screenTest ("src/org/h3270/test/screen2.txt");
  }

  public static void main (String[] args) {
    junit.textui.TestRunner.run (FileScreenTest.class);
  }

}
