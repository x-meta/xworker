<div id="${data.id?if_exists}" class="carousel slide" data-ride="carousel" data-interval="${data.interval?default("5000")}">
<#if data.getStringBlankAsNull("iamgesVarName")?exists == false>
<#if data.getBoolean("indicators") == true>
  <!-- Indicators -->
  <ol class="carousel-indicators">
  <#list images as img>
    <li data-target="#${data.id?if_exists}" data-slide-to="${img_index}"<#if img_index == 0> class="active"</#if>></li>
  </#list>
  </ol>
</#if>

  <!-- Wrapper for slides -->
  <div class="carousel-inner" role="listbox">
  <#list images as img>
    <div class="item<#if img_index == 0> active</#if>">
      <#if img.href?exists && img.href != "">
      <a href="${img.href}" target="${img.target?if_exists}"><img src="${img.src?if_exists}" alt="${img.alt?if_exists}"></a>
      <#else>
      <img src="${img.src?if_exists}" alt="${img.alt?if_exists}">
      </#if>
      <div class="carousel-caption">
        <h3>${img.title?if_exists}</h3>
        <p>${img.content?if_exists}</p>
      </div>
    </div>
  </#list>  
  </div>
<#else>
<#if data.getBoolean("indicators") == true>
  <!-- Indicators -->
  <ol class="carousel-indicators">
  <${r"#"}list ${data.iamgesVarName} as img>
    <li data-target="#${data.id?if_exists}" data-slide-to="${r"$"}{img_index}"<${r"#"}if img_index == 0> class="active"</${r"#"}if>></li>
  </${r"#"}list>
  </ol>
</#if>
  <!-- Wrapper for slides -->
  <div class="carousel-inner" role="listbox">
  <${r"#"}list ${data.iamgesVarName} as img>
    <div class="item<${r"#"}if img_index == 0> active</${r"#"}if>">
      <${r"#"}if img.href?exists && img.href != "">
      <a href="${r"$"}{img.href}" target="${r"$"}{img.target?if_exists}"><img src="${r"$"}{img.src?if_exists}" alt="${r"$"}{img.alt?if_exists}"></a>
      <${r"#"}else>
      <img src="${r"$"}{img.src?if_exists}" alt="${r"$"}{img.alt?if_exists}">
      </${r"#"}if>
      <div class="carousel-caption">
        <h3>${r"$"}{img.title?if_exists}</h3>
        <p>${r"$"}{img.content?if_exists}</p>
      </div>
    </div>
  </${r"#"}list>    
  </div>
</#if>
<#if data.getBoolean("controls") == true>
  <!-- Controls -->
  <a class="left carousel-control" href="#${data.id?if_exists}" role="button" data-slide="prev">
    <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
    <span class="sr-only">Previous</span>
  </a>
  <a class="right carousel-control" href="#${data.id?if_exists}" role="button" data-slide="next">
    <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
    <span class="sr-only">Next</span>
  </a>
</#if>  
</div>