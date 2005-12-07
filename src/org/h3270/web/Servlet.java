package org.h3270.web;

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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.h3270.host.Field;
import org.h3270.host.FileTerminal;
import org.h3270.host.InputField;
import org.h3270.host.S3270;
import org.h3270.host.Screen;
import org.h3270.regex.Pattern;
import org.h3270.render.Engine;
import org.h3270.render.H3270Configuration;
import org.h3270.render.HtmlRenderer;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel </a>
 * @version $Id$
 */
public class Servlet extends AbstractServlet {

  private static final long serialVersionUID = 1L;

  private static final String STYLE_JSP = "/screen.jsp";
  private static final String DEFAULT_JSP = "/simple-screen.jsp";

  private String targetHost;
  private String execPath;
  private String templateDir;

  private final HtmlRenderer basicRenderer = new HtmlRenderer();
  private Engine engine = null;

  private H3270Configuration configuration;

  private String mainJSP = DEFAULT_JSP;

  public void init() throws ServletException {
    super.init();

    configuration = getConfiguration();
    Configuration styleConfig = configuration.getChild("style");

    try {
      if (styleConfig.getValue() != null) {
        mainJSP = STYLE_JSP;
      }
    } catch (ConfigurationException e) {
      logger.info("Set main jsp to default");
    }

    targetHost = configuration.getChild("target-host").getValue(null);
    Configuration dirConfig = configuration.getChild("template-dir");
    templateDir = getRealPath(dirConfig.getValue("/WEB-INF/templates"));
    engine = new Engine(templateDir);

    execPath = configuration.getChild("exec-path").getValue("/usr/local/bin");

    if (logger.isDebugEnabled()) {
      logger.debug("Using main JSP: " + mainJSP);
      logger.debug("Set template-dir to " + templateDir);
      logger.debug("Set exec-path to " + execPath);
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    SessionState state = getSessionState(request);

    if (state.terminal != null) {
      state.terminal.updateScreen();
      Screen s = state.terminal.getScreen();

      if (state.isUseRenderers() && engine.canRender(s)) {
        state.setScreen(engine.render(s));
      } else {
        state.setScreen(basicRenderer.render(s));
      }
    }
    getServletContext().getRequestDispatcher(mainJSP)
        .forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    SessionState state = getSessionState(request);

    boolean prevRendering = state.isUseRenderers();
    handlePreferences(state, request, response);
    if (state.isUseRenderers() && !prevRendering)
      engine = new Engine(templateDir);

    if (request.getParameter("connect") != null) {
      String hostname = (targetHost == null)
                        ? request.getParameter("hostname")
                        : targetHost;

      // TODO message to user if no hostname specified
      if (!hostname.equals("")) {
        if (logger.isInfoEnabled()) {
          logger.info("Connecting to " + hostname);
        }

        if (hostname.startsWith("file:")) {
          String filename = new File (getRealPath("/WEB-INF/dump"),
                                      hostname.substring(5)).toString();
          state.terminal = new FileTerminal(new URL("file:" + filename));
        } else {
          state.terminal = new S3270(hostname, configuration);
        }

        state.setUseKeypad(false);
      }
    } else if (request.getParameter("disconnect") != null) {
      if (state.terminal != null)
        state.terminal.disconnect();
      state.terminal = null;
      state.setScreen(null);
    } else if (request.getParameter("refresh") != null) {
      state.terminal.updateScreen();
    } else if (request.getParameter("dumpfile") != null
        && !request.getParameter("dumpfile").equals("")) {
      String filename = new File(getRealPath("/WEB-INF/dump"), request
          .getParameter("dumpfile")).toString();
      state.terminal.dumpScreen(filename);
    } else if (request.getParameter("keypad") != null) {
      state.setUseKeypad(!state.isUseKeypad());
    } else if (state.terminal != null) {
      submitScreen(request);
      String key = request.getParameter("key");
      if (key != null)
        state.terminal.doKey(key);
    }
    doGet(request, response);
  }

  private void handlePreferences(SessionState state,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String colorscheme = URLDecoder.decode(request.getParameter("colorscheme"), "UTF-8");
    String render = request.getParameter("render");
    String font = request.getParameter("font");
    boolean modified = false;

    response.setContentType("text/html");

    if (colorscheme != null) {
      modified = state.setActiveColorScheme(colorscheme);
    }

    if (render != null) {
      if (render.equals("true")) {
        state.setUseRenderers(true);
      } else if (render.equals("false")) {
        state.setUseRenderers(false);
      }
      modified = true;
    }

    if (font != null && !font.equals("")) {
      state.setFontName(font);

      modified = true;
    }

    if (modified) {
      Cookie cookie = new Cookie(SessionState.COOKIE_NAME, state
          .getSavedState());

      if (logger.isDebugEnabled()) {
        logger.debug("Sending Cookie: " + state);
      }

      cookie.setMaxAge(Integer.MAX_VALUE);

      response.addCookie(cookie);
    }
  }

  /**
   * Gets the input field parameters from the request and fills them into the
   * Fields of the Screen, then submits them back to s3270.
   */
  private void submitScreen(HttpServletRequest request) throws IOException {
    SessionState state = getSessionState(request);
    Screen s = state.terminal.getScreen();
    if (s.isFormatted()) {
      for (Iterator i = s.getFields().iterator(); i.hasNext();) {
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
      state.terminal.submitScreen();
    } else {
      state.terminal.submitUnformatted((String) request.getParameter("field"));
    }
  }

  /**
   * Returns the SessionState object that is associated with the given request,
   * creates the object if it doesn't already exist.
   */
  private SessionState getSessionState(HttpServletRequest request)
      throws IOException {
    boolean forceNewSession = request.getParameter("dump.session") != null;

    HttpSession session = request.getSession(forceNewSession);

    if (session == null) {
      session = request.getSession();
    }

    if (this.targetHost != null) {
      String targetHost = (String)session.getAttribute("targetHost");
      if (targetHost == null) {
        session.setAttribute ("targetHost", this.targetHost);
      }
    }
    
    SessionState result = (SessionState) session.getAttribute("sessionState");
    if (result == null) {
      String savedState = getSavedSessionState(request);

      result = new SessionState(configuration, savedState);
      session.setAttribute("sessionState", result);
    }
    return result;
  }

  private String getSavedSessionState(HttpServletRequest request) {
    Cookie cookie = getCookie(request);

    if (cookie != null) {
      return cookie.getValue();

    }

    return "";
  }

  private Cookie getCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (int x = 0; x < cookies.length; ++x) {
        if (SessionState.COOKIE_NAME.equals(cookies[x].getName())) {
          return cookies[x];
        }
      }
    }
    return null;
  }

}