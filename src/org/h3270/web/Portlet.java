package org.h3270.web;

/*
 * Copyright (C) 2006 akquinet
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

import java.io.*;
import java.util.*;

import javax.portlet.*;

import org.h3270.host.*;
import org.h3270.render.*;

/**
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class Portlet extends GenericPortlet {

  public static final String CONFIGURATION = "h3270.portlet.configuration";
  public static final String TERMINAL = "h3270.portlet.terminal";
  
  private H3270Configuration configuration = null;
  private Engine engine = null;
  
  /**
   * Called by the portlet container at initialization time.
   */
  public void init() throws PortletException {
    getPortletContext().setAttribute (CONFIGURATION, getConfiguration());
  }
  
  public void doView (RenderRequest request, RenderResponse response)
    throws PortletException, IOException {

    PortletPreferences prefs = request.getPreferences();
    ColorScheme colorScheme = getConfiguration().getColorScheme (
      prefs.getValue("color-scheme", "White Background")
    );
    
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    // stlye
    out.println("<style type=\"text/css\">");
    out.println(".h3270-form pre, .h3270-form pre input, .h3270-form textarea {");
    out.println("  font-family: " + prefs.getValue("font", "courier") + ";");
    out.println("  font-size: " + prefs.getValue("font-size", "8") + "pt;");
    out.println("  border-width: 0pt;");
    out.println("}");
    out.println(colorScheme.toCSS());
    out.println("</style>");

    // keyboard-handling
    out.println("<script type=\"text/javascript\">");
    include (out, "/common/keyboard.js");
    out.println("</script>");
    
    // screen image
    out.println("<table cellpadding=0 cellspacing=0 border=0><tr><td>");
    out.println (getRenderedScreen(request, response));
    out.println("</td>");
    if (prefs.getValue("keypad", "false").equals("true")) {
      out.println("<td>");
      include (out, "/keys.html");
      out.println("</td>");
    }
    out.println("</tr></table>");
    
  }
  
  protected void doEdit(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    getPortletContext().getRequestDispatcher ("/common/portlet-prefs.jsp")
                       .include (request, response);
  }
  
  public void processAction (ActionRequest request, ActionResponse response)
      throws PortletException, IOException {
    PortletMode mode = request.getPortletMode();
    if      (PortletMode.EDIT.equals(mode)) processEditAction (request, response);
    else if (PortletMode.VIEW.equals(mode)) processViewAction (request, response);
  }

  /**
   * Called for action requests in EDIT mode.
   */
  public void processEditAction (ActionRequest request,
                                 ActionResponse response)
      throws PortletException, IOException {
    // always store preferences, except when "Cancel" was pressed
    if (request.getParameter("cancel") == null) {
      storePreferences (request);
    }
      
    if (request.getParameter("connect") != null) {
      createTerminal(request);
      response.setPortletMode (PortletMode.VIEW);
    } else if (request.getParameter("disconnect") != null) {
      Terminal t = getTerminal(request);
      t.disconnect();
      request.getPortletSession().setAttribute(TERMINAL, null);
    } else if (request.getParameter("ok") != null) {
      response.setPortletMode (PortletMode.VIEW);
    }
  }
  
  private void storePreferences (ActionRequest request) {
    transferPreference (request, "color-scheme");
    transferPreference (request, "font");
    transferPreference (request, "font-size");
    transferPreference (request, "target-host");
    
    transferBooleanPreference (request, "render");
    transferBooleanPreference (request, "auto-connect");
    transferBooleanPreference (request, "keypad");

    try {
      request.getPreferences().store();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private void transferPreference (ActionRequest request, String name) {
    PortletPreferences prefs = request.getPreferences();
    String value = request.getParameter(name);
    if (!prefs.isReadOnly(name) && value != null) {
      try {
        prefs.setValue (name, value);
      } catch (ReadOnlyException ex) {
        // cannot happen, ignore if it does
      }
    }
  }
  
  private void transferBooleanPreference (ActionRequest request, String name) {
    PortletPreferences prefs = request.getPreferences();
    String value = request.getParameter(name);
    if (!prefs.isReadOnly(name)) {
      try {
        if (value == null)
          prefs.setValue (name, "false");
        else if (value.equals("on"))
          prefs.setValue (name, "true");
      } catch (ReadOnlyException ex) {
        // cannot happen, ignore if it does
      }
    }
  }
  
  /**
   * Called for action requests in VIEW mode.
   */
  public void processViewAction (ActionRequest request,
                                 ActionResponse response)
      throws PortletException, IOException {
    Terminal terminal = getTerminal(request);
    if (terminal != null && !terminal.isConnected()) {
      createTerminal(request);
    } else {
      submitScreen (request);
      String key = request.getParameter("key");
      if (key != null)
        getTerminal(request).doKey(key);
    }
  }
  
  private void submitScreen (ActionRequest request) {
    Terminal terminal = getTerminal(request);
    Screen screen = terminal.getScreen();
    if (screen.isFormatted()) {
      for (Iterator i = screen.getFields().iterator(); i.hasNext();) {
        Field f = (Field) i.next();
        if (f instanceof InputField) {
          if (!f.isMultiline()) {
            String value = request.getParameter("field_" + f.getStartX() + "_"
                + f.getStartY());
            if (value != null) {
              ((InputField) f).setValue(value);
            }
          } else { // multi-line field
            for (int j = 0; j < f.getHeight(); j++) {
              String value = request.getParameter("field_" + f.getStartX()
                  + "_" + f.getStartY() + "_" + j);
              if (value != null) {
                ((InputField) f).setValue(j, value);
              }
            }
          }
        }
      }
      terminal.submitScreen();
    } else {
      terminal.submitUnformatted((String) request.getParameter("field"));
    }
  }
  
  private Terminal getTerminal (PortletRequest request) {
    PortletPreferences prefs = request.getPreferences();
    PortletSession session = request.getPortletSession();
    Terminal t = (Terminal)session.getAttribute(TERMINAL);
    //if (t != null && !t.isConnected()) {
    //  session.setAttribute(TERMINAL, null);
    //}
    if (t == null && prefs.getValue("auto-connect", "false").equals("true")) {
      t = createTerminal(request);
    }
    return t;
  }
  
  private Terminal createTerminal (PortletRequest request) {
    PortletPreferences prefs = request.getPreferences();
    String targetHost = prefs.getValue("target-host", null);
    Terminal t = new S3270 (targetHost, getConfiguration());
    request.getPortletSession().setAttribute (TERMINAL, t);
    return t;
  }
  
  private String getRenderedScreen (RenderRequest request,
                                    RenderResponse response) {
    Terminal terminal = getTerminal (request);
    PortletPreferences prefs = request.getPreferences();
    if (terminal == null)
      return "h3270 version " + org.h3270.Version.value + "<br>not connected";
    else {
      terminal.updateScreen();
      Screen s = terminal.getScreen();
      String actionURL = response.createActionURL().toString();
      if (prefs.getValue("render", "false").equals("true"))
        return getEngine().render(s, actionURL);
      else
        return new HtmlRenderer().render(s, actionURL);
    }
  }
  
  private Engine getEngine() {
    if (engine == null) {
      engine = new Engine(getRealPath("/WEB-INF/templates"));
    }
    return engine;
  }
  
  private H3270Configuration getConfiguration() {
    if (configuration == null) {
      configuration = H3270Configuration.create (
        getRealPath("/WEB-INF/h3270-config.xml")
      );
    }
    return configuration;
  }
  
  private String getRealPath (String path) {
    return getPortletContext().getRealPath(path);
  }
  
  private void include (PrintWriter out, String filename) throws IOException {
    BufferedReader in = new BufferedReader (
      new FileReader (getRealPath (filename))
    );
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      out.println (line);
    }
  }
}
