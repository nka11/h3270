<%@ page import="java.util.*" %>

<jsp:useBean id="sessionState" scope="session" class="org.h3270.web.SessionState" />

<html>
<head>
<title>Preferences</title>

<style>
td { font-family:freesans,arial,helvetica; }
</style>

<script type="text/javascript">
  function doApply() {
    opener.document.control.colorscheme.value
      = document.prefs.colorscheme.options
                              [document.prefs.colorscheme.selectedIndex].text;

	opener.document.control.font.value
	  = document.prefs.font.options[document.prefs.font.selectedIndex].value;

    if (document.prefs.render.checked) {
      opener.document.control.render.value = "true";
    } else {
      opener.document.control.render.value = "false";
    }
    opener.document.control.submit();
  }

  function doSubmit() {
    doApply();
    window.close();
  }
</script>
</head>

<body>
  <form name=prefs action="" method=POST>
  <table cellspacing="4" style="font-size:10pt;">
    <tr>
      <td>Color Scheme:</td>
      <td>
        <select name=colorscheme style="min-width:12em;">
          <%
 			Iterator i = sessionState.getColorschemeSelectOptions();
 			while(i.hasNext()) { %>
 				<%= i.next() %>
 	      <% } %>	
        </select>
      </td>
    </tr>
    <tr>
      <td>Font:</td>
      <td>
        <select name=font style="min-width:12em;">
        <% i = sessionState.getFontSelectOptions();
           while(i.hasNext()) { %>
				<%= i.next() %>          
		<% } %>
        </select>
    </tr>
<!--    <tr>
      <td>Charset:</td>
      <td>
      <select name=charset style="min-width:12em;" selected=german>
        <option>apl</option>
        <option>belgian</option>
        <option>bracket</option>
        <option>brazilian</option>
        <option>finnish</option>
        <option>french</option>
        <option selected>german</option>
        <option>icelandic</option>
        <option>iso-hebrew</option>
        <option>iso-turkish</option>
        <option>italian</option>
        <option>japanese</option>
        <option>norwegian</option>
        <option>russian</option>
        <option>simplified-chinese</option>
        <option>slovenian</option>
        <option>thai</option>
        <option>uk</option>
        <option>us-intl</option>
      </select>
      </td>
    </tr> -->
    <tr>
      <td colspan=2>
        <input type=checkbox name=render value="render"
        <% if (sessionState.isUseRenderers())
             out.print (" checked ");
        %>
        > Use Regex Rendering Engine
      </td>
    </tr>
    <tr>
      <td colspan=3 align=right  style="padding-top:1em;">
        <input type=button id="prefs-ok" name=prefs-ok value=OK
               onClick="doSubmit();">
        <input type=button id="prefs-apply" name=prefs-apply value=Apply
               onClick="doApply();">
        <input type=submit name=prefs-cancel value=Cancel 
               onClick="window.close();">
      </td>
    </tr>
  </table>
  </form>
</body>
</html>
