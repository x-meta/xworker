<div class="row">
  <#if data.getStringBlankAsNull("listVar")?exists>
  <${r"#"}list ${data.listVar} as item>
    <div class="<#if data.getStringBlankAsNull("md")?exists>col-md-${data.md}</#if><#if data.getStringBlankAsNull("sm")?exists> col-sm-${data.sm}</#if><#if data.getStringBlankAsNull("xs")?exists> col-xs-${data.xs}</#if>">
    <div class="thumbnail">
    ${r"$"}{item?if_exists}
    </div>
   </div>
  </${r"#"}list>
  <#else>
  <#list items as item>
    <div class="<#if data.getStringBlankAsNull("md")?exists>col-md-${data.md}</#if><#if data.getStringBlankAsNull("sm")?exists> col-sm-${data.sm}</#if><#if data.getStringBlankAsNull("xs")?exists> col-xs-${data.xs}</#if>">
    <div class="thumbnail"> 
    ${item?if_exists}
    </div>
    </div>
  </#list>
  </#if>
</div>