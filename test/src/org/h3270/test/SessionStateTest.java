package org.h3270.test;

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
    private MockControl control;
    private H3270Configuration mock;
    private ColorScheme colorScheme;
    
    public void setUp() throws Exception 
    {
        control = MockClassControl.createControl(H3270Configuration.class);
        
        mock = (H3270Configuration)control.getMock();
        
        colorScheme = new ColorScheme();
        mock.getColorScheme("Blank");
        control.setDefaultReturnValue(colorScheme);
        
        control.replay();
        
        objectUnderTest = new SessionState(mock, "");
    }
    
    public void tearDown() {
        control.verify();
    }
    
    public void testGetDefaultColorscheme() {
        assertEquals(colorScheme, objectUnderTest.getActiveColorScheme());
    }
    
    public void testGetDefaultFont() {
        assertEquals("courier", objectUnderTest.getFontName());
    }
    
    public void testSaveFontname() throws Exception {
        objectUnderTest.setFontName("monospace");
        
        SessionState restoredState = getRestoredSessionState();
        
        assertEquals(objectUnderTest.getFontName(), restoredState.getFontName());
    }
    
    public void testSaveColorscheme() throws Exception {
        assertTrue(objectUnderTest.setActiveColorScheme("Dark Background"));
        
        assertEquals(objectUnderTest.getActiveColorScheme(), getRestoredSessionState().getActiveColorScheme());
    }

    public void testSaveMultiple() throws Exception {
        objectUnderTest.setActiveColorScheme("White Background");
        objectUnderTest.setFontName("monospace");
        
        SessionState restoredState = getRestoredSessionState();
        
        assertEquals("monospace", restoredState.getFontName());
        assertEquals("White Background", restoredState.getActiveColorScheme().getName());
    }
    
    private SessionState getRestoredSessionState() throws Exception {
        String savedState = objectUnderTest.getSavedState();
        
        SessionState restoredState = new SessionState(mock, savedState);
        return restoredState;
    }
}
