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
    </script>
  </head>

  <body>
    <%@ include file="common/h3270-screen.jsp" %>
  </body>
</html>
