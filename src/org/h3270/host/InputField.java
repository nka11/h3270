package org.h3270.host;

import org.h3270.regex.*;

public class InputField extends Field {

  protected boolean isNumeric;
  protected boolean isFocused;
  protected boolean changed;

  public InputField (Screen screen,
                     byte fieldCode,
                     int startx, int starty, int endx, int endy) {
    super (screen,
           fieldCode,
           startx, starty, endx, endy);
    if ((fieldCode & ATTR_NUMERIC) != 0) {
      isNumeric = true;
    }
  }

  public boolean isNumeric() {
    return this.isNumeric;
  }

  public void setFocused (boolean flag) {
    this.isFocused = flag;
  }
  
  public boolean isFocused() {
    return this.isFocused;
  }

  public boolean isChanged() {
    return this.changed;
  }

  /**
   * Sets the value of this Field.
   */
  public void setValue (String value) { 
    if (value == null) getValue();
    if (!value.equals (trim (this.value))) {
      if (value.length() > getWidth())
        this.value = value.substring (0, getWidth());
      else
        this.value = value;
      changed = true;
    }
  }

  private static Pattern trimPattern = 
    Pattern.compile ("^[\\x00 _]*(.*?)[\\x00 _]*$", 0);

  /**
   * Returns a string that is the same as the argument, with leading
   * and trailing ASCII NUL characters removed.
   */
  public static String trim (String value) {
    Matcher m = trimPattern.matcher (value);
    if (m.matches()) 
      return m.group (1);
    else
      return value;
  }


}
