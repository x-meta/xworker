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
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap-theme.min.css" 
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
        <li>
          <a <#if bar.target?exists>target="${bar.target}"</#if>  href="do?sc=${self.metadata.path}&cat=${bar.metadata.path}">${bar.metadata.label}</a>
        </li>
        </#list>
        <#if logined>
         <li class="dropdown">
          <a id="editModel" href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">编辑菜单<span class="caret"></span></a>
          <ul class="dropdown-menu" aria-labelledby="editModel">
            <li><a href="do?sc=${doc.metadata.path}&ac=addRootCategoryPage">添加菜单</a></li>            
            <li><a href="do?sc=${doc.metadata.path}&ac=addRootCategoryPage">菜单管理</a></li>
            <li><a href="do?sc=${doc.metadata.path}&ac=editDoc">设置文档</a></li>
            <li role="separator" class="divider"></li>
            <#list doc.getChilds("Category") as root>
            <li><a href="do?sc=${doc.metadata.path}&cat=${root.metadata.path}&ac=editPage">编辑${root.metadata.label}</a>
            </li>
            </#list>
            <li role="separator" class="divider"></li>
            <li><a href="do?sc=${doc.metadata.path}&ac=exitEditModel">退出编辑模式</a></li>
          </ul>
        </li>
        <#else>
        <li><a href="do?sc=${doc.metadata.path}&ac=editModel">编辑模式</a></li>
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
 <!-- 主内容结束 -->
 
 <!-- 侧面菜单 -->
<div class=" col-md-12 col-xs-12">   
    
    <ul class="list-group">
    <li class="list-group-item active">
    <span><strong>菜单列表</span>
    </li>       
    <#list doc.getChilds("Category") as menu>
    <li class="list-group-item">
        <b><a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}">${menu.metadata.label}</a></b>
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&ac=editPage""><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>        
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&content=${menu.metadata.path}&ac=moveUp&nextAc=editMenu"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&content=${menu.metadata.path}&ac=moveDown&nextAc=editMenu"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&content=${menu.metadata.path}&ac=addCategoryPage&type=menu"><img src="icons/add.png" style="height:12px; width:12px" />添加子菜单</a>&nbsp;&nbsp;
       
    </li>
    <#list menu.getChilds() as child>
      <li class="list-group-item">
        &nbsp;&nbsp;<b><a href="do?sc=${doc.metadata.path}&cat=${child.metadata.path}">${child.metadata.label}(${child.descriptor.metadata.label})</a></b>
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&content=${child.metadata.path}&ac=editPagep"><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>   
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&content=${child.metadata.path}&ac=moveUp&nextAc=editMenu"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
        <a href="do?sc=${doc.metadata.path}&cat=${menu.metadata.path}&content=${child.metadata.path}&ac=moveDown&nextAc=editMenu"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>         
        </#list>
      </li>
    </#list>
    </ul>
</div>

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
</html>