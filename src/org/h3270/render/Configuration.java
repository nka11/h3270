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

import java.util.*;

/**
 * Represents the configuration of h3270 for a particular user session.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class Configuration {

  private List        colorSchemes;
  private ColorScheme activeColorScheme;
  
  public Configuration() {
    colorSchemes = new ArrayList();
    createDefaultColorSchemes();
  }

  public List getColorSchemes() {
    return colorSchemes;   
  }

  public ColorScheme getActiveColorScheme() {
    return activeColorScheme;
  }

  public ColorScheme getColorScheme (String name) {
    for (Iterator i = colorSchemes.iterator(); i.hasNext();) {
      ColorScheme cs = (ColorScheme)i.next();
      if (cs.getName().equals (name))
        return cs;
    }
    return null;
  }

  public void setActiveColorScheme (String name) {
    ColorScheme cs = getColorScheme (name);
    if (cs != null)
      activeColorScheme = cs;
  }

  private void createDefaultColorSchemes() {
    colorSchemes.add (new ColorScheme(
      "White Background",
      "black", "white",
      "blue",  "white",
      "white",   "white",
      "green", "lightgrey",
      "red",   "lightgrey",
      "red",   "lightgrey"));
    colorSchemes.add (new ColorScheme(
      "Dark Background",
      "cyan",  "black",
      "white", "black",
      "black", "black",
      "lime",  "black",
      "red",   "black",
      "red",   "black"));
    colorSchemes.add (new ColorScheme(
      "Amber",
      "orange", "black",
      "orange", "black",
      "black", "black",
      "white", "black",
      "orange", "black",
      "orange", "black"));
    colorSchemes.add (new ColorScheme(
      "Black and White",
      "black", "white",
      "black", "white",
      "white", "white",
      "black", "lightgrey",
      "black", "lightgrey",
      "black", "lightgrey"));
    setActiveColorScheme ("White Background");
  }
  
}
