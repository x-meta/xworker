<html>
<head>
<title>${doc.title?if_exists}</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
<!--
form {
	margin: 0px;
}
.label-title {
	font-family: "Verdana", "Arial", "Helvetica", "sans-serif";
	font-size: 24pt;
	font-weight: bold; 
	color: #333333;
	text-decoration: none;
}
.label-login {
	font-family: "Verdana", "Arial", "Helvetica", "sans-serif";
	font-size: 12pt;
	font-weight: bold; 
	color: #333333;
	text-decoration: none;
}
.input-1 {
	font-family: "Verdana", "Arial", "Helvetica", "sans-serif";
	font-size: 12px;
	color: #000000;
	border: 1px solid #AAAAAA;
	background-color: #FFFFFF;
	height: 20px;
}
.input-2 {
	font-family: "Verdana", "Arial", "Helvetica", "sans-serif";
	font-size: 12px;
	color: #333333;
	padding-top: 2px;
	width: 60px;
}
.input-3 {
	font-family: "Verdana", "Arial", "Helvetica", "sans-serif";
	font-size: 12px;
	color: #333333;
	padding-top: 2px;
	padding-bottom: 5px;
	vertical-align: middle;
	align:right;
}

-->
</style>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="100" marginwidth="0" marginheight="0">
<table width="600"  border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td nowrap class="label-title" align="center">${doc.title?if_exists}</td>
  </tr>
</table>
<table width="600"  height="1" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#000000">
  <tr>
    <td nowrap height="1"></td>
  </tr>
</table>
<table width="600"  border="0" align="center" cellpadding="0" cellspacing="0">
  <tr width="50%">
    <td nowrap width="300" rowspan="2"><img src="${doc.logo?if_exists}" width="300" height="216"></td>
  </tr>
  <tr width="50%">
  <td nowrap>
<form name="nwLoginForm" method="POST" action="do?sc=${doc.metadata.path}">
    <input type="hidden" name="ac" value="checkLogin"/>    
	<table width="100%"  border="0" cellpadding="0" cellspacing="5">
	    <tr>
		  <td nowrap height="18" align="right"><img src="${(request.contextPath)?if_exists}/images/login.jpg"></td>
		  <td nowrap height="18" class="label-login">用户登录</td>
	     </tr>
		  <#if message?exists>
		  <tr>
				<td nowrap height="18" colspan="2" class="login_text">
					<span style="color:red;" class="input-3" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;${message?if_exists}</span>
				</td>
		 </tr>
		  </#if>
	      <tr>
	        <td nowrap width="30%" height="18" align="right" class="input-3">用户名:</td>
	        <td nowrap width="70%" height="18"><input type="text" name="name" maxlength="30" size="20" value="" id="name" class="input-1"></td>
	      </tr>
	      <tr>
	        <td nowrap width="30%" height="18" align="right" class="input-3">密 码:</td>
	        <td nowrap width="70%" height="18"><input type="password" name="password" maxlength="30" size="20" value="" class="input-1"></td>
	      </tr>
	      <tr>
	        <td nowrap>&nbsp;</td>
	        <td nowrap height="18">
	        	<input name="Submit3" type="submit" value="  登录  " class="input-2">     	
	        </td>
	      </tr>
    	</table>
</form>
     </td>
  </tr>
</table>
<table width="600"  height="1" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#000000">
  <tr>
    <td nowrap height="1"></td>
  </tr>
</table>
<table cellpadding='0' cellspacing='0' width='100%' border='0'>
	<tr>
		<td nowrap height="50" align='center' class='input-3'>&copy; ${doc.company?if_exists}
		</td>
	</tr>
</table>
</body>
<script language="javascript">
nwLoginForm.name.focus();
</script>
</html>