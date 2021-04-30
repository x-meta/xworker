 <div class="panel-group" id="${data.id?if_exists}" role="tablist" aria-multiselectable="true">  
   <#list contents as content>
   <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="heading${content.id?if_exists}">
      <h4 class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#${data.id?if_exists}" href="#${content.id?if_exists}" aria-controls="${content.id?if_exists}" <#if content.expanded?exists>aria-expanded="${content.expanded?if_exists}"</#if>>
          ${content.title?if_exists}
        </a>
      </h4>
    </div>
    <div id="${content.id?if_exists}" class="panel-collapse collapse<#if content.expanded?exists && content.expanded == "true"> in</#if>" role="tabpanel" aria-labelledby="heading${content.id?if_exists}" <#if content.expanded?exists>aria-expanded="${content.expanded?if_exists}"</#if>>
      <#if content.nobody?exists && content.nobody == "true">
      ${content.content?if_exists}
      <#else>
      <div class="panel-body">
        ${content.content?if_exists}
      </div>
      </#if>
    </div>
  </div>
  </#list>
 <#if data.contentsVarName?exists && data.contentsVarName != "">
   <${r"#"}list ${data.contentsVarName} as content>
   <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="heading${r"$"}{content.id?if_exists}">
      <h4 class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#${data.id?if_exists}" href="#${r"$"}{content.id?if_exists}" aria-controls="${r"$"}{content.id?if_exists}"  <${r"#"}if content.expanded?exists>aria-expanded="${r"$"}{content.expanded?if_exists}"</${r"#"}if>>
          ${r"$"}{content.title?if_exists}
        </a>
      </h4>
    </div>
    <div id="${r"$"}{content.id?if_exists}" class="panel-collapse collapse<${r"#"}if content.expanded?exists && content.expanded == "true"> in</${r"#"}if>" role="tabpanel" aria-labelledby="heading${r"$"}{content.id?if_exists}" <${r"#"}if content.expanded?exists>aria-expanded="${r"$"}{content.expanded?if_exists}"</${r"#"}if>>
      <${r"#"}if content.nobody?exists && content.nobody == "true">
      ${r"$"}{content.content?if_exists}
      <${r"#"}else>
      <div class="panel-body">
        ${r"$"}{content.content?if_exists}
      </div>
      </${r"#"}if>
    </div>
  </div>
  </${r"#"}list>
 </#if>
</div>