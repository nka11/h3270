
  // Keyboard Handling Code

  if (isIE()) {
    document.onkeydown = handleIEKeyDownEvent;
    document.onhelp = handleIEKeyHelpEvent;
  } else {
    window.onkeydown = handleKeyDownEvent;
    window.onkeypress = handleKeyPressEvent;
    window.onhelp = handleKeyHelpEvent;
    window.oncontextmenu = handleOnContextMenu;
  }

  function handleIEKeyDownEvent() {
    return handleKeyDownEvent(event);
  }

  function handleIEKeyHelpEvent() {
    return handleKeyHelpEvent(event);
  }

  function handleKeyDownEvent(eventObj) {
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

  function handleKeyPressEvent(eventObj) {
    var iKC = eventObj.keyCode;
    if ((!(eventObj.altKey)) && (!(eventObj.ctrlKey)) && (iKC >=112) && (iKC <= 123)) {
      cancelKeyEvent(eventObj);
    }
  }

  function handleKeyHelpEvent(eventObj) {
    cancelKeyEvent(eventObj);
  }

  function handleOnContextMenu(eventObj) {
    // Disable context menu to allow Shift + F10 
    return false;
  }

  function cancelKeyEvent(eventObj) {
    try {
      eventObj.keyCode = 0;
    } catch(e) {
      eventObj.preventDefault();
    }
    eventObj.cancelBubble = true;
    eventObj.returnValue = false;
  }

  function sendFormWithKey(strKey) {
    if (document.forms["screen"] != null) {
      document.forms["screen"].key.value = strKey;
      document.forms["screen"].submit();
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

  function isIE() {
    if (document.all) {
      return true;
    } else {
      return false;
    }
  }
