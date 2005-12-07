package org.h3270.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

import org.h3270.render.H3270Configuration;

/**
 * @author Alphonse Bendt
 * @version $Id$
 */
public abstract class AbstractServlet extends HttpServlet {

  protected final Log logger = LogFactory.getLog(getClass());

  private static final String FILE_CONFIGURATION = "file.configuration";
  private static final String FILE_CONFIGURATION_DEFAULT = "/WEB-INF/h3270-config.xml";

  private H3270Configuration configuration;

  public void init() throws ServletException {
    super.init();

    String configFile = getInitParameter(FILE_CONFIGURATION);
    if (configFile == null) {
      configFile = FILE_CONFIGURATION_DEFAULT;
    }
    configuration = H3270Configuration.create (getRealPath(configFile));
  }

  protected H3270Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Convenience method, to save some typing.
   */
  protected String getRealPath(String path) {
    return getServletContext().getRealPath(path);
  }
}