<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--NewPage-->
<HTML>
<HEAD>
<TITLE>
SWT元素列表
</TITLE>

<META NAME="keywords" CONTENT="<#list things as thing>${thing.metadata.name},</#list>">
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<LINK REL ="stylesheet" TYPE="text/css" HREF="css/thingDoc.css" TITLE="Style">
<script type="text/javascript" src="${request.contextPath}/js/xworker/InnerBrowserUtil.js"></script>
<SCRIPT type="text/javascript">
function windowTitle(){
    if(parent){
        parent.document.title="SWT元素列表";
    }
}

function openThing(thingName){
    window.status = "openThing:" + thingName;
    window.status = "";
}

function openDesc(url){
    document.location = "do?sc=ui:editor:swt.http.ThingDoc:/@desc&thing=" + url;
}
</SCRIPT>
<NOSCRIPT>
</NOSCRIPT>
</HEAD>

<BODY BGCOLOR="white" onload="windowTitle();">

<!-- ======== START OF CLASS DATA ======== -->
<#list things as thing>
<DL>
<DT><B>${thing.metadata.name}：</B> 
  <DD><A HREF="do?sc=ui:editor:swt.http.ThingDoc:/@view&thing=${thing.metadata.path}" title="${thing.metadata.path}">${thing.metadata.label}</A></DD>  
</DL>
</#list>
<HR>

<!-- =========== FIELD SUMMARY =========== -->

<A NAME="field_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>元素摘要</B></FONT></TH>
</TR>
<#list elements as field>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${field.thing.thingName?default("String")}</CODE></FONT></TD>
<TD><CODE><B><a href="#${field.thing.metadata.name}">${field.thing.metadata.name}</a>
<img src="images/file/open.gif" align="top" alt="点击打开事物" onclick="openThing('${field.thing.metadata.path}')"/></CODE></B><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${utilDoc[field.thing.description?if_exists]}</TD>
</TR>
</#list>
</TABLE>
&nbsp;

<!-- ============ FIELD DETAIL =========== -->

<A NAME="field_detail"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="1"><FONT SIZE="+1">
<B>字段详细信息</B></FONT></TH>
</TR>
</TABLE>
<#list elements as field>
<A NAME="${field.thing.metadata.name}"><!-- --></A><H3>
${field.thing.metadata.name}(${field.thing.metadata.label})<img src="images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${field.thing.metadata.path}')"/></H3>
<DL>
<DD>${field.thing.description?if_exists}
<DL>
</DL>
</DL>

<HR>
</#list>
</BODY>
</HTML>