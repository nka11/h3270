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

/**
 * @author <a href="mailto:spiegel@gnu.org">Andre Spiegel</a>
 * @version $Id$
 */
public class HtmlRenderer implements Renderer {

  /**
   * If set, contains the URL to be placed into the screen form's ACTION
   * field.  This is used when rendering for a Portlet.
   */
  private String actionURL = "";
  
  /**
   * Constructs an HtmlRenderer with an empty actionURL, suitable
   * for rendering by a Servlet.  When rendering for a Portlet, use
   * the constructor that takes an actionURL parameter.
   */
  public HtmlRenderer() {
  }

  /**
   * Constructs an HtmlRenderer with a given actionURL, suitable for rendering
   * by a Portlet.
   * @param actionURL the URL to be used in the screen form's ACTION field
   */
  public HtmlRenderer (String actionURL) {
    this.actionURL = actionURL;
  }
  
  public boolean canRender (Screen s) {
    return true;
  }
  
  public boolean canRender (String screenText) {
    return true;
  }

  public String render (Screen screen) {
    StringBuffer result = new StringBuffer();
    
    result.append ("<form name=\"screen\" action=\"" + actionURL + "\" method=\"POST\" class=\"h3270-form\">\n");
    if (screen.isFormatted())
      renderFormatted (screen, result);
    else
      renderUnformatted (screen, result);
    result.append ("<input type=hidden name=key>\n");
    result.append ("</form>\n");
    
    appendFocus (screen, result);
    
    return result.toString();
  }

  /**
   * If screen has a focused field, append Javascript code to buffer 
   * so that this field gets the focus in the client browser.
   */
  protected void appendFocus (Screen screen, StringBuffer buffer) {
    Field f = screen.getFocusedField();
    if (f != null) {
      buffer.append ("<script>\n");
      buffer.append ("  document.screen.field_" + f.getStartX() + "_" + f.getStartY() +
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
        if (f.isIntensified()) 
          result.append ("<span class=\"h3270-intensified\">");
        else if (f.isHidden())
          result.append ("<span class=\"h3270-hidden\">");
        String text = f.getText();
        for (int j=0; j<text.length(); j++) {
          char ch = text.charAt(j);
          if (ch == '\u0000')
            result.append (' ');
          else
            result.append (ch);
        }
        if (f.isIntensified() || f.isHidden()) result.append ("</span>");
      }
    }
    result.append ("</pre>");
  }

  private void renderUnformatted (Screen screen, StringBuffer result) {
    result.append ("<textarea name=field class=\"h3270-input\" "
                   + "cursor=lime "
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
    result.append ("<script>\n");
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
    result.append ("type=" + (f.isHidden() ? "password " : "text "));
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
    result.append ("value=\"" + InputField.trim (value) + "\" ");
    result.append ("maxlength=\"" + width + "\" ");
    result.append ("size=\"" + width + "\" ");
    result.append (">");
  }
     
}
