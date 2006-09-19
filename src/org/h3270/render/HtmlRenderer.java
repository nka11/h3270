package org.h3270.render;

/*
 * Copyright (C) 2003, 2004 it-frameworksolutions
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
import org.h3270.host.*;
import org.h3270.web.*;

/**
 * @author <a href="mailto:spiegel@gnu.org">Andre Spiegel</a>
 * @version $Id$
 */
public class HtmlRenderer implements Renderer {

  /**
   * Maps the integer values of extended color attributes to the
   * corresponding CSS classes.
   */
  private IntMap extendedColorMap = null;
  
  /**
   * Maps the integer values of extended highlight attributes to the
   * corresponding CSS classes.
   */
  private IntMap extendedHighlightMap = null;
  
  public boolean canRender (Screen s) {
    return true;
  }
  
  public boolean canRender (String screenText) {
    return true;
  }

  public static String escapeHTMLAttribute (String value) {
	    return value.replaceAll("\\&","&amp;").
	                  replaceAll("\"","&quot;").
	                  replaceAll("\'","&#39;");
  }
  
  public static String escapeHTMLText (String value) {
	    return value.replaceAll("\\&","&amp;").
                     replaceAll("\\<","&lt;").
                     replaceAll("\\>","&gt;");
  }
  
  public String render (Screen screen, String actionURL, int number) {
    StringBuffer result = new StringBuffer();
    
    result.append ("<form id=\"screen\" action=\"" + actionURL + "\" method=\"post\" class=\"h3270-form\">\n");
    if (screen.isFormatted())
      renderFormatted (screen, result);
    else
      renderUnformatted (screen, result);
    result.append ("<div><input type=\"hidden\" name=\"key\" /></div>\n");
    if (number >= 0) {
      result.append ("<div><input type=\"hidden\" name=\"" 
                     + SessionState.TERMINAL + "\" value=\"" 
                     + number + "\"></div>\n");
    }
    result.append ("</form>\n");
    
    appendFocus (screen, result);
    
    return result.toString();
  }

  public String render (Screen screen) {
    return this.render (screen, "", -1);
  }
  
  public String render (Screen screen, String actionURL) {
    return this.render (screen, actionURL, -1);
  }
  
  /**
   * If screen has a focused field, append Javascript code to buffer 
   * so that this field gets the focus in the client browser.
   */
  protected void appendFocus (Screen screen, StringBuffer buffer) {
    Field f = screen.getFocusedField();
    if (f != null) {
      buffer.append ("<script type=\"text/javascript\">\n");
      buffer.append ("  document.forms[\"screen\"]." +
         "field_" + f.getStartX() + "_" + f.getStartY() +
         ".focus()\n");
      buffer.append ("</script>\n");
    }
  }

  private void renderFormatted (Screen screen, StringBuffer result) {
    result.append ("<pre>");
    for (Iterator i = screen.getFields().iterator(); i.hasNext();) {
      Field f = (Field)i.next();
      if (f instanceof InputField) {
        if (f.getStartX() == 0) {
          if (f.getStartY() > 0) {
            result.append (" \n");
          }
        } else
          result.append (" ");          
        renderInputField (result, (InputField)f);
        if (f.getEndX() == screen.getWidth()-1 && f.getEndY() >= f.getStartY())
          result.append ("\n");
      } else {
        String text = f.getText();
        // First append the control character that started the field,
        // without any formatting applied to it.
        if (text.length() > 0) {
          result.append(text.charAt(0));
        }
        if (needSpan (f)) {
          result.append ("<span class=\"" + protectedFieldClass(f) + "\">");
        }
        // Now the rest of the field.
        if (text.length() > 1) {
          String newText = text.substring(1).replaceAll ("\u0000", " ");
          result.append (escapeHTMLText (newText));
        }
        if (needSpan(f)) result.append ("</span>");
      }
    }
    result.append ("</pre>");
  }
  
  private boolean needSpan (Field f) {
    return f.isIntensified()    || f.isHidden() || 
           f.hasExtendedColor() || f.hasExtendedHighlight();
  }
   
