<#import "/xworker/html/bootstrap/template/common.ftl" as xw_bootstrap_lib/>
<nav class="navbar navbar-default<#if data.getStringBlankAsNull("fixed")?exists> ${data.fixed}</#if><#if data.getBoolean("navbar-inverse")> navbar-inverse</#if>">
  <div class="container<#if data.getBoolean("fluid")>-fluid</#if>" id="${data.id?if_exists}">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#${data.id?if_exists}" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="${data.brandHref?if_exists}">
      <#if data.getStringBlankAsNull("brankImgSrc")?exists><img alt="Brand" src="${data.brankImgSrc}"></#if>${data.brandLabel?if_exists}      
      </a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse">      
      <ul class="nav navbar-nav">
        <#if leftItems?exists> 
        <@xw_bootstrap_lib.showItems items=leftItems  ident="        "/>       
        </#if>
        <#if data.getStringBlankAsNull("leftBarVar")?exists>
        <${r"@"}xw_bootstrap_lib.showItems items=${data.leftBarVar}  ident="        "/>       
        </#if>        
      </ul>            
      <ul class="nav navbar-nav navbar-right">
        <#if rightItems?exists>
        <@xw_bootstrap_lib.showItems items=rightItems ident="        "/>    
        </#if>
        <#if data.getStringBlankAsNull("rightBarVar")?exists>
        <${r"@"}xw_bootstrap_lib.showItems items=${data.rightBarVar} ident="        "/>    
        </#if>
      </ul>           
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
