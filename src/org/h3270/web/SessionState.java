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

import org.h3270.host.*;

/**
 * 
 * @author <a href="mailto:spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class SessionState {

  public Terminal terminal    = null;
  public boolean  useKeypad    = false;
  public boolean  useRenderers = true;
  
}
