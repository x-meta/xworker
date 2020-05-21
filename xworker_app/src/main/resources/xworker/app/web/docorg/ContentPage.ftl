<!DOCTYPE HTML>
<html>
<head><title>${doc.title?if_exists}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shCore.css" /> 
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shThemeEclipse.css" /> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shCore.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shAutoloader.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/jquery/jquery-2.0.3.min.js"></script> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap.min.css" /> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap-theme.min.css"/>
<script type="text/javascript" src="${request.contextPath}/js/xworker/InnerBrowserUtil.js"></script>
</head>
<body >

<!--整体布局-->
<div class="container">
  <div class="row">
    <div class=" col-md-12 col-xs-12">  
<!-- 导航 -->
<nav class="navbar navbar-default">
  <div class="container" id="navbar">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
       <a class="navbar-brand" href="#">
      ${doc.title?default("标题")}
      </a>
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="navbar-collapse-1">      
      <ul class="nav navbar-nav">      
        <#list menus as bar>
        <#if bar.getChilds("Category")?size == 0>
        <li>
          <a <#if bar.target?exists>target="${bar.target}"</#if>  href="<#if bar.href?exists && bar.href != "">${bar.href}<#else>do?sc=${self.metadata.path}&cat=${bar.metadata.path}</#if>">${bar.metadata.label}</a>
        </li>
        <#else>
        <li class="dropdown">
          <a id="${bar.metadata.path}" href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${bar.metadata.label}<span class="caret"></span></a>
          <ul class="dropdown-menu" aria-labelledby="${bar.metadata.path}">
            <#list bar.getChilds("Category") as subBar>
            <li>
              <a <#if subBar.target?exists>target="${subBar.target}"</#if>  href="do?sc=${self.metadata.path}&cat=${subBar.metadata.path}">${subBar.metadata.label}</a>
            </li>
            </#list>
          </ul>
        </li>
        </#if>
        </#list>
        <#if logined>
         <li class="dropdown">
          <a id="editModel" href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">编辑菜单<span class="caret"></span></a>
          <ul class="dropdown-menu" aria-labelledby="editModel">
            <li><a href="do?sc=${doc.metadata.path}&ac=addRootCategoryPage">添加菜单</a></li>            
            <li><a href="do?sc=${doc.metadata.path}&ac=editMenu">菜单管理</a></li>            
            <li role="separator" class="divider"></li>
            <li><a href="do?sc=${doc.metadata.path}&ac=editDoc">设置文档</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="do?sc=${doc.metadata.path}&cat=${(category.metadata.path)?if_exists}&ac=exitEditModel">退出编辑模式</a></li>
          </ul>
        </li>
        <#else>
        <#if editable?exists && editable>
        <li><a href="do?sc=${doc.metadata.path}&cat=${(category.metadata.path)?if_exists}&ac=editModel">编辑模式</a></li>
        </#if>
        </#if>         
      </ul>          
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<!--路径导航 -->
<#--
<ol class="breadcrumb">
   <#list indexPaths as path><li<#if path_has_next == false> class="active"</#if>><#if path_index == 0><b>路径：</b></#if><a href="${path.getUrl()}">${path.getLabel()}</a></li></#list>
</ol>
-->
    </div>
  </div>
  <div class="row">    
    <div class=" col-md-9 col-xs-12">
<!-- 主内容 -->
<#if content?exists>
<#assign contentHtml = content.doAction("toHtml", actionContext)?default("toHtml is null, path=${content.getMetadata().getPath()}")>
${contentHtml?if_exists}
<#else>还没有内容。</#if>
 </div>
 <!-- 主内容结束 -->
 
 <!-- 侧面菜单 -->
<#if category?exists>
<div class=" col-md-3 col-xs-12">   
    <#list category.childs as menuSet>
    <ul class="list-group">
    <li class="list-group-item active">
    <span><strong>${menuSet.metadata.label?if_exists}
                    <#if logined><a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menuSet.metadata.path}&ac=editPage"><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>
                         <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menuSet.metadata.path}&ac=moveUp"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                         <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menuSet.metadata.path}&ac=moveDown"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
                    </#if></strong></span>
    </li>       
    <#list menuSet.childs as menu>
    <@listMenu menu,""/>  
    </#list>
    <#if logined>
    <li class="list-group-item">
        <a href="do?sc=${doc.metadata.path}&ac=addCategoryPage&cat=${category.metadata.path}&content=${menuSet.metadata.path}"><img src="icons/add.png" style="height:12px; width:12px" />添加页面</a>                                               
    </li>
    </#if>
    </ul>
    </#list>
