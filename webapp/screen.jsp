<html>
<head>
<title>h3270</title>
<link rel="stylesheet" type="text/css" href="cics-style.css">
</head>
<body>

<script language="javascript1.2">

  function handler(e) { 
    if (e.keyCode == 13) {
      document.screen.key.value = "enter";
      document.screen.submit();
    } else if (e.keyCode >= 112 && e.keyCode <= 123) {
      document.screen.key.value = "pf" + (e.keyCode - 111);
      document.screen.submit();
    }
  }
  document.onkeydown=handler;

</script>

<table height=100% style="border-style:solid; border-width:1px; border-collapse:collapse;">
  <tr>
    <td width=720px align=center valign=center>
      <% String screen = (String)request.getAttribute ("screen");
         if (screen != null)
           out.println (screen);
         else
           out.println ("not connected");
       %>
    </td>
    <% if (request.getAttribute("keypad") != null) { %>
       <td rowspan=2 valign=top style="border-style:solid; border-width:1px;">
         <jsp:include page="keys.html" flush="true"/>
       </td>
    <% } %>
  </tr>
  <tr>
    <td align=left valign=bottom style="height:30px; border-style:solid; border-width:1px;">
      <form name="control" action="" method=POST>
        <table width=100%>
          <tr>
            <td width=30% align=left>
              <% if (request.getAttribute("screen") == null) { %>
                 Host:&nbsp;<input type=text
                                   style="background-color:lightgrey;"
                                   name=hostname></td><td width=70% align=right>
                 <input type=submit name=connect value="Connect">
              <% } else { %>
                 Host: <b><%= request.getAttribute ("hostname") %></b></td><td width=70% align=right>
                 <input type=submit name=disconnect value="Disconnect">
                 <input type=submit name=refresh value="Refresh">
                 <input type=submit name=render value="Render">
                 <input type=hidden name=dumpfile value="">
                 <input type=button name=dump value="Dump"
                        onClick="document.control.dumpfile.value=prompt('Filename:',''); document.control.submit();">
                 <input type=submit name=log value="Log">
                 <input type=submit name=keypad value="Keypad">
              <% } %>
            </td>
        </table>
      </form>
    </td>
  </tr>
</table>
</body>
</html>
