<table cellspacing="0" cellpadding="0" style="border-collapse:collapse">
  <tr>
    <td style="width:50em;height:37em;" align="center" valign="middle"
      class="h3270-screen-border h3270-form">
      <%= sessionState.getScreen(request) %>
    </td>

    <% if (sessionState.useKeypad(request)) { %>
       <td rowspan=2 valign=top
           class="h3270-screen-border">
         <jsp:include page="keys.jsp" flush="true"/>
       </td>
    <% } %>

  </tr>
  <tr>
    <td align="left" valign="bottom" class="h3270-screen-border">
      <%@ include file="h3270-control.jsp" %>
    </td>
  </tr>
</table>