<#if logined><#--登录下的添加子菜单 -->
    <ul class="list-group">
    <li class="list-group-item active">
    根栏目管理
    </li>
  
    <li class="list-group-item" >
        <span><strong>
             <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${category.metadata.path}&ac=addCategoryPage"><img src="icons/add.png" style="height:12px; width:12px" />添加子栏目</a>&nbsp;&nbsp;
        </strong></span>
    </li>
    </ul>

    <ul class="list-group">
    <li class="list-group-item active">
    顶部菜单管理
    </li>
    
    <li class="list-group-item" >
        <span><strong> ${category.metadata.label}            
                    <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${category.metadata.path}&ac=editPage"><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>
                    <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${category.metadata.path}&ac=moveUp"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                         <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${category.metadata.path}&ac=moveDown"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
                    </strong></span>
    </li>   
    <li class="list-group-item" >
        <span><strong>
             <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${category.metadata.path}&ac=addCategoryPage&type=menu"><img src="icons/add.png" style="height:12px; width:12px" />添加子菜单</a>&nbsp;&nbsp;
        </strong></span>
    </li>
    </ul>
 </#if>
</div>
</#if>

<!-- 页尾 -->
    <div class="row">
    <div class=" col-md-12 col-xs-12">
    <div class="panel panel-default">
  <div class="panel-body">
      ${doc.bottom?if_exists}
      </div>
      </div>
    </div>
    </div>
<!--结尾结束 -->    
  </div>
</div>

<script src="${request.contextPath}/js/bootstrap-3.3.5/js/bootstrap.min.js"></script>  
    </body>
    <script type="text/javascript">
function path()
{
  var args = arguments,
      result = []
      ;
        
  for(var i = 0; i < args.length; i++)
      result.push(args[i].replace('@', 'bower_components/SyntaxHighlighter/scripts/'));
        
  return result
};
  
SyntaxHighlighter.autoloader.apply(null, path(
  'applescript            @shBrushAppleScript.js',
  'actionscript3 as3      @shBrushAS3.js',
  'bash shell             @shBrushBash.js',
  'coldfusion cf          @shBrushColdFusion.js',
  'cpp c                  @shBrushCpp.js',
  'c# c-sharp csharp      @shBrushCSharp.js',
  'css                    @shBrushCss.js',
  'delphi pascal          @shBrushDelphi.js',
  'diff patch pas         @shBrushDiff.js',
  'erl erlang             @shBrushErlang.js',
  'groovy                 @shBrushGroovy.js',
  'java                   @shBrushJava.js',
  'jfx javafx             @shBrushJavaFX.js',
  'js jscript javascript  @shBrushJScript.js',
  'perl pl                @shBrushPerl.js',
  'php                    @shBrushPhp.js',
  'text plain             @shBrushPlain.js',
  'py python              @shBrushPython.js',
  'ruby rails ror rb      @shBrushRuby.js',
  'sass scss              @shBrushSass.js',
  'scala                  @shBrushScala.js',
  'sql                    @shBrushSql.js',
  'vb vbnet               @shBrushVb.js',
  'xml xhtml xslt html    @shBrushXml.js'
));
SyntaxHighlighter.all();
</script>
</html>

<#macro listMenu menu ident>
<li class="list-group-item">
   ${ident}
   <#if content?exists && menu.metadata.path == content.metadata.path>
   <b>
      <span  style="background-color:rgb(211, 211, 211)">
         ${ident?if_exists}${content.metadata.label}
      </span>&nbsp;
   </b>  
   <#else>   
   <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menu.metadata.path}" <#if menu.target?exists>target="${menu.target}"</#if>>${ident?if_exists}${menu.metadata.label}</a>
   </#if>  
   <#if logined>
   <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menu.metadata.path}&ac=editPage"><img src="icons/page_edit.png" style="height:12px; width:12px"  alt="编辑"/></a>
                             <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menu.metadata.path}&ac=addCategoryPage"><img src="icons/page_add.png" style="height:12px; width:12px" alt="添加子节点"/></a>
                             <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menu.metadata.path}&ac=moveUp"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                             <a href="do?sc=${doc.metadata.path}&cat=${category.metadata.path}&content=${menu.metadata.path}&ac=moveDown"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
   </#if>                             
   <#if menu.childs?exists>
   <#list menu.childs as child>
   <@listMenu child,"${ident}&nbsp;&nbsp;"/>
   </#list>
   </#if>
</li>
</#macro>