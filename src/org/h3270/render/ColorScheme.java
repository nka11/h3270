package org.h3270.render;

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

import org.h3270.host.Field;
import java.util.*;

/**
 * Represents a color scheme for an h3270 terminal.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class ColorScheme {

  private TextStyle protectedNormalStyle = null;
  private TextStyle protectedIntensifiedStyle = null;
  private TextStyle protectedHiddenStyle = null;

  private TextStyle unprotectedNormalStyle = null;
  private TextStyle unprotectedIntensifiedStyle = null;
  private TextStyle unprotectedHiddenStyle = null;
  
  public ColorScheme() {
    protectedNormalStyle = new TextStyle ("black", "white");
    protectedIntensifiedStyle = new TextStyle ("blue", "white");
    completeStyles();
  }
  
  private void completeStyles() {
    if (protectedNormalStyle == null)
      protectedNormalStyle = new TextStyle ("black", "white");
    
    if (protectedIntensifiedStyle == null)
      protectedIntensifiedStyle = new TextStyle (protectedNormalStyle);
    else
      protectedIntensifiedStyle.completeFrom (protectedNormalStyle);
      
    if (protectedHiddenStyle == null) {
      protectedHiddenStyle = new TextStyle (protectedNormalStyle);
      protectedHiddenStyle.foregroundColor = 
        protectedHiddenStyle.backgroundColor;
    } else {
      protectedHiddenStyle.completeFrom (protectedNormalStyle); 
    }

    if (unprotectedNormalStyle == null)
      unprotectedNormalStyle = new TextStyle ("black", "lightgrey");

    if (unprotectedIntensifiedStyle == null)
      unprotectedIntensifiedStyle = new TextStyle (unprotectedNormalStyle);
    else
      unprotectedIntensifiedStyle.completeFrom (unprotectedNormalStyle);
      
    if (unprotectedHiddenStyle == null)
      unprotectedHiddenStyle = new TextStyle (unprotectedNormalStyle);

  }

  public TextStyle getFieldStyle (int fieldCode) {
    if ((fieldCode & Field.ATTR_PROTECTED) != 0) { // protected
      if ((fieldCode & Field.ATTR_DISP_1) == 0)
        return protectedNormalStyle;
      else if ((fieldCode & Field.ATTR_DISP_2) == 0)
        return protectedIntensifiedStyle;
      else
        return protectedHiddenStyle;
    } else {                                       // unprotected
      if ((fieldCode & Field.ATTR_DISP_1) == 0)
        return protectedNormalStyle;
      else if ((fieldCode & Field.ATTR_DISP_2) == 0)
        return protectedIntensifiedStyle;
      else
        return protectedHiddenStyle;
    }
  }

  public String getFieldForegroundColor (int fieldCode) {
    return getFieldStyle(fieldCode).foregroundColor;
  }

  public String toCSS() {
    StringBuffer result = new StringBuffer();
    result.append (".h3270-form {\n");
    result.append (protectedNormalStyle.toCSS());
    result.append ("}\n");

    result.append (".h3270-intensified {\n");
    result.append (protectedIntensifiedStyle.toCSS());
    result.append ("}\n");
    
    result.append (".h3270-hidden {\n");
    result.append (protectedHiddenStyle.toCSS());
    result.append ("}\n");
    
    result.append (".h3270-input {\n");
    result.append (unprotectedNormalStyle.toCSS());
    result.append ("}\n");
    
    result.append (".h3270-input-intensified {\n");
    result.append (unprotectedIntensifiedStyle.toCSS());
    result.append ("}\n");

    result.append (".h3270-input-hidden {\n");
    result.append (unprotectedHiddenStyle.toCSS());
    result.append ("}\n");
    
    return result.toString();
  }

  private class TextStyle {

    public String foregroundColor;
    public String backgroundColor;

    public TextStyle() {}
    
    public TextStyle (TextStyle other) {
      this.foregroundColor = other.foregroundColor; 
      this.backgroundColor = other.backgroundColor;
    }

    public TextStyle (String fg, String bg) {
      foregroundColor = fg;
      backgroundColor = bg;
    }
    
    public void completeFrom (TextStyle other) {
      if (this.foregroundColor == null)
        this.foregroundColor = other.foregroundColor;
      if (this.backgroundColor == null)
        this.backgroundColor = other.backgroundColor;
    }
    
    public String toCSS() {
      StringBuffer result = new StringBuffer();
      if (backgroundColor != null)
        result.append ("  background-color:" + backgroundColor + ";\n");
      if (foregroundColor != null)
        result.append ("  color:" + foregroundColor + ";\n");
      return result.toString();
    }
  }



}
