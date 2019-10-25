package xworker.app.orgweb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.utils.DbUtil;

public class ContentTreeManager {
	private static Logger logger = LoggerFactory.getLogger(ContentTreeManager.class);
	
	/** 内容树的缓存 */
	private static Map<Integer, ContentTree> cache = new HashMap<Integer, ContentTree>();
	
	static{
		loadCache();
	}
	
	public static Map<Integer, ContentTree> getCache(){
		return cache;
	}
	
	public static void loadCache(){
		try {
			Thing dataSource = World.getInstance().getThing("xworker.app.orgweb.datasource.OrgwebDataSource");
			ActionContext actionContext = new ActionContext();
			Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
			try{
				actionContext.put("con", con);
				
				//装载缓存
				loadCache(actionContext);
				
				//装载数量
				reloadSearchCounts(actionContext);
				
				//执行定时器
				//Thing thing = World.getInstance().getThing("xworker.app.orgweb.utils.ReloadCacheTask");
				//thing.doAction("reloadSearchTask", actionContext);
			}finally{
				con.close();
			}
		} catch (Exception e) {
			logger.warn("load cache error", e);
		}
	}
	
	public static ContentTree getContentTree(int id){
		return cache.get(id);
	}
	
	/**
	 * 随机返回默认的一个，避免返回空的界面。
	 * 
	 * @return
	 */
	public static ContentTree getAContentTree(){
		if(cache.keySet().size() > 0){
			return cache.get(cache.keySet().iterator().next());
		}else{
			return null;
		}
	}
	
	/**
	 * 重新装载查找数量。
	 */
	public static void reloadSearchCounts(ActionContext actionContext){
		try {
			Thing dataSource = World.getInstance().getThing("xworker.app.orgweb.datasource.OrgwebDataSource");
			Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
			try{
				for(Integer key : cache.keySet()){
					ContentTree tree = cache.get(key);
					if(tree.getType() == ContentTree.TYPE_PAGE_LIST && tree.searchKeys != null && !"".equals(tree.searchKeys)){
						reloadSearchCount(tree, con);
					}
				}
			}finally{
				con.close();
			}
		} catch (Exception e) {
			logger.error("load search count error", e);
		}
	}
	
