package org.h3270.test;

/*
 * Copyright (C) 2003-2006 akquinet framework solutions
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */

import java.io.*;

import junit.framework.TestCase;

import org.h3270.host.*;
import org.h3270.render.*;
import java.net.URL;

/**
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class FileScreenTest extends TestCase {

  public FileScreenTest (String name) {
    super (name);
  }

  private void screenTest (String filename) {
    FileScreen f = new FileScreen (filename);
    String result = new TextRenderer().render (f);

    URL url = getClass().getResource(filename);

    StringBuffer expected = new StringBuffer();
    try {
      BufferedReader in =
        new BufferedReader
         (new InputStreamReader
          (url.openStream(),
             "ISO-8859-1"));
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        expected.append (line);
        expected.append ('\n');
      }
      assertEquals (expected.toString(), result);
    } catch (IOException ex) {
      fail ("IOException during test case: " + ex);
    }
  }

  public void test_screen_1() {
    screenTest ("/org/h3270/test/screen1.txt");
  }

  public void test_screen_2() {
    screenTest ("/org/h3270/test/screen2.txt");
  }

  public static void main (String[] args) {
    junit.textui.TestRunner.run (FileScreenTest.class);
  }

}
