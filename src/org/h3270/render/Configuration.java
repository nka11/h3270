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

/**
 * Represents the various configuration options of h3270 which can be set for a
 * particular user session.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel </a>
 * @version $Id$
 */
public class Configuration {

    private static final String DEFAULT_COLORSCHEME = "White Background";

    private static final String DEFAULT_FONT = "courier";

    private static final boolean DEFAULT_USE_RENDERER = true;

    private final List colorSchemes = new ArrayList();
    
    private final Map validFonts = new HashMap();

    public Configuration() {
        createDefaultColorSchemes();
        createValidFonts();
    }

    public boolean getDefaultUseRenderer() {
        return DEFAULT_USE_RENDERER;
    }

    public String getDefaultFontname() {
        return DEFAULT_FONT;
    }

    public String getDefaultColorscheme() {
        return DEFAULT_COLORSCHEME;
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

    private void createValidFonts() {
        validFonts.put("courier", "Courier");
        validFonts.put("freemono", "Free Mono");
        validFonts.put("terminal", "Terminal");
        validFonts.put("couriernew", "Courier New");
        validFonts.put("monospace", "Monospace");
    }
    
    private void createDefaultColorSchemes() {
        colorSchemes.add(new ColorScheme("White Background", "black", "white",
                "blue", "white", "white", "white", "green", "lightgrey", "red",
                "lightgrey", "red", "lightgrey"));
        colorSchemes.add(new ColorScheme("Dark Background", "cyan", "black",
                "white", "black", "black", "black", "lime", "#282828", "red",
                "#282828", "red", "#282828"));
        colorSchemes.add(new ColorScheme("Amber", "orange", "black", "white",
                "black", "black", "black", "white", "#282828", "red",
                "#282828", "orange", "#282828"));
        colorSchemes.add(new ColorScheme("Black and White", "black", "white",
                "black", "white", "white", "white", "black", "lightgrey",
                "black", "lightgrey", "black", "lightgrey"));
    }
}