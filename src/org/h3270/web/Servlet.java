package org.h3270.web;

/*
 * Copyright (C) 2003, 2004 it-frameworksolutions
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

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h3270.regex.*;
import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.h3270.host.*;
import org.h3270.host.Field;
import org.h3270.render.*;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel </a>
 * @version $Id$
 */
public class Servlet extends HttpServlet {

    private final static Log logger = LogFactory.getLog(Servlet.class);

    private static final String PATH_S3270 = "path.s3270";

    private HtmlRenderer basicRenderer = new HtmlRenderer();

    private Engine engine = null;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        //        System.out.println(getServletContext().getContextPath());
        SessionState state = getSessionState(request);

        if (state.terminal != null) {
            state.terminal.updateScreen();
            Screen s = state.terminal.getScreen();
            //if (false)
            if (state.isUseRenderers() && engine.canRender(s))
                request.setAttribute("screen", engine.render(s));
            else
                request.setAttribute("screen", basicRenderer.render(s));
            request.setAttribute("hostname", state.terminal.getHostname());
            request.setAttribute("keypad", state.isUseKeypad() ? "on" : null);
            String style = "pre, pre input, textarea {\n" + "  font-family: "
                    + state.getFontName() + ";\n" + "}\n"
                    + state.getActiveColorScheme().toCSS();
            request.setAttribute("style", style);
        }
        getServletContext().getRequestDispatcher("/screen.jsp").forward(
                request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        SessionState state = getSessionState(request);

        boolean prevRendering = state.isUseRenderers();
        handlePreferences(state, request, response);
        if (state.isUseRenderers() && !prevRendering)
            engine = new Engine(getRealPath("/WEB-INF/templates"));

        if (request.getParameter("connect") != null) {
            String hostname = request.getParameter("hostname");
            if (hostname.startsWith("file:")) {
                String filename = new File(getRealPath("/WEB-INF/dump"),
                        hostname.substring(5)).toString();
                state.terminal = new FileTerminal(filename);
            } else {
                String execPath = getInitParameter(PATH_S3270);

                if (execPath == null || "".equals(execPath)) {
                    execPath = getRealPath("/WEB-INF/bin");
                }

                state.terminal = new S3270(hostname, execPath);
            }
            state.setUseKeypad(false);
        } else if (request.getParameter("disconnect") != null) {
            if (state.terminal != null)
                state.terminal.disconnect();
            state.terminal = null;
        } else if (request.getParameter("refresh") != null) {
            state.terminal.updateScreen();
        } else if (request.getParameter("dumpfile") != null
                && !request.getParameter("dumpfile").equals("")) {
            String filename = new File(getRealPath("/WEB-INF/dump"), request
                    .getParameter("dumpfile")).toString();
            state.terminal.dumpScreen(filename);
        } else if (request.getParameter("log") != null) {
            if (state.terminal.getLog() == null) {
                state.terminal.startLogging();
            } else {
                getServletContext().log("*** COMMUNICATION LOG ***");
                for (Iterator i = state.terminal.getLog().iterator(); i
                        .hasNext();) {
                    getServletContext().log(i.next().toString());
                }
                state.terminal.stopLogging();
            }
        } else if (request.getParameter("keypad") != null) {
            state.setUseKeypad(!state.isUseKeypad());
        } else if (state.terminal != null) {
            submitScreen(request);
            String key = request.getParameter("key");
            if (key != null)
                performKeyAction(state.terminal, key);
        }
        doGet(request, response);
    }

