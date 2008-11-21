/*
 * Copyright (C) 2003-2008 akquinet tech@spree
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

package org.h3270.test;

import junit.framework.*;

import org.h3270.host.*;
import org.h3270.render.*;

/**
 * Tests the basic interface to s3270 (starting the process, catching errors).
 * 
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class S3270Test extends TestCase {

  public S3270Test (String name) {
    super (name);
  }
  
  public void testUnknownHost() {
    H3270Configuration config =
      H3270Configuration.create ("webapp/WEB-INF/h3270-config.xml");
    try {
      new S3270 (null, "cunvm.cuny.ed", config);
      fail ("should have thrown UnknownHostException");
    } catch (UnknownHostException ex) {
      assertEquals("cunvm.cuny.ed", ex.getHost());
    }
  }
}
