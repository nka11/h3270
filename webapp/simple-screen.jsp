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

<table width=100%>
  <tr>
    
    <td width=80% height=510px align=center valign=center class="h3270-screen-border h3270-form">
      	<%= sessionState.getScreen() %>
    </td>
    
    <td width=20% rowspan=2 valign=top >   
       	<% if (sessionState.isUseKeypad()) { %>    
         	<jsp:include page="keys.html" flush="true"/>
    		<% } %>
     </td>
  </tr>
  
  <tr>
    
    <td align=left height="10px" valign=bottom class="h3270-screen-border">
        <%@ include file="common/h3270-control.jsp" %>
    </td>
  </tr>
</table>
</body>
</html>
