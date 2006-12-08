
  // Keyboard Handling Code

  enter = false;

  if (document.all) { 
    // block help function in IE so we can use F1
    document.onhelp = cancelKeyEvent;
  } else {
    // block context menu in FF so we can use Shift + F10
    window.oncontextmenu = cancelKeyEvent;
  }

  function handleKeyDownEvent(eventObj) {
    eventObj = (eventObj) ? eventObj
                          : ((window.event) ? window.event : "");
    if (eventObj) {
      var keyCode = eventObj.keyCode;
      if (keyCode == 13) {
        cancelKeyEvent(eventObj);
        sendFormWithKey("enter");
      } else if ((!(eventObj.altKey)) 
              && (!(eventObj.ctrlKey))
              && (keyCode >=112)
              && (keyCode <= 123)) {
        cancelKeyEvent(eventObj);
        handleFunctionKeyEvent(eventObj, keyCode);
      }
    }
  }

  function handleKeyPressEvent(eventObj) {
    eventObj = (eventObj) ? eventObj
                          : ((window.event) ? window.event : "");
    if (eventObj) {
      var keyCode = eventObj.keyCode;
      if ((!(eventObj.altKey)) && 
          (!(eventObj.ctrlKey)) && 
          (keyCode >=112) && 
          (keyCide <= 123)) {
        cancelKeyEvent(eventObj);
      }
    }
  }

  function cancelKeyEvent(eventObj) {
    eventObj = (eventObj) ? eventObj
                          : ((event) ? event : "");
    if (eventObj) {
      try {
        eventObj.keyCode = 0;
      } catch(e) {
        eventObj.preventDefault();
      }
      eventObj.cancelBubble = true;
      eventObj.returnValue = false;
    }
    return false;
  }

  function sendFormWithKey(strKey) {
    if (document.forms["screen"] != null && enter == false) {
      document.forms["screen"].key.value = strKey;
      document.forms["screen"].submit();
      enter = true;
    }
    else if (enter == true)
    {
      alert("The current transaction is still running. Please try again.");
    }
  }

  function handleFunctionKeyEvent(eventObj, keyCode) {
    if ((!(eventObj.altKey)) && (!(eventObj.ctrlKey))) {
      var functionKey;
      if (!(eventObj.shiftKey)) {
        functionKey = keyCode - 111;
      } else {
        functionKey = keyCode - 99;
      }
      sendFormWithKey("pf" + functionKey);
    }
  }

