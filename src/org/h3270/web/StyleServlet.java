package org.h3270.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.configuration.Configuration;

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

/**
 * @author Alphonse Bendt
 * @version $Id$
 */
public class StyleServlet extends AbstractServlet {

  private String styleDirectory;

  private final static String DEFAULT_STYLE = "h3270";

  public void init() throws ServletException {
    super.init();
    Configuration config = getConfiguration();
    Configuration dirConfig = config.getChild("style");
    styleDirectory = dirConfig.getValue(DEFAULT_STYLE);
    if (!styleDirectory.startsWith("/")) {
      styleDirectory = "/" + styleDirectory;
    }
    if (!styleDirectory.endsWith("/")) {
      styleDirectory += "/";
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String uri = req.getParameter("resource");
    String newPath = styleDirectory + uri;
    if (logger.isDebugEnabled()) {
      logger.debug("Fetching Resource " + uri + " from " + newPath);
    }

    getServletContext().getRequestDispatcher(newPath).include(req, resp);
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }
}