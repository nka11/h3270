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

/**
 * Test implementation of the Screen interface.  It reads an ASCII dump
 * of a 3270 screen from a file.  In this file, input fields must be delimited
 * by '{' and '}'.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class FileScreen extends AbstractScreen {

  public FileScreen (String filename) {
    try {
      Reader in = new InputStreamReader (new FileInputStream (filename),
                                         "ISO-8859-1");
      width = 80;
      height = 24;
      buffer = new char[height][width];
      for (int y=0; y<height; y++)
        for (int x=0; x<width; x++)
          buffer[y][x] = ' '; 

      for (int y=0; y<height; y++) {
        for (int x=0; x<=width; x++) {
          int ch = in.read();
          if (ch == -1) 
            return;
          else if (ch == '\n')
            break;
          else if (ch == '\r') {
          	in.read(); // skip \n
          	break;
          }
          else if (ch == '{')
            x = x + readField (x, y, in);
          else
          {
            buffer[y][x] = (char)ch;
          }
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException ("IOException while reading file");
    }
  }

  /**
   * Reads a field from the file and inserts it into the internal
   * data structures.  Returns the number of characters read. 
   */
  private int readField (int x, int y, Reader in) throws IOException {
    buffer[y][x] = ' '; // replace the delimiter that our caller already read
    int index = x;
    int length = 0;
    StringBuffer value = new StringBuffer();
    while (true) {
      int ch = in.read();
      if (ch == -1)
        throw new RuntimeException ("EOF while reading field");
      else if (ch == '}') {
        buffer[y][index+1] = ' ';
        Field f = new Field (this, x+1, y, index+1, y, value.toString(),
                             false, false, false, true);
        fields.add (f);
        return length + 1;
      } else {
        index++;
        length++;
        value.append ((char)ch);
        buffer[y][index] = (char)ch;
      }
    }
  }

  public static void main (String[] args) {
    FileScreen f = new FileScreen ("src/org/h3270/test/screen1.txt");
    for (int y = 0; y < f.getHeight(); y++) {
      for (int x = 0; x < f.getWidth(); x++) {
        System.out.print (f.charAt(x, y));
      }
      System.out.println();
    }
    System.out.println();
    System.out.println("Fields: ");
    for (Iterator i = f.getFields().iterator(); i.hasNext();) {
      System.out.println (i.next());
    }
  }

}
