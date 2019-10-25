jQuery.noConflict();

var ebadusmenu={

effectduration: 0, //出现动画的时间 毫秒
delaytimer: 200, //菜单隐藏时间  毫秒

//这里不要编辑
badugmenulabels: [],
badugmenus: [],
zIndexVal: 1000, //从 z-index 后显示下拉菜单
$shimobj: null,

addshim:function($){
	$(document.getElementById("headerb")).append('<IFRAME id="outlineiframeshim" src="'+(location.protocol=="https:"? 'blank.htm' : 'about:blank')+'" style="display:none; left:0; top:0; z-index:999; position:absolute; filter:progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)" frameBorder="0" scrolling="no"></IFRAME>')
	this.$shimobj=$("#outlineiframeshim")
},

alignmenu:function($, e, badugmenu_pos){
	var badugmenu=this.badugmenus[badugmenu_pos]
	var $anchor=badugmenu.$anchorobj
	var $menu=badugmenu.$menuobj
	var menuleft=($(window).width()-(badugmenu.offsetx-$(document).scrollLeft())>badugmenu.actualwidth)? badugmenu.offsetx : badugmenu.offsetx-badugmenu.actualwidth+badugmenu.anchorwidth //获取菜单的坐标
	//var menutop=($(window).height()-(badugmenu.offsety-$(document).scrollTop()+badugmenu.anchorheight)>badugmenu.actualheight)? badugmenu.offsety+badugmenu.anchorheight : badugmenu.offsety-badugmenu.actualheight
	//var menutop=badugmenu.offsety+badugmenu.anchorheight  //获取菜单的y值
 	//$menu.css({left:menuleft+"px", top:menutop+"px"})
 	//$menu.css({left:menuleft+"px"})
	//this.$shimobj.css({width:badugmenu.actualwidth+"px", height:badugmenu.actualheight+"px", left:menuleft+"px", top:menutop+"px", display:"block"})
},

showmenu:function(e, badugmenu_pos){
	var badugmenu=this.badugmenus[badugmenu_pos]
	var $menu=badugmenu.$menuobj
	var $menuinner=badugmenu.$menuinner
	if ($menu.css("display")=="none"){
		this.alignmenu(jQuery, e, badugmenu_pos)
		$menu.css("z-index", ++this.zIndexVal)
		$menu.show(this.effectduration, function(){
			$menuinner.css('visibility', 'visible')
		})
	}
	else if ($menu.css("display")=="block" && e.type=="click"){ //如果菜单是隐藏的，这是一个"点击"事件（或"鼠标离开状态"）
		this.hidemenu(e, badugmenu_pos)
	}
	return false
},

hidemenu:function(e, badugmenu_pos){
	var badugmenu=this.badugmenus[badugmenu_pos]
	var $menu=badugmenu.$menuobj
	var $menuinner=badugmenu.$menuinner
	$menuinner.css('visibility', 'hidden')
	this.$shimobj.css({display:"none", left:0, top:0})
	$menu.hide(this.effectduration)
},

definemenu:function(anchorid, menuid, revealtype){
	this.badugmenulabels.push([anchorid, menuid, revealtype])
},

render:function($){
	for (var i=0, labels=this.badugmenulabels[i]; i<this.badugmenulabels.length; i++, labels=this.badugmenulabels[i]){
		if ($('#'+labels[0]).length!=1 || $('#'+labels[1]).length!=1) //当两个元素不明确的时候存在
			return
		this.badugmenus.push({$anchorobj:$("#"+labels[0]), $menuobj:$("#"+labels[1]), $menuinner:$("#"+labels[1]).children('ul:first-child'), revealtype:labels[2], hidetimer:null})
		var badugmenu=this.badugmenus[i]	
		badugmenu.$anchorobj.add(badugmenu.$menuobj).attr("_badugmenupos", i+"pos") //记住下拉菜单的历史
		badugmenu.actualwidth=badugmenu.$menuobj.outerWidth()
		badugmenu.actualheight=badugmenu.$menuobj.outerHeight()
		badugmenu.offsetx=badugmenu.$anchorobj.offset().left
		badugmenu.offsety=badugmenu.$anchorobj.offset().top
		badugmenu.anchorwidth=badugmenu.$anchorobj.outerWidth()
		badugmenu.anchorheight=badugmenu.$anchorobj.outerHeight()
		$(document.getElementById("headerb")).append(badugmenu.$menuobj) //下拉菜单移动结束
		badugmenu.$menuobj.css("z-index", ++this.zIndexVal).hide()
		badugmenu.$menuinner.css("visibility", "hidden")
		badugmenu.$anchorobj.bind(badugmenu.revealtype=="click"? "click" : "mouseenter", function(e){
			var menuinfo=ebadusmenu.badugmenus[parseInt(this.getAttribute("_badugmenupos"))]
			clearTimeout(menuinfo.hidetimer) 
			return ebadusmenu.showmenu(e, parseInt(this.getAttribute("_badugmenupos")))
		})
		badugmenu.$anchorobj.bind("mouseleave", function(e){
			var menuinfo=ebadusmenu.badugmenus[parseInt(this.getAttribute("_badugmenupos"))]
			if (e.relatedTarget!=menuinfo.$menuobj.get(0) && $(e.relatedTarget).parents("#"+menuinfo.$menuobj.get(0).id).length==0){ //检查鼠标有没有进入下拉菜单里面
				menuinfo.hidetimer=setTimeout(function(){ //添加延迟显示的隐藏菜单
					ebadusmenu.hidemenu(e, parseInt(menuinfo.$menuobj.get(0).getAttribute("_badugmenupos")))
				}, ebadusmenu.delaytimer)
			}
		})
		badugmenu.$menuobj.bind("mouseenter", function(e){
			var menuinfo=ebadusmenu.badugmenus[parseInt(this.getAttribute("_badugmenupos"))]
			clearTimeout(menuinfo.hidetimer) 
		})
		badugmenu.$menuobj.bind("click mouseleave", function(e){
			var menuinfo=ebadusmenu.badugmenus[parseInt(this.getAttribute("_badugmenupos"))]
			menuinfo.hidetimer=setTimeout(function(){
				ebadusmenu.hidemenu(e, parseInt(menuinfo.$menuobj.get(0).getAttribute("_badugmenupos")))
			}, ebadusmenu.delaytimer)
		})
	} //end
	if(/Safari/i.test(navigator.userAgent)){ //if Safari
		$(window).bind("resize load", function(){
			for (var i=0; i<ebadusmenu.badugmenus.length; i++){
				var badugmenu=ebadusmenu.badugmenus[i]
				var $anchorisimg=(badugmenu.$anchorobj.children().length==1 && badugmenu.$anchorobj.children().eq(0).is('img'))? badugmenu.$anchorobj.children().eq(0) : null
				if ($anchorisimg){ //使用此菜单请保留版权信息  http://www.ebadu.net
					badugmenu.offsetx=$anchorisimg.offset().left
					badugmenu.offsety=$anchorisimg.offset().top
					badugmenu.anchorwidth=$anchorisimg.width()
					badugmenu.anchorheight=$anchorisimg.height()
				}
			}
		})
	}
	else{
		$(window).bind("resize", function(){
			for (var i=0; i<ebadusmenu.badugmenus.length; i++){
				var badugmenu=ebadusmenu.badugmenus[i]	
				badugmenu.offsetx=badugmenu.$anchorobj.offset().left
				badugmenu.offsety=badugmenu.$anchorobj.offset().top
			}
		})
	}
	ebadusmenu.addshim($)
}

}

jQuery(document).ready(function($){
	ebadusmenu.render($)
})