package org.h3270.render;

/*
 * Copyright (C) 2003 it-frameworksolutions
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
import org.h3270.regex.*;

import org.h3270.host.*;

/**
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class RegexRenderer extends HtmlRenderer {

  private Pattern acceptPattern = null;
  private Pattern matchPattern  = null;
  private String  htmlTemplate  = null;

  private static Pattern filePattern =
    Pattern.compile (  "^<!-- accept\\n(.*?)\\n^-->\\s+"
                     + "^<!-- match\\n(.*?)\\n^-->\\s+"
                     + "(.*)",
                     Pattern.MULTILINE | Pattern.DOTALL);

  public RegexRenderer (String filename) {
    try {
      BufferedReader input = new BufferedReader (new FileReader (filename));
      StringBuffer   contents = new StringBuffer();
      while (true) {
        String line = input.readLine();
        if (line == null) break;
        contents.append (line);
        contents.append ("\n");
      }
      Matcher m = filePattern.matcher (contents);
      if (m.find()) {
        acceptPattern = Pattern.compile (m.group(1));
        matchPattern  = Pattern.compile (m.group(2), Pattern.DOTALL);
        htmlTemplate  = m.group(3);
      } else
        throw new RuntimeException ("could not parse template " + filename);
    } catch (IOException ex) {
      throw new RuntimeException ("IOException while reading " + filename +
                                  " " + ex);
    } 
  }

  public boolean canRender (Screen s) {
    return canRender (new TextRenderer().render(s));
  }
  
  public boolean canRender (String screenText) {
    Matcher m = acceptPattern.matcher (screenText);
    return m.find();
  }
  
  private static Pattern placeholderPattern =
    Pattern.compile ("#([0-9]+)(?:\\{(.*?)\\})?");

  public String render (Screen s) {
    ScreenCharSequence screenSeq = new ScreenCharSequence(s);
    Matcher m = matchPattern.matcher(screenSeq.toString());
    if (m.find()) {
      // Generate HTML page from template, replacing placeholders
      // in the template with matches from the matchPattern.
      StringBuffer result = new StringBuffer();
      result.append ("<form name=\"screen\" action=\"\" method=\"POST\">\n");
      Matcher placeholder = placeholderPattern.matcher (htmlTemplate);
      int index = 0;
      while (placeholder.find()) {
        result.append (htmlTemplate.substring (index, placeholder.start()));
        int number = Integer.parseInt (placeholder.group(1));
        String filterName = placeholder.group(2);
        String replacement = m.group(number);
        if (replacement.startsWith("{") && replacement.endsWith("}"))
          renderField (result, screenSeq.getFieldAt(m.start(number)+1));
        else if (filterName != null)
          result.append (getFilter(filterName).filter(replacement));
        else
          result.append (replacement);
        
        index = placeholder.end();        
      }
      result.append (htmlTemplate.substring (index));
      result.append ("<input type=hidden name=key>\n");
      result.append ("</form>");
      return result.toString();
    } else
      return "no match";
  }
  
  private Filter getFilter (String name) {
    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      Class clz = cl.loadClass(name);
      Filter result = (Filter)clz.newInstance();
      return result;
    } catch (Exception ex) {
      throw new RuntimeException ("error: " + ex);
    }
  }
  
  public static void main(String[] args) {
    String path = "/home/spiegel/com/h3270/";
    RegexRenderer r = new RegexRenderer
      (path + "templates/dr-tpx.html");
    Screen s = new S3270Screen(path + "dump/dr-1.edump");
    if (r.canRender(s))
      System.out.println(r.render(s));
    else
      System.out.println("no match");
  }
    

}
