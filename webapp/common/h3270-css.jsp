
pre, pre input, textarea {
	font-size: 10pt;
	border-width: 0pt;
}

pre, pre input, textarea {
    font-family: <%= sessionState.getFontName() %>; 
}

.h3270-screen-border {
    border-style:solid; 
    border-width:1px;
    border-collapse:collapse;
}

<%= sessionState.getActiveColorScheme().toCSS() %>
