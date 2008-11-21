/*
 * Copyright (C) 2003-2007 akquinet framework solutions
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

package org.h3270.logicalunit;

import java.util.Collection;

import junit.framework.TestCase;

public class SimpleLogicalUnitBuilderTest extends TestCase {

  public void testCreation() throws Exception {
    SimpleLogicalUnitBuilder builder = new SimpleLogicalUnitBuilder("LU", 123, 127, 5);
    Collection lus = builder.getLogicalUnits();
    assertEquals(5, lus.size());
    assertFalse(lus.contains("LU00122"));
    assertTrue(lus.contains("LU00123"));
    assertTrue(lus.contains("LU00124"));
    assertTrue(lus.contains("LU00125"));
    assertTrue(lus.contains("LU00126"));
    assertTrue(lus.contains("LU00127"));
    assertFalse(lus.contains("LU00128"));
  }
  
  public void testCreationWithMinGreaterThanMax() throws Exception {
    SimpleLogicalUnitBuilder builder = new SimpleLogicalUnitBuilder("LU", 127, 123, 5);
    Collection lus = builder.getLogicalUnits();
    assertEquals(0, lus.size());
  }
}
