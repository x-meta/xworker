<#import "./common.ftl" as lib/>
<!DOCTYPE HTML>
<html>
<head><title><#if contentTree.label?exists>${contentTree.label?if_exists}</#if>-${(content.project.label)?if_exists}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="Keywords" content="${contentTree.keywords?if_exists}"/>
<meta name="description" content="${(contentTree.description?html)?if_exists}"/>
<script type="text/javascript" src="${request.getContextPath()}/js/xworker/InnerBrowserUtil.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="${request.getContextPath()}/js/syntaxhighlighter/styles/shCoreEclipse.css" />
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
    <div class=" col-md-9 col-xs-12">        
    ${contentHtml?if_exists}
    <p>
    <a class="btn btn-default" href="do?sc=xworker.app.orgweb.web.IndexSearch&id=${contentTree.id}&page=${requestBean.page?default("1")}">返回</a><#if logined>&nbsp;<a class="btn btn-default" href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${content.id}&backUrl=do%3F${request.getQueryString()?url("utf-8")}">编辑</a></#if>
    </p>
    </div>
 <!-- 主内容结束 -->
  <#--侧面菜单-->
    <@lib.rightMenu menuSets=contentTree.getRoot().getChilds()/>
  </div>
<#--底部-->
<@lib.bottom indexPaths=indexPaths/>
</html>