package org.h3270.render;

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

import org.h3270.host.*;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class HtmlRenderer implements Renderer {

  public boolean canRender (Screen s) {
    return true;
  }
  
  public boolean canRender (String screenText) {
    return true;
  }

  public String render (Screen screen) {
    StringBuffer result = new StringBuffer();
    
    result.append ("<form name=\"screen\" action=\"\" method=\"POST\" class=\"cicsform\">\n");
    if (screen.isFormatted())
      renderFormatted (screen, result);
    else
      renderUnformatted (screen, result);
    result.append ("<input type=hidden name=key>\n");
    result.append ("</form>"); 
    
    return result.toString();
  }

  private void renderFormatted (Screen screen, StringBuffer result) {
    for (int y = 0; y < screen.getHeight(); y++) {
      for (int x = 0; x < screen.getWidth(); x++) {
        Field f = screen.getFieldAt (x, y);
        if (f != null) {
          renderField (result, f);
          x += f.getWidth() - 1;  
        } else {
          char ch = screen.charAt (x, y);
          if (ch == ' ' || ch == '\u0000')
            result.append ("&nbsp;");
          else
            result.append (ch);
        }
      }
      result.append ("<br>\n");
    }     
  }

  private void renderUnformatted (Screen screen, StringBuffer result) {
    result.append ("<textarea name=field class=cicsfield "
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
  }

  protected void renderField (StringBuffer result, Field f) {
    result.append ("<input ");
    result.append ("type=" + (f.isHidden() ? "password " : "text "));
    result.append ("onKeyDown=\"handler()\" ");    result.append ("name=\"field_" + f.getX() + "_" + f.getY() + "\" ");
    result.append ("class=cicsfield ");
    String value = f.getValue();
    result.append ("value=\"" + Field.trim (value) + "\" ");
    result.append ("size=\"" + f.getWidth() + "\" ");
    result.append (">");
  }

}
