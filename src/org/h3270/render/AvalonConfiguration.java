package org.h3270.render;

/*
 * Copyright (C) 2004 it-frameworksolutions
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Represents the various configuration options of h3270 which can be set for a
 * particular user session. The Configuration is read from an Avalon
 * Configuration.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel </a>
 * @author Alphonse Bendt
 * 
 * @version $Id$
 */
public class AvalonConfiguration implements H3270Configuration {

  private String defaultColorScheme;
  private String defaultFont;
  private final boolean defaultUseRenderer = true;

  private final List colorSchemes = new ArrayList();
  private final Map validFonts = new HashMap();

  public AvalonConfiguration(Configuration config)
      throws ConfigurationException {
    createDefaultColorSchemes(config.getChild("colorschemes"));

    createValidFonts(config.getChild("fonts"));
  }

  public boolean getDefaultUseRenderer() {
    return defaultUseRenderer;
  }

  public String getDefaultFontname() {
    return defaultFont;
  }

  public String getDefaultColorscheme() {
    return defaultColorScheme;
  }

  public List getColorSchemes() {
    return colorSchemes;
  }

  public ColorScheme getColorScheme(String name) {
    for (Iterator i = colorSchemes.iterator(); i.hasNext();) {
      ColorScheme cs = (ColorScheme) i.next();
      if (cs.getName().equals(name))
        return cs;
    }
    return null;
  }

  public Map getValidFonts() {
    return validFonts;
  }

  private void createValidFonts(Configuration config)
      throws ConfigurationException {
    Configuration[] fonts = config.getChildren();

    for (int x = 0; x < fonts.length; ++x) {
      String fontName = fonts[x].getAttribute("name");
      String fontDescription = fonts[x].getAttribute("description", fontName);

      validFonts.put(fontName, fontDescription);

      defaultFont = fontName;
    }

    defaultFont = config.getAttribute("default", defaultFont);
  }

  private void createDefaultColorSchemes(Configuration config)
      throws ConfigurationException {
    Configuration[] cs = config.getChildren();

    for (int x = 0; x < cs.length; ++x) {
      ColorScheme scheme = new ColorScheme (
        cs[x].getAttribute("name"),
        cs[x].getAttribute("pnfg"),
        cs[x].getAttribute("pnbg"),
        cs[x].getAttribute("pifg"),
        cs[x].getAttribute("pibg"),
        cs[x].getAttribute("phfg"),
        cs[x].getAttribute("phbg"),
        cs[x].getAttribute("unfg"),
        cs[x].getAttribute("unbg"),
        cs[x].getAttribute("uifg"),
        cs[x].getAttribute("uibg"),
        cs[x].getAttribute("uhfg"),
        cs[x].getAttribute("uhbg")
      );
      colorSchemes.add(scheme);
      defaultColorScheme = scheme.getName();
    }
    defaultColorScheme = config.getAttribute("default", defaultColorScheme);
  }
}