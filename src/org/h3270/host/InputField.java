package org.h3270.host;

/*
 * Copyright (C) 2004 it-frameworksolutions
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

import org.h3270.regex.*;

public class InputField extends Field {

  protected boolean isNumeric;
  protected boolean isFocused;
  protected boolean changed;

  public InputField (Screen screen,
                     byte fieldCode,
                     int startx, int starty, int endx, int endy) {
    super (screen,
           fieldCode,
           startx, starty, endx, endy);
    if ((fieldCode & ATTR_NUMERIC) != 0) {
      isNumeric = true;
    }
  }

  public boolean isNumeric() {
    return this.isNumeric;
  }

  public void setFocused (boolean flag) {
    this.isFocused = flag;
  }
  
  public boolean isFocused() {
    return this.isFocused;
  }

  public boolean isChanged() {
    return this.changed;
  }

  /**
   * Sets the value of this Field.
   */
  public void setValue (String value) { 
    if (value == null) getValue();
    if (!value.equals (trim (this.value))) {
      if (value.length() > getWidth())
        this.value = value.substring (0, getWidth());
      else
        this.value = value;
      changed = true;
    }
  }

  private static Pattern trimPattern = 
    Pattern.compile ("^[\\x00 _]*(.*?)[\\x00 _]*$", 0);

  /**
   * Returns a string that is the same as the argument, with leading
   * and trailing ASCII NUL characters removed.
   */
  public static String trim (String value) {
    Matcher m = trimPattern.matcher (value);
    if (m.matches()) 
      return m.group (1);
    else
      return value;
  }


}
