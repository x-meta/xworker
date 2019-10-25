<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9"> <![endif]-->
<!--[if !IE]><!--> <html lang="en"> <!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
	<meta charset="utf-8" />
	<title>${data.title?if_exists}</title>
	<meta content="width=device-width, initial-scale=1.0" name="viewport" />
	<meta content="${data.description?if_exists}" name="description" />
	<meta content="${data.author?if_exists}" name="author" />
	<!-- BEGIN GLOBAL MANDATORY STYLES -->
	<link href="${data.basePath?if_exists}/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/style-metro.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/style.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/style-responsive.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/default.css" rel="stylesheet" type="text/css" id="style_color"/>
	<link href="${data.basePath?if_exists}/css/uniform.default.css" rel="stylesheet" type="text/css"/>
	<!-- END GLOBAL MANDATORY STYLES -->
	<!-- BEGIN PAGE LEVEL STYLES -->
	<link href="${data.basePath?if_exists}/css/login.css" rel="stylesheet" type="text/css"/>
	<!-- END PAGE LEVEL STYLES -->
	<link rel="shortcut icon" href="${data.basePath?if_exists}/image/favicon.ico" />
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="login">
	<!-- BEGIN LOGO -->
	<div class="logo">
		<img src="${data.basePath?if_exists}${data.logoPath?if_exists}" alt="" /> 
	</div>
	<!-- END LOGO -->
	<!-- BEGIN LOGIN -->
	<div class="content">
		<!-- BEGIN LOGIN FORM -->
		<form class="form-vertical login-form" action="${data.loginAction?if_exists}">
			<h3 class="form-title">${data.loginTitle?if_exists}</h3>
			<div class="alert alert-error hide">
				<button class="close" data-dismiss="alert"></button>
				<span>${data.loginErrorTip?if_exists}</span>
			</div>
			<div class="control-group">
				<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
				<label class="control-label visible-ie8 visible-ie9">${data.userNameLabel?if_exists}</label>
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-user"></i>
						<input class="m-wrap placeholder-no-fix" type="text" placeholder="${data.userNameLabel?if_exists}" name="username"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label visible-ie8 visible-ie9">${data.passwordLabel?if_exists}</label>
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-lock"></i>
						<input class="m-wrap placeholder-no-fix" type="password" placeholder="${data.passwordLabel?if_exists}" name="password"/>
					</div>
				</div>
			</div>
			<div class="form-actions">
			   <#if data.getBoolean("hasRememberMe") == true>
				<label class="checkbox">
				<input type="checkbox" name="remember" value="1"/> ${data.rememberLabel?if_exists}
				</label>
                </#if>
				<button type="submit" class="btn green pull-right">
				${data.loginLabel?if_exists} <i class="m-icon-swapright m-icon-white"></i>
				</button>            
			</div>
			<#if data.getBoolean("hasForgetPassword") == true>
			<div class="forget-password">
				<h4>${data.forgetPasswordTitle?if_exists}</h4>
				<p>
					${data.fp_tip?if_exists}
				</p>
			</div>
			</#if>
			<#if data.getBoolean("hasCreateAcount") == true>
			<div class="create-account">
				<p>
					${data.cc_tip?if_exists}
				</p>
			</div>
			</#if>
		</form>
		<!-- END LOGIN FORM -->        
		<!-- BEGIN FORGOT PASSWORD FORM -->
		<form class="form-vertical forget-form" action="${data.fp_action?if_exists}">
			<h3 class="">${data.forgetPasswordTitle?if_exists}</h3>
			<p>${data.fp_emailTip?if_exists}</p>
			<div class="control-group">
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-envelope"></i>
						<input class="m-wrap placeholder-no-fix" type="text" placeholder="${data.fp_emailLabel?if_exists}" name="email" />
					</div>
				</div>
			</div>
			<div class="form-actions">
				<button type="button" id="back-btn" class="btn">
				<i class="m-icon-swapleft"></i>${data.fp_backLabel?if_exists}
				</button>
				<button type="submit" class="btn green pull-right">
				${data.fp_submitLabel?if_exists} <i class="m-icon-swapright m-icon-white"></i>
				</button>            
			</div>
		</form>
		<!-- END FORGOT PASSWORD FORM -->
		<!-- BEGIN REGISTRATION FORM -->
		<form class="form-vertical register-form" action="${data.cc_action?if_exists}">
			<h3 class="">${data.cc_title?if_exists}</h3>
			<p>${data.cc_enterTip?if_exists}</p>
			<div class="control-group">
				<label class="control-label visible-ie8 visible-ie9">{data.userNameLabel?if_exists}</label>
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-user"></i>
						<input class="m-wrap placeholder-no-fix" type="text" placeholder="${data.userNameLabel?if_exists}" name="username"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label visible-ie8 visible-ie9">${data.passwordLabel?if_exists}</label>
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-lock"></i>
						<input class="m-wrap placeholder-no-fix" type="password" id="register_password" placeholder="${data.passwordLabel?if_exists}" name="password"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label visible-ie8 visible-ie9">${data.cc_rePassword?if_exists}</label>
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-ok"></i>
						<input class="m-wrap placeholder-no-fix" type="password" placeholder="${data.cc_rePassword?if_exists}" name="rpassword"/>
					</div>
				</div>
			</div>
			<div class="control-group">
				<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
				<label class="control-label visible-ie8 visible-ie9">${data.fp_emailLabel?if_exists}</label>
				<div class="controls">
					<div class="input-icon left">
						<i class="icon-envelope"></i>
						<input class="m-wrap placeholder-no-fix" type="text" placeholder="${data.fp_emailLabel?if_exists}" name="email"/>
					</div>
				</div>
			</div>
			<#if data.getBoolean("cc_hasLiscense")>
			<div class="control-group">
				<div class="controls">
					<label class="checkbox">
					<input type="checkbox" name="tnc"/> ${data.cc_liscenseTip?if_exists}
					</label>  
					<div id="register_tnc_error"></div>
     			</div>
			</div>
			</#if>
			<div class="form-actions">
				<button id="register-back-btn" type="button" class="btn">
				<i class="m-icon-swapleft"></i>  ${data.cc_backLabel?if_exists}
				</button>
				<button type="submit" id="register-submit-btn" class="btn green pull-right">
				${data.cc_submitLabel?if_exists} <i class="m-icon-swapright m-icon-white"></i>
				</button>            
			</div>
		</form>
		<!-- END REGISTRATION FORM -->
	</div>
	<!-- END LOGIN -->
	<!-- BEGIN COPYRIGHT -->
	<div class="copyright">
		${data.trademark?if_exists}
	</div>
	<!-- END COPYRIGHT -->
	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<!-- BEGIN CORE PLUGINS -->
	<script src="${data.basePath?if_exists}/js/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
	<!-- IMPORTANT! Load jquery-ui-1.10.1.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
	<script src="${data.basePath?if_exists}/js/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>      
	<script src="${data.basePath?if_exists}/js/bootstrap.min.js" type="text/javascript"></script>
	<!--[if lt IE 9]>
	<script src="${data.basePath?if_exists}/js/excanvas.min.js"></script>
	<script src="${data.basePath?if_exists}/js/respond.min.js"></script>  
	<![endif]-->   
	<script src="${data.basePath?if_exists}/js/jquery.slimscroll.min.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.blockui.min.js" type="text/javascript"></script>  
	<script src="${data.basePath?if_exists}/js/jquery.cookie.min.js" type="text/javascript"></script>
	<script src=${data.basePath?if_exists}/js/jquery.uniform.min.js" type="text/javascript" ></script>
	<!-- END CORE PLUGINS -->
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script src="${data.basePath?if_exists}/js/jquery.validate.min.js" type="text/javascript"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="${data.basePath?if_exists}/js/app.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/login.js" type="text/javascript"></script>      
	<!-- END PAGE LEVEL SCRIPTS --> 
	<script>
		jQuery(document).ready(function() {     
		  App.init();
		  Login.init();
		});
	</script>
	<!-- END JAVASCRIPTS -->
<script type="text/javascript">  var _gaq = _gaq || [];  _gaq.push(['_setAccount', 'UA-37564768-1']);  _gaq.push(['_setDomainName', 'keenthemes.com']);  _gaq.push(['_setAllowLinker', true]);  _gaq.push(['_trackPageview']);  (function() {    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;    ga.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'stats.g.doubleclick.net/dc.js';    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);  })();</script></body>
<!-- END BODY -->
</html>