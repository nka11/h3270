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

  protected Screen screen;

  protected int startx;
  protected int starty;
  protected int endx;
  protected int endy;
  protected String value;
  
  public static final int DISPLAY_NORMAL      = 0;
  public static final int DISPLAY_INTENSIFIED = 1;
  public static final int DISPLAY_HIDDEN      = 2;
  
  private int displayMode = DISPLAY_NORMAL;

  public Field (Screen screen,
                byte fieldCode,
                int startx, int starty, int endx, int endy) {
    this.screen = screen;
    this.startx = startx;
    this.starty = starty;
    this.endx = endx;
    this.endy = endy;
    if ((fieldCode & ATTR_DISP_1) == 0)
      displayMode = DISPLAY_NORMAL;
    else if ((fieldCode & ATTR_DISP_2) == 0)
      displayMode = DISPLAY_INTENSIFIED;
    else
      displayMode = DISPLAY_HIDDEN; 
  }

  /**
   * Returns the x coordinate (column) at which this Field begins.
   * Column numbers start at zero.  The number returned is the position
   * of the Field's first character, not of the control character that
   * opens the Field.
   */
  public int getStartX() { return startx; }
  
  /**
   * Returns the y coordinate (row) in which this Field begins.  Row numbers
   * start at zero, increasing downward from the top.
   */  
  public int getStartY() { return starty; }
  
  /**
   * Returns the x coordinate (column) at which this Field ends.
   * Column numbers start at zero.  The number returned is the position
   * of the Field's last character, not of the control character that
   * terminates the Field.
   */
  public int getEndX() { return endx; }
  
  /**
   * Returns the y coordinate (row) in which this Field ends.  Row numbers
   * start at zero, increasing downward from the top.
   */  
  public int getEndY() { return endy; }
    
  /**
   * Returns the width (number of characters) of this Field.
   * This does not include the control characters that delimit the Field.
   * @deprecated this method will disappear soon
   */
  public int getWidth() { return endx - startx; } 
  
  /**
   * Returns the Screen of which this Field is a part.
   */
  public Screen getScreen() { return screen; }
  
  /**
   * Returns the current value of this Field.
   */
  public String getValue() { 
    if (value == null) {
      value = screen.substring (startx, starty, endx, endy);
    }
    return value; 
  }
  
  public String getText() {
    StringBuffer result = new StringBuffer();
    if (startx == 0) {
      if (starty > 0)
        result.append (" \n");
    } else
      result.append (" ");
    result.append (this.getValue());
    if (endx == screen.getWidth() - 1 && starty <= endy)
      result.append ("\n");
    return result.toString();
  }
  

    
  
  public boolean isIntensified() {
    return displayMode == DISPLAY_INTENSIFIED;
  }

  public boolean isHidden() {
    return displayMode == DISPLAY_HIDDEN;
  }


}
