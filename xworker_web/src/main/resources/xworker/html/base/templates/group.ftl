<#import "common.ftl" as lib>
<#if (group.align?exists && group.align != "") || (group.scrollable?exists && group.scrollable == "true")>
<div <@lib.showProperties names=["height", "width", "align"] dataObject=group/> <#if group.scrollable?exists && group.scrollable == "true">style="overflow:auto"</#if>>
</#if>
<#assign cols = group.cols?number/>
<#if group.isFieldSet?exists && group.isFieldSet = "true">
<fieldSet><lenged>${group.title?if_exists}</lenged>
</#if>
<#if !group.layout?exists || group.layout == "formLayout">
<#if (group.align?exists && group.align != "") || (group.scrollable?exists && group.scrollable == "true")>
<table <@lib.showProperties names=["cellpadding", "cellspacing", "border", "style", "class"] dataObject=group/> ${group.otherAttributes?if_exists}>
<#else>
<table <@lib.showProperties names=["height", "width", "cellpadding", "cellspacing", "border", "style", "class"] dataObject=group/> ${group.otherAttributes?if_exists}>
</#if>
<#if group.haveTitle?exists && group.haveTitle = "true">
  <tr>
	<th colspan="${cols * 2}" align="left" class="groupTitle">${group.title?if_exists}</th>
  </tr>
</#if>
<#if rows?exists>
<#list rows as row>
  <tr>
  <#list row as cold>
  <#assign col = cold.userData>
  <#if group.haveLabel?exists && group.haveLabel = "true" && (!col.showTitle?exists || col.showTitle == "true")>
    <td <#if group.tdTitleClass?exists>class="${group.tdTitleClass?if_exists}"</#if> width="1%" nowrap  rowspan="${cold.rowspan}" align="right"><label for="${col.name?if_exists}">${col.label?if_exists}</label></td>
    <td <#if col.thingName="cell"><@lib.showClass dataObject=col default="dataField"/><#else>class="${group.tdClass?if_exists}"</#if> width="1%" <@lib.showProperties names=["height", "valign", "align", "rowspan", "style", "bgColor"] dataObject=col showStyle=false/> <#if col.nowrap?exists && col.nowrap=="true">nowrap</#if> colspan="${cold.colspan * 2 - 1}">${cold.html?if_exists}</td>
  <#else>
    <td <#if col.thingName="cell"><@lib.showClass dataObject=col default="dataField"/><#else>class="${group.tdClass?if_exists}"</#if> <@lib.showProperties names=["width", "height", "valign", "align", "rowspan", "style", "bgColor"] dataObject=col showStyle=false/> <#if col.nowrap?exists && col.nowrap=="true">nowrap</#if> colspan="${cold.colspan?number * 2}">${cold.html?if_exists}</td>
  </#if>
  </#list>
  </tr>
</#list>
</#if>
</table>
<#elseif group.layout == "flowLayout">
${childHtmls?if_exists}
</#if>
<#if group.isFieldSet?exists && group.isFieldSet = "true">
</fieldSet>
</#if>
<#if (group.align?exists && group.align != "") || (group.scrollable?exists && group.scrollable == "true")>
</div>
</#if>