<jsp:useBean id="sessionState"
             scope="session"
             class="org.h3270.web.SessionState"/>
<html>
  <head>
    <title>h3270</title>
    <style>
      <%@ include file="common/h3270-css.jsp" %>
    </style>
    <script type="text/javascript" >
      <%@ include file="common/keyboard.js" %>
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
