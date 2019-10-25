<html>
<head><title>X-Meta<#if content.label?exists>-${content.label?if_exists}</#if></title>
<meta name="baidu-site-verification" content="CBraPKe4lh" />
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta name="Keywords" content=${content.keywords?if_exists}"/>
<script type="text/javascript" src="js/xworker/InnerBrowserUtil.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="js/syntaxhighlighter/styles/shCore.css" />
<link rel="stylesheet" type="text/css" media="all" href="js/syntaxhighlighter/styles/shThemeEclipse.css" />
<script type="text/javascript" src="js/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="js/syntaxhighlighter/scripts/shAutoloader.js"></script>
<link rel="stylesheet" href="css/xworker_org/ebadusmenu.css" type="text/css" />
<script src="js/jquery/jquery-1.4.2.min.js" type="text/javascript"></script>
<script src="js/xworker_org/ebadusmenu.js" type="text/javascript"></script>
<#if topRightMenu?exists>
<!-- 右上角的菜单 -->
<script type="text/javascript">
<#list topRightMenu.childs as bar>
<#if bar.childs?exists>
ebadusmenu.definemenu("${bar.id}", "${bar.id}_sub", "mouseover")
</#if>
</#list>
</script>
</#if>       
</head>
<body >

<!-- 右上角的菜单 -->
<div id="headerb">
  <div class="logo"><img src="${menuSets.getIconPath()?default("images/xworker_org/xworker_logo.png")}" /></div>
  <div class="hdright">
    <div class="hdrow">
    <#if topRightMenu?exists>
      <ul id="ebadu_nav" class="ebadu_nav">
      
        <#list topRightMenu.childs as bar>
        <li>
          <a <#if bar.getTarget()?exists>target="${bar.getTarget()}"</#if>  href="${bar.getUrl()?if_exists}" <#if bar.childs?exists>id="${bar.id?if_exists}"</#if>>${bar.getLabel()?if_exists}</a>
        </li>
        <#if bar_has_next>
        <span class="sep">|</span>
        </#if>
        </#list>
        
      </ul>
      </#if>
     </div>
   </div>
</div> 
<#if topRightMenu?exists>
<!-- 二级菜单 -->
<#list topRightMenu.childs as bar>
<#if bar.childs?exists>
<div id="${bar.id?if_exists}_sub" class="cssbadumenu">
  <div class="jjt"></div>
  <#list bar.childs as childBar>
  <div class="column">
    <h3 class="navt">
      ${childBar.getLabel()?if_exists}
      	<span>
          &raquo;
        </span>      
    </h3>
    <ul>
      <#if childBar.childs?exists>
      <#list childBar.childs as ccBar>
      <li>
        <a href="${ccBar.url}" <#if bar.getTarget()?exists>target="${bar.getTarget()}"</#if>>
          ${ccBar.label}
        </a>
      </li>
      </#list>
      <#else>
      <li>需要设置子节点ﺿ{childBar.label}</li>
      </#if>
    </ul>
  </div>
  </#list>
</div>
</#if>
</#list>
</#if>
<div id="container" class="content">
<table cellspacing="1" cellpadding="1" width="100%" border="0">
    <tbody>
        <tr>
            <td valign="top"><b>位置<#list indexPaths as path><#if path_index != 0>-></#if><a href="${path.getUrl()}">${path.getLabel()}</a></#list>
            <#if indexPaths?size != 0>-></#if>${content.getLabel()}</b></td>
        </tr>
    </tbody>
</table>
            
