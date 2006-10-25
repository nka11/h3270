package org.h3270.render;

/*
 * Copyright (C) 2004-2006 akquinet framework solutions
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

import org.h3270.host.Field;

/**
 * Represents a color scheme for an h3270 terminal.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel </a>
 * @version $Id$
 */
public class ColorScheme {

  private String name = null;

  private TextStyle protectedNormalStyle = null;
  private TextStyle protectedIntensifiedStyle = null;
  private TextStyle protectedHiddenStyle = null;
  private TextStyle unprotectedNormalStyle = null;
  private TextStyle unprotectedIntensifiedStyle = null;
  private TextStyle unprotectedHiddenStyle = null;

  public ColorScheme() {
    name = "White Background";
    protectedNormalStyle = new TextStyle("black", "white");
    protectedIntensifiedStyle = new TextStyle("blue", "white");
    unprotectedNormalStyle = new TextStyle("green", "lightgrey");
    unprotectedHiddenStyle = new TextStyle("red", "lightgrey");
    completeStyles();
  }

  public boolean equals(Object o) {
    if (o instanceof ColorScheme) {
      ColorScheme other = (ColorScheme) o;

      return name.equals(other.name); // TODO Compare TextStyles?
    } else {
      return false;
    }
  }

  /**
   * Monster constructor.
   */
  public ColorScheme (String name,
                      String protectedNormalForeground,
                      String protectedNormalBackground,
                      String protectedIntensifiedForeground,
                      String protectedIntensifiedBackground,
                      String protectedHiddenForeground,
                      String protectedHiddenBackground,
                      String unprotectedNormalForeground,
                      String unprotectedNormalBackground,
                      String unprotectedIntensifiedForeground,
                      String unprotectedIntensifiedBackground,
                      String unprotectedHiddenForeground,
                      String unprotectedHiddenBackground) {
    this.name = name;
    protectedNormalStyle = new TextStyle (protectedNormalForeground,
                                          protectedNormalBackground);
    protectedIntensifiedStyle = new TextStyle (protectedIntensifiedForeground,
                                               protectedNormalBackground);
    protectedHiddenStyle = new TextStyle (protectedHiddenForeground,
                                          protectedHiddenBackground);
    unprotectedNormalStyle = new TextStyle (unprotectedNormalForeground,
                                            unprotectedNormalBackground);
    unprotectedIntensifiedStyle = new TextStyle (unprotectedIntensifiedForeground,
                                                 unprotectedIntensifiedBackground);
    unprotectedHiddenStyle = new TextStyle (unprotectedHiddenForeground,
                                            unprotectedHiddenBackground);
  }

  public String toString()
  {
      return "ColorScheme: " + getName();
  }
  
  public String getName() {
    return this.name;
  }

  private void completeStyles() {
    if (protectedNormalStyle == null)
      protectedNormalStyle = new TextStyle("black", "white");

    if (protectedIntensifiedStyle == null)
      protectedIntensifiedStyle = new TextStyle(protectedNormalStyle);
    else
      protectedIntensifiedStyle.completeFrom(protectedNormalStyle);

    if (protectedHiddenStyle == null) {
      protectedHiddenStyle = new TextStyle(protectedNormalStyle);
      protectedHiddenStyle.foregroundColor = protectedHiddenStyle.backgroundColor;
    } else {
      protectedHiddenStyle.completeFrom(protectedNormalStyle);
    }

    if (unprotectedNormalStyle == null)
      unprotectedNormalStyle = new TextStyle("black", "lightgrey");

    if (unprotectedIntensifiedStyle == null)
      unprotectedIntensifiedStyle = new TextStyle(unprotectedNormalStyle);
    else
      unprotectedIntensifiedStyle.completeFrom(unprotectedNormalStyle);

    if (unprotectedHiddenStyle == null)
      unprotectedHiddenStyle = new TextStyle(unprotectedNormalStyle);

  }

