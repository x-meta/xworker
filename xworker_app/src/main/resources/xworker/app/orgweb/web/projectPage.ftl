<!DOCTYPE HTML>
<html>
<head><title><#if contentTree.label?exists>${contentTree.label?if_exists}</#if>-${(content.project.label)?if_exists}</title>
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
  <div class="row">
    <div class=" col-md-12 col-xs-12">  
<!-- 导航 -->
<#if topRightMenu?exists>
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
      <img src="${menuSets.getIconPath()?default("/images/xworker_org/xworker_logo.png")}" />
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="navbar-collapse-1">      
      <ul class="nav navbar-nav">      
        <#list topRightMenu.childs as bar>
        <#if 0 < bar.childs?size>
         <li class="dropdown">
          <a id="dorpdown-bar-${bar.id}" href="${bar.getUrl()?default("#")}" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${bar.getLabel()?if_exists}<span class="caret"></span></a> 
          <ul class="dropdown-menu" aria-labelledby="dorpdown-bar-${bar.id}">
            <#list bar.childs as barItem>
            <li><a href="${barItem.getUrl()?if_exists}">${barItem.label?if_exists}</a></li>            
            </#list>            
          </ul>
        </li>
        <#else>
        <li>
          <a <#if bar.getTarget()?exists>target="${bar.getTarget()}"</#if>  href="${bar.getUrl()?if_exists}" <#if bar.childs?exists>id="${bar.id?if_exists}"</#if>>${bar.getLabel()?if_exists}</a>
        </li>
        </#if>
        </#list>
         <#if iconLogin>
         <li>
         <a href="<#if logined>do?sc=xworker.app.orgweb.web.Index&id=${content.id}&logout=true">退出<#else>do?sc=xworker.app.orgweb.web.admin.AdminLogin">登录</#if></a>
         </li>
         </#if>
      </ul>          
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
</#if>
    </div>
  </div>
  <div class="row">    
    <div class=" col-md-12 col-xs-12">
<h1>HTML网站内容管理</h1>
<!-- 主内容 -->
<h3>${contentTree.getLabel()}</h3>
<ol>
<#list contentTree.childs as menuSet>
    <#if logined>
	<li>
<span <#if menuSet.status == 0>class="label label-warning"</#if>><img src="${menuSet.getTypeIcon()}" style="height:12px; width:12px" alt="项目"/><strong><a href="do?sc=xworker.app.orgweb.web.Index&id=${menuSet.id}">${menuSet.label?if_exists}</a>
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
	<li><span><strong><a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${contentTree.id}&type=category"><img src="icons/add.png" style="height:12px; width:12px" /><a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${contentTree.id}&type=category">添加栏目</a></strong></span>&nbsp;&nbsp;</li>
	<li><span><strong><a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${contentTree.id}"><img src="icons/add.png" style="height:12px; width:12px" />编辑项目</a></strong></span>&nbsp;&nbsp;</li>
    <#else>
	</#if>
	<li><span><strong><img src="icons/house.png" style="height:12px; width:12px" /><a href="do?sc=xworker.app.orgweb.web.Index&id=0">主页</a></strong></span></li>
</ol>
<h3>账号管理</h3>
<ul>
    <#if logined>
	<li><a href="do?sc=xworker.app.orgweb.web.Index&id=${contentTree.id}&action=logout"><img src="icons/user_delete.png" style="height:12px; width:12px" />退出</a>&nbsp;&nbsp;</li>
    <#else>
    <li><a href="do?sc=xworker.app.orgweb.web.Index&id=${contentTree.id}&action=login"><img src="icons/user_add.png" style="height:12px; width:12px" />登录</a>&nbsp;&nbsp;</li>	
	</#if>
</ul>

<h3>提示</h3>
<p>&nbsp;&nbsp;&nbsp;&nbsp;一个类型为主题的栏目可以作为项目的主页。水平菜单的类型可以是目录。</p>
 </div>
 <!-- 主内容结束 -->
 
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
    <script type="text/javascript">
    <#if news?exists>
jQuery(document).ready(function(){
  jQuery("#newsContent").html('${news?js_string}');
  jQuery("#newsContent").css('display','block');
});    
    </#if>
function path()
{
  var args = arguments,
      result = []
      ;
        
  for(var i = 0; i < args.length; i++)
      result.push(args[i].replace('@', '/js/syntaxhighlighter/scripts/'));
        
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
 <#if menu.id == content.id>
   <b><span  style="background-color:rgb(211, 211, 211)"><a href="${menu.getUrl()}" <#if menu.getTarget()?exists>target="${menu.getTarget()}"</#if>>${ident?if_exists}${menu.getLabelCount()?if_exists}</a></span>&nbsp;<#if logined && indexSearch?exists && indexSearch><a target="_blank" href="do?sc=xworker.app.orgweb.web.admin.AddSearchPage&parentId=${menu.id?if_exists}"><img src="icons/page_white_add.png"/></a></#if></b>  
   <#else>   
   <a href="${menu.getUrl()}" <#if menu.getTarget()?exists>target="${menu.getTarget()}"</#if>>${ident?if_exists}${menu.getLabelCount()?if_exists}</a>
   </#if>  
   <#if logined>
   <a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${menu.id}"><img src="icons/page_edit.png" style="height:12px; width:12px"  alt="编辑"/></a>
                             <a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${menu.id?if_exists}&type=page"><img src="icons/page_add.png" style="height:12px; width:12px" alt="添加子节点"/></a>
                             <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=up&id=${menu.id?if_exists}"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                             <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=down&id=${menu.id?if_exists}"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
   </#if>                             
   <#if menu.childs?exists>
   <#list menu.childs as child>
   <@listMenu child,"${ident}&nbsp;&nbsp;"/>
   </#list>
   </#if>
</li>
</#macro>