<table cellspacing="1" cellpadding="1" width="100%" border="0">
    <tbody>
        <tr>
            <td valign="top">
            <table border="0" cellspacing="0" cellpadding="0">
    <tbody>
        <tr>
            <td>
            <#list menuSets.childs as menuSet>
            <table border="0" cellspacing="0" cellpadding="0" width="100%"> 
                <tbody>
                    <tr valign="top">
                        <td class="v14-header-1" height="20" valign="middle"><span><span><span><strong>&nbsp;${menuSet.label?if_exists}</strong></span></span></span></td>
                    </tr>
                    <tr valign="top">
                        <td>
                        <table style="width: 190px" class="v14-gray-table-border" border="0" cellspacing="0" cellpadding="0">
                            <tbody>
                                <#list menuSet.childs as menu>
                                            <tr class="bullet-list"  valign="top" width="100%">                                                
                                                <td>
                                                   <@listMenu menu,""/>                                                
                                                </td>
                                            </tr>
                                            </#list>
                            </tbody>
                        </table>
                        </td>
                    </tr>
                </tbody>
            </table>
            <br />
            </#list>
            <#if logined>
            <table border="0" cellspacing="0" cellpadding="0" width="100%"> 
                <tbody>
                    <tr valign="top">
                        <td class="v14-header-1" height="20" valign="middle"><span><span><span><strong>&nbsp;<a href="do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId=0&type=category">添加子栏目</a></strong></span></span></span></td>
                    </tr>                    
                </tbody>
            </table>
            </#if>
            </td>
        </tr>
    </tbody>
</table>
<p>&nbsp;</p>

            </td>
            <td valign="top" width="100%">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tbody>
        <tr valign="top">
            <td class="v14-header-1" height="20" valign="middle" width="100%"><span ><span><strong>&nbsp;${content.label?if_exists}</strong></span></span></td>
        </tr>
        <tr>
            <td>
            <table style="width: 100%" class="v14-gray-table-border" border="0" cellspacing="0" cellpadding="0" width="100%">
                <tbody>
                    <tr>
                        <td>
                        ${contentHtml?if_exists}
                        </td>
                    </tr>
                </tbody>
            </table>
           </td>
        </tr>
    </tbody>
</table>
            </td>
        </tr>
    </tbody>
</table>
    <div align="center">
<p>Copyright &copy;&nbsp; 2007-2014 XWorker.org &nbsp;版权所暿/p>
<p><a target="_blank" href="http://www.miibeian.gov.cn/">沪ICP墿8000575卿/a></p>
</div>
</div>
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
      result.push(args[i].replace('@', 'js/syntaxhighlighter/scripts/'));
        
  return result
};
  
SyntaxHighlighter.autoloader.apply(null, path(
  //'applescript            @shBrushAppleScript.js',
  //'actionscript3 as3      @shBrushAS3.js',
  //'bash shell             @shBrushBash.js',
  //'coldfusion cf          @shBrushColdFusion.js',
  //'cpp c                  @shBrushCpp.js',
  //'c# c-sharp csharp      @shBrushCSharp.js',
  'css                    @shBrushCss.js',
  //'delphi pascal          @shBrushDelphi.js',
  //'diff patch pas         @shBrushDiff.js',
  //'erl erlang             @shBrushErlang.js',
  'groovy                 @shBrushGroovy.js',
  'java                   @shBrushJava.js',
  //'jfx javafx             @shBrushJavaFX.js',
  'js jscript javascript  @shBrushJScript.js',
  //'perl pl                @shBrushPerl.js',
  //'php                    @shBrushPhp.js',
  'text plain             @shBrushPlain.js',
  //'py python              @shBrushPython.js',
  //'ruby rails ror rb      @shBrushRuby.js',
  //'sass scss              @shBrushSass.js',
  //'scala                  @shBrushScala.js',
  //'sql                    @shBrushSql.js',
  //'vb vbnet               @shBrushVb.js',
  'xml xhtml xslt html    @shBrushXml.js'
));
SyntaxHighlighter.all();
</script>
</html>

<#macro listMenu menu ident>
<table>
<tr>
<td valign="top"><#if menu.parentSelected?exists && menu.parentSelected == "true"><img border="0" alt="" width="2" height="8" src="/files/Image/bk-bullet.gif" /></#if></td>
<td>
   <#if menu.id == content.id>
   <b><span  style="background-color:rgb(211, 211, 211)">${menu.getLabel()}</span></b>  
   <#else>   
   <a href="${menu.getUrl()}" <#if menu.getTarget()?exists>target="${menu.getTarget()}"</#if>>${menu.getLabel()?if_exists}</a>
   </#if>  
   <#if menu.childs?exists>
   <#list menu.childs as child>
   <@listMenu child,"${ident}&nbsp;&nbsp;"/>
   </#list>
   </#if>
   </td></tr></table>
</#macro>