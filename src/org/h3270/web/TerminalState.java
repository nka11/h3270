package org.h3270.web;

import org.h3270.host.*;
import org.h3270.render.*;

/**
 * Encapsulates the state for an individual terminal window.
 * 
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class TerminalState {

  private Terminal terminal = null;
  private boolean useKeypad = false;
  private ColorScheme activeColorScheme = null;
  private String screen = null;
  
  public ColorScheme getActiveColorScheme() {
    return activeColorScheme;
  }
  
  public void setActiveColorScheme(ColorScheme activeColorScheme) {
    this.activeColorScheme = activeColorScheme;
  }
  
  public String getScreen() {
    return screen;
  }
  
  public void setScreen(String screen) {
    this.screen = screen;
  }
  
  public Terminal getTerminal() {
    return terminal;
  }
  public void setTerminal(Terminal terminal) {
    this.terminal = terminal;
  }
  
  public boolean useKeypad() {
    return useKeypad;
  }

  public void useKeypad(boolean useKeypad) {
    this.useKeypad = useKeypad;
  }
}
