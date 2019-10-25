<#macro bottom indexPaths>
<#if 0 < indexPaths?size>
  <div class="row">
    <div class=" col-md-12 col-xs-12">
<ol class="breadcrumb">
   <#list indexPaths as path>
   <#if path_index == 0><b><#if lang?exists && lang=="en">Path: <#else>路径：</#if></b></#if>
   <#if 0 < path.childs?size>   
    <li class="dropdown">
      <span <#if path.status == 0>class="label label-warning"</#if>><a href="${path.getUrl()?default("#")}" ><#if path_index == 0><#if lang?exists && lang=="en">Home <#else>主页</#if><#else>${path.getLabel()?if_exists}</#if></a></span>
      <#if path.getType() == 2>
      <a id="dorpdown-path-${path.id}" href="${path.getUrl()?default("#")}" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></a>       
      <ul class="dropdown-menu" aria-labelledby="dorpdown-path-${path.id}">
        <#list path.childs as barItem>
        <li><a href="${barItem.getUrl()?if_exists}">${barItem.label?if_exists}</a></li>            
        </#list>            
      </ul>
      </#if>
    </li>
   <#else>
   <li<#if path_has_next == false> class="active"</#if>>   
   <a href="${path.getUrl()}">${path.getLabel()}</a>
   </li>
   </#if>
   </#list>
</ol>
</div>
</div>
</#if>
<!-- 页尾 -->
  <div class="row">
    <div class=" col-md-12 col-xs-12">
    <div class="panel panel-default">
  <div class="panel-body">
      ${contentTree.project.description?if_exists}
      </div>
      </div>
    </div>
    </div>
<!--结尾结束 -->    
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
<#if true || (share?exists && share=="false") || (lang?exists && lang == "en")><#else>
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
</#macro>

<#macro rightMenu menuSets>
 <!-- 侧面菜单 -->
    <div class=" col-md-3 col-xs-12">
        <ul class="list-group">
        <#list menuSets as menuSet>
        <li class="list-group-item active">
        <span <#if menuSet.status == 0>class="label label-warning"</#if>><strong>${menuSet.label?if_exists}
                        <#if logined><a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${menuSet.id}"><img src="icons/page_edit.png" style="height:12px; width:12px" /></a>
                         <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=up&id=${menuSet.id?if_exists}"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
                             <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=down&id=${menuSet.id?if_exists}"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
                        </#if></strong></span>
        </li>               
        <#list menuSet.childs as menu>        
        <@listMenu menu,""/>          
        </#list>
        <#if logined>
        <li class="list-group-item">
            <a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${menuSet.id?if_exists}&type=page"><img src="icons/add.png" style="height:12px; width:12px" />添加页面</a>                                               
        </li>
        </#if>
        </#list>
        <#if logined><#--登录下的添加子菜单 -->
        <li class="list-group-item">
            <span><strong>
                        <a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${contentTree.getRoot().id}&type=category"><img src="icons/add.png" style="height:12px; width:12px" />添加子栏目</a>&nbsp;&nbsp;<br/>
                        <a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${contentTree.getRoot().id}"><img src="icons/page_edit.png" style="height:12px; width:12px"  alt="编辑"/>编辑主题</a><br/>
                        <a href="do?sc=xworker.app.orgweb.web.Index&id=${contentTree.project.id}"><img src="icons/house.png" style="height:12px; width:12px" />编辑项目</a><br/>
                        <a href="do?sc=xworker.app.orgweb.web.Index&id=0"><img src="icons/house.png" style="height:12px; width:12px" />项目列表</a>
                        </strong></span>
        </li>
        </#if>
        </ul>
    </div>
<!-- 侧面菜单结束 -->
</#macro>

<#macro listMenu menu ident>
<li class="list-group-item">
 <#if menu.id == contentTree.id>
   <b><span  style="background-color:rgb(211, 211, 211)"><a href="${menu.getUrl()}" <#if menu.getTarget()?exists>target="${menu.getTarget()}"</#if>>${ident?if_exists}${menu.getLabelCount()?if_exists}</a></span>&nbsp;</b>  
   <#else>   
   <span <#if menu.status == 0>class="label label-warning"</#if>><a href="${menu.getUrl()}" <#if menu.getTarget()?exists>target="${menu.getTarget()}"</#if>>${ident?if_exists}${menu.getLabelCount()?if_exists}</a><span>
   </#if>  
   <#if logined>
   <a href="do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=${menu.id}"><img src="icons/page_edit.png" style="height:12px; width:12px"  alt="编辑"/></a>            
   <#if menu.getType() == 3>
   <a href="do?sc=xworker.app.orgweb.web.admin.AddSearchPage&parentId=${menu.id?if_exists}"><img src="icons/page_add.png" style="height:12px; width:12px" alt="添加子节点"/></a>
   <#else>
   <a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=${menu.id?if_exists}&type=page"><img src="icons/page_add.png" style="height:12px; width:12px" alt="添加子节点"/></a>
   </#if>
   <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=up&id=${menu.id?if_exists}"><img src="icons/arrow_up.png" style="height:12px; width:12px" alt="上移"/></a>
   <a href="do?sc=xworker.app.orgweb.web.admin.Move&action=down&id=${menu.id?if_exists}"><img src="icons/arrow_down.png" style="height:12px; width:12px" alt="下移"/></a>    
   </#if>                  
   <#if menu.childs?exists && menu.getType() != 1 && menu.getType() != 2 && menu.getType() != 3>
   <#list menu.childs as child>
   <@listMenu child,"${ident}&nbsp;&nbsp;"/>
   </#list>
   </#if>