	public static void reloadSearchCount(ContentTree tree, Connection con) throws SQLException{
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			String sql = "select distinct contentTreeId FROM tblcontentkeywords where keyword in (";
			String[] keys = tree.searchKeys.split("[,]");
			for(int i=0; i<keys.length; i++){
				if(i > 0){
					sql = sql + ",";
				}
				sql = sql + "?";
			}
			sql = sql + ")";
			//System.out.println(sql);
			
			pst = con.prepareStatement(sql);
			for(int i=0; i<keys.length; i++){
				pst.setString(i + 1, keys[i]);
			}
						
			rs = pst.executeQuery();
			int count = 0;
			int countAll = 0;
			while(rs.next()){
				int id = rs.getInt("contentTreeId");
				ContentTree contentTree = ContentTreeManager.getContentTree(id);
				if(contentTree != null){
					if(contentTree.getStatus() == 1){
						count++;
					}
					countAll++;
				}
			}
			
			tree.searchCount = count;
			tree.searchCountAll = countAll;
		}finally{
			DbUtil.close(pst);
			DbUtil.close(rs);
		}
	}
	
	public static ContentTree getContentTree(int contentTreeId, ActionContext actionContxt) throws SQLException{
		Thing dataSource = World.getInstance().getThing("xworker.app.orgweb.datasource.OrgwebDataSource");
		ActionContext actionContext = new ActionContext();
		Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			pst = con.prepareStatement("select * from tblcontenttree where tid=?");
			pst.setInt(1, contentTreeId);
			rs = pst.executeQuery();
			ContentTree content = null;
			if(rs.next()){
				content = ContentTree.createFromResultSet(rs);
			}else{
				return null;
			}
			
			rs.close();
			pst.close();
			
			//读取关键字
			pst = con.prepareStatement("select * from tblcontentkeywords where contentTreeId =?");
			pst.setInt(1, contentTreeId);
			rs = pst.executeQuery();
			String keyword = null;
			while(rs.next()){
				String key = rs.getString("keyword");
				if(key != null && !"".equals(key.trim())){
					key = key.trim();
					if(keyword == null){
						keyword = key;
					}else{
						keyword = keyword + "," + key;
					}
				}
			}
			
			if(keyword != null){
				content.keywords = keyword;
			}
			
			return content;			
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}
	
	/**
	 * 内容树的缓存。
	 *  
	 * @throws SQLException */
	public static void loadCache(ActionContext actionContext) throws SQLException{
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			Map<Integer, ContentTree> cache_ = new HashMap<Integer, ContentTree>();
			ContentTree root = new ContentTree();
			root.id = 0;
			root.parentId = -1;
			root.label = "根节点";
			root.createDate = new Date();
			root.updateDate = new Date();
			cache_.put(0, root);
			
			//读取所有的内容
			List<ContentTree> contentList = new ArrayList<ContentTree>();
			contentList.add(root);
			pst = con.prepareStatement("select * from tblcontenttree order by orderWeight");
			rs = pst.executeQuery();
			while(rs.next()){
				ContentTree content = ContentTree.createFromResultSet(rs);
				contentList.add(content);
				cache_.put(content.id, content);
			}
			rs.close();
			pst.close();
			
			//读取关键字
			pst = con.prepareStatement("select * from tblcontentkeywords where contentTreeId in (select tid  from tblcontenttree where status=1) order by contentTreeId");
			rs = pst.executeQuery();
			int contentTreeId = 0;
			String keyword = null;
			while(rs.next()){
				int tid = rs.getInt("contentTreeId");
				if(tid != contentTreeId){
					if(keyword != null){
						ContentTree content = cache_.get(contentTreeId);
						if(content != null){
							content.keywords = keyword;
						}
					}
					contentTreeId = tid;
					keyword = null;
				}
				String key = rs.getString("keyword");
				if(key != null && !"".equals(key.trim())){
					key = key.trim();
					if(keyword == null){
						keyword = key;
					}else{
						keyword = keyword + "," + key;
					}
				}
			}
			
			if(keyword != null){
				ContentTree content = cache_.get(contentTreeId);
				if(content != null){
					content.keywords = keyword;
				}
			}
			
			//初始化子节点和路径
			for(ContentTree content : contentList){
				//初始化父子节点
				ContentTree parent = cache_.get(content.parentId);
				if(parent != null){
					parent.childs.add(content);
					content.parent = parent;
				}
				
				//初始化索引路径
				if(content.getType() == ContentTree.TYPE_DIRECTORY || content.getType() == ContentTree.TYPE_TOPIC){
					if(content.id != content.getProject().refId){
						content.indexPaths.add(content);
					}
				}
				ContentTree indexParent = cache_.get(content.getCategoryId());
				initIndexPath(content, indexParent, cache_, new HashMap<Integer, Integer>());
				/*
				if(content.indexPath != null && !"".equals(content.indexPath)){
					for(String id : content.indexPath.split("[,]")){
						try{
							int tid = Integer.parseInt(id);
							ContentTree index = cache_.get(tid);
							if(index != null){
								content.indexPaths.add(index);
							}
						}catch(Exception e){							
						}
					}
				}*/
				
				//topRightMenu
				if(content.topRightMenuId != 0){
					content.topRightMenu = cache_.get(content.topRightMenuId);
				}
				if(content.refId != 0){
					content.refContent = cache_.get(content.refContent);
				}
			}
						
			cache = cache_;			
			
			//装载数量
			reloadSearchCounts(actionContext);
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
		}
	}	
	
	public static void initIndexPath(ContentTree contentTree, ContentTree parentContent, Map<Integer, ContentTree> cache_, Map<Integer, Integer> context){

		if(parentContent == null){
			return;
		}
		
		if(context.get(parentContent.id) != null){
			return;
		}else{
			context.put(parentContent.id, parentContent.id);
		}
		
		int type = parentContent.getType();
		if(parentContent.parentId == 0 || type == ContentTree.TYPE_DIRECTORY || type == ContentTree.TYPE_TOPIC){
			if(parentContent.id != contentTree.getProject().refId){
				boolean have = false;
				//路径中不能重复
				for(ContentTree ip : contentTree.indexPaths){
					if(parentContent.id == ip.id || parentContent.refId == ip.id){
						have = true;
						break;
					}
				}
				
				if(!have){
					contentTree.indexPaths.add(0, parentContent);
				}
			}
		}
		
		int parentId = parentContent.getCategoryId();
		if(parentId == 0){
			return;
		}

		initIndexPath(contentTree, cache_.get(parentId), cache_, context);
	}
}
