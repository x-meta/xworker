package xworker.app.orgweb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ContentTree implements Comparable<ContentTree>{
	public static final int TYPE_PAGE = 0;
	public static final int TYPE_TOPIC = 1;
	public static final int TYPE_DIRECTORY = 2;
	public static final int TYPE_PAGE_LIST = 3;
	public static final int TYPE_PAGE_LIST_PAGE = 4;
	public static final int TYPE_URL = 5;
	public static final int TYPE_PROJECT = 6;
	
	/** 标识 */
	public int id;	
	/** parentId */
	public int parentId;
	public ContentTree parent;
	/** 标签 */
	protected String label;
	public String oldLabel;
	/** 英文标签 */
	public String label_en;
	/** URL地址 */
	public String url;
	/** 目标 */
	public String target;
	/** 生效状态 */
	public byte status;
	/** 排序权重 */
	public int orderWeight;
	/** 是否是根节点 */
	public boolean isRoot;
	/** 图标路径 */
	public String iconPath;
	/** 右上角菜单标识 */
	public int topRightMenuId;
	public ContentTree topRightMenu;
	/** 索引路径  */
	public String indexPath;
	public List<ContentTree> indexPaths = new ArrayList<ContentTree>();
	/** 页面模板  */
	public String template;
	/** 描述 */
	public String description;
	/** 关键字列表 */
	public String keywords;
	/** 是否是关键字 */
	public boolean isPage;
	/** 查找关键字 */
	public String searchKeys;
	/** 符合关键字的文章的数量 */
	public int searchCount;
	public int searchCountAll;
	/** 目录标识 */
	public int categoryId;
	/** 子节点列表 */
	public List<ContentTree> childs = new ArrayList<ContentTree>();
	/** 引用标识 */
	public int refId;
	/** 类型 */
	private int type;
	
	public ContentTree refContent;
	
	public boolean childSorted = false;
	public Date updateDate = null;
	public Date createDate = null;
	private  static ThreadLocal<String> langLocal = new ThreadLocal<String>();
	private  static ThreadLocal<Boolean> loginLocal = new ThreadLocal<Boolean>();
	private  static ThreadLocal<Map<Integer, Integer>> getChildsLocal = new ThreadLocal<Map<Integer, Integer>>();
	public boolean isCategoryContainer = false;
	
	public static String getLang(){
		return langLocal.get();
	}
	
	public static void setLang(String lang){
		langLocal.set(lang);
	}
	
	
	public static void setLogin(boolean logined){
		loginLocal.set(logined);
	}
	
	/**
	 * 是否是管理登陆了。
	 * 
	 * @return
	 */
	public static boolean isLogined(){
		if(loginLocal.get() != null && loginLocal.get() == true){
			return true;
		}else{
			return false;
		}
	}
	
	public int getType(){
		if(type == 0){
			if(isRoot){
				return TYPE_TOPIC;
			}else if(isCategoryContainer){
				return TYPE_DIRECTORY;
			}
		}
		if(parentId == 0){
			return TYPE_PROJECT;
		}
		
		if(parent != null && (parent.type == ContentTree.TYPE_DIRECTORY || parent.parentId == 0)){
			//目录下的内容要么是目录，要么是主题
			if(this.type != ContentTree.TYPE_TOPIC){
				return ContentTree.TYPE_DIRECTORY;
			}
		}
		
		return type;
	}
	
	/**
	 * 获取根
	 * 
	 * @return
	 */
	public ContentTree getRoot(){
		ContentTree root = this;
		while(root.getType() != TYPE_TOPIC){
			if(root.parent == null || root.parent.id == 0){
				return root;
			}else{
				root = root.parent;
			}
		}
		
		return root;
	}
	
	public ContentTree getProject(){
		ContentTree root = this;
		while(true){
			if(root.parent == null || root.parent.id == 0){
				return root;
			}else{
				root = root.parent;
			}
		}
	}
	
	public ContentTree getParent() {
		return parent;
	}

	public String getOldLabel() {
		return oldLabel;
	}

	public int getOrderWeight() {
		return orderWeight;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public int getTopRightMenuId() {
		ContentTree topRightMenu = getTopRightMenu();
		if(topRightMenu != null){
			return topRightMenu.id;
		}else{
			return topRightMenuId;
		}
	}

	public ContentTree getTopRightMenu() {
		if(topRightMenu != null){
			return topRightMenu;
		}else{
			ContentTree parent = this.parent;
			while(parent != null){
				if(parent.topRightMenu != null){
					return parent.topRightMenu;
				}else{
					parent = parent.parent;
				}
			}
		}
		return topRightMenu;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public String getTemplate() {
		return template;
	}

	public String getKeywords() {
		return keywords;
	}

	public boolean isPage() {
		return isPage;
	}

	public String getSearchKeys() {
		return searchKeys;
	}

	public int getSearchCount() {
		return searchCount;
	}

	public boolean isChildSorted() {
		return childSorted;
	}

	public String getIconPath(){
		if(iconPath != null && !"".equals(iconPath)){
			return iconPath;
		}
		
		ContentTree project = getProject();
		if(project !=  this){
			return project.getIconPath();
		}else{
			return null;
		}
	}
	
	public String getTypeIcon(){
		switch(getType()){
		case TYPE_PROJECT:
			return "icons/folder_table.png";
		case TYPE_TOPIC:
			return "icons/folder_image.png";
		case TYPE_DIRECTORY:
			return "icons/folder.png";
		case TYPE_PAGE_LIST:
			return "icons/folder_page.png";
		default:
			return "icons/xhtml.png";
		
		}
	}
	
	public static ContentTree createRoot(){
		ContentTree ex = new ContentTree();
		ex.id = 0;
		ex.label = "网站内容";
		ex.oldLabel = "网站内容";
		ex.label_en = "Contents";
		ex.target = "main";
		ex.url = "do?sc=xworker.app.orgweb.web.admin.AdminMainPage";
		ex.parentId = -1;
		ex.status = 1;
		return ex;
	}
	
	public static ContentTree createFromResultSet(ResultSet rs) throws SQLException{
		ContentTree ex = new ContentTree();
		ex.id = rs.getInt("tid");
		ex.parentId = rs.getInt("parentId");
		ex.label = rs.getString("label");
		ex.oldLabel = ex.label;
		ex.label_en = rs.getString("label_en");
		ex.url = rs.getString("url");
		ex.target = rs.getString("target");
		ex.status = rs.getByte("status");
		ex.orderWeight = rs.getInt("orderWeight");
		ex.isRoot = rs.getBoolean("isRoot");
		ex.iconPath = rs.getString("iconPath");
		ex.topRightMenuId = rs.getInt("topRightMenuId");
		ex.indexPath = rs.getString("indexPath");
		ex.template = rs.getString("template");
		ex.description = rs.getString("description");
		ex.isPage = rs.getBoolean("isPage");
		ex.categoryId = rs.getInt("categoryId");
		ex.refId = rs.getInt("refId");
		ex.type = rs.getInt("type");
		ex.searchKeys = rs.getString("searchKeys");
		ex.updateDate = rs.getTimestamp("updateDate");
		ex.createDate = rs.getTimestamp("createDate");
		ex.isCategoryContainer = rs.getBoolean("isCategoryContainer");
		if("".equals(ex.searchKeys)){
			ex.searchKeys = null;
		}
		return ex;
	}
	
	public List<ContentTree> getChilds(){
		Map<Integer, Integer> context = getChildsLocal.get();
		boolean setter = false;
		try{
			//避免列出子节点时的递归
			if(context == null){
				context = new HashMap<Integer, Integer>();
				setter = true;
				getChildsLocal.set(context);
			}
			if(context.get(id) != null){
				return Collections.emptyList();
			}else{
				context.put(id, id);
			}
			
			
			if(!childSorted){
				Collections.sort(childs);
				childSorted = true;
			}
			
			if(!isLogined()){
				List<ContentTree> list = new ArrayList<ContentTree>();
				for(ContentTree child : childs){
					if(child.status == 1){ //生效
						list.add(child);
					}
				}
				
				return list;
			}else{
				return childs;
			}
		}finally{
			if(setter){
				getChildsLocal.set(null);
			}
		}
	}
	
	public void addChild(ContentTree child){
		childs.add(child);
	}
	
	public int getId() {
		return id;
	}

	public int getParentId() {
		return parentId;
	}

	public String getDescription(){
		return description;
	}
	
	public String getHtml(ActionContext actionContext){
		World world = World.getInstance();
		Thing page = world.getThing("orgweb.pages.Page" + id);
		if(page != null){
			return page.doAction("toString", actionContext);
		}
		
		return description;
	}
	
	public void initLabel(String lang, boolean logined){		
		/*en_label = label_en;
		if(label_en == null || "".equals(label_en)){
			en_label = label;
		}
		
		en_label = getLabel(en_label, logined);*/
		label = getLabel(label, logined);
	}
	
	private String getLabel(String label, boolean logined){
		/*
		if(logined){
			if(id == 0){
				label = label + " <a target=\"main\" href=\"do?sc=xworker.app.orgweb.web.admin.EditPage&thingPath=xworker.app.orgweb.web.admin.AdminMainPage \">" +
						"<img src=\"icons/page_edit.png\" style=\"height:10px; width:10px\"/>" + "</a>" +
						"<a target=\"main\" href=\"do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId="
								+ getId() + "\">" +
								"<img src=\"icons/page_add.png\" style=\"height:10px; width:10px\" />" + "</a>";
			}else{			
				if(status != 1){
					label = "<span style=\"background-color:rgb(255, 0, 0)\">" + label + "</span>";
				}
				
				label = label + " <a target=\"main\" href=\"do?sc=xworker.app.orgweb.web.admin.EditContentTree&id="
						+ id + "\">" +
						"<img src=\"icons/page_edit.png\" style=\"height:10px; width:10px\"/>" + "</a>" +
						"<a target=\"main\" href=\"do?sc=xworker.app.orgweb.web.admin.AddContentTree&parentId="
								+ getId() + "\">" +
								"<img src=\"icons/page_add.png\" style=\"height:10px; width:10px\" />" + "</a>";
			}
		}
		*/
		return label;
	}
	
	public String getLabel() {
		if("en".equals(getLang())){
			if(label_en == null || "".equals(label_en)){
				return label;
			}
			return label_en;
		}else{
			return label;
		}
		/*
		if(this.searchKeys != null && !"".equals(this.searchKeys)){
			//return label + "(" + this.searchCount + ")";
			return label;
		}else{
			return label;
		}*/
	}

	public String getLabelCount() {
		if(this.searchKeys != null && !"".equals(this.searchKeys)){
			if(ContentTree.isLogined()){
				return getLabel() + "(" + this.searchCountAll + ")";
			}else{
				return getLabel() + "(" + this.searchCount + ")";
			}
			//return label;
		}else{
			return getLabel();
		}
	}
	
	public String getLabel_en() {
		return label_en;
	}

	public String getUrl() {
		int urlId = id;
		if(refId != 0){
			urlId = refId;
		}
		if(url != null && !"".equals(url)){
			if(url.startsWith("thing:")){
				if(this.searchKeys != null && !"".equals(this.searchKeys)){
					return "do?sc=xworker.app.orgweb.web.IndexSearch&id=" + urlId;
				}else{
					return "do?sc=xworker.app.orgweb.web.Index&id=" + urlId;
				}
			}else{
				return url;
			}
		}else{
			if(this.getType() == ContentTree.TYPE_PAGE_LIST){
				return "do?sc=xworker.app.orgweb.web.IndexSearch&id=" + urlId;
			}
			
			return "do?sc=xworker.app.orgweb.web.Index&id=" + urlId;
		}
	}

	public String getTarget() {
		if(target != null && !"".equals(target)){
			return target;
		}
		
		return null;
	}

	public byte getStatus() {
		return status;
	}

	@Override
	public int compareTo(ContentTree o) {
		if(orderWeight > o.orderWeight){
			return 1;
		}else if(orderWeight == o.orderWeight){			
			return label.compareTo(o.label);
		}else{
			return -1;
		}
	}

	public List<ContentTree> getIndexPaths() {
		return indexPaths;
	}

	public boolean isCategoryContainer() {
		return isCategoryContainer;
	}

	public void setIndexPaths(List<ContentTree> indexPaths) {
		this.indexPaths = indexPaths;
	}

	public int getCategoryId() {
		if(categoryId == 0 && parentId != -1){
			return parentId;
		}
		
		return categoryId;
	}
}
