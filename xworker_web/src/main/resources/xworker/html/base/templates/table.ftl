<#import "common.ftl" as lib>
<table border="1" class="sort-table" name="${object.name?default("table_1")}" <#if object.width?exists>width="${object.width}"</#if> cellspacing='0'>
<#-- Draw Table Header -->
  <thead>
    <tr color="gray">  
<#list object["/column@"] as column>
      <td width="${column.width?if_exists}" nowrap>${column.label?if_exists}</td>
</#list>
    </tr>
  </thead>
  <tbody>
  <#if object.value?exists>
  <${r"#"}list ${object.value} as row>
   <${r"#"}if row_index % 2 == 0>
    <tr class="sorttable_odd">
    <${r"#"}else>
    <tr class="sorttable_even">
    </${r"#"}if>
    <#list object["/column@"] as column>
    <td class="srottable_td" nowrap><div class="tabletext"><#if column.childs?exists && 0 < column.childs?size><#list column.childs as child><@showObject object=child/></#list><#elseif column.value?exists && column.value != "">${column.value?if_exists}</#if>&nbsp;</div></td>
    </#list>
    </tr>
  </${r"#"}list>
  </#if>
  </tbody>
</table>