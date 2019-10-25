<html>
<head><meta http-equiv="content-type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shCore.css" /> 
<link rel="stylesheet" type="text/css" media="all" href="${request.contextPath}/js/syntaxhighlighter/styles/shThemeEclipse.css" /> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shCore.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/syntaxhighlighter/scripts/shAutoloader.js"></script> 
<script type="text/javascript" src="${request.contextPath}/js/jquery/jquery-2.0.3.min.js"></script> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap.min.css" /> 
<link rel="stylesheet" href="${request.contextPath}/js/bootstrap-3.3.5/css/bootstrap-theme.min.css"/>
<title>编辑文档</title>
</head>
<body >
<div class="container">
  <div class="row">
    <div class="">
      <form class="" action="do" name="form">
        <input type="hidden" name="sc" value="${doc.metadata.path}"/>
        <input type="hidden" name="ac" value="saveDoc"/>
          <label for="title">标题</label>
          <input type="text" class="form-control" id="title" name="title" placeholder="文档的标题" value="${doc.title?if_exists}"/>
        </div>
        <div class="form-group">
          <label for="icon">图标</label>
          <input type="text" class="form-control" id="icon" name="icon" placeholder="文档的图标" value="${doc.icon?if_exists}"/>
        </div>
        <div class="form-group">
          <label for="buttom">底部代码</label>
          <textarea type="textarea"  class="form-control" id="buttom" name="bottomV"  rows="10"> ${(doc.bottom?html)?if_exists}</textarea>
        </div>
      
        <input class="btn btn-primary" type="submit" value="确定"></input>       
        <input type="button" class="btn btn-default" value="返回" onclick= "document.location='do?sc=${doc.metadata.path}'"/>
      </form>
    </div>
  </div>
</div>
<script src="${request.contextPath}/js/bootstrap-3.3.5/js/bootstrap.min.js"></script>
 </body>
</html>