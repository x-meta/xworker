<#import "common.ftl" as lib>
<select name="${object.name}" id="${object.name?if_exists}" multiple  <@lib.showEvent object=object/> size="${object.size?if_exists}" <@lib.showProperties names=["alt", "emsg", "class", "style"] dataObject=object/>>
<#if object.haveBlank = "true">
<option></option>
</#if>
<#if (!object.values?exists || object.values == "")>
<#list object["constantValues@"] as value>
  <!--
  <${r"#"}if "${value.value?if_exists}" == "${object.value?if_exists}">
  <${r"#"}assign s = "selected">
  </${r"#"}if>
  -->
  <option value="${value.value?if_exists}" ${r"$"}{s?if_exists}>${value.label?if_exists}</option>
</#list>
<#else>
<${r"#"}list ${object.values} as value>
  <option value="${r"$"}{value.${object.valueAlias?if_exists}}" <${r"#"}if value.${object.valueAlias?if_exists} = "${object.value}">selected</${r"#"}if>>${r"$"}{value.${object.valueLabelAlias?if_exists}}</option>
</${r"#"}list>
</#if>
</select>