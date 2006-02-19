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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h3270.host.Terminal;
import org.h3270.render.ColorScheme;
import org.h3270.render.H3270Configuration;
import org.h3270.render.SelectOptionBean;

/**
 * @author <a href="mailto:spiegel@it-fws.de">Andre Spiegel </a>
 * @version $Id$
 */
public class SessionState {

  private static final StringEncoder ENCODER;
  private static final StringDecoder DECODER;

  static {
    URLCodec codec = new URLCodec();

    ENCODER = codec;
    DECODER = codec;
  }

  private static final Log logger = LogFactory.getLog(SessionState.class);

  public static final String COOKIE_NAME = "org.h3270.Settings";
  private static final String COLORSCHEME = "colorscheme";
  private static final String RENDERER = "renderer";
  private static final String KEYPAD = "keypad";
  private static final String FONTNAME = "fontname";

  private final static String[] KEYS = 
    { COLORSCHEME, RENDERER, KEYPAD, FONTNAME };

  public Terminal terminal = null;

  private final H3270Configuration h3270Config;
  private boolean useKeypad = false;
  private final Properties properties_ = new Properties();
  private ColorScheme activeColorScheme;
  private String screen;

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

    setActiveColorScheme(getStringProperty(COLORSCHEME, h3270Config.getColorSchemeDefault()));

    if (isPropertyDefined(KEYPAD)) {
      useKeypad = getBooleanProperty(KEYPAD);
    }
  }

  public String toString() {
    return "<SessionState: " + properties_.toString() + ">";
  }

  public String getHostname() {
    if (terminal != null) {
      return terminal.getHostname();
    }
    return null;
  }

  public boolean isConnected() {
    return terminal != null;
  }

  public String getScreen() {
    if (screen != null) {
      return screen.toString();
    } else {
      StringBuffer b = new StringBuffer();
      b.append("<b>h3270 version ");
      b.append(org.h3270.Version.value);
      b.append("</b>\n<br><br>not connected\n");

      return b.toString();
    }
  }

  void setScreen(String screen) {
    this.screen = screen;
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

  public Iterator getColorschemeSelectOptions() {
    List list = new ArrayList();

    List colorSchemes = getColorSchemes();
    ColorScheme acs = getActiveColorScheme();
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

  public ColorScheme getActiveColorScheme() {
    return activeColorScheme;
  }

  public List getColorSchemes() {
    return h3270Config.getColorSchemes();
  }

  public boolean setActiveColorScheme(String schemeName) {
    ColorScheme scheme = h3270Config.getColorScheme(schemeName);

	if (logger.isDebugEnabled()) {
    	logger.debug("setActiveColorScheme: " + scheme);
	}
	
    if (scheme != null) {
      logger.debug("OK");
      activeColorScheme = scheme;

      put(COLORSCHEME, schemeName);

      return true;
    }
    return false;
  }

  public void setUseKeypad(boolean useKeypad) {
    this.useKeypad = useKeypad;

    put(KEYPAD, useKeypad);
  }

  public boolean isUseKeypad() {
    return useKeypad;
  }

  public void setUseRenderers(boolean useRenderers) {
    put(RENDERER, useRenderers);
  }

  public boolean isUseRenderers() {
    return getBooleanProperty(RENDERER, true);
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
}