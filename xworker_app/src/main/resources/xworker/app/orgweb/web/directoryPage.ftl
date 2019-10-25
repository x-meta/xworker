<#import "./common.ftl" as lib/>
<!DOCTYPE HTML>
<html>
<head><title><#if contentTree.label?exists>${contentTree.label?if_exists}</#if>-${(contentTree.project.label)?if_exists}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="Keywords" content="${contentTree.keywords?if_exists}"/>
<meta name="description" content="${(contentTree.description?html)?if_exists}"/>
<script type="text/javascript" src="${request.getContextPath()}/js/xworker/InnerBrowserUtil.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="${request.getContextPath()}/js/syntaxhighlighter/styles/shCore.css" />
<link rel="stylesheet" type="text/css" media="all" href="${request.getContextPath()}/js/syntaxhighlighter/styles/shThemeEclipse.css" />
<script type="text/javascript" src="${request.getContextPath()}/js/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="${request.getContextPath()}/js/syntaxhighlighter/scripts/shAutoloader.js"></script>
<script type="text/javascript" src="${request.getContextPath()}/js/jquery/jquery-2.0.3.min.js"></script>
<link rel="stylesheet" href="${request.getContextPath()}/js/bootstrap-3.3.5/css/bootstrap.min.css"/>
<link rel="stylesheet" href="${request.getContextPath()}/js/bootstrap-3.3.5/css/bootstrap-theme.min.css"/>
<!-- 百度统计 -->
<script>
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?c8e1e83fccb59c22f0ad4281f344b4cf";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
</script>
</head>
<body >

<!--整体布局-->
<div class="container">
<#--菜单和路径导航-->
<@lib.topMenu topRightMenu=topRightMenu?default("null") indexPaths=indexPaths/>
  <div class="row">    
    <div class=" col-md-12 col-xs-12">
<!-- 主内容 -->
<h2>${content.label?if_exists}</h2>
<#if content.parentId != 0>
${contentHtml?if_exists}
</#if>
<#list content.childs as menuSet>
  <div class="media">
  <div class="media-left">
    <a href="#">
      <img class="media-object" src="${menuSet.getTypeIcon()}" alt="${menuSet.getLabel()}">
    </a>
  </div>
  <div class="media-body">
    <#if logined>
    <h4 class="media-heading"><span <#if menuSet.status == 0>class="label label-warning"</#if>><strong><a href="${menuSet.getUrl()}">${menuSet.label?if_exists}</a>
                        <#if logined><a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${menuSet.id}&currentId=${contentTree.id}"><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>
                         <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=up&id=${menuSet.id?if_exists}&showParent=true"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                             <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=down&id=${menuSet.id?if_exists}&showParent=true"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
                        </#if></strong></span></h4>
    <#else>
    <h4 class="media-heading"><a href="${menuSet.getUrl()}">${menuSet.label?if_exists}</a></h4>
    </#if>
    ${menuSet.description?if_exists}    
  </div>
</div>
</#list>
  <div class="media">
  <div class="media-left">
    <a href="#">      
    </a>
  </div>
  <div class="media-body">    
    <ul>
    <#if logined>	
	<li><a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${content.id}&type=category"><img src="icons/add.png" style="height:12px; width:12px" />添加栏目</a>&nbsp;&nbsp;</li>
	<li><a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${content.id}"><img src="icons/page_edit.png" style="height:12px; width:12px" />编辑当前目录</a>&nbsp;&nbsp;</li>	
	</#if>  	
	<#if content.parent?exists && content.parent.getType() == 2>
	<li><a href="do?sc=xworker.app.orgweb.web.Index&id=${content.parent.id}"><img src="icons/arrow_turn_left.png" style="height:12px; width:12px" />返回上级</a>&nbsp;&nbsp;</li>
	</#if>
	<#if logined>	
	<li><a href="do?sc=xworker.app.orgweb.web.Index&id=0"><img src="icons/house.png" style="height:12px; width:12px" />项目列表</a></li>
	</#if>
	</ul>
	  </div>
   </div>
 </div>
 <!-- 主内容结束 -->
<#--底部-->
<@lib.bottom indexPaths=indexPaths/>
</html>
