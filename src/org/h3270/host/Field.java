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

import org.h3270.regex.*;

/**
 * Represents a Field that allows user input.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class Field {

  public static final byte ATTR_PROTECTED = 0x20;
  public static final byte ATTR_NUMERIC   = 0x10;
  public static final byte ATTR_DISP_1    = 0x08;
  public static final byte ATTR_DISP_2    = 0x04;
  
  // pseudo attribute for fields that should not appear on the screen at all
  public static final byte ATTR_NOT_RENDERED =   ATTR_PROTECTED 
                                               | ATTR_DISP_1
                                               | ATTR_DISP_2;

  private Screen screen;

  private int x;
  private int y;
  private int width;
  private String value;
  private boolean isNumeric;
  private boolean isHidden;
  private boolean isFocused;
  private boolean isRendered;
  
  private boolean changed = false;
  
  public Field (Screen screen, int x, int y, int width,
                String value, boolean isNumeric, boolean isHidden,
                boolean isFocused, boolean isRendered) {
    this.screen = screen;
    this.x = x;
    this.y = y;
    this.width = width;
    this.value = value;
    this.isNumeric = isNumeric;
    this.isHidden = isHidden;
    this.isFocused = isFocused;
    this.isRendered = isRendered;
  }

  /**
   * Returns the x coordinate (column) at which this Field begins.
   * Column numbers start at zero.  The number returned is the position
   * of the Field's first character, not of the control character that
   * opens the Field.
   */
  public int getX() { return x; }
  
  /**
   * Returns the y coordinate (row) of this Field.  Row numbers
   * start at zero, increasing downward from the top.
   */  
  public int getY() { return y; }
  
  /**
   * Returns the width (number of characters) of this Field.
   * This does not include the control characters that delimit the Field.
   */
  public int getWidth() { return width; } 
  
  /**
   * Returns the Screen of which this Field is a part.
   */
  public Screen getScreen() { return screen; }
  
  /**
   * Returns the current value of this Field.
   */
  public String getValue() { return value; }
  
  /**
   * Sets the value of this Field.
   */
  public void setValue (String value) { 
    if (!value.equals (trim (this.value))) {
      if (value.length() > width)
        this.value = value.substring (0, width);
      else
        this.value = value;
      changed = true;
    }
  }

  /**
   * Returns true if the value of this field has been changed to
   * a different value.
   */ 
  public boolean isChanged() {
    return changed;
  }
  
  public boolean isNumeric()    { return isNumeric; }
  public boolean isHidden()     { return isHidden; }
  public boolean isFocused()    { return isFocused; }
  public boolean isRendered()   { return isRendered; }

  public void setFocused (boolean flag) {
    this.isFocused = flag;
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
