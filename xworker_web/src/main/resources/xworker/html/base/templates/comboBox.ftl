<#import "common.ftl" as lib>
<select name="${object.name}" id="${object.name?if_exists}" <@lib.showEvent object=object/> <@lib.showProperties names=["alt", "emsg", "class", "style"] dataObject=object/> <#if object.disabled?exists && object.disabled == "true">disabled </#if>${object.otherAttributes?if_exists}>
<#if object.haveBlank == "true">
<option></option>
</#if>
<#if (!object.values?exists || object.values = "")>
<#if object["/constantValues@"]?exists>
<#list object["/constantValues@"] as value>
  <option value="${value.value?if_exists}" <#if value.value?exists && value.value != "" && object.value?exists && object.value != ""><${r"#"}if "${value.value?if_exists}"?exists && (${object.value?if_exists})?exists && "${value.value?if_exists}" == ${object.value?if_exists}?string>selected</${r"#"}if></#if>>${value.label?if_exists}</option>
</#list>
</#if>
<#else>
<${r"#"}list ${object.values} as value>
  <option value="${r"$"}{value.${object.valueName?if_exists}}" <${r"#"}if <#if object.expression?exists>${object.expression}<#else>value.${object.valueName?if_exists}?exists && value.${object.valueName?if_exists} = "${object.value?if_exists}"</#if>>selected</${r"#"}if>>${r"$"}{value.${object.labelName?if_exists}}</option>
</${r"#"}list>
</#if>
</select>