  public TextStyle getFieldStyle(int fieldCode) {
    if ((fieldCode & Field.ATTR_PROTECTED) != 0) { // protected
      if ((fieldCode & Field.ATTR_DISP_1) == 0)
        return protectedNormalStyle;
      else if ((fieldCode & Field.ATTR_DISP_2) == 0)
        return protectedIntensifiedStyle;
      else
        return protectedHiddenStyle;
    } else { // unprotected
      if ((fieldCode & Field.ATTR_DISP_1) == 0)
        return protectedNormalStyle;
      else if ((fieldCode & Field.ATTR_DISP_2) == 0)
        return protectedIntensifiedStyle;
      else
        return protectedHiddenStyle;
    }
  }

  public String getFieldForegroundColor(int fieldCode) {
    return getFieldStyle(fieldCode).foregroundColor;
  }

  public String toCSS() {
    StringBuffer result = new StringBuffer();
    result.append (standardCSS());
    result.append (extendedHighlightCSS());
    result.append (extendedColorCSS());
    return result.toString();
  }
  
  private String css (String name, TextStyle style) {
    StringBuffer result = new StringBuffer();
    result.append(".");
    result.append(name);
    result.append(" {\n");
    result.append(style.toCSS());
    result.append("\n}\n");
    return result.toString();
  }

  private String css (String name, String color) {
    StringBuffer result = new StringBuffer();
    result.append(".");
    result.append(name);
    result.append(" {\n");
    result.append("  color: " + color + "!important;");
    result.append("\n}\n");
    return result.toString();
  }

  private String standardCSS() {
    StringBuffer result = new StringBuffer();
    result.append (css ("h3270-form",              protectedNormalStyle));
    result.append (css ("h3270-intensified",       protectedIntensifiedStyle));
    result.append (css ("h3270-hidden",            protectedHiddenStyle));
    result.append (css ("h3270-input",             unprotectedNormalStyle));
    result.append (css ("h3270-input-intensified", unprotectedIntensifiedStyle));
    result.append (css ("h3270-input-hidden",      unprotectedHiddenStyle));
    return result.toString();
  }
  
  private String extendedHighlightCSS() {
    StringBuffer result = new StringBuffer();
    result.append(".h3270-highlight-blink {\n");
    result.append("  text-decoration:blink;\n");
    result.append("}\n");

    result.append(".h3270-highlight-underscore {\n");
    result.append("  text-decoration:underline;\n");
    result.append("}\n");

    result.append(".h3270-highlight-rev-video {\n");
    // the following is not quite correct, because "reverse video"
    // should reverse the style that would otherwise be effective
    // in a given field, not just the "normal" style, but we'll
    // leave it at this for now
    result.append(protectedNormalStyle.reverse().toCSS());
    result.append("}\n");
    
    return result.toString();
  }
  
  private String extendedColorCSS() {
    StringBuffer result = new StringBuffer();
    // these will soon be configurable in h3270-config.xml
    result.append (css ("h3270-color-blue",      "blue"));
    result.append (css ("h3270-color-red",       "red"));
    result.append (css ("h3270-color-pink",      "#ffb6c1"));
    result.append (css ("h3270-color-green",     "lime"));
    result.append (css ("h3270-color-turquoise", "#40e0d0"));
    result.append (css ("h3270-color-yellow",    "yellow"));
    result.append (css ("h3270-color-white",     "white"));
    return result.toString();
  }
  
  /**
   * Represents the foreground and background colors of a given field.
   */
  public class TextStyle {

    public String foregroundColor;
    public String backgroundColor;

    public TextStyle() {
    }

    public TextStyle(TextStyle other) {
      this.foregroundColor = other.foregroundColor;
      this.backgroundColor = other.backgroundColor;
    }

    public TextStyle(String fg, String bg) {
      foregroundColor = fg;
      backgroundColor = bg;
    }

    public void completeFrom(TextStyle other) {
      if (this.foregroundColor == null)
        this.foregroundColor = other.foregroundColor;
      if (this.backgroundColor == null)
        this.backgroundColor = other.backgroundColor;
    }

    public TextStyle reverse() {
      return new TextStyle(this.backgroundColor, this.foregroundColor);
    }
    
    public String toCSS() {
      StringBuffer result = new StringBuffer();
      if (backgroundColor != null)
        result.append("  background-color:" + backgroundColor + ";\n");
      if (foregroundColor != null)
        result.append("  color:" + foregroundColor + ";\n");
      return result.toString();
    }
  }

}