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

import java.util.*;

import org.h3270.host.*;

/**
 * @author <a href="mailto:spiegel@gnu.org">Andre Spiegel</a>
 * @version $Id$
 */
public class TextRenderer implements Renderer {

  public boolean canRender (Screen s) {
    return true;
  }
  
  public boolean canRender (String screenText) {
    return true;
  }

  public String render (Screen s) {
    StringBuffer result = new StringBuffer();
    for (int y = 0; y < s.getHeight(); y++) {
      for (int x = 0; x < s.getWidth(); x++) {
        char ch = s.charAt (x, y);
        if (ch == '\u0000')
          result.append (' ');
        else
          result.append (s.charAt(x, y));
      }
      result.append ('\n');
    }
    for (Iterator i = s.getFields().iterator(); i.hasNext();) {
      Field f = (Field)i.next();
      setChar (s, result, f.getX()-1, f.getY(), '{');
      if (f.getX() + f.getWidth() < s.getWidth())
        setChar (s, result, f.getX()+f.getWidth(), f.getY(), '}');
    }
    return result.toString();
  }

  private void setChar (Screen s, StringBuffer buf, int x, int y, char ch) {
    int index = y * (s.getWidth() + 1) + x;
    buf.setCharAt (index, ch);
  }

}
