package org.h3270.host;

/*
 * Copyright (C) 2004-2008 akquinet tech@spree
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

import java.util.regex.*;

/**
 * Represents a Field that allows user input.
 * 
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class InputField extends Field {

  protected boolean isNumeric;
  protected boolean isFocused;
  protected boolean changed;

  public InputField (Screen screen,
                     byte fieldCode,
                     int startx, int starty, int endx, int endy,
                     int color, int ext_highlight) {
    super (screen,
           fieldCode,
           startx, starty, endx, endy, color, ext_highlight);
    if ((fieldCode & ATTR_NUMERIC) != 0) {
      isNumeric = true;
    }
  }

  public InputField (Screen screen,
                     byte fieldCode,
                     int startx, int starty, int endx, int endy) {
    this (screen, fieldCode, startx, starty, endx, endy,
          ATTR_COL_DEFAULT, ATTR_EH_DEFAULT);
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
   * Sets the value of this Field.  This method does not work for
   * multiline fields; use setValue(int, String) instead.
   */
  public void setValue (String newValue) {
    if (this.isMultiline())
      throw new RuntimeException ("use setValue(int, String) for multiline field");
    if (this.value == null) getValue();
    if (!newValue.equals (trim (this.value))) {
      int width = endx - startx + 1;
      if (newValue.length() > width)
        this.value = newValue.substring (0, width);
      else
        this.value = newValue;
      changed = true;
    }
  }

  private static final Pattern LINE_PATTERN =
    Pattern.compile (".*\n", Pattern.MULTILINE);

  /**
   * Sets the value of one of the lines in a multi-line field.
   * 
   * @param lineNumber the number of the line to be changed, starting at zero
   * @param newValue The new value for this line.  It is not supposed to have
   *                 a trailing newline.
   */
  public void setValue (int lineNumber, String newValue) {
    if (this.value == null) getValue();
    StringBuffer result = new StringBuffer();
    Matcher m = LINE_PATTERN.matcher (this.value);
    for (int i=0; i < lineNumber; i++) {
      m.find();
      result.append (m.group(0));
    }
    result.append (trim (newValue));
    if (lineNumber < getHeight()-1) {
      result.append ("\n");
      m.find();
      result.append (this.value.substring (m.end()));
    }
    String val = result.toString();
    if (!val.equals(this.value)) {
      this.value = val;
      changed = true;
    }
  }

  private static final Pattern TRIM_PATTERN = 
    Pattern.compile ("^[\\x00 _]*(.*?)[\\x00 _]*$", 0);

  /**
   * Returns a string that is the same as the argument, with leading
   * and trailing ASCII NUL characters, blanks and underscores removed.
   */
  public static String trim (String value) {
    Matcher m = TRIM_PATTERN.matcher (value);
    if (m.matches()) 
      return m.group(1).replace((char)0, ' ');
    else
      return value;
  }


}
