<!DOCTYPE html>
<!-- 
Template Name: Metronic - Responsive Admin Dashboard Template build with Twitter Bootstrap 2.3.1
Version: 1.3
Author: KeenThemes
Website: http://www.keenthemes.com/preview/?theme=metronic
Purchase: http://themeforest.net/item/metronic-responsive-admin-dashboard-template/4021469
-->
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!--> <html lang="en" class="no-js"> <!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
	<meta charset="utf-8" />
	<title>${data.title?if_exists}</title>
	<meta content="width=device-width, initial-scale=1.0" name="viewport" />
	<meta content="" name="description" />
	<meta content="" name="author" />
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
	<link href="${data.basePath?if_exists}/css/jquery.gritter.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/daterangepicker.css" rel="stylesheet" type="text/css" />
	<link href="${data.basePath?if_exists}/css/fullcalendar.css" rel="stylesheet" type="text/css"/>
	<link href="${data.basePath?if_exists}/css/jqvmap.css" rel="stylesheet" type="text/css" media="screen"/>
	<link href="${data.basePath?if_exists}/css/jquery.easy-pie-chart.css" rel="stylesheet" type="text/css" media="screen"/>
	<!-- END PAGE LEVEL STYLES -->
	<link rel="shortcut icon" href="${data.basePath?if_exists}/image/favicon.ico" />
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="page-header-fixed">
	<!-- BEGIN HEADER -->
	<div class="header navbar navbar-inverse navbar-fixed-top">
		<!-- BEGIN TOP NAVIGATION BAR -->
		<div class="navbar-inner">
			<div class="container-fluid">
				<!-- BEGIN LOGO -->
				<a class="brand" href="${data.logoUrl?if_exists}">
				<img src="${data.basePath?if_exists}${data.logoPath?if_exists}" alt="logo"/>
				</a>
				<!-- END LOGO -->
				<#if topNavbar?exists>
				<!-- BEGIN HORIZANTAL MENU -->
				<div class="navbar hor-menu hidden-phone hidden-tablet">
                    <div class="navbar-inner">
                        ${topNavbar?if_exists}
                    </div>
                </div>
                <!-- END HORIZANTAL MENU -->
				</#if>
				<!-- BEGIN RESPONSIVE MENU TOGGLER -->
				<a href="javascript:;" class="btn-navbar collapsed" data-toggle="collapse" data-target=".nav-collapse">
				<img src="${data.basePath?if_exists}/image/menu-toggler.png" alt="" />
				</a>          
				<!-- END RESPONSIVE MENU TOGGLER -->            
				<#if rightNavbar?exists>
				<!-- BEGIN TOP NAVIGATION MENU -->              			
				${rightNavbar?if_exists}
				<!-- END TOP NAVIGATION MENU --> 
				</#if>
			</div>
		</div>
		<!-- END TOP NAVIGATION BAR -->
	</div>
	<!-- END HEADER -->
	<!-- BEGIN CONTAINER -->
	<div class="page-container">
		<!-- BEGIN SIDEBAR -->
		<div class="page-sidebar nav-collapse collapse">
			<!-- BEGIN SIDEBAR MENU -->        
			<ul class="page-sidebar-menu">
				<li>
					<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
					<div class="sidebar-toggler hidden-phone"></div>
					<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
				</li>
				<#if data.getBoolean("hasSearch")>
				<li>
					<!-- BEGIN RESPONSIVE QUICK SEARCH FORM -->
					<form class="sidebar-search" action="${data.searchAction?if_exists}">
						<div class="input-box">
							<a href="javascript:;" class="remove"></a>
							<input type="text" placeholder="${data.searchLabel?if_exists}" />
							<input type="button" class="submit" value=" " />
						</div>
					</form>
					<!-- END RESPONSIVE QUICK SEARCH FORM -->
				</li>
                </#if>
                <${r"#"}list ${data.menuVar} as menu>
                <#noparse>
				<li class="<#if menu.start?exists && menu.start>start</#if><#if menu.active?exists && menu.active> active</#if>">
					<a href="${menu.href?if_exists}">
					<i class="${menu.iconClass?if_exists}"></i> 
					<span class="title">${menu.title?if_exists}</span>
					<#if menu.selected?exists && menu.selected>
					<span class="selected"></span>
					</#if>
					<#if menu.childs?exists>
					<span class="arrow <#if menu.selected?exists && menu.selected>open</#if>"></span>
					</#if>
					</a>
					<#if menu.childs?exists>
					<ul class="sub-menu">
					<#list menu.childs as child>
					    <li class="<#if child.active?exists && child.active> active</#if>">
					        <a href="${child.href?if_exists}">${child.title?if_exists}</a>
					    </li>
					</#list>
					</ul>
                    </#if>
				</li>
				</#noparse>
                </${r"#"}list>
			</ul>
			<!-- END SIDEBAR MENU -->
		</div>
		<!-- END SIDEBAR -->
		<!-- BEGIN PAGE -->
		<div class="page-content">
			<!-- BEGIN SAMPLE PORTLET CONFIGURATION MODAL FORM-->
			<div id="portlet-config" class="modal hide">
				<div class="modal-header">
					<button data-dismiss="modal" class="close" type="button"></button>
					<h3>Widget Settings</h3>
				</div>
				<div class="modal-body">
					Widget settings form goes here
				</div>
			</div>
			<!-- END SAMPLE PORTLET CONFIGURATION MODAL FORM-->
			<!-- BEGIN PAGE CONTAINER-->
			<div class="container-fluid">
				<!-- BEGIN PAGE HEADER-->
				<div class="row-fluid">				   
					<div class="span12">
					     <#if data.getBoolean("hasSetting")>
						<!-- BEGIN STYLE CUSTOMIZER -->
						<div class="color-panel hidden-phone">
							<div class="color-mode-icons icon-color"></div>
							<div class="color-mode-icons icon-color-close"></div>
							<div class="color-mode">
								<p>THEME COLOR</p>
								<ul class="inline">
									<li class="color-black current color-default" data-style="default"></li>
									<li class="color-blue" data-style="blue"></li>
									<li class="color-brown" data-style="brown"></li>
									<li class="color-purple" data-style="purple"></li>
									<li class="color-grey" data-style="grey"></li>
									<li class="color-white color-light" data-style="light"></li>
								</ul>
								<label>
									<span>Layout</span>
									<select class="layout-option m-wrap small">
										<option value="fluid" selected>Fluid</option>
										<option value="boxed">Boxed</option>
									</select>
								</label>
								<label>
									<span>Header</span>
									<select class="header-option m-wrap small">
										<option value="fixed" selected>Fixed</option>
										<option value="default">Default</option>
									</select>
								</label>
								<label>
									<span>Sidebar</span>
									<select class="sidebar-option m-wrap small">
										<option value="fixed">Fixed</option>
										<option value="default" selected>Default</option>
									</select>
								</label>
								<label>
									<span>Footer</span>
									<select class="footer-option m-wrap small">
										<option value="fixed">Fixed</option>
										<option value="default" selected>Default</option>
									</select>
								</label>
							</div>
						</div>
						<!-- END BEGIN STYLE CUSTOMIZER -->    
						</#if>
						<!-- BEGIN PAGE TITLE & BREADCRUMB-->
						<h3 class="page-title">
							${data.contentTitle?if_exists}
						</h3>
						<#if data.navLinksVar?exists>
						<#noparse>
						<ul class="breadcrumb">
  						    <#list navLinksVar as link>
							<li>
							    <#if link_index == 0>
								<i class="icon-home"></i>
								</#if>
								<a href="${link.href?if_exists}">${link.title?if_exists}</a> 
								<#if link_has_next>
								<i class="icon-angle-right"></i>
								</#if>
							</li>
							</#list>
						</ul>
						</#noparse>
						</#if>
						<!-- END PAGE TITLE & BREADCRUMB-->
					</div>
				</div>
				<!-- END PAGE HEADER-->
				<div id="dashboard">
                    ${mainPage?if_exists}
				</div>
			</div>
			<!-- END PAGE CONTAINER-->    
		</div>
		<!-- END PAGE -->
	</div>
	<!-- END CONTAINER -->
	<!-- BEGIN FOOTER -->
	<div class="footer">
		<div class="footer-inner">
			${data.copyRight?if_exists}
		</div>
		<div class="footer-tools">
			<span class="go-top">
			<i class="icon-angle-up"></i>
			</span>
		</div>
	</div>
	<!-- END FOOTER -->
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
	<script src="${data.basePath?if_exists}/js/jquery.uniform.min.js" type="text/javascript" ></script>
	<!-- END CORE PLUGINS -->
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script src=${data.basePath?if_exists}/js/jquery.vmap.js" type="text/javascript"></script>   
	<script src="${data.basePath?if_exists}/js/jquery.vmap.russia.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.vmap.world.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.vmap.europe.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.vmap.germany.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.vmap.usa.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.vmap.sampledata.js" type="text/javascript"></script>  
	<script src="${data.basePath?if_exists}/js/jquery.flot.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.flot.resize.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.pulsate.min.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/date.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/daterangepicker.js" type="text/javascript"></script>     
	<script src="${data.basePath?if_exists}/js/jquery.gritter.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/fullcalendar.min.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/jquery.easy-pie-chart.js" type="text/javascript"></script>
	<script src=${data.basePath?if_exists}/js/jquery.sparkline.min.js" type="text/javascript"></script>  
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="${data.basePath?if_exists}/js/app.js" type="text/javascript"></script>
	<script src="${data.basePath?if_exists}/js/index.js" type="text/javascript"></script>        
	<!-- END PAGE LEVEL SCRIPTS -->  
	<script>
		jQuery(document).ready(function() {    
		   App.init(); // initlayout and core plugins
		});
	</script>
	<!-- END JAVASCRIPTS -->
<script type="text/javascript">  var _gaq = _gaq || [];  _gaq.push(['_setAccount', 'UA-37564768-1']);  _gaq.push(['_setDomainName', 'keenthemes.com']);  _gaq.push(['_setAllowLinker', true]);  _gaq.push(['_trackPageview']);  (function() {    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;    ga.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'stats.g.doubleclick.net/dc.js';    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);  })();</script></body>
<!-- END BODY -->

</html>