<jsp:useBean id="sessionState"
             scope="session"
             class="org.h3270.web.SessionState"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
    <title>h3270</title>
    <meta http-equiv="expires" content="0">
    <style type="text/css">
      <%@ include file="common/h3270-css.jsp" %>
    </style>

    <script src="common/keyboard.js" type="text/javascript"></script>
    <script type="text/javascript" >
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
    <%@ include file="common/h3270-screen.jsp" %>
  </body>
</html>
