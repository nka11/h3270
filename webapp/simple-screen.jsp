<jsp:useBean id="sessionState" scope="session" class="org.h3270.web.SessionState" />

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

<table>
  <tr>
    <td style="width:50em;padding-right:0;">
      <%@ include file="common/h3270-screen.jsp" %>
    </td>   
    
    <% if (sessionState.isUseKeypad()) { %>    
      <td valign=top>
        <table>
          <tr>
            <td valign=top>
              <jsp:include page="keys.html" flush="true"/>
            </td>
          </tr>
        </table>
      </td>
    <% } %>
  </tr>
</table>
</body>
</html>
