<%@ page import="org.h3270.host.*,org.h3270.render.*,javax.portlet.*,java.util.*" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<% Terminal terminal = (Terminal)renderRequest.getPortletSession()
     .getAttribute(org.h3270.web.Portlet.TERMINAL);
   PortletPreferences prefs = renderRequest.getPreferences();
   H3270Configuration configuration = 
     (H3270Configuration)portletConfig.getPortletContext()
                           .getAttribute(org.h3270.web.Portlet.CONFIGURATION);
%>

<form action="<portlet:actionURL/>" method="POST">
  <table>
    <tr>
      <td>Host:</td>
      <td>
        <% if (terminal == null) { %>
          <input type=text
                 name="target-host"
                 value="<%= prefs.getValue("target-host","") %>">
        <% } else { %>
          <%= terminal.getHostname() %>
        <% } %>
      </td>
      <td>
  <% if (terminal == null) { %>
    <input type=submit name=connect value="Connect">
  <% } else { %>
    <input type=submit name=disconnect value="Disconnect">
  <% } %>
      </td>
    </tr>
    <tr>
      <td>Auto-Connect:</td>
      <td>
        <input type=checkbox name=auto-connect
               <% if (prefs.getValue("auto-connect","false").equals("true"))
                    out.print(" checked ");
               %>
        >
      </td>
    </tr>
    <tr>
      <td>Keypad:</td>
      <td>
        <input type=checkbox name=keypad
               <% if (prefs.getValue("keypad","false").equals("true"))
                    out.print(" checked ");
               %>
        >
      </td>
    </tr>
    <tr>
      <td>Regex Rendering:</td>
      <td>
        <input type=checkbox name=render
               <% if (prefs.getValue("render","false").equals("true"))
                    out.print(" checked ");
               %>
        >
      </td>
    </tr>
    <tr>
      <td>Color Scheme:</td>
      <td>
        <select name=color-scheme style="min-width:12em;">
          <% Iterator i = configuration.getColorSchemes().iterator();
 	     while(i.hasNext()) {
               ColorScheme cs = (ColorScheme)i.next();
               out.print ("<option");
               if (cs.getName().equals(prefs.getValue("color-scheme",
                                                      "White Background")))
                 out.print (" selected");
               out.println(">" + cs.getName() + "</option>");
             } %>
        </select>
      </td>
    </tr>
    <tr>
      <td>Font:</td>
      <td>
        <select name=font style="min-width:12em;">
          <% Iterator j = configuration.getValidFonts().keySet().iterator();
 	     while(j.hasNext()) {
               String fontName = (String)j.next();
               String title = (String)configuration.getValidFonts()
                                                   .get(fontName);
               out.print ("<option value=\"" + fontName + "\"");
               if (fontName.equals(prefs.getValue("font",
                                                  "Courier")))
                 out.print (" selected");
               out.println(">" + title + "</option>");
             } %>
        </select>
      </td>
    </tr>
    <tr>
      <td>Font Size:</td>
      <td>
        <select name=font-size>
          <% int value = Integer.parseInt(prefs.getValue("font-size","8"));
             for (int k=6; k<=12; k++) {
               out.print("<option");
               if (k==value) out.print(" selected");
               out.print(">" + k + "</option>");
             }
          %>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan=2 align=right>
  &nbsp;<input type=submit name=ok value="OK">
  &nbsp;<input type=submit name=cancel value="Cancel">
      </td>
    </tr>
  </table>
</form>
