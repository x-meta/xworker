<#import "/xworker/html/bootstrap/template/common.ftl" as xw_bootstrap_lib/>

<#-- NavTabs -->
<#macro showTabItems items>
  <#list items as item>
  <#if item.childs?exists && 0 < item.childs?size >
  <li role="presentation" class="dropdown">
    <a href="${item.href?if_exists}" id="${item.id?if_exists}" class="dropdown-toggle" data-toggle="dropdown" aria-controls="${item.id?if_exists}-Dropcontents">${item.label?if_exists} <span class="caret"></span></a>
    <ul class="dropdown-menu" aria-labelledby="${item.id?if_exists}" id="${item.id?if_exists}-Dropcontents">
      <#list item.childs as citem>
      <li><a href="${citem.href?if_exists}" role="tab" id="${citem.id?if_exists}-tab" data-toggle="tab" aria-controls="${item.id?if_exists}">${citem.label?if_exists}</a></li>
      </#list>
    </ul>
  </li>
  <#else>
  <li role="presentation" <#if item.active?exists && item.active == "true">class="active"</#if>><a href="${item.href?if_exists}" id="${item.id?if_exists}-tab" role="tab" data-toggle="tab" aria-controls="${item.id?if_exists}" aria-expanded="true">${item.label?if_exists}</a></li>
  </#if>
  </#list>
</#macro>

<#macro showTabItemsContent items>
  <#list items as item>
  <div role="tabpanel" class="tab-pane fade <#if item.active?exists && item.active == "true">in active</#if>" id="${item.id?if_exists}" aria-labelledBy="${item.id?if_exists}-tab">
    ${item.content?if_exists}
  </div>
  <#if item.childs?exists>
  <#list item.childs as citem>
  <div role="tabpanel" class="tab-pane fade <#if citem.active?exists && citem.active == "true">in active</#if>" id="${citem.id?if_exists}" aria-labelledBy="${citem.id?if_exists}-tab">
    ${citem.content?if_exists}
  </div>
  </#list>
  </#if>
  </#list> 
</#macro>


<#--  NavBar -->
<#macro showItems items ident>
<#list items as item>
<#if !item.type?exists || item.type == "link">
<#if !item.childs?exists || item.childs?size == 0>
${ident}<li><a href="${item.href?if_exists}">${item.label?if_exists}</a></li>
<#else>
${ident}<li class="dropdown">
${ident}  <a <#if item.id?exists>id="${item.id}"</#if> href="${item.href?if_exists}" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${item.label?if_exists}<span class="caret"></span></a>
${ident}  <ul class="dropdown-menu"<#if item.id?exists> aria-labelledby="${item.id}"</#if>>
    <@showItems item.childs, "${ident}    "/>
${ident}  </ul>
${ident}</li>
</#if>
<#elseif item.type == "button">
<#if !item.childs?exists || item.childs?size == 0>
${ident}<li><a class="btn btn-default" role="button" href="${item.href?if_exists}">${item.label?if_exists}</a></li>
<#else>
${ident}<li class="dropdown">
${ident}  <a <#if item.id?exists>id="${item.id}"</#if> href="${item.href?if_exists}" class="btn btn-default dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${item.label?if_exists}<span class="caret"></span></a>
${ident}  <ul class="dropdown-menu"<#if item.id?exists> aria-labelledby="${item.id}"</#if>>
    <@showItems item.childs, "${ident}    "/>
${ident}  </ul>
${ident}</li>
</#if>
<#elseif item.type == "separator">
${ident}<li role="separator" class="divider"></li>
<#elseif item.type =="content">
${ident}${item.content?if_exists}
</#if>
</#list>
</#macro>