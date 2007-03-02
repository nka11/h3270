package org.h3270.test;

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

import org.h3270.test.render.H3270ConfigurationTest;

import junit.framework.*;

/**
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class AllTest extends TestCase {

  public AllTest(String name) {
    super(name);
  }

  public static Test suite() {
    
    TestSuite suite = new TestSuite ("All h3270 tests");

    suite.addTestSuite (S3270Test.class);
    suite.addTestSuite (S3270ScreenTest.class);
    suite.addTestSuite (SessionStateTest.class);
    suite.addTestSuite (Bug1063147Test.class);
    suite.addTestSuite (BugRenderTest.class);
    suite.addTestSuite (H3270ConfigurationTest.class);
    
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTest.class);
  }

}
