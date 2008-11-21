/*
 * Copyright (C) 2003--2008007 akquinet tech@spree
 *
 * This file is part of h3270.
 *
 * h3270 is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
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

package org.h3270.logicalunit;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Example implementation of the LogicalUnitBuilder interface which reflects
 * a widely used pattern for LU names.
 * It creates LU names consisting of a prefix and a number, e.g. LU0001... LU0099.
 * The prefix, the length of the number and min and max values are specified in
 * the file SimpleLogicalUnitBuilder.properties. Alternatively, it can be created
 * with these values as Constructor arguments.
 *
 * @author Carsten Erker
 * @version $Id$
 */

public class SimpleLogicalUnitBuilder implements LogicalUnitBuilder {

  private final String prefix;

  private final int numberMin;

  private final int numberMax;

  private final int numberDigits;

  public SimpleLogicalUnitBuilder() {
    Properties properties = new Properties();
    InputStream inputStream = getClass().getResourceAsStream(
        "SimpleLogicalUnitBuilder.properties");
    try {
      properties.load(inputStream);
      prefix = properties.getProperty("lu.prefix");
      numberMin = Integer.parseInt(properties.getProperty("lu.number.min"));
      numberMax = Integer.parseInt(properties.getProperty("lu.number.max"));
      numberDigits = Integer.parseInt(properties
          .getProperty("lu.number.digits"));
      // TODO check consitency
    } catch (IOException e) {
      throw new RuntimeException(
          "File SimpleLogicalUnitBuilder.properties could not be opened");
    }
  }

  public SimpleLogicalUnitBuilder(String prefix, int numberMin, int numberMax, int numberDigits)
  {
    this.prefix = prefix;
    this.numberMin = numberMin;
    this.numberMax = numberMax;
    this.numberDigits = numberDigits;
  }

  public Collection getLogicalUnits() {
    Set logicalUnits = new HashSet(numberMax);

    for (int i = numberMin; i <= numberMax; i++) {
      logicalUnits.add(prefix + getNumberAsString(i));
    }
    return logicalUnits;
  }

  private String getNumberAsString(int number) {
    String s = String.valueOf(number);
    StringBuffer zeros = new StringBuffer();

    for (int i = s.length(); i < numberDigits; i++) {
      zeros.append("0");
    }

    return zeros.append(s).toString();
  }
}
