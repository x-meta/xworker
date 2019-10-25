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

<META NAME="keywords" CONTENT="${thing.metadata.name},${thing.th_keywords?if_exists}">
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<LINK REL ="stylesheet" TYPE="text/css" HREF="css/thingDoc.css" TITLE="Style">
<script type="text/javascript" src="${thingDoc.getRelationPath()}/js/xworker/InnerBrowserUtil.js"></script>
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
事物 ${thing.metadata.label}(${(thing.metadata.name)?if_exists})<img src="${thingDoc.getRelationPath()}/images/file/open.gif" alt="点击打开事物" align="top" onClick="invoke('thing:${thing.metadata.path?if_exists}')"/><img src="${thingDoc.getRelationPath()}/images/file/javaDoc.gif" alt="点击浏览描述" align="top" onClick="openDesc('${thing.metadata.path?if_exists}')"/></H2>
<#if thing.parent?exists>
<DL>
<DT><B>父事物：</B>    
<#if thingDoc.urlExists(thing.parent)>
  <DD><A HREF="${thingDoc.getUrl(thing.parent)}" title="${thing.parent.metadata.path}">${thing.parent.metadata.label}</A></DD>
<#else>
  <DD>${thing.parent.metadata.label}</DD>
</#if>  
</DL>
</#if>
<DL>
<DT><B>描述者：</B> 
  <#list thing.descriptors as desc>
<#if thingDoc.urlExists(desc)>  
  <DD><A HREF="${thingDoc.getUrl(desc)}" title="${desc.metadata.path}">${desc.metadata.label}</A></DD>
<#else>
  <DD>${desc.metadata.label}</DD>
</#if>  
  </#list>
</DL>
<#if thing.extends?size != 0>
<DL>
<DT><B>继承：</B> 
  <#list thing.extends as desc>
<#if thingDoc.urlExists(desc)>    
  <DD><A HREF="${thingDoc.getUrl(desc)}" title="${desc.metadata.path}">${desc.metadata.label}</A></DD>
<#else>
  <DD>${desc.metadata.label}</DD>
</#if>  
  </#list>
</DL>
</#if>
<HR>
<P>
${thing.description?if_exists}
<P>
<#if 0 < thingDoc.getAttributes()?size>
<!-- =========== FIELD SUMMARY =========== -->
<A NAME="field_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>字段摘要</B></FONT></TH>
<TH ALIGN="left"><FONT SIZE="+1">
<B>定义者</B></FONT></TH>
</TR>
<#list thingDoc.getAttributes() as field>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${field.thing.type?default("String")}</CODE></FONT></TD>
<TD><CODE><B><a href="#${field.thing.metadata.name}">${field.getLabel()}</a>
<!--<img src="images/file/open.gif" align="top" alt="点击打开事物" onclick="openThing('${field.thing.metadata.path}')"/>--></CODE></B><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${field.getSimpleDescription()}</TD>
<TD nowrap>${field.getDefiendThingUrl()}</TD>
</TR>
</#list>
</TABLE>
&nbsp;
</#if>
<!-- ======== CHILDTHING SUMMARY ======== -->
<A NAME="node"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>子事物摘要</B></FONT></TH>
<TH><B>定义者</B></FONT></TH>
</TR>
<#list thingDoc.getChilds() as node>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</CODE></FONT></TD>
<TD><CODE><B><a href="#${node.thing.metadata.name}">${node.getLabel()}</A></B><!--<img src="images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${node.thing.metadata.path}')"/>--></CODE>

<BR>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${node.getSimpleDescription()}</TD>
<TD nowrap>${node.getDefiendThingUrl()}</TD>
</#list>
</TABLE>

<!-- ========== METHOD SUMMARY =========== -->

<A NAME="method_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+1">
<B>方法摘要</B></FONT></TH>
<TH ALIGN="left"><FONT SIZE="+1">
<B>定义者</B></FONT></TH>
</TR>
<#list thingDoc.getActions() as method>
<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>${method.thing.metadata.name}</CODE></FONT></TD>
<TD><CODE><B><a href="#${method.thing.metadata.name}">${method.thing.metadata.name}</A></B></CODE>
<BR>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${method.getDescription()}</TD>
<TD nowrap>${method.getDefiendThingUrl()}</TD>
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
<#list thingDoc.getAttributes() as field>
<A NAME="${field.thing.metadata.name}"><!-- --></A><H3>
${field.getLabel()}<img src="${thingDoc.getRelationPath()}/images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${field.thing.metadata.path}')"/></H3>
<DL>
<DD>${field.getDescription()}
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
<#list thingDoc.getChilds() as node>
<A NAME="${node.thing.metadata.name}"><!-- --></A><H3>
<#if thingDoc.urlExists(node.getThing())> 
<a href="${thingDoc.getUrl(node.getThing())}">${node.getLabel()}</a><img src="${thingDoc.getRelationPath()}/images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${node.thing.metadata.path}')"/></H3>
<#else>
${node.getLabel()}<img src="${thingDoc.getRelationPath()}/images/file/open.gif" alt="点击打开事物" align="top" onclick="openThing('${node.thing.metadata.path}')"/></H3>
</#if>

<DL>
<DD>${node.getDescription()?if_exists}
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
<#list thingDoc.getActions() as method>
<A NAME="${method.thing.metadata.name}"><!-- --></A><H3>
${method.thing.metadata.name}</H3>
<DL>
<DD>${method.getDescription()?if_exists}
<DL>
</DL>
</DL>

<HR>
</#list>
&nbsp;
</BODY>
</HTML>
</#if>