<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
	<title>Move page</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shCore.css" /> 
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shThemeEclipse.css" /> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shCore.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shAutoloader.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/jquery/jquery-2.0.3.min.js"></script> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap.min.css" /> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap-theme.min.css"/>
<script type="text/javascript" src="/fckeditor/4_1/ckeditor.js">
	<script type="text/javascript">
function getContents(){	
	var oEditor = CKEDITOR.instances.data ;	
	window.status = "html_edit_content:" + oEditor.getData();
  	//alert(window.status) ;	
}

function setContents(html){
    var oEditor = CKEDITOR.instances.data ;	
    
    html = html.replace(new RegExp("&_n_;","gm"), "\n");
    html = html.replace(new RegExp("&_r_;","gm"), "\r");
    html = html.replace(new RegExp("&_quot_;","gm"), "\"");
    oEditor.setData(html);
    //oEditor.SetHTML(html);
    //var contentDiv = document.getElementById("data");
    //contentDiv.value = html;
}

function setContentsFirst(html){
    //var oEditor = FCKeditorAPI.GetInstance('data') ;
    //oEditor.SetHTML(html);
    
    html = html.replace(new RegExp("&_n_;","gm"), "\n");
    html = html.replace(new RegExp("&_r_;","gm"), "\r");
    html = html.replace(new RegExp("&_quot_;","gm"), "\"");
    var contentDiv = document.getElementById("data");
    contentDiv.value = html;
}

function executeCommand( commandName ){
	  var oEditor = FCKeditorAPI.GetInstance('data') ;

	  // Execute the command.
	  oEditor.Commands.GetCommand( commandName ).Execute() ;
}

function FCKeditor_OnComplete( editorInstance ){
	  executeCommand("FitWindow");	  
}
</script>
</head>
<body>	
<!--整体布局-->
<div class="container">
  <div class="row">
    <div class=" col-md-12 col-xs-12">  
<h1>移动栏目/页面</h1>
点击下面的链接，把<b>${content.metadata.label}(${content.thingName})</b>移动到链接下面，当前内容路径：${content.metadata.path}
<br/>
<a href="do?sc=${doc.metadata.path}&content=${content.metadata.path}&ac=editPage">返回</a><br/><br/>
<@listMenu doc, ""/>
<br/>
<br/>
<!--结尾结束 -->    
    </div>
  </div>
</div>
</body>
</html>

<#macro listMenu menu ident>
<#if menu.metadata.path == content.metadata.path>
<#else>
${ident?if_exists}<a href="do?sc=${doc.metadata.path}&content=${content.metadata.path}&target=${menu.metadata.path}&ac=doMovePage">${menu.metadata.label}(${menu.thingName})</a><br/>
<#list  menu.getChilds() as child>
<@listMenu child,"${ident}&nbsp;&nbsp;"/>
</#list>
</#if>
</#macro>