    private void handlePreferences(SessionState state,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String colorscheme = request.getParameter("colorscheme");
        String render = request.getParameter("render");
        String font = request.getParameter("font");
        boolean modified = false;

        response.setContentType("text/html");

        if (colorscheme != null) {
            modified = state.setActiveColorScheme(colorscheme);
        }

        if (render != null) {
            if (render.equals("true")) {
                state.setUseRenderers(true);
            } else if (render.equals("false")) {
                state.setUseRenderers(false);
            }
            modified = true;
        }

        if (font != null && !font.equals("")) {
            state.setFontName(font);

            modified = true;
        }

        if (modified) {
            // if the SessionState has been modified send a Cookie to
            // the Browser to maintain State across Sessions
            Cookie cookie = new Cookie(SessionState.COOKIE_NAME, state
                    .getSavedState());

            if (logger.isDebugEnabled()) {
                logger.debug("Sending Cookie: " + state);
            }
            
            cookie.setMaxAge(Integer.MAX_VALUE);

            response.addCookie(cookie);
        }
    }

    /**
     * Convenience method, to save some typing.
     */
    private String getRealPath(String path) {
        return getServletContext().getRealPath(path);
    }

    private static Pattern functionKeyPattern = Pattern
            .compile("p(f|a)([0-9]{1,2})");

    /**
     * Perform the s3270 action that is specified by the given key name.
     */
    private void performKeyAction(Terminal terminal, String key) {
        Matcher m = functionKeyPattern.matcher(key);
        if (m.matches()) { // function key
            int number = Integer.parseInt(m.group(2));
            if (m.group(1).equals("f"))
                terminal.pf(number);
            else
                terminal.pa(number);
        } else if (key.equals("")) {
            // use ENTER as a default action if the actual key got lost
            terminal.enter();
        } else { // other key: find a parameterless method of the same name
            try {
                Class c = terminal.getClass();
                Method method = c.getMethod(key, new Class[] {});
                method.invoke(terminal, new Object[] {});
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("no s3270 method for key: " + key);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(
                        "illegal s3270 method access for key: " + key);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException("error invoking s3270 for key: "
                        + key + ", exception: " + ex.getTargetException());
            }
        }
    }

    /**
     * Gets the input field parameters from the request and fills them into the
     * Fields of the Screen, then submits them back to s3270.
     */
    private void submitScreen(HttpServletRequest request) throws IOException {
        SessionState state = getSessionState(request);
        Screen s = state.terminal.getScreen();
        if (s.isFormatted()) {
            for (Iterator i = s.getFields().iterator(); i.hasNext();) {
                Field f = (Field) i.next();
                if (f instanceof InputField) {
                    if (!f.isMultiline()) {
                        String value = request.getParameter("field_"
                                + f.getStartX() + "_" + f.getStartY());
                        if (value != null) {
                            ((InputField) f).setValue(value);
                        }
                    } else { // multi-line field
                        for (int j = 0; j < f.getHeight(); j++) {
                            String value = request.getParameter("field_"
                                    + f.getStartX() + "_" + f.getStartY() + "_"
                                    + j);
                            if (value != null) {
                                ((InputField) f).setValue(j, value);
                            }
                        }
                    }
                }
            }
            state.terminal.submitScreen();
        } else {
            state.terminal.submitUnformatted((String) request
                    .getParameter("field"));
        }
    }

    /**
     * Returns the SessionState object that is associated with the given
     * request, creates the object if it doesn't already exist.
     */
    private SessionState getSessionState(HttpServletRequest request)
            throws IOException {
        HttpSession session = request.getSession();
        
        logger.debug("got Session new? " + session.isNew());
        
        SessionState result = (SessionState) session
                .getAttribute("sessionState");
        if (result == null) {
            String savedState = getSavedSessionState(request);

            result = new SessionState(savedState);
            session.setAttribute("sessionState", result);
        }
        return result;
    }

    private String getSavedSessionState(HttpServletRequest request) {
        Cookie cookie = getCookie(request);

        if (cookie != null) {
            return cookie.getValue();

        }

        return "";
    }

    private Cookie getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int x = 0; x < cookies.length; ++x) {
                if (SessionState.COOKIE_NAME.equals(cookies[x].getName())) {
                    return cookies[x];
                }
            }
        }
        return null;
    }

    public void init() throws ServletException {
        super.init();

        engine = new Engine(getRealPath("/WEB-INF/templates"));
    }

}
