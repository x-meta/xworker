<#import "common.ftl" as lib>
<#if (group.align?exists && group.align != "") || (group.scrollable?exists && group.scrollable == "true")>
<div <@lib.showProperties names=["height", "width", "align"] dataObject=group/> <#if group.scrollable?exists && group.scrollable == "true">style="overflow:auto"</#if>>
</#if>
<#if group.isFieldSet?exists && group.isFieldSet = "true">
<fieldSet><lenged>${group.title?if_exists}</lenged>
</#if>
<#if !group.layout?exists || group.layout == "formLayout">
<#if (group.align?exists && group.align != "") || (group.scrollable?exists && group.scrollable == "true")>
<table <@lib.showProperties names=["cellpadding", "cellspacing", "border", "style", "class"] dataObject=group/> ${group.otherAttributes?if_exists}>
<#else>
<table <@lib.showProperties names=["height", "width", "cellpadding", "cellspacing", "border", "style", "class"] dataObject=group/> ${group.otherAttributes?if_exists}>
</#if>
<#if group.haveTitle?exists && group.haveTitle == "true">
  <tr>
	<th colspan="${cols * 2}" class="${group.titleClass?if_exists}" style="${group.titleStyle?if_exists}">${group.title?if_exists}</th>
  </tr>
</#if>
<#if rows?exists>
<#list rows as row>
  <tr>
  <#list row as cold>
  <#assign col = cold.userData>
  <#if group.haveLabel?exists && group.haveLabel = "true" && (!col.showTitle?exists || col.showTitle == "true")>
    <td class="${cold["labelClass"]?if_exists}" style="${cold["labelStyle"]?if_exists}" align="${cold.labelHAlign?if_exists}" valign="${cold.labelVAlign?if_exists}" nowrap  rowspan="${cold.rowspan}"><label for="${col.inuptName?if_exists}">${col.label?if_exists}</label></td>
    <td class="${cold.get("class")?if_exists}" align="${cold.hAlign?if_exists}" valign="${cold.vAlign?if_exists}" <@lib.showProperties names=["width", "height", "rowspan", "style", "bgColor"] dataObject=col showStyle=false/> <#if col.nowrap?exists && col.nowrap=="true">nowrap</#if> colspan="${cold.colspan * 2 - 1}">${cold.html?if_exists}</td>
  <#else>
    <td class="${cold.get("class")?if_exists}" align="${cold.hAlign?if_exists}" valign="${cold.vAlign?if_exists}" <@lib.showProperties names=["width", "height", "rowspan", "style", "bgColor"] dataObject=col showStyle=false/> <#if col.nowrap?exists && col.nowrap=="true">nowrap</#if> colspan="${cold.colspan?number * 2}">${cold.html?if_exists}</td>
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