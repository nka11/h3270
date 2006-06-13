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

/**
 * @author Alphonse Bendt
 * @version $Id$
 */
public class SelectOptionBean {

  private final static String SELECTED = "selected=\"selected\"";
  private final static String NOT_SELECTED = "";

  private final String optionValue;
  private final String optionDescription;

  private final boolean isOptionSelected;

  //private final String stringValue;

  public SelectOptionBean (String description,
                           String value, boolean isSelected) {
    super();

    this.optionValue = value;
    this.optionDescription = description;
    this.isOptionSelected = isSelected;
  }

  public SelectOptionBean (String description, boolean isSelected) {
    this(description, null, isSelected);
  }

  public String getOption() {
    return optionValue;
  }

  public String getDescription() {
    return optionDescription;
  }

  public String getSelected() {
    return isOptionSelected ? SELECTED : NOT_SELECTED;
  }

  public String toString() {
    return getStringValue();
  }

  private String getStringValue() {
    StringBuffer buffer = new StringBuffer("<option");

    if (isOptionSelected) {
      buffer.append(" ");
      buffer.append(getSelected());

    }

    if (optionValue != null) {
      buffer.append(" ");
      buffer.append("value=\"");
      buffer.append(getOption());
      buffer.append("\"");
    }

    buffer.append(">");
    buffer.append(getDescription());
    buffer.append("</option>");

    return buffer.toString();
  }
}