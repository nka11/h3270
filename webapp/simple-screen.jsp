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
 
 <td>
    <%@ include file="common/h3270-screen.jsp" %>
 </td>   
    
    <td valign=top >   
       	<% if (sessionState.isUseKeypad()) { %>    
         	<jsp:include page="keys.html" flush="true"/>
    		<% } %>
     </td>
  </tr>
  
</table>
</body>
</html>
