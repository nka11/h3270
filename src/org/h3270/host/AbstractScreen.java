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

/**
 * @author <a href="mailto:spiegel@gnu.org">Andre Spiegel</a>
 * @version $Id$
 */
public abstract class AbstractScreen implements Screen {

  protected char buffer[][] = null;
  
  protected int width  = 0;
  protected int height = 0;
  protected int cursorX = 0;
  protected int cursorY = 0;

  protected boolean isFormatted = true;

  protected List fields = new ArrayList();

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public char charAt(int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height)
      throw new IndexOutOfBoundsException
        ("(" + x + ", " + y +")" 
         + ", should be in (0.." + width + ", 0.." + height + ")");
    InputField f = getInputFieldAt (x, y);
    if (f != null) {
      String value = f.getValue();
      return value.charAt (x - f.getStartX()); 
    } else {
      char[] line = buffer[y];
      if (x >= line.length)
        return ' ';
      else
        return line[x];
    }
  }

  public String substring (int startx, int starty, int endx, int endy) {
    if (starty > endy) { 
      return "";
    } else if (starty == endy) {
      if (startx > endx)
        return "";
      else
        return this.substring (startx, endx, starty);
    } else {
      StringBuffer result = new StringBuffer();
      result.append (this.substring (startx, width-1, starty));
      result.append ('\n');
      for (int y = starty+1; y < endy; y++) {
        result.append (this.substring (y));
        result.append ('\n');
      }
      result.append (this.substring (0, endx, endy));
      return result.toString();
    }
  }
    
  public String substring (int startx, int endx, int y) {
    return new String (buffer[y], startx, endx - startx + 1);
  }  
   
  public String substring (int y) {
    return new String (buffer[y]);    
  }

  public List getFields() {
    return Collections.unmodifiableList (fields);
  }

  public InputField getInputFieldAt(int x, int y) {
    for (Iterator i = fields.iterator(); i.hasNext();) {
      Field f = (Field)i.next();
      if (!(f instanceof InputField))
        continue;
      if (y == f.getStartY()) {
        int fx = f.getStartX();
        if (x == fx || (x > fx && x < fx + f.getWidth()))
          return (InputField)f;
      }
    }
    return null;    
  }

  public boolean isInputField(int x, int y) {
    return getInputFieldAt (x, y) != null;
  }

  public InputField getFocusedField() {
    return this.getInputFieldAt (cursorX, cursorY);
  }

  public boolean isFormatted() {
    return isFormatted;
  }

}
