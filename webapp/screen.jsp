<jsp:useBean id="sessionState" scope="session" class="org.h3270.web.SessionState" />

<html>

<head>

<title>h3270</title>

<style>

<jsp:include page="/style" >
    <jsp:param name="resource" value="stylesheet.jsp" />
</jsp:include>

<%@ include file="common/h3270-css.jsp" %>

</style>

<script type="text/javascript" >
 <%@ include file="common/keyboard.js" %>
</script>

</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<!-- begin include head -->
<jsp:include page="/style" flush="true" >
  <jsp:param name="resource" value="head.jsp" />
</jsp:include>
<!-- end include head -->

<table width=100% border=0 cellpadding=0 cellspacing=0 >
  <tr>
    <td width=10% valign="top">
        <!-- begin include navbar -->
  		<jsp:include page="/style" flush="true">
			<jsp:param name="resource" value="navbar.jsp" />
		</jsp:include>
        <!-- end include navbar -->
    </td>
    
    <td width=80% valign=top>
        <br>
        <%@ include file="common/h3270-screen.jsp" %>
    </td>
    
    <td width="10%" valign="top">   
         <% if (sessionState.isUseKeypad()) { %>    
               <jsp:include page="keys.html" flush="true"/>
         <% } %>
     </td>
  </tr>
</table>
</body>
</html>
