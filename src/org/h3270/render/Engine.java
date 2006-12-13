package org.h3270.render;

/*
 * Copyright (C) 2003-2006 akquinet framework solutions
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.h3270.host.Screen;

/**
 * An Engine is a collection of Renderers.
 *
 * @author Andre Spiegel spiegel@gnu.org
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
    if (templates != null) {
        for (int i=0; i<templates.length; i++) {
            renderers.add (new RegexRenderer(templates[i].toString()));
        }
    }
  }

  public boolean canRender (Screen s) {
    return true;
  }

  public boolean canRender (String screenText) {
    return true;
  }

  public String render (Screen s) {
    return this.render (s, "", null);
  }
  
  public String render (Screen s, String actionURL) {
    return this.render (s, "", null);
  }
  
  public String render (Screen s, String actionURL, String id) {
    String screenText = new TextRenderer().render(s, actionURL, id);
    for (Iterator i = renderers.iterator(); i.hasNext();) {
      Renderer r = (Renderer)i.next();
      if (r.canRender (screenText)) {
        return r.render(s, actionURL, id);
      }
    }
    return fallback.render(s, actionURL, id);
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
