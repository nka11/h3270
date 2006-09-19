<%@ page import="java.util.*" %>
<jsp:useBean id="sessionState" scope="session" class="org.h3270.web.SessionState" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>Preferences</title>

<style type="text/css">
td { font-family:freesans,arial,helvetica; }
</style>

<script type="text/javascript">
  function doApply() {
    var control = opener.document.forms["control"];
    var prefs = document.forms["prefs"];
    control.colorscheme.value
      = prefs.colorscheme.options[prefs.colorscheme.selectedIndex].text;

	control.font.value
	  = prefs.font.options[prefs.font.selectedIndex].value;

    if (prefs.render.checked) {
      control.render.value = "true";
    } else {
      control.render.value = "false";
    }
    control.submit();
  }

  function doSubmit() {
    doApply();
    window.close();
  }
</script>
</head>

<body>
  <form id="prefs" action="" method="post">
  <table cellspacing="4" style="font-size:10pt;">
    <tr>
      <td>Color Scheme:</td>
      <td>
        <select name="colorscheme" style="min-width:12em;">
          <%
 			Iterator i = sessionState.getColorschemeSelectOptions(request);
 			while(i.hasNext()) { %>
 			  <%= i.next() %>
 	      <% } %>	
        </select>
      </td>
    </tr>
    <tr>
      <td>Font:</td>
      <td>
        <select name="font" style="min-width:12em;">
        <% i = sessionState.getFontSelectOptions();
           while(i.hasNext()) { %>
				<%= i.next() %>          
		<% } %>
        </select>
      </td>
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
        <option>hebrew</option>
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
      <td colspan="2">
        <input type="checkbox" name="render" value="render"
        <% if (sessionState.useRenderers())
             out.print (" checked=\"checked\" ");
        %>
        /> Use Regex Rendering Engine
      </td>
    </tr>
    <tr>
      <td colspan="3" align="right"  style="padding-top:1em;">
        <input type="button" id="prefs-ok" name="prefs-ok" value="OK"
               onclick="doSubmit();" />
        <input type="button" id="prefs-apply" name="prefs-apply" value="Apply"
               onclick="doApply();" />
        <input type="submit" name="prefs-cancel" value="Cancel" 
               onclick="window.close();" />
      </td>
    </tr>
  </table>
  </form>
</body>
</html>
