
<%
  String id = request.getParameter(org.h3270.web.SessionState.TERMINAL);
  if (id == null) {
    id = (String)request.getAttribute(org.h3270.web.SessionState.TERMINAL);
  }
  String screenName = "screen";
  if (id != null) {
    screenName = "screen-" + id;
  }
%>

<style type="text/css">
.smallkey { width:40px; height:28px; font-size:8pt;}
.largekey { width:64px; height:28px; font-size:7pt; }
</style>

<script>
function handleKey (key, id) {
  var scr = document.forms[id];
  scr.key.value = key;
  scr.submit();
}

function noSuchKey (key) {
  window.alert("Sorry, this key is not yet implemented.");
}

</script>

<table border="0" style="border-spacing:0px;">

    <tr>
    <td style="padding:0px;">
  <table>
    <!-- function keys -->
    <!-- pf1 - pf3 -->
    <tr>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf1">PF1</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf2">PF2</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf3">PF3</button></td>
    </tr>

    <!-- pf4 - pf6 -->
    <tr>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf4">PF4</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf5">PF5</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf6">PF6</button></td>
    </tr>

    <!-- pf7 - pf9 -->
    <tr>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf7">PF7</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf8">PF8</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf9">PF9</button></td>
    </tr>

    <!-- pf10 - pf12 -->
    <tr>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf10">PF10</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf11">PF11</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pf12">PF12</button></td>
    </tr>

    <!-- cursor/pa block -->
    <tr>
      <td></td>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="up">U</button></td>
      <td></td>
    </tr>
    <tr>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="left">L</button></td>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="home">Home</button></td>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="right">R</button></td>
    </tr>
    <tr>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="xxx">x</button></td>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="down">D</button></td>
      <td><button onclick="noSuchKey(this.name);" type="button" class="smallkey" name="xxy">x</button></td>
    </tr>
    <tr >
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pa1">PA1</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pa2">PA2</button></td>
      <td><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="smallkey" name="pa3">PA3</button></td>
    </tr>

  </table>
</td><tr>

<tr><td style="padding:0px;">
  <!-- large keys -->
  <table width=100%>
    <tr>
      <td style="padding-top:0px" align="left" ><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="pos1">Pos1</button></td>
      <td style="padding-top:0px" align="right" ><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="end">End</button></td>
    </tr>
    <tr>
      <td align="left"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="clear">Clear</button></td>
      <td align="right"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="reset">Reset</button></td>
    </tr>
    <tr>
      <td align="left"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="eraseEOF">Erase EOF</button></td>
      <td align="right"><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="eraseInput">Erase Input</button></td>
    </tr>
    <tr>
      <td align="left"><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="dup">Dup</button></td>
      <td align="right"><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="fieldMark">Field Mark</button></td>
    </tr>
    <tr>
      <td align="left"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="sysReq">Sys Req</button></td>
      <td align="right"><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="cursorSelect">Cursor Select</button></td>
    </tr>
    <tr>
      <td align="left"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="attn">Attn</button></td>
      <td align="right"><button onclick="noSuchKey(this.name);" type="button" class="largekey" name="compose">Compose</button></td>
    </tr>
    <tr>
      <td align="left"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="newline">Newline</button></td>
      <td align="right"><button onclick="handleKey(this.name, '<%= screenName %>');" type="button" class="largekey" name="enter">Enter</button></td>
    </tr>
  </table>
</td><tr>


</table>
