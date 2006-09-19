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

import java.util.Iterator;

import org.h3270.host.Field;
import org.h3270.host.InputField;
import org.h3270.host.Screen;

/**
 * @author <a href="mailto:spiegel@gnu.org">Andre Spiegel</a>
 * @version $Id$
 */
public class TextRenderer implements Renderer {

  private boolean markIntensified = false;
  private boolean markHidden = false;
  
  public TextRenderer() {
  }
  
  public TextRenderer (boolean markIntensified,
                       boolean markHidden) {
    this.markIntensified = markIntensified;
    this.markHidden = markHidden;                       
  }

  public boolean canRender (Screen s) {
    return true;
  }
  
  public boolean canRender (String screenText) {
    return true;
  }

  public String render (Screen s, String actionURL, int number) {
    return this.render(s);
  }
  
  public String render (Screen s, String actionURL) {
    return this.render(s);
  }
  
  public String render (Screen s) {
    StringBuffer result = new StringBuffer();
    for (Iterator i = s.getFields().iterator(); i.hasNext();) {
      Field f = (Field)i.next();
      result.append (f.getText());
    }

    if (markIntensified) {
      markFields (s, result, '[', ']', new FieldSelector() {
        public boolean checkField (final Field f) {
          return !(f instanceof InputField) && f.isIntensified();
        }
      });
    }    
      
    markFields (s, result, '{', '}', new FieldSelector() {
      public boolean checkField (final Field f) {
        return f instanceof InputField;
      }
    });
    
    for (int i=0; i<result.length(); i++) {
      if (result.charAt(i) == '\u0000')
        result.setCharAt(i, ' ');
    }
    return result.toString();
  }

  /**
   * This method marks some of the Fields in a textual screen representation
   * by replacing the control characters with other characters.  For example,
   * InputFields can be surrounded by '{' and '}' to make them visible and
   * detectable.
   * @param s the Screen on which we operate
   * @param buf a StringBuffer holding the textual representation of the screen,
   *            with individual lines separated by newline characters.
   * @param openCh the character to be used for the initial control character
   *               of a field
   * @param closeCh the character to be used for the terminating control
   *                character of the field
   * @param fs a FieldSelector that decides which of the Fields should be marked
   */
  private void markFields (Screen s, StringBuffer buf,
                           char openCh, char closeCh,
                           FieldSelector fs) {
    for (Iterator i = s.getFields().iterator(); i.hasNext();) {
      Field f = (Field)i.next();
      if (!fs.checkField(f)) continue;
      int startx = f.getStartX();
      int starty = f.getStartY();
      int endx   = f.getEndX();
      int endy   = f.getEndY();
      int width  = s.getWidth();
      
      if (startx == 0)
        setChar (buf, width, width-1, starty-1, openCh); 
      else
        setChar (buf, width, startx-1, starty, openCh);
        
      if (endx == width-1)
        setChar (buf, width, 0, endy+1, closeCh);
      else
        setChar (buf, width, endx+1, endy, closeCh);
    }
  }

  /**
   * Changes one character in the given StringBuffer.  The character position
   * is given in screen (x,y) coordinates.  The buffer holds the entire screen
   * contents, with lines separated by a single newline character each.  If
   * the x and y coordinates are out of range, this method silently ignores
   * the request -- this makes the caller's code easier.
   */
  private void setChar (StringBuffer buf, int width, int x, int y, char ch) {
    int index = y * (width + 1) + x;
    if (index >= 0 && index < buf.length())
      buf.setCharAt (index, ch);
  }

  /**
   * Interface for selecting Fields.
   */
  private interface FieldSelector {
    public boolean checkField (final Field f);
  }

}
