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

import java.io.Serializable;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements a pool of logical units. Each time a new LU is requested, the next
 * free LU from the pool is returned. This is suitable for scenarios where users
 * log on to a host using any LU out of a given set.
 *
 * @author Carsten Erker
 * @version $Id$
 */
public class LogicalUnitPool implements Serializable {

  public final static String SERVLET_CONTEXT_KEY = "org.h3270.LogicalUnitPool";

  private final static Log logger = LogFactory.getLog(LogicalUnitPool.class);

  private Map luPool;

  public LogicalUnitPool(LogicalUnitBuilder builder) {
    Collection logicalUnits = builder.getLogicalUnits();

    luPool = Collections.synchronizedMap(new HashMap(logicalUnits.size()));

    for (Iterator iter = logicalUnits.iterator(); iter.hasNext();) {
      String name = (String) iter.next();
      luPool.put(name, new Boolean(false));
    }
  }

  public String leaseLogicalUnit() throws LogicalUnitException {
    Set names = luPool.keySet();
    synchronized (luPool) {
      for (Iterator iter = names.iterator(); iter.hasNext();) {
        String name = (String) iter.next();
        boolean isInUse = ((Boolean) luPool.get(name)).booleanValue();
        if (!isInUse) {
          luPool.put(name, new Boolean(true));
          logger.debug("LU akquired = " + name);
          return name; // new LogicalUnit(name);
        }
      }
    }
    throw new LogicalUnitException("All logical units are in use");
  }

  public void releaseLogicalUnit(String logicalUnit) {
    if (luPool.get(logicalUnit) == null)
      logger.error("Trying to release non-existent logical unit!");
    else {
      synchronized (luPool) {
        luPool.put(logicalUnit, new Boolean(false));
        logger.debug("LU released = " + logicalUnit);
      }
    }
  }
}
