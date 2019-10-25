package xworker.app.orgweb;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilThing;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.DbUtil;

public class OrgwebActions {
	public static final String orgwebSessionName = "__orgWebAdmin__";
	
	public static boolean isLogined(HttpServletRequest request){
		return request.getSession().getAttribute(orgwebSessionName) != null;
	}
	
	public static String handleAction(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext) throws IOException{
		//动作
		String action = request.getParameter("action");
		if(action != null){
			action = action.toLowerCase();
			if("login".equals(action)){
				response.sendRedirect("do?sc=xworker.app.orgweb.web.admin.AdminLogin");
				return "login";
			}else if("logout".equals(action)){
				request.getSession().setAttribute(orgwebSessionName, null);
			}
		}
		//是否退出
		if("true".equals(request.getParameter("logout"))){
			request.getSession().setAttribute(orgwebSessionName, null);
		}
		
		boolean logined = isLogined(request);
		if(logined && "true".equals(request.getParameter("refresh"))){
			ContentTreeManager.loadCache();
		}
		
		return null;
	}
	
	
	/**
	 * 初始化语言。
	 * 
	 * @param request
	 * @param response
	 * @param actionContext
	 */
	public static void initLanguage(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){		
		String langReverse = request.getParameter("lang");
		String lang = (String) request.getSession().getAttribute("__org_web_lang__");
		String setted = (String) request.getSession().getAttribute("__org_web_lang_setted__");
		if("reverse".equals(langReverse)){
			if(lang == null){
				lang = "en";
			}else{
				lang = null;
			}
			request.getSession().setAttribute("__org_web_lang__", lang);
			request.getSession().setAttribute("__org_web_lang_setted__", "true");
		}
		
		if("en".equals(lang) || "en".equals(langReverse)){
			actionContext.getScope(0).put("lang", "en");
			request.getSession().setAttribute("__org_web_lang_setted__", "true");
			ContentTree.setLang(lang);
		}else{
			if(setted == null) {
				//从Request中
				Locale locale = request.getLocale();
				if(locale != null && locale.getLanguage().equals("zh")) {
					//中文
					request.getSession().setAttribute("__org_web_lang_setted__", "true");
					ContentTree.setLang(null);
				}else {
					//其它默认英文
					actionContext.getScope(0).put("lang", "en");
					ContentTree.setLang("en");
				}
			}else {
				ContentTree.setLang(null);
			}
		}
	}
	
	public static ContentTree getContentTree(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){	
		String id = request.getParameter("id");

		//0是默认的根，总是存在的虚拟机的内容
		if(id == null || "".equals(id)){
			id = "0";
		}
		
		int tid = Integer.parseInt(id);
		ContentTree contentTree = ContentTreeManager.getContentTree(tid);
		if(contentTree == null){
			contentTree = ContentTreeManager.getAContentTree();
		}
		
		return contentTree;
	}
	
	public static void handleRoot(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		actionContext.put("contentTree", contentTree);
		actionContext.put("iconLogin", false);
		
		actionContext.put("templatePath", "xworker/app/orgweb/web/root.ftl");
		
	}
	
	public static void handleProject(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		actionContext.put("contentTree", contentTree);
		actionContext.put("iconLogin", false);
		
		actionContext.put("templatePath", "xworker/app/orgweb/web/projectPage.ftl");
		
	}
	
	public static void handlePage(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		actionContext.put("topRightMenu", contentTree.getRoot().getTopRightMenu());
		actionContext.put("indexPaths", contentTree.indexPaths);		
		actionContext.put("contentTree", contentTree);
		actionContext.put("menuSets", contentTree.getRoot());
		actionContext.put("iconLogin", false);
		actionContext.put("templatePath", "xworker/app/orgweb/web/page.ftl");
		actionContext.put("contentHtml", contentTree.getHtml(actionContext));
		actionContext.put("content", contentTree);
	}
	
	public static void handleUrl(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		
	}

	public static void handleTopic(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){				
		actionContext.put("topRightMenu", contentTree.getRoot().getTopRightMenu());
		actionContext.put("indexPaths", contentTree.getRoot().indexPaths);		

		List<ContentTree> childs = contentTree.getChilds();
		if(childs.size() > 0 && childs.get(0).getChilds().size() > 0){
			ContentTree childTree = childs.get(0).getChilds().get(0);
			if(childTree.getType() == ContentTree.TYPE_PAGE){
				contentTree = childTree;
			}
		}
		actionContext.put("contentTree", contentTree);
		actionContext.put("iconLogin", false);
		actionContext.put("templatePath", "xworker/app/orgweb/web/topicPage.ftl");
		actionContext.put("content", contentTree.getHtml(actionContext));		
	}
	
	
	public static void handleDirectory(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		actionContext.put("topRightMenu", contentTree.getRoot().getTopRightMenu());
		actionContext.put("indexPaths", contentTree.indexPaths);		
		actionContext.put("contentTree", contentTree);
		actionContext.put("iconLogin", false);
		actionContext.put("templatePath", "xworker/app/orgweb/web/directoryPage.ftl");
		actionContext.put("contentHtml", contentTree.getHtml(actionContext));
		actionContext.put("content", contentTree);
	}
	
