        <table style="width:100%;">
          <tr>
            <form name="control" 
                  action="<%= response.encodeURL("servlet") %>"
                  method=POST>
            <td width=30% align=left>
              <% if (!sessionState.isConnected(request)) {
                   String targetHost = 
                     (String)session.getAttribute("targetHost");
                   if (targetHost == null) { %>
                     Host:&nbsp;<input type=text
                                       style="background-color:lightgrey;"
                                       name=hostname>
                 <% } else { %>
                     Host: <b><%= targetHost %></b>
                     <input type=hidden value=<%= targetHost %> name=hostname>
                 <% } %>
            </td>
            <td width=70% align=right>
                 <input type=submit name=connect value="Connect">
                 <input type=button id="prefs" name=prefs value="Preferences..."
                        onClick="openPrefs();">
            <% } else { %>
                 Host: <b><%= sessionState.getHostname(request) %></b>
              </td>
              <td width=70% align=right>
                 <input type=submit name=disconnect value="Disconnect">
                 <input type=submit name=refresh value="Refresh">
                 <input type=hidden name=dumpfile value="">
                 <input type=button name=dump value="Dump"
                        onClick="document.control.dumpfile.value=prompt('Filename:',''); document.control.submit();">
                 <input type=button name=prefs value="Preferences..."
                        onClick="openPrefs();">
                 <input type=submit name=keypad value="Keypad">
              <% } %>
              </td>
              <input type=hidden name=colorscheme>
              <input type=hidden name=font>
              <input type=hidden name=render>
		      <%= sessionState.getTerminalParam(request) %>
            </form>
            </tr>
        </table>
      </form>
