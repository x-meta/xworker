<#if !thing?exists>
  Thing not found!
<#else>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--NewPage-->
<HTML>
<HEAD>
<TITLE>
${thing.metadata.label}
</TITLE>

<META NAME="keywords" CONTENT="${thing.metadata.name}">
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<LINK REL ="stylesheet" TYPE="text/css" HREF="css/thingDoc.css" TITLE="Style">
<script type="text/javascript" src="${request.contextPath}/js/xworker/InnerBrowserUtil.js"></script>
<SCRIPT type="text/javascript">
function windowTitle(){
    if(parent){
        parent.document.title="${thing.metadata.label}";
    }
}

function openThing(thingName){
    window.status = "openThing:" + thingName;
    window.status = "";
}

function openDesc(url){
    document.location = "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + url;
}
</SCRIPT>
<NOSCRIPT>
</NOSCRIPT>
</HEAD>

<BODY BGCOLOR="white" onload="windowTitle();">

<!-- ======== START OF CLASS DATA ======== --> 
<H2>
<FONT SIZE="-1">
${(thing.metadata.path)?if_exists}</FONT>
<BR>
事物 ${thing.metadata.label}(${(thing.metadata.name)?if_exists})<img src="images/file/open.gif" alt="点击打开事物" align="top" onClick="invoke('thing:${thing.metadata.path?if_exists}')"/><img src="images/file/javaDoc.gif" alt="点击浏览描述" align="top" onClick="openDesc('${thing.metadata.path?if_exists}')"/></H2>
<#if thing.parent?exists>
<DL>
<DT><B>父事物：</B> 
  <DD><A HREF="do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@view&thing=${thing.parent.metadata.path}" title="${thing.parent.metadata.path}">${thing.parent.metadata.label}</A></DD>  
</DL>
</#if>
<DL>
<DT><B>描述者：</B> 
  <#list thing.descriptors as desc>
  <DD><A HREF="do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@view&thing=${desc.metadata.path}" title="${desc.metadata.path}">${desc.metadata.label}</A></DD>
  </#list>
</DL>
<#if thing.extends?size != 0>
<DL>
<DT><B>继承：</B> 
  <#list thing.extends as desc>
  <DD><A HREF="do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@view&thing=${desc.metadata.path}" title="${desc.metadata.path}">${desc.metadata.label}</A></DD>
  </#list>
</DL>
</#if>
<HR>
<P>
${thing.description?if_exists}
<P>
<!-- =========== FIELD SUMMARY =========== -->

<A NAME="field_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>字段摘要</B></FONT></TH>
</TR>
<#list thing["attribute@"] as field>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${field.type?default("String")}</CODE></FONT></TD>
<TD><CODE><B><a href="#${field.metadata.name}">${field.metadata.name}(${field.metadata.label})</a>
<!--<img src="images/file/open.gif" align="top" alt="点击打开事物" onclick="openThing('${field.metadata.path}')"/>--></CODE></B><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${utilDoc[field.description?if_exists]}</TD>
</TR>
</#list>
</TABLE>
&nbsp;
<!-- ======== CONSTRUCTOR SUMMARY ======== -->

<!-- ========== METHOD SUMMARY =========== -->

<A NAME="method_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>方法摘要</B></FONT></TH>
</TR>
<#list thing.actionThings as method>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</CODE></FONT></TD>
<TD><CODE><B><a href="#${method.metadata.name}">${method.metadata.name}</A></B></CODE>

<BR>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${utilDoc[method.description?if_exists]}</TD>
</#list>
</TABLE>
&nbsp;
<A NAME="node"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>子事物摘要</B></FONT></TH>
</TR>
<#list thing["thing@"] as node>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</CODE></FONT></TD>
<TD><CODE><B><a href="#${node.metadata.name}">${node.metadata.label}(${node.metadata.name})</A></B><!--<img src="images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${node.metadata.path}')"/>--></CODE>

<BR>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${utilDoc[node.description?if_exists]}</TD>
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
<#list thing["attribute@"] as field>
<A NAME="${field.metadata.name}"><!-- --></A><H3>
${field.metadata.name}(${field.metadata.label})<img src="images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${field.metadata.path}')"/></H3>
<DL>
<DD>${field.description?if_exists}
<DL>
</DL>
</DL>

<HR>
</#list>
&nbsp;
<!-- ============ METHOD DETAIL =========== -->

<A NAME="field_detail"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="1"><FONT SIZE="+1">
<B>方法详细信息</B></FONT></TH>
</TR>
</TABLE>
<#list thing.actionThings as method>
<A NAME="${method.metadata.name}"><!-- --></A><H3>
${method.metadata.name}</H3>
<DL>
<DD>${method.description?if_exists}
<DL>
</DL>
</DL>

<HR>
</#list>
&nbsp;
<!-- ============ CHILD DETAIL =========== -->

<A NAME="field_detail"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="1"><FONT SIZE="+1">
<B>子事物详细信息</B></FONT></TH>
</TR>
</TABLE>
<#list thing["thing@"] as node>
<A NAME="${node.metadata.name}"><!-- --></A><H3>
<a href="do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@view&thing=${node.metadata.path}">${node.metadata.name}(${node.metadata.label})</a><img src="images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${node.metadata.path}')"/></H3>
<DL>
<DD>${node.description?if_exists}
<DL>
</DL>
</DL>

<HR>
</#list>

</BODY>
</HTML>
</#if>