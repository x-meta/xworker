<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--NewPage-->
<HTML>
<HEAD>

<META http-equiv="Content-Type" content="text/html; charset=utf-8">
<TITLE>
Overview 
</TITLE>

<META NAME="date" CONTENT="${date}">

<LINK REL ="stylesheet" TYPE="text/css" HREF="stylesheet.css" TITLE="Style">

<SCRIPT type="text/javascript">
function windowTitle()
{
    if (location.href.indexOf('is-external=true') == -1) {
        parent.document.title="Overview";
    }
}
</SCRIPT>
<NOSCRIPT>
</NOSCRIPT>

</HEAD>

<BODY BGCOLOR="white" onload="windowTitle();">
<HR>

<CENTER>
<H1>
Specification
</H1>
</CENTER>

<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+2">
<B>Packages</B></FONT></TH>
</TR>
<#list context.getCategorys() as pkg>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD WIDTH="20%"><B><A HREF="${pkg?replace(".", "/")}/package-summary.html">${pkg}</A></B></TD>
<TD>Primary package containing general things.</TD>
</TR>
</#list>
</TABLE>

</BODY>
</HTML>
