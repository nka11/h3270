package org.h3270.test;

/*
 * Copyright (C) 2004-2008 akquinet tech@spree
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

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;
import org.h3270.render.ColorScheme;
import org.h3270.render.H3270Configuration;
import org.h3270.web.SessionState;

/**
 * @author Alphonse Bendt
 * @version $Id$
 */
public class SessionStateTest extends TestCase {
    
    private SessionState objectUnderTest;
    private MockControl h3270ConfigControl;
    private H3270Configuration h3270ConfigMock;
    private MockControl httpRequestControl;
    private HttpServletRequest httpRequestMock;
    private ColorScheme colorScheme;
    
    public void setUp() throws Exception 
    {
        h3270ConfigControl = MockClassControl.createControl(H3270Configuration.class);
        h3270ConfigMock = (H3270Configuration)h3270ConfigControl.getMock();
        
        httpRequestControl = MockControl.createNiceControl(HttpServletRequest.class);
        httpRequestMock = (HttpServletRequest) httpRequestControl.getMock();

        colorScheme = new ColorScheme();
        h3270ConfigMock.getColorScheme("Blank");
        h3270ConfigControl.setDefaultReturnValue(colorScheme);
        
        h3270ConfigMock.getColorSchemeDefault();
        h3270ConfigControl.setDefaultReturnValue("Blank");
        
        h3270ConfigMock.getFontnameDefault();
        h3270ConfigControl.setDefaultReturnValue("terminal");
        
        httpRequestMock.getParameter(SessionState.TERMINAL);
        httpRequestControl.setReturnValue(null);
        
        httpRequestMock.getAttribute(SessionState.TERMINAL);
        httpRequestControl.setReturnValue(null);
        
        httpRequestMock.setAttribute(SessionState.TERMINAL, "0");
        httpRequestControl.setVoidCallable();
        
        h3270ConfigControl.replay();
        httpRequestControl.replay();
        
        objectUnderTest = new SessionState(h3270ConfigMock, "");
    }
    
    public void tearDown() {
        h3270ConfigControl.verify();
    }
    
    public void testGetDefaultColorscheme() {
    	assertEquals(colorScheme, objectUnderTest.getActiveColorScheme(httpRequestMock));
    }
    
    public void testSaveFontname() throws Exception {
        objectUnderTest.setFontName("monospace");
        
        SessionState restoredState = getRestoredSessionState();
        
        assertEquals(objectUnderTest.getFontName(), restoredState.getFontName());
    }
    
    public void testSaveColorscheme() throws Exception {
        assertTrue(objectUnderTest.setActiveColorScheme(httpRequestMock, "Dark Background"));
        
        assertEquals(objectUnderTest.getActiveColorScheme(httpRequestMock), getRestoredSessionState().getActiveColorScheme(httpRequestMock));
    }

    public void testSaveMultiple() throws Exception {
        objectUnderTest.setActiveColorScheme(httpRequestMock, "White Background");
        objectUnderTest.setFontName("monospace");
        
        SessionState restoredState = getRestoredSessionState();
        
        assertEquals("monospace", restoredState.getFontName());
        assertEquals("White Background", restoredState.getActiveColorScheme(httpRequestMock).getName());
    }
    
    private SessionState getRestoredSessionState() throws Exception {
        String savedState = objectUnderTest.getSavedState();
        
        SessionState restoredState = new SessionState(h3270ConfigMock, savedState);
        return restoredState;
    }
}
