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
import java.io.*;

import org.h3270.host.*;

/**
 * An Engine is a collection of Renderers.
 *
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class Engine implements Renderer {

  private Renderer fallback  = null;
  private List     renderers = null;
  
  /**
   * Constructs an Engine based on all template files in the given basedir.
   */
  public Engine (String basedir) {
    fallback = new HtmlRenderer();
    renderers = new ArrayList();
    File dir = new File (basedir);
    File[] templates = dir.listFiles (new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        return filename.endsWith(".html");
      }
    });
    for (int i=0; i<templates.length; i++) {
      renderers.add (new RegexRenderer(templates[i].toString()));
    }
  }
  
  public boolean canRender (Screen s) {
    return true;
  }
  
  public boolean canRender (String screenText) {
    return true; 
  }  
  
  public String render (Screen s) {
    String screenText = new TextRenderer().render(s);
    for (Iterator i = renderers.iterator(); i.hasNext();) {
      Renderer r = (Renderer)i.next();
      if (r.canRender (screenText))
        return r.render(s);
    }
    return fallback.render(s);
  }  

  private Renderer getRenderer (Screen s) {
    for (Iterator i = renderers.iterator(); i.hasNext();) {
      Renderer r = (Renderer)i.next();
      if (r.canRender(s))
        return r;
    }
    return fallback;
  }

  public static void main(String[] args) {
    Engine e = new Engine("/usr/java/tomcat/webapps/h3270/WEB-INF/templates"); 
    System.out.println(e);
  }
}
