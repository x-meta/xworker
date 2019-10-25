<!DOCTYPE HTML>
<html>
<head><title><#if contentTree.label?exists>${contentTree.label?if_exists}</#if>-${(content.project.label)?if_exists}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="Keywords" content="${contentTree.keywords?if_exists}"/>
<meta name="description" content="${(contentTree.getDescription()?html)?if_exists}"/>
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
  <div class="row">
    <div class=" col-md-12 col-xs-12">  
<h1>HTML网站内容管理</h1>
<h3>项目列表</h3>
<ol>
<#list contentTree.childs as menuSet>
    <#if logined>
	<li><img src="${menuSet.getTypeIcon()}" style="height:12px; width:12px" alt="项目"/>
<span <#if menuSet.status == 0>class="label label-warning"</#if>><strong><a href="do?sc=xworker.app.orgweb.web.Index&id=${menuSet.id}">${menuSet.label?if_exists}</a>
                        <#if logined><a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${menuSet.id}&currentId=${contentTree.id}"><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>
                         <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=up&id=${menuSet.id?if_exists}&showParent=true"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                             <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=down&id=${menuSet.id?if_exists}&showParent=true"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
                        </#if></strong></span></li>
    <#else>
    <li><img src="${menuSet.getTypeIcon()}" style="height:12px; width:12px" alt="项目"/>
    <span><strong><a href="do?sc=xworker.app.orgweb.web.Index&id=${menuSet.id}">${menuSet.label?if_exists}</a></strong></span></li>
    </#if>
</#list>
    <#if logined>
	<li><a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=0&type=category"><img src="icons/add.png" style="height:12px; width:12px" />添加项目</a>&nbsp;&nbsp;</li>
    </#if>
</ol>
<h3>账号管理</h3>
<ul>
    <#if logined>
	<li><a href="do?sc=xworker.app.orgweb.web.Index&id=0&action=logout"><img src="icons/user_delete.png" style="height:12px; width:12px" />退出</a>&nbsp;&nbsp;</li>
    <#else>
    <li><a href="do?sc=xworker.app.orgweb.web.Index&id=0&action=login"><img src="icons/user_add.png" style="height:12px; width:12px" />登录</a>&nbsp;&nbsp;</li>	
	</#if>
</ul>

<h3>提示</h3>
<p>&nbsp;&nbsp;&nbsp; 在这个页面中的栏目都被当成项目，栏目中的类型将会被忽略。项目图标的大小是171x38。</p>

<h4>项目主页</h4>

<p>&nbsp;&nbsp;&nbsp; 编辑项目时，通过<strong>引用标识</strong>来设置项目的主页，应用标识对应的是。</p>

<h4>默认头部水平菜单</h4>

<p>&nbsp;&nbsp;&nbsp; 编辑项目时，设置<strong>右上角菜单标识</strong>。</p>
</div>
 </div>  
</div>

<#-- <#if comment?exists && comment=="false"><#else> -->
<#if false>
<!-- 多说评论框 start -->
	<div class="ds-thread" data-thread-key="${contentTree.id}" data-title="${contentTree.label?if_exists}" data-url="${config.host?if_exists}${request.getRequestURI()}?${request.getQueryString()}"></div>
<!-- 多说评论框 end -->
<!-- 多说公共JS代码 start (一个网页只需插入一次) -->
<script type="text/javascript">
var duoshuoQuery = {short_name:"${config.websiteName?default("shortname")}"};
	(function() {
		var ds = document.createElement('script');
		ds.type = 'text/javascript';ds.async = true;
		ds.src = (document.location.protocol == 'https:' ? 'https:' : 'http:') + '//static.duoshuo.com/embed.js';
		ds.charset = 'UTF-8';
		(document.getElementsByTagName('head')[0] 
		 || document.getElementsByTagName('body')[0]).appendChild(ds);
	})();
	</script>
<!-- 多说公共JS代码 end -->
</#if>
<#if (share?exists && share=="false") || (lang?exists && lang == "en")><#else>
<!-- 百度分享-->
<script>window._bd_share_config={"common":{"bdSnsKey":{},"bdText":"","bdMini":"2","bdMiniList":false,"bdPic":"","bdStyle":"0","bdSize":"16"},"slide":{"type":"slide","bdImg":"0","bdPos":"right","bdTop":"100"}};with(document)0[(getElementsByTagName('head')[0]||body).appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion='+~(-new Date()/36e5)];</script>
<!-- 百度分享end -->
</#if>
<script src="/js/bootstrap-3.3.5/js/bootstrap.min.js"></script>
</body>
</html>