	public static void handlePageList(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){			
		actionContext.put("topRightMenu", contentTree.getRoot().getTopRightMenu());
		actionContext.put("indexPaths", contentTree.indexPaths);		
		actionContext.put("contentTree", contentTree);
		actionContext.put("menuSets", contentTree.getRoot());
		actionContext.put("iconLogin", false);
		actionContext.put("templatePath", "xworker/app/orgweb/web/pageList.ftl");
		actionContext.put("contentHtml", contentTree.getHtml(actionContext));
		actionContext.put("content", contentTree);
		
		//分页信息
		int currentPage = 0;
		try{
			currentPage = Integer.parseInt(request.getParameter("page"));
		}catch(Exception e){			
		}
		List<ContentTree> childs = contentTree.getChilds();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setTotalCount(childs.size());
		pageInfo.setPage(currentPage);
		pageInfo.setPageSize(10);
	}
	
	public static void handlePageListPage(ContentTree contentTree, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		
	}
	
	/**
	 * WEB动作。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static String doAction(ActionContext actionContext) throws IOException{
		//ContentTreeManager.loadCache(actionContext);
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
	
		//处理动作
		String actionResult = handleAction(request, response, actionContext);
		if(actionResult != null){
			return actionResult;
		}

		boolean logined = isLogined(request);
		actionContext.put("logined", logined);
		ContentTree.setLogin(logined);
		
		//orgweb设置，如果存在
		actionContext.g().put("webConfig", World.getInstance().getThing("_local.xworker.config.OrgwebConfig"));
		
		//-----------初始化语言--------------
		initLanguage(request, response, actionContext);
		
		//----------获取节点----------------
		ContentTree contentTree = getContentTree(request, response, actionContext);
		
		//根节点和项目节点不受类型限制
		if(contentTree.parent == null){
			handleRoot(contentTree, request, response, actionContext);
			return "success";
		}else if(contentTree.parentId == 0){
			handleProject(contentTree, request, response, actionContext);
			return "success";
		}
		
		//根据类型来处理页面
		switch(contentTree.getType()){
		case ContentTree.TYPE_TOPIC:
			handleTopic(contentTree, request, response, actionContext);
			return "success";
		case ContentTree.TYPE_DIRECTORY:
			handleDirectory(contentTree, request, response, actionContext);
			return "success";
		case ContentTree.TYPE_PAGE_LIST:
			handlePageList(contentTree, request, response, actionContext);
			return "success";
			default:
				handlePage(contentTree, request, response, actionContext);
				return "success";
		}
		/*
		//到这里的url应该只是thing:xxxx的情况
		String url = contentTree.url;
		if(url != null && !"".equals(url)){
			String thingPath = url.substring(6, url.length());
			Thing thing = World.getInstance().getThing(thingPath);
			if(thing == null){
				//response.getWriter().println("Thing not exists, id=" + id + ", thingPath=" + thingPath);
				//return "nomessage";
				actionContext.put("contentHtml", "Thing not exists, id=" + contentTree.id + ", thingPath=" + thingPath);
			}else{
				actionContext.put("contentHtml", thing.doAction("toString", actionContext));
			}
		}else{
			actionContext.put("contentHtml", "<h1>" + contentTree.getLabel() + "</h1>");
		}
		actionContext.put("topRightMenu", contentTree.getRoot().getTopRightMenu());
		actionContext.put("menuSets", contentTree.getRoot());
		actionContext.put("indexPaths", contentTree.getRoot().indexPaths);
		actionContext.put("content", contentTree);
		actionContext.put("contentTree", contentTree);
		actionContext.put("logined", logined);
		actionContext.put("iconLogin", false);
		String template = contentTree.getTemplate();
		if(template != null){
			for(String s1 : template.split("[,]")){
				String s2[] = s1.split("[=]");
				if(s2.length == 2){
					actionContext.put(s2[0], s2[1]);
				}
			}
		}
		
		actionContext.put("templatePath", "xworker/app/orgweb/web/default.ftl");
		actionContext.put("logined", request.getSession().getAttribute(orgwebSessionName) != null);
		
		//orgweb的配置		
		//actionContext.put("config", config);
		
		return "success";*/
	}
	
	/**
	 * 创建搜索类的页面。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 */
	public static String createSearchPage(ActionContext actionContext) throws IOException{
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		//需要创建的内容
		Thing contentTree = World.getInstance().getThing("xworker.app.orgweb.dataobjects.ContentTree");
		
		DataObject dataObject = new DataObject(contentTree);
		for(Thing attr : dataObject.getMetadata().getAttributes()){
			String name = attr.getString("name");
			dataObject.put(name, request.getParameter(name));
		}
		
		int parentId = dataObject.getInt("parentId");
		dataObject.put("parentId", parentId);
		DataObject parent = new DataObject("xworker.app.orgweb.dataobjects.ContentTree");
		parent.put("tid", parentId);
		parent = parent.load(actionContext);
		if(parent == null){
			response.getWriter().println("ContentTree not exists, id=" + parentId );
			return "nomessage";
		}
		
		
		String keys = parent.getString("searchKeys");
		/*
		if(keys == null || keys.equals("")){
			response.getWriter().println("ContentTree searchKey is null, id=" + parentId );
			return "nomessage";
		}*/
		
		//创建新的页面
		dataObject.put("type", 4);
		dataObject.put("isPage", 1);
		dataObject.put("status", 0); //创建时未激活的状态
		dataObject = dataObject.create(actionContext);
		
		//创建关键字		
		if(keys != null && !"".equals(keys)) {
			for(String key : keys.split("[,]")){
				DataObject keyObj = new DataObject("xworker.app.orgweb.dataobjects.ContentKeywords");
				keyObj.put("contentTreeId", dataObject.get("tid"));
				keyObj.put("keyword", key);
				keyObj.create(actionContext);
			}
		}
		
		String backUrl = "do?sc=xworker.app.orgweb.web.IndexSearch&id=" + parentId;
		backUrl = URLEncoder.encode(backUrl, "utf-8");
		response.sendRedirect("do?sc=xworker.app.orgweb.web.admin.EditContentTree&id=" + dataObject.getLong("tid") + "&backUrl=" + backUrl);
		return "success";
	}
	
	public static String indexSearchView(ActionContext actionContext) throws IOException, SQLException{
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String id = request.getParameter("id");
		if(id == null || "".equals(id)){
			response.getWriter().println("Param id is null");
			return "nomessage";
		}
		
		int tid = Integer.parseInt(id);
		int contentId = Integer.parseInt(request.getParameter("contentId"));
		ContentTree content = ContentTreeManager.getContentTree(contentId, actionContext); //要显示内容的
		ContentTree contentTree = ContentTreeManager.getContentTree(tid); //左面树节点的
		if(contentTree == null){
			response.getWriter().println("Content not exist, id=" + id);
			return "nomessage";
		}
		if(content == null){
			response.getWriter().println("Content not exist, id=" + contentId);
			return "nomessage";
		}
		
		//到这里的url应该只是thing:xxxx的情况
		actionContext.put("contentHtml", content.getHtml(actionContext));
		/*
		String url = content.url;
		if(url != null && !"".equals(url)){
			String thingPath = url.substring(6, url.length());
			Thing thing = World.getInstance().getThing(thingPath);
			if(thing == null){
				response.getWriter().println("Thing not exists, id=" + id + ", thingPath=" + thingPath);
				return "nomessage";
			}
			actionContext.put("contentHtml", thing.doAction("toString", actionContext));
		}else{
			actionContext.put("contentHtml", "No content");
		}*/
		actionContext.put("topRightMenu", contentTree.getRoot().getTopRightMenu());
		actionContext.put("menuSets", contentTree.getRoot());
		actionContext.put("indexPaths", contentTree.indexPaths);
		actionContext.put("contentTree", contentTree);
		actionContext.put("content", content);
		actionContext.put("iconLogin", false);
		
		String template = contentTree.getTemplate();
		if(template != null){
			for(String s1 : template.split("[,]")){
				String s2[] = s1.split("[=]");
				if(s2.length == 2){
					actionContext.put(s2[0], s2[1]);
				}
			}
		}
		actionContext.put("templatePath", "xworker/app/orgweb/web/searchView.ftl");
		actionContext.put("logined", request.getSession().getAttribute(orgwebSessionName) != null);
		
		//orgweb的配置
		Thing config = UtilThing.getThingIfNotExistsCreate("_local.xworker.config.OrgwebConfig", "_local", "xworker.app.orgweb.utils.OrgwebConfig");
		actionContext.put("config", config);
		
		return "success";
	}
	/**
	 * 查询的操作。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String search(ActionContext actionContext){
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String keys = request.getParameter("q");
		if(keys == null){
			keys = "";
		}
		
		String qs[] = keys.split("[ ]");
		
		return "success";
	}
	
	@SuppressWarnings("unused")
	public static Object getMenu(ActionContext actionContext) throws SQLException{
		String lang = null;
		String keyword = null;
		boolean logined = false;
		if(actionContext.get("request") != null){
			HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
			lang = request.getParameter("lang");
			keyword = request.getParameter("keyword");
			if(keyword == null || "".equals(keyword)){
				keyword = request.getParameter("params");
			}
			logined = request.getSession().getAttribute(orgwebSessionName) != null;
		}
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			if(keyword == null || "".equals(keyword)){
				//显示全部
				if(logined){
					pst = con.prepareStatement("SELECT * FROM tblcontenttree order by orderWeight");
				}else{
					pst = con.prepareStatement("SELECT * FROM tblcontenttree where status=1  order by orderWeight");
				}
			}else{
				//通过关键字查找
				String keys[] = keyword.split("[ ]");
				List<String> ks = new ArrayList<String>();
				for(String key : keys){
					key = key.trim();
					if(!"".equals(key)){
						ks.add(key.toLowerCase());
					}
				}
				String clause = "select contentTreeId FROM tblcontentkeywords where keyword in (";
				for(int i=0; i<ks.size(); i++){
					if( i != 0){
						clause = clause + ",";
					}
					clause = clause + "?";
				}
				clause = clause + " ) group by contentTreeId   having count(distinct keyword) = " + ks.size();
				if(logined){
					pst = con.prepareStatement("select * from tblcontenttree t where tid in (" + clause + ")  order by orderWeight");
				}else{
					pst = con.prepareStatement("select * from tblcontenttree t where tid in (" + clause + ") and status=1  order by orderWeight");
				}
				int index = 1;
				for(String key : ks){
					pst.setString(index, key);
					index++;
				}
			}
			rs = pst.executeQuery();
			
			Map<Integer, ContentTree> examples = new HashMap<Integer, ContentTree>();
			ContentTree root = ContentTree.createRoot();
			examples.put(0, root);
			while(rs.next()){
				ContentTree ex = ContentTree.createFromResultSet(rs);								
				examples.put(ex.getId(), ex);
			}
			
			//递归查找所有的未知父节点
			int nodeDepeth = 0;
			while(true){
				nodeDepeth ++ ;
				if(nodeDepeth > 250){ //最多搜索250层
					break;
				}
				
				Map<Integer, Integer> parentIds = new HashMap<Integer, Integer>();
				for(int id : examples.keySet()){
					ContentTree ex = examples.get(id);
					if(ex.getParentId() > 0 && examples.get(ex.getParentId()) == null){
						parentIds.put(ex.getParentId(), ex.getParentId());
					}
				}
				if(parentIds.size() > 0){
					DbUtil.close(rs);
					DbUtil.close(pst);
					
					String sql = "select * from tblcontenttree t where tid in (" ;
					int index = 0;
					for(Integer key : parentIds.keySet()){
						if(index != 0){
							sql = sql + ",";
						}
						index++;
						sql = sql + "?";
					}
					sql = sql + ")";
					pst = con.prepareStatement(sql);
					index = 1;
					for(Integer key : parentIds.keySet()){
						pst.setInt(index, key);
						index++;
					}
					rs = pst.executeQuery();
					while(rs.next()){
						ContentTree ex = ContentTree.createFromResultSet(rs);								
						examples.put(ex.getId(), ex);
					}
				}else{
					break;
				}					
			}
			
			//删除状态不为1的节点
			if(!logined){
				List<Integer> forRemoved = new ArrayList<Integer>();
				for(int id : examples.keySet()){
					ContentTree ex = examples.get(id);
					if(ex.getStatus() != 1){
						forRemoved.add(id);
					}
				}
				for(Integer key : forRemoved){
					examples.remove(key);
				}
			}
			
			//初始化引用
			List<Integer> forRemoved = new ArrayList<Integer>();
			for(Integer key : forRemoved){
				examples.remove(key);
			}
			
			//初始化父子节点
			for(int id : examples.keySet()){
				ContentTree ex = examples.get(id);
				ex.initLabel(lang, logined);
				if(ex.parentId != -1){
					ContentTree parent = examples.get(ex.parentId);
					if(parent != null){
						parent.addChild(ex);
					}
				}
			}
			
			return root;
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
		}
	}
}
