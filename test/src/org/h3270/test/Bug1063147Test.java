package org.h3270.test;

import java.net.URL;

import junit.framework.TestCase;

import org.h3270.host.FileTerminal;
import org.h3270.host.Screen;
import org.h3270.host.Terminal;
import org.h3270.render.HtmlRenderer;

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

/**
 * @author Alphonse Bendt
 * @version $Id$
 */
public class Bug1063147Test extends TestCase {
    
    public void testBug() throws Exception {
        URL url = getClass().getResource("/org/h3270/test/1063147.dump");
        
        Terminal terminal = new FileTerminal(url);
        terminal.updateScreen();
        Screen s = terminal.getScreen();
        assertNotNull(new HtmlRenderer().render(s));
        terminal.submitScreen();
    }
}