package org.h3270.sites.locis;

/*
 * Copyright (C) 2003-2006 akquinet framework solutions
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, 
 * MA 02110-1301 USA
 */

import java.util.regex.*;
import org.h3270.render.Filter;

public class MainMenuFilter implements Filter {

  private static Pattern choicePattern =
    Pattern.compile ("^ +([0-9]+) +(.*?)(?:-- (.*?))?$", Pattern.MULTILINE);

  private static Pattern announcementPattern =
    Pattern.compile ("\\n(?:\\* +)+\\n(.*?)\\n(?:\\* +)+\\n", 
                     Pattern.MULTILINE | Pattern.DOTALL);

  public String filter(String input) {
    Matcher m = choicePattern.matcher(input);
    StringBuffer buffer = new StringBuffer();
    while (m.find()) {
      if (m.group(3) == null)
        m.appendReplacement (
          buffer,
          "<tr><td align=\"right\">$1</td> <td colspan=\"2\">$2</td></tr>\n"
        );
      else
        m.appendReplacement (
          buffer,
          "<tr><td align=\"right\">$1</td> <td>$2</td>" 
          + "<td style=\"width: 60%;\"><i>$3</i></td></tr>\n"
        );
    }
    m.appendTail (buffer);
    m = announcementPattern.matcher(buffer);
    String result = m.replaceAll(
      "\n<tr><td align=\"center\" colspan=\"3\" " +      "style=\"padding:10px;\"" +      ">$1</td></tr>\n"
    );
    return result;
  }

}
