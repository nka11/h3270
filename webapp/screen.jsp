<html>

<head>
<title>h3270</title>

<style>
pre, pre input, textarea {
	font-size: 10pt;
	border-width: 0pt;
}
<%= request.getAttribute("style") %>
</style>

<script language="javascript1.2">
 
  // Keyboard Handling Code

  if (document.screen != null) {
  	if (isIE()) {
      document.onkeydown = handleIEKeyDownEvent;
      document.onhelp = handleIEKeyHelpEvent;
    } else {
      window.onkeydown = handleKeyDownEvent;
      window.onkeypress = handleKeyPressEvent;
      window.onhelp = handleKeyHelpEvent;
      window.oncontextmenu = handleOnContextMenu;
    }
  }

  function handleIEKeyDownEvent() {
    return handleKeyDownEvent(event);
  }

  function handleIEKeyHelpEvent() {
    return handleKeyHelpEvent(event);
  }

  function handleKeyDownEvent(eventObj) {
    var keyCode = eventObj.keyCode;
    if (keyCode == 13) {
      cancelKeyEvent(eventObj);
      sendFormWithKey("enter");
    } else if ((!(eventObj.altKey)) 
            && (!(eventObj.ctrlKey))
            && (keyCode >=112)
            && (keyCode <= 123)) {
      cancelKeyEvent(eventObj);
      handleFunctionKeyEvent(eventObj, keyCode);
    }
  }

  function handleKeyPressEvent(eventObj) {
    var iKC = eventObj.keyCode;
    if ((!(eventObj.altKey)) && (!(eventObj.ctrlKey)) && (iKC >=112) && (iKC <= 123)) {
      cancelKeyEvent(eventObj);
    }
  }

  function handleKeyHelpEvent(eventObj) {
    cancelKeyEvent(eventObj);
  }

  function handleOnContextMenu(eventObj) {
    // Disable context menu to allow Shift + F10 
    return false;
  }

  function cancelKeyEvent(eventObj) {
    try {
      eventObj.keyCode = 0;
    } catch(e) {
      eventObj.preventDefault();
    }
    eventObj.cancelBubble = true;
    eventObj.returnValue = false;
  }

  function sendFormWithKey(strKey) {
    document.screen.key.value = strKey;
    document.screen.submit();
  }

  function handleFunctionKeyEvent(eventObj, keyCode) {
    if ((!(eventObj.altKey)) && (!(eventObj.ctrlKey))) {
      var functionKey;
      if (!(eventObj.shiftKey)) {
        functionKey = keyCode - 111;
      } else {
        functionKey = keyCode - 99;
      }
      sendFormWithKey("pf" + functionKey);
    }
  }

  function isIE() {
    if (document.all) {
      return true;
    } else {
      return false;
    }
  }

  function openPrefs() {
    prefsWindow = window.open ("<%= response.encodeURL("prefs.jsp") %>",
                               "Preferences",
                               "width=280,height=170,left=500,top=300");
    if (prefsWindow.opener == null)
      prefsWindow.opener = self;
  }
</script>
</head>

<body>

<table height=100% style="border-style:solid; border-width:1px; border-collapse:collapse;">
  <tr>
    <td width=720px align=center valign=center class="h3270-form">
      <% String screen = (String)request.getAttribute ("screen");
         if (screen != null)
           out.println (screen);
         else {
           out.println ("<b>h3270 version " + org.h3270.Version.value + "</b>");
           out.println ("<br><br>not connected");
         }
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
      <form name="control" action="<%= response.encodeURL("servlet") %>" method=POST>
        <table width=100%>
          <tr>
            <td width=30% align=left>
              <% if (request.getAttribute("screen") == null) { %>
                 Host:&nbsp;<input type=text
                                   style="background-color:lightgrey;"
                                   name=hostname></td><td width=70% align=right>
                 <input type=submit name=connect value="Connect">
                 <input type=button id="prefs" name=prefs value="Preferences..."
                        onClick="openPrefs();">
              <% } else { %>
                 Host: <b><%= request.getAttribute ("hostname") %></b></td><td width=70% align=right>
                 <input type=submit name=disconnect value="Disconnect">
                 <input type=submit name=refresh value="Refresh">
                 <input type=hidden name=dumpfile value="">
                 <input type=button name=dump value="Dump"
                        onClick="document.control.dumpfile.value=prompt('Filename:',''); document.control.submit();">
                 <input type=button name=prefs value="Preferences..."
                        onClick="openPrefs();">
                 <input type=submit name=log value="Log">
                 <input type=submit name=keypad value="Keypad">
              <% } %>
                 <input type=hidden name=colorscheme>
                 <input type=hidden name=font>
                 <input type=hidden name=render>
            </td>
        </table>
      </form>
    </td>
  </tr>
</table>
</body>
</html>
