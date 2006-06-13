
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

.h3270-highlight-underscore {
    text-decoration: underline;
}

.h3270-highlight-rev-video {
    color: <%= sessionState.getActiveColorScheme().getFieldStyle(0xf0).backgroundColor %>;
    background-color: <%= sessionState.getActiveColorScheme().getFieldStyle(0xf0).foregroundColor %>;
}

.h3270-highlight-blink {
    text-decoration:blink;
}

.h3270-color-blue {
    color:blue !important;
}
.h3270-color-red {
    color:red !important;
}
.h3270-color-pink {
    color:#ffb6c1 !important;
}
.h3270-color-green {
    color:lime !important;
}
.h3270-color-turq {
    color:#40e0d0 !important;
}
.h3270-color-yellow {
    color:yellow !important;
}
.h3270-color-white {
    color:white !important;
}

<%= sessionState.getActiveColorScheme().toCSS() %>