  private String protectedFieldClass (Field f) {
    StringBuffer result = new StringBuffer();
    boolean isIntensified = f.isIntensified();
    boolean isHidden      = f.isHidden();
    boolean hasExtendedColor     = f.hasExtendedColor();
    boolean hasExtendedHighlight = f.hasExtendedHighlight();

    if (isIntensified) result.append ("h3270-intensified");
    else if (isHidden) result.append ("h3270-hidden");
    
    if (hasExtendedColor) {
      if (isIntensified || isHidden) result.append (" ");
      result.append (getExtendedColorMap().get (f.getExtendedColor()));
    }
    
    if (hasExtendedHighlight) {
      if (isIntensified || isHidden || hasExtendedColor) result.append (" ");
      result.append (getExtendedHighlightMap().get (f.getExtendedHighlight()));
    }
      
    return result.toString();
  }
  
  private IntMap getExtendedColorMap() {
    if (extendedColorMap == null) {
      extendedColorMap = new IntMap();
      extendedColorMap.put (Field.ATTR_COL_BLUE,      "h3270-color-blue");
      extendedColorMap.put (Field.ATTR_COL_RED,       "h3270-color-red");
      extendedColorMap.put (Field.ATTR_COL_PINK,      "h3270-color-pink");
      extendedColorMap.put (Field.ATTR_COL_GREEN,     "h3270-color-green");
      extendedColorMap.put (Field.ATTR_COL_TURQUOISE, "h3270-color-turquoise");
      extendedColorMap.put (Field.ATTR_COL_YELLOW,    "h3270-color-yellow");
      extendedColorMap.put (Field.ATTR_COL_WHITE,     "h3270-color-white");
    }
    return extendedColorMap;
  }
   
  private IntMap getExtendedHighlightMap() {
    if (extendedHighlightMap == null) {
      extendedHighlightMap = new IntMap();
      extendedHighlightMap.put (Field.ATTR_EH_BLINK,     "h3270-highlight-blink");
      extendedHighlightMap.put (Field.ATTR_EH_REV_VIDEO, "h3270-highlight-rev-video");
      extendedHighlightMap.put (Field.ATTR_EH_UNDERSCORE,"h3270-highlight-underscore");
    }
    return extendedHighlightMap;
  }

  private void renderUnformatted (Screen screen, StringBuffer result) {
    result.append ("<textarea name=\"field\" class=\"h3270-input\" "
                   + "cursor=\"lime\" "
                   + "rows=" + screen.getHeight()
                   + " cols=" + screen.getWidth() + ">\n");
    for (int y = 0; y < screen.getHeight(); y++) {
      for (int x = 0; x < screen.getWidth(); x++) {
        char ch = screen.charAt (x, y);
        if (ch == '\u0000')
          result.append (' ');
        else
          result.append (ch);
      }
      result.append ("\n");
    }
    
    result.append ("</textarea>\n");
    result.append ("<script type=\"text/javascript\">\n");
    result.append ("  document.screen.field.focus();\n");
    result.append ("</script>\n");
  }

  protected void renderInputField (StringBuffer result, InputField f) {
    if (!f.isMultiline()) {
      createHtmlInput (result, f, f.getValue(), 
                       -1, f.getEndX() - f.getStartX() + 1);
    } else {
      createHtmlInput (result, f, f.getValue(0), 0, 
                       f.getScreen().getWidth() - f.getStartX());
      result.append ("\n");
      for (int i=1; i < f.getHeight() - 1; i++) {
        createHtmlInput (result, f, f.getValue(i), i, f.getScreen().getWidth());
        result.append ("\n");
      }
      int lastLine = f.getHeight() - 1;
      createHtmlInput (result, f, f.getValue(lastLine), lastLine,
                       f.getEndX()+1);
    }
  }

  protected void createHtmlInput (StringBuffer result, InputField f,
                                  String value, int lineNumber, int width) {
    result.append ("<input ");
    result.append ("type=" + (f.isHidden() ? "\"password\" " : "\"text\" "));
    result.append ("name=\"field_" + f.getStartX() + "_" + f.getStartY());
    if (lineNumber != -1)
      result.append ("_" + lineNumber);
    result.append ("\" ");
    if (f.isIntensified()) 
      result.append ("class=\"h3270-input-intensified\" ");
    else if (f.isHidden())
      result.append ("class=\"h3270-input-hidden\" ");
    else
      result.append ("class=\"h3270-input\" ");
    result.append ("value=\"" + escapeHTMLAttribute(InputField.trim(value)) + "\" ");
    result.append ("maxlength=\"" + width + "\" ");
    result.append ("size=\"" + width + "\" ");
    result.append ("/>");
  }
     
}
