<#import "/xworker/html/bootstrap/template/common.ftl" as xw_bootstrap_lib/>
<ul id="${data.id?if_exists}" class="nav nav-tabs" role="tablist">
<#if data.getStringBlankAsNull("tabVar")?exists>
<${r"@"}xw_bootstrap_lib.showTabItems items=${data.tabVar?if_exists}/>
<#else>
<@xw_bootstrap_lib.showTabItems items=items/>
</#if>
</ul>
<#if data.getBoolean("hasContent")>
<div id="${data.id?if_exists}-tab-content" class="tab-content">
<#if data.getStringBlankAsNull("tabVar")?exists>
<${r"@"}xw_bootstrap_lib.showTabItemsContent items=${data.tabVar?if_exists}/>
<#else>
<@xw_bootstrap_lib.showTabItemsContent items=items/>
</#if>
</div>
</#if>