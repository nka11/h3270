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

package org.h3270.logicalunit;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

public class LogicalUnitPoolTest extends TestCase {

  private LogicalUnitPool pool;

  private MockControl sessionBindingEventControl;

  private HttpSessionBindingEvent sessionBindingEventMock;

  private MockControl sessionControl;

  private HttpSession sessionMock;

  private MockControl servletContextControl;
  
  private ServletContext servletContextMock;
  
  protected void setUp() throws Exception {
    pool = new LogicalUnitPool(new LogicalUnitBuilder() {
      public Collection getLogicalUnits() {
        return Collections.singletonList("IC01");
      }
    });
    sessionBindingEventControl = MockClassControl
        .createControl(HttpSessionBindingEvent.class);
    sessionBindingEventMock = (HttpSessionBindingEvent) sessionBindingEventControl
        .getMock();
    sessionControl = MockControl.createControl(HttpSession.class);
    sessionMock = (HttpSession) sessionControl.getMock();
    servletContextControl = MockControl.createControl(ServletContext.class);
    servletContextMock= (ServletContext) servletContextControl.getMock();

    sessionBindingEventMock.getSession();
    sessionBindingEventControl.setDefaultReturnValue(sessionMock);
    sessionMock.getServletContext();
    sessionControl.setDefaultReturnValue(servletContextMock);
    servletContextMock.getAttribute(LogicalUnitPool.SERVLET_CONTEXT_KEY);
    servletContextControl.setDefaultReturnValue(pool);
    
    sessionBindingEventControl.replay();
    sessionControl.replay();
    servletContextControl.replay();
}

  public void testGetLogicalUnit() throws Exception {

    assertEquals("IC01", pool.leaseLogicalUnit());

    try {
      pool.leaseLogicalUnit();
      fail();
    } catch (LogicalUnitException e) {
      // expected
    }
  }

  public void testReleaseLogicalUnit() throws Exception {
    String logicalUnit = pool.leaseLogicalUnit();
    assertEquals("IC01", logicalUnit);
    pool.releaseLogicalUnit(logicalUnit);
    assertEquals("IC01", pool.leaseLogicalUnit());
  }
}
