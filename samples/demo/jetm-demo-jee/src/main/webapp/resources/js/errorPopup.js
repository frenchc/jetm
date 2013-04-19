function calculatePosition(fieldString, fieldId, promptWidth) {
      var promptElmt = $("div[id$='error\\_" + fieldId + "']");
      var field = $("#" + escapeId(fieldString));

      var promptTopPosition = field.position().top;
      var promptleftPosition = field.position().left;


      if(!promptWidth) {
          promptWidth = field.width() - 20;
      }
      promptElmt.css("width", promptWidth);

      var marginTopSize = -promptElmt.height();

      return {
        "callerTopPosition": promptTopPosition + "px",
        "callerleftPosition": promptleftPosition + "px",
        "marginTopSize": marginTopSize + "px"
      };
}

function escapeId(clientId) {
  var chars = [':', '_']; // add more if you like;

  for(i = 0; i < chars.length; i++) {
    clientId = clientId.replace(new RegExp(chars[i], "g"), "\\" + chars[i]);
  }

  return clientId;
}