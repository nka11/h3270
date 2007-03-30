package org.h3270.web;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.*;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h3270.host.*;
import org.h3270.render.ColorScheme;
import org.h3270.render.H3270Configuration;
import org.h3270.render.SelectOptionBean;

/**
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class SessionState implements HttpSessionBindingListener {

  private static final StringEncoder ENCODER;
  private static final StringDecoder DECODER;

  static {
    URLCodec codec = new URLCodec();

    ENCODER = codec;
    DECODER = codec;
  }

  private static final Log logger = LogFactory.getLog(SessionState.class);

  public static final String TERMINAL = "org.h3270.Terminal.id";
  public static final String COOKIE_NAME = "org.h3270.Settings";
  private static final String COLORSCHEME = "colorscheme";
  private static final String RENDERER = "renderer";
  private static final String KEYPAD = "keypad";
  private static final String FONTNAME = "fontname";

  private final static String[] KEYS = { COLORSCHEME, RENDERER, KEYPAD, FONTNAME };

  private final H3270Configuration h3270Config;
  private final Properties properties_ = new Properties();

  private List terminalStates = null;
  
  /**
   * although it is never called tomcat seems to insist to have a no-args
   * constructor.
   */
  public SessionState() {
    throw new IllegalStateException();
  }

  public SessionState(H3270Configuration config, String savedState)
      throws IOException {

    h3270Config = config;

    String decoded;
    try {
      decoded = DECODER.decode(savedState);
    } catch (DecoderException e) {
      throw new IOException();
    }

    // for safety do not use the restored properties directly. it could
    // contain unknown key/value mappings.
    Properties props = new Properties();

    props.load(new ByteArrayInputStream(decoded.getBytes()));

    if (logger.isDebugEnabled()) {
      logger.debug("trying to restore SessionState " + props);
    }

    for (int x = 0; x < KEYS.length; ++x) {
      String key = KEYS[x];

      if (props.containsKey(key)) {
        properties_.put(key, props.get(key));
      }
    }

  }

  public String toString() {
    return "<SessionState: " + properties_.toString() + ">";
  }
  
  public Terminal getTerminal (HttpServletRequest request) {
    return getTerminalState(request).getTerminal();
  }
  
  public void setTerminal (HttpServletRequest request, Terminal terminal) {
    getTerminalState(request).setTerminal(terminal);
  }
  
  public String getHostname (HttpServletRequest request) {
    if (getTerminalState(request).getTerminal() != null) {
      return getTerminalState(request).getTerminal().getHostname();
    }
    return null;
  }

  public boolean isConnected (HttpServletRequest request) {
    return getTerminalState(request).getTerminal() != null;
  }

  /**
   * Returns the HTML code for the terminal area, or an informational
   * message if there is no active session.
   */
  public String getScreen (HttpServletRequest request) {
    TerminalState tState = getTerminalState(request);
    Throwable exception = tState.getException();
    if (exception != null) {
      StringBuffer b = new StringBuffer();
      b.append ("<font color=\"red\"><b>Error</b></font><br/><br/>");
      if (exception instanceof UnknownHostException) {
        String host = ((UnknownHostException)exception).getHost();
        b.append ("Host <b>" + host + "</b> is unknown");
      } else if (exception instanceof HostUnreachableException) {
        HostUnreachableException ex = (HostUnreachableException)exception;
        b.append ("Host <b>" + ex.getHost() + 
                  "</b> is not reachable from the h3270 server machine<br/>");
        b.append ("(" + ex.getReason() + ")");
      } else {
        b.append (exception.toString());
      }
      return b.toString();
    } else if (tState.getTerminal() != null) {
      return tState.getScreen().toString();
    } else {
      StringBuffer b = new StringBuffer();
      b.append("<b>h3270 version ");
      b.append(org.h3270.Version.value);
      b.append("</b>\n<br /><br />not connected\n");
      return b.toString();
    }
  }

  void setScreen (HttpServletRequest request, String screen) {
    getTerminalState(request).setScreen(screen);
  }

  public String getSavedState() throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    properties_.store(stream, "");

    String asString = stream.toString();

    String state;
    try {
      state = ENCODER.encode(asString);
    } catch (EncoderException e) {
      throw new IOException();
    }

    return state;
  }

  public String getFontName() {
    return getStringProperty(FONTNAME, h3270Config.getFontnameDefault());
  }

  public Iterator getColorschemeSelectOptions (HttpServletRequest request) {
    List list = new ArrayList();

    List colorSchemes = getColorSchemes();
    ColorScheme acs = getActiveColorScheme (request);
    Iterator i = colorSchemes.iterator();

    while (i.hasNext()) {
      ColorScheme scheme = (ColorScheme) i.next();

      boolean selected = scheme.equals(acs);

      list.add(new SelectOptionBean(scheme.getName(), selected));
    }

    return list.iterator();
  }

  public Iterator getFontSelectOptions() {
    List list = new ArrayList();
    Map validFonts = h3270Config.getValidFonts();

    Iterator i = validFonts.keySet().iterator();
    while (i.hasNext()) {
      String font = (String) i.next();
      String fontName = (String) validFonts.get(font);
      boolean selected = font.equals(getFontName());

      list.add(new SelectOptionBean(fontName, font, selected));
    }

    return list.iterator();
  }

  public void setFontName(String fontName) {
    put(FONTNAME, fontName);
  }

  private void put(String name, String value) {
    properties_.put(name, value);
  }

  private void put(String name, boolean value) {
    put(name, Boolean.toString(value));
  }

  public ColorScheme getActiveColorScheme (HttpServletRequest request) {
    return getTerminalState(request).getActiveColorScheme();
  }

  public List getColorSchemes() {
    return h3270Config.getColorSchemes();
  }

  public boolean setActiveColorScheme (HttpServletRequest request, 
                                       String schemeName) {
    ColorScheme scheme = h3270Config.getColorScheme(schemeName);

	if (logger.isDebugEnabled()) {
	  logger.debug("setActiveColorScheme: " + scheme);
	}
	
    if (scheme != null) {
      logger.debug("OK");
      getTerminalState(request).setActiveColorScheme (scheme);

      put(COLORSCHEME, schemeName);

      return true;
    }
    return false;
  }

  public void useKeypad (HttpServletRequest request, boolean useKeypad) {
    getTerminalState(request).useKeypad (useKeypad);

    put(KEYPAD, useKeypad);
  }

  public boolean useKeypad (HttpServletRequest request) {
    return getTerminalState(request).useKeypad();
  }

  public void useRenderers (boolean useRenderers) {
    put(RENDERER, useRenderers);
  }

  public boolean useRenderers() {
    return getBooleanProperty(RENDERER, true);
  }

  public void setException (HttpServletRequest request, Throwable exception) {
    getTerminalState(request).setException(exception);
  }
  
  public Throwable getException (HttpServletRequest request) {
    return getTerminalState(request).getException();
  }
  
  public void valueBound(HttpSessionBindingEvent arg0) {
    // nothing
  }
  
  public void valueUnbound(HttpSessionBindingEvent arg0) {
    // disconnect all s3270 sessions when the HttpSession times out
    for (Iterator i = terminalStates.iterator(); i.hasNext();) {
      TerminalState ts = (TerminalState)i.next();
      if (ts.getTerminal() != null && ts.getTerminal().isConnected()) {
        if (logger.isInfoEnabled())
          logger.info ("Session unbound, disconnecting terminal " 
                       + ts.getTerminal());
        ts.getTerminal().disconnect();
      }
    }
  }

  private boolean parseBooleanString(String s) {
    return (s != null) && Boolean.valueOf(s).booleanValue();
  }

  private boolean getBooleanProperty(String name) {
    return parseBooleanString((String) properties_.get(name));
  }

  private boolean getBooleanProperty(String name, boolean defaultValue) {
    if (isPropertyDefined(name)) {
      return getBooleanProperty(name);
    }
    return defaultValue;
  }

  private boolean isPropertyDefined(String name) {
    return properties_.containsKey(name);
  }

  private String getStringProperty(String name) {
    String prop = (String) properties_.get(name);

    if (prop != null) {
      prop = prop.trim();
    }

    return prop;
  }

  private String getStringProperty(String name, String defaultValue) {
    String prop = getStringProperty(name);

    if (prop == null) {
      prop = defaultValue;
    }

    return prop;
  }

  /**
   * Returns the identifier of the terminal that is associated
   * with a given request.
   */
  public String getTerminalId (HttpServletRequest request) {
    String id = request.getParameter (TERMINAL);
    if (id == null) {
      id = (String)request.getAttribute (TERMINAL);
    }
    return id;
  }
  
  /**
   * Returns HTML code for a hidden parameter that stores the 
   * identifier of the terminal that is associated with a request.
   */
  public String getTerminalParam (HttpServletRequest request) {
    String id = request.getParameter (TERMINAL);
    if (id == null) {
      id = (String)request.getAttribute (TERMINAL);
    }
    if (id != null) {
      return "<input type=hidden name=" + TERMINAL + " value=" + id + ">";
    } else {
      return "";
    }
  }
  
  /**
   * Returns the TerminalState object that belongs to the terminal
   * that is associated with a particular request.
   */
  private TerminalState getTerminalState (HttpServletRequest request) {
    if (terminalStates == null) {
      terminalStates = new ArrayList();
    }
    
    String id = request.getParameter(TERMINAL);
    if (id == null) {
      id = (String)request.getAttribute(TERMINAL);
    }
    if (id == null) {
      request.setAttribute (TERMINAL, Integer.toString(terminalStates.size()));
      TerminalState result = createTerminalState();
      terminalStates.add (result);
      return result;
    } else {
      // TODO error check
      int num = Integer.parseInt(id);
      return (TerminalState)terminalStates.get(num);
    }
  }
  
  /**
   * Creates a new TerminalState object, initialized from the default
   * preferences for a terminal in this session.
   */
  private TerminalState createTerminalState() {
    boolean useKeypad = false;
    if (isPropertyDefined (KEYPAD)) {
      useKeypad = getBooleanProperty(KEYPAD);
    }
    String schemeName = 
      getStringProperty(COLORSCHEME, h3270Config.getColorSchemeDefault());
    ColorScheme scheme = h3270Config.getColorScheme (schemeName);
    return new TerminalState (useKeypad, scheme);
  }

}