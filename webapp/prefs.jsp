<%@ page import="java.util.*,org.h3270.web.*,org.h3270.render.*" %>

<script>
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

<style>
td { font-family:freesans,arial,helvetica; }
</style>

<html>
<body>
  <form name=prefs action="" method=POST>
  <table cellspacing="4" style="font-size:10pt;">
    <tr>
      <td>Color Scheme:</td>
      <td>
        <select name=colorscheme style="min-width:12em;">
          <%
             SessionState s = 
               (SessionState)session.getAttribute("sessionState");
             List colorSchemes = s.configuration.getColorSchemes();
             ColorScheme acs = s.configuration.getActiveColorScheme();
             for (Iterator i = colorSchemes.iterator(); i.hasNext();) {
               ColorScheme cs = (ColorScheme)i.next();
               if (cs == acs)
                 out.print ("<option selected>");
               else
                 out.print ("<option>");
               out.print (cs.getName() + "</option>\n");
             }
           %>
        </select>
      </td>
    </tr>
    <tr>
      <td>Font:</td>
      <td>
        <% String currentFont = s.configuration.getFontName(); %>
        <select name=font style="min-width:12em;">
          <option <%= currentFont.equals("courier") ? "selected" : "" %> value="courier">Courier</option>
          <option <%= currentFont.equals("freemono") ? "selected" : "" %> value="freemono">Free Mono</option>
		  <option <%= currentFont.equals("terminal") ? "selected" : "" %> value="terminal">Terminal</option>
		  <option <%= currentFont.equals("couriernew") ? "selected" : "" %> value="couriernew">Courier New</option>
		  <option <%= currentFont.equals("monospace") ? "selected" : "" %> value="monospace">Monospace</option>
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
        <% if (s.useRenderers)
             out.print (" checked ");
        %>
        > Use Regex Rendering Engine
      </td>
    </tr>
    <tr>
      <td colspan=3 align=right  style="padding-top:1em;">
        <input type=button name=prefs-ok value=OK
               onClick="doSubmit();">
        <input type=button name=prefs-apply value=Apply
               onClick="doApply();">
        <input type=submit name=prefs-cancel value=Cancel 
               onClick="window.close();">
      </td>
    </tr>
  </table>
  </form>
</body>
</html>