</li>
</#macro>

<#macro topMenu topRightMenu indexPaths>
  <div class="row">
    <div class=" col-md-12 col-xs-12">  
<!-- 导航 -->
<#if topRightMenu?exists && topRightMenu != "null">
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
      <#if contentTree.project?exists && contentTree.project.url?exists>
      <#if contentTree.getIconPath()?exists>
      <a href="${contentTree.project.url}"><img src="${contentTree.getIconPath()?default("/images/xworker_org/xworker_logo.png")}" /></a>
      <#else>
      <a class="navbar-brand" href="${contentTree.project.url}">${contentTree.project.getLabel()?default("请设置标题或图标")}</a>
      </#if>
      <#else>
      <#if contentTree.getIconPath()?exists>
      <img src="${contentTree.getIconPath()?default("/images/xworker_org/xworker_logo.png")}" />
      <#else>
      <a class="navbar-brand" href="#">${contentTree.project.getLabel()?default("请设置标题或图标")}</a>
      </#if>
      </#if>
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="navbar-collapse-1">      
      <ul class="nav navbar-nav">      
        <#list topRightMenu.childs as bar>
        <#if 0 < bar.childs?size>        
         <li class="dropdown">        
          <a id="dorpdown-bar-${bar.id}" href="${bar.getUrl()?default("#")}" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
          <span <#if bar.status == 0>class="label label-warning"</#if>>${bar.getLabel()?if_exists}</span><span class="caret"></span></a> 
          <ul class="dropdown-menu" aria-labelledby="dorpdown-bar-${bar.id}">
            <#list bar.childs as barItem>
            <li><a href="${barItem.getUrl()?if_exists}">${barItem.label?if_exists}</a></li>            
            </#list>            
          </ul>
        </li>
        <#else>
        <li>
          <#if bar.getUrl() == "lang:">
          <a href="do?${request.getQueryString()}&lang=reverse" <#if bar.childs?exists>id="${bar.id?if_exists}"</#if>><span <#if bar.status == 0>class="label label-warning"</#if>>${bar.getLabel()?if_exists}</span></a>
          <#else>
          <a <#if bar.getTarget()?exists>target="${bar.getTarget()}"</#if>  href="${bar.getUrl()?if_exists}" <#if bar.childs?exists>id="${bar.id?if_exists}"</#if>><span <#if bar.status == 0>class="label label-warning"</#if>>${bar.getLabel()?if_exists}</span></a>
          </#if>
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
<!--路径导航 -->
<#if 0 < indexPaths?size>
<ol class="breadcrumb">
   <#list indexPaths as path>
   <#if path_index == 0><b><#if lang?exists && lang=="en">Path: <#else>路径：</#if></b></#if>
   <#if 0 < path.childs?size>   
    <li class="dropdown">
      <span <#if path.status == 0>class="label label-warning"</#if>><a href="${path.getUrl()?default("#")}" ><#if path_index == 0><#if lang?exists && lang=="en">Home<#else>主页</#if><#else>${path.getLabel()?if_exists}</#if></a></span>
      <#if path.getType() == 2>
      <a id="dorpdown-path-${path.id}" href="${path.getUrl()?default("#")}" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="caret"></span></a>       
      <ul class="dropdown-menu" aria-labelledby="dorpdown-path-${path.id}">
        <#list path.childs as barItem>
        <li><a href="${barItem.getUrl()?if_exists}">${barItem.label?if_exists}</a></li>            
        </#list>            
      </ul>
      </#if>
    </li>
   <#else>
   <li<#if path_has_next == false> class="active"</#if>>   
   <a href="${path.getUrl()}">${path.getLabel()}</a>
   </li>
   </#if>
   </#list>
</ol>
</#if>
    </div>
  </div>
</#macro>