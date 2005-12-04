package org.h3270.host;

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

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class FileTerminal implements Terminal {

  private String filename = null;
  private Screen screen = null;

  public FileTerminal (URL url) throws IOException {
    this.filename = url.toString();
    this.screen   = new S3270Screen (url.openStream()); 
  }

  public void disconnect() {
    // TODO Auto-generated method stub

  }

  public String getHostname() {
    return "&lt;file&gt;";
  }

  public void dumpScreen(String filename) {
    // TODO Auto-generated method stub

  }

  public void startLogging() {
    // TODO Auto-generated method stub

  }

  public List getLog() {
    // TODO Auto-generated method stub
    return null;
  }

  public void stopLogging() {
    // TODO Auto-generated method stub

  }

  public void updateScreen() {
    // TODO Auto-generated method stub

  }

  public Screen getScreen() {
    return screen;
  }

  public void submitScreen() {
    // TODO Auto-generated method stub

  }

  public void submitUnformatted(String data) {
    // TODO Auto-generated method stub

  }

  public void clear() {
    // TODO Auto-generated method stub

  }

  public void enter() {
    // TODO Auto-generated method stub

  }

  public void eraseEOF() {
    // TODO Auto-generated method stub

  }

  public void pa(int number) {
    // TODO Auto-generated method stub

  }

  public void pf(int number) {
    // TODO Auto-generated method stub

  }

  public void reset() {
    // TODO Auto-generated method stub

  }

  public void sysReq() {
    // TODO Auto-generated method stub

  }

  public void attn() {
    // TODO Auto-generated method stub

  }

  public void doKey (String key) {
    
  }
  
}
