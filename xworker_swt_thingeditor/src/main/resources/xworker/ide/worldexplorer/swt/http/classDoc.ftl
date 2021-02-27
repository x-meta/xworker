<#if !cls?exists>
  Class not found!
<#else>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--NewPage-->
<HTML>
<HEAD>
<TITLE>
${cls.name}
</TITLE>

<META NAME="keywords" CONTENT="${cls.name}">
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<LINK REL ="stylesheet" TYPE="text/css" HREF="css/thingDoc.css" TITLE="Style">
<script type="text/javascript" src="${request.contextPath}/js/xworker/InnerBrowserUtil.js"></script>
<SCRIPT type="text/javascript">
function windowTitle(){
    if(parent){
        parent.document.title="${cls.name}";
    }
}

function openThing(thingName){
    window.status = "openThing:" + thingName;
    window.status = "";
}

function openDesc(url){
    document.location = "do?sc=xworker:ui:worldExplorer/swt.http.ThingDoc:/@desc&thing=" + url;
}
</SCRIPT>
<NOSCRIPT>
</NOSCRIPT>
</HEAD>

<BODY BGCOLOR="white" onload="windowTitle();">

<!-- ======== START OF CLASS DATA ======== --> 
<H2>
<FONT SIZE="-1">
${(cls.path)?if_exists}</FONT>
<BR>
类 ${cls.name}</H2>
<HR>
<P>
${cls.description?if_exists}
<P>
<!-- =========== FIELD SUMMARY =========== -->

<A NAME="field_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>字段摘要</B></FONT></TH>
</TR>
<#list cls.fields as field>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#if field.className?exists><a href="do?sc=xworker.ide.worldexplorer.swt.http.ClassDoc/@clsDoc&className=${field.className}">${field.type?default("String")}</a><#else>${field.type?default("String")}</#if></CODE></FONT></TD>
<TD><CODE><B>${field.name}
</CODE></B></TD>
</TR>
</#list>
</TABLE>
&nbsp;
<!-- ======== CONSTRUCTOR SUMMARY ======== -->
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="1"><FONT SIZE="+1">
<B>构造方法摘要</B></FONT></TH>
</TR>
<#list cls.constructors as cons>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD><CODE><B>${cons.name}(<#list cons.params as param><#if param.className?exists><a href="do?sc=xworker.ide.worldexplorer.swt.http.ClassDoc/@clsDoc&className=${param.className}">${param.type}</a><#else>${param.type}</#if>&nbsp;${param.name}<#if param_has_next>,&nbsp;</#if></#list>)
</CODE></B></TD>
</TR>
</#list>
</TABLE>
<!-- ========== METHOD SUMMARY =========== -->

<A NAME="method_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>方法摘要</B></FONT></TH>
</TR>
<#list cls.methods as method>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<B><#if method.returnTypeCls?exists><a href="do?sc=xworker.ide.worldexplorer.swt.http.ClassDoc/@clsDoc&className=${method.returnTypeCls?if_exists}">${method.returnType}</a><#else>${method.returnType}</#if></B></CODE></TD>
<TD><CODE><B>${method.name}(<#list method.params as param><#if param.className?exists><a href="do?sc=xworker.ide.worldexplorer.swt.http.ClassDoc/@clsDoc&className=${param.className}">${param.type}</a><#else>${param.type}</#if>&nbsp;${param.name}<#if param_has_next>,&nbsp;</#if></#list>)</B></CODE>
</TD>
</#list>
</TABLE>
</BODY>
</HTML>
</#if>