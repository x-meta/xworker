<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
	<title>SWT Kckeditor</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shCore.css" /> 
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shThemeEclipse.css" /> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shCore.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shAutoloader.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/jquery/jquery-2.0.3.min.js"></script> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap.min.css" /> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap-theme.min.css"/>
<script type="text/javascript" src="/fckeditor/4_1/ckeditor.js">
</script>

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

function removePage(){
  if(confirm('确定要删除！')){
      document.getElementById("ac").value = "removePage";
      document.forms[0].submit();
  }
}
function movePage(){
   document.getElementById("ac").value = "movePage";
   document.forms[0].submit();
}

function saveAndReturnf(){
  document.getElementById("saveAndReturn").value = "true";
  document.forms[0].submit();  
}

function back(){
  document.location= "do?sc=${doc.metadata.path}&cat=${(category.metadata.path)?if_exists}&content=${(content.metadata.path)?if_exists}&ac=${requestBean.nextAc?if_exists}";  
}

</script>
</head>
<body>	
<h1>编辑页面-${doc.metadata.label}</h1>
	<form id="contentForm" name="contentForm" method="post" action="do" >
	    <input type="hidden" name="sc" value="${doc.metadata.path}"/>
	     <input id="ac" type="hidden" name="ac" value="savePage"/>
	     <input id="saveAndReturn" type="hidden" name="saveAndReturn" value="false"/>
	     <input type="hidden" name="cat" value="${(category.metadata.path)?if_exists}"/>
	     <input type="hidden" name="content" value="${(content.metadata.path)?if_exists}"/>

        <div class="form-group">
          <label for="label">标题</label>
          <input type="text" class="form-control" id="label" name="label" placeholder="标题" value="${(content.metadata.label?html)?if_exists}"/>
        </div>
        <div class="form-group">
          <label for="href">链接</label>
          <input type="text" class="form-control" id="href" name="href" placeholder="如果不为空使用该链接" value="${(content.href?html)?if_exists}"/>
        </div>
        <div class="form-group">
          <label for="data">内容</label>
          <textarea  id="data" name="data" rows=60" cols="60">${(content.description?html)?if_exists}</textarea>
        </div>		
<script language="javascript">    
var editor = CKEDITOR.replace('data',{ 
filebrowserImageBrowseUrl : 'do?sc=${doc.metadata.path}&ac=ckbrowser',  
filebrowserFlashBrowseUrl : 'do?sc=${doc.metadata.path}&ac=ckbrowser',
allowedContent : true  
        });
    editor.on('instanceReady',   // editor是CKEDITOR的一个实例
                 function(event){
                      //event.editor.getCommand('maximize').exec();
                  }
    );    

    //var oFCKeditor = new FCKeditor( 'data' ) ;    
    //oFCKeditor.BasePath = "fckeditor/";
    //oFCKeditor.ToolbarSet="${(requestBean.toolbarSet)?default("SwtEdit")}";
    //oFCKeditor.ReplaceTextarea() ;         
</script>
    <input type="button" class="btn btn-primary" value="保存并返回" onclick= "saveAndReturnf()"/>
    <input type="submit" class="btn btn-info" value="保存"/>
    <input type="button" class="btn btn-warning"  value="删除" onclick="removePage()"/>   
    <#if content.parent.thingName != "OrgDocument">
    <input type="button" class="btn btn-default"  value="移动" onclick="movePage()"/>   
    </#if>
    <input type="button" class="btn btn-default"  value="全屏" onclick="editor.getCommand('maximize').exec()"/>   
    <input type="button" class="btn btn-default" value="返回" onclick= "back()"/>
    </form>
<script src="${request.contextPath}/js/bootstrap-3.3.5/js/bootstrap.min.js"></script>
</body>
</html>