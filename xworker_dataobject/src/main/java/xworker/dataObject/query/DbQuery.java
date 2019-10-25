package xworker.dataObject.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.DbUtil;

/**
 * 数据库的统计查询。
 * 
 * @author Administrator
 *
 */
public class DbQuery {
	private static Logger log = LoggerFactory.getLogger(DbQuery.class);
	
	public static void iterator(ActionContext actionContext) throws SQLException, OgnlException{		
		Object ac = actionContext.get("action");
		Action action = null;
		if(ac instanceof Action){
			action = (Action) ac;
		}else if(ac instanceof Thing){
			action = ((Thing) ac).getAction();
		}else if(ac instanceof String){
			action = World.getInstance().getAction((String) ac);
		}else{
			Thing self = (Thing) actionContext.get("self");
			throw new ActionException("Iterator: action is null or not a (Action、Thing or String), path=" + self.getMetadata().getPath());
		}
		
		actionContext.peek().put("action", action);
		queryOrIterator(actionContext, true);
	}
	
	public static List<DataObject> query(ActionContext actionContext) throws SQLException, OgnlException{
		return queryOrIterator(actionContext, false);
	}
	
	@SuppressWarnings("unchecked")
	public static List<DataObject> queryOrIterator(ActionContext actionContext, boolean iterator) throws SQLException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		Action action = null;
		if(iterator){
			action = (Action) actionContext.get("action");
		}
	
		Object conditionData = actionContext.get("conditionData");
		//查看是否是聚合和分组
		String aggregateColumnStr = (String) actionContext.get("aggregateColumns");
		if(aggregateColumnStr == null && actionContext.get("conditionData") != null){			
		    aggregateColumnStr = (String) OgnlUtil.getValue("aggregateColumns", conditionData);
		}
		String groupColumnStr = (String) actionContext.get("groupColumns");
		if(groupColumnStr == null && actionContext.get("conditionData") != null){
		    groupColumnStr = (String) OgnlUtil.getValue("groupColumns", conditionData);
		}

		List<Thing> aggregateColumns = null;
		List<Thing> groupColumns = null;
		if(self.getBoolean("groupFirst")) {
			groupColumns = getValidGroupColumns(self, groupColumnStr);
			aggregateColumns = getValidAggregateColumns(self, aggregateColumnStr);			
		}else {
			aggregateColumns = getValidAggregateColumns(self, aggregateColumnStr);
			groupColumns = getValidGroupColumns(self, groupColumnStr);
		}
		boolean isAggregate = aggregateColumns.size() > 0 && groupColumns.size() > 0;
		
		PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
		if(!isAggregate){
		    //不是分组聚合查询的普通查询调用DbDataObject查询
		    Action queryAction = null;
		    if(iterator){
		    	queryAction = world.getAction("xworker.dataObject.db.DbDataObject/@actions/@iterator");
		    }else{
		    	queryAction = world.getAction("xworker.dataObject.db.DbDataObject/@actions/@query");
		    }
		    
		    //动态数据对象，用于调用者动态生成界面等
		    if(actionContext.get("pageInfo") != null){
		        pageInfo.put("dynamicDataObject", self);
		    }
		    
		    List<DataObject> datas = (List<DataObject>) queryAction.run(actionContext);
		    if(self.getBoolean("afterQueryed")){
		        self.doAction("afterQueryed", actionContext, UtilMap.toMap("datas",  datas));
		    }
		    return datas;
		}else{
		    //有分组查询的生成新的数据对象
		     //由于汇总查询等每次的列可能都不同，所以做了一个缓存保存不同的汇总列的查询
		    String key = "aggregate:";
		    for(int i=0; i<aggregateColumns.size(); i++){
		        Thing aggregate = aggregateColumns.get(i);
		        key = key + "," + aggregate.getString("name");
		    }
		    key = key + ",group:";
		    for(int i=0; i<groupColumns.size(); i++){
		        Thing group = groupColumns.get(i);
		        key = key + "," + group.getString("name");
		    }    
		    Thing dataObject = (Thing) self.getData(key);
		    boolean haveDataObjectCache = dataObject != null;
		    
		    if(!haveDataObjectCache){
		        //创建新的查询对象
		        dataObject = new Thing("xworker.dataObject.db.DbDataObject");
		        dataObject.set("dataSource", self.getString("dataSource"));
		        dataObject.put("showSql",  self.get("showSql"));
		    }
		    
		    //生成基本的查询SQL
		    String sql = "select ";
		    if(!self.getBoolean("groupFirst")) { 
			    for(int i=0; i<aggregateColumns.size(); i++){
			        Thing aggregate = aggregateColumns.get(i);
			        if(i != 0){
			            sql = sql + ",";
			        }
			        sql = sql + aggregate.getString("fieldName") + " as " + aggregate.getString("name");
			        
			        if(!haveDataObjectCache){
			            Thing aggregateAttr = aggregate.detach();
			            aggregateAttr.set("fieldName", aggregateAttr.get("name")); //select的属性
			            aggregateAttr.set("descriptors", "xworker.dataObject.db.DbDataObject/@attribute");
			            dataObject.addChild(aggregateAttr);
			        }
			    }
		    }

		    for(int i=0; i<groupColumns.size(); i++){
		    	if(i != 0 || !self.getBoolean("groupFirst")){
		            sql = sql + ",";
		        }
		        Thing group = groupColumns.get(i);
		        String selectFiled = group.getStringBlankAsNull("selectFiled");
		        String selectFil =  selectFiled==null? group.getString("fieldName") : group.getString("selectFiled");
		        sql = sql + selectFil + " as " + group.getString("name");

		        if(!haveDataObjectCache){
		            Thing groupAttr = group.detach();
		            //log.info(groupAttr.getString("name") + "=" + groupAttr.getBoolean("gridSortable"));
		            groupAttr.set("fieldName", groupAttr.get("name")); //select的属性
		            groupAttr.set("descriptors", "xworker.dataObject.db.DbDataObject/@attribute");
		            dataObject.addChild(groupAttr);
		            dataObject.setData("sourceDataObject", self);
		        }
		    }
		    
		    if(self.getBoolean("groupFirst")) { 
			    for(int i=0; i<aggregateColumns.size(); i++){
			        Thing aggregate = aggregateColumns.get(i);
			        sql = sql + ",";
			        
			        sql = sql + aggregate.getString("fieldName") + " as " + aggregate.getString("name");
			        
			        if(!haveDataObjectCache){
			            Thing aggregateAttr = aggregate.detach();
			            aggregateAttr.set("fieldName", aggregateAttr.get("name")); //select的属性
			            aggregateAttr.set("descriptors", "xworker.dataObject.db.DbDataObject/@attribute");
			            dataObject.addChild(aggregateAttr);
			        }
			    }
		    }
		    
		    if(!haveDataObjectCache){
		        for(Thing relationThing : self.getChilds("thing")){
		            dataObject.addChild(relationThing);
		        }
		        
		        self.setData(key, dataObject);
		    }
		    
		    //新的数据对象生成的事件
		    if(!haveDataObjectCache){
		    	self.doAction("afterDataObjectGenerated", actionContext, "dataObject", dataObject);
		    }
		    
		    sql = sql + " from ";
		    if(self.getString("querySql") != null && !"".equals(self.getString("querySql"))){
		        sql = sql + self.getString("querySql");
		    }else{
		        sql = sql + self.getString("tableName");
		    }
		    
		    //生成查询sql语句
		    Object datas = actionContext.get("conditionData");
		    if(datas == null){
		        datas = Collections.EMPTY_MAP;
		    }
		    //log.info("" + datas);
		    Object conditionConfig = actionContext.get("conditionConfig");
		    if(conditionConfig == null){
		        conditionConfig = Collections.EMPTY_MAP;
		    }

		    Object cds = new ArrayList<Object>();
		    String clause = DbUtil.getConditionSql(conditionConfig, actionContext, datas, cds);
		    //log.info("clause=" + clause);
		    if(clause != null && clause != ""){
		        sql = sql + " where " + clause;
		    }
		    
		    //加上group
		    sql = sql + " group by ";
		    for(int i=0; i<groupColumns.size(); i++){
		        Thing group = groupColumns.get(i);
		        if(i != 0){
		            sql = sql + ",";
		        }
		        sql = sql + group.getString("fieldName");
		    }

		    String sortField = null;
		    String sortDir = null;
		    if(actionContext.get("pageInfo") != null){
		    	String sort = (String) pageInfo.get("sort");
		    	
		        //分页查询中的指定排序
		        sortField = sort;
		        String dir = (String) pageInfo.get("dir");		    
		        if(dir != null && !"".equals(dir)){
		            sortDir = dir;
		        }
			}
				
				//排序，排序的字段是否选择的列中呢？
			if(sortField  != null && !"".equals(sortField.trim())){
			    sql = sql + " order by " + sortField;
			    if(sortDir != null){
			        sql = sql + " " + sortDir;
			    }
			}
				
		    if(self.getBoolean("showSql")){
		        log.info("sql=" + sql);
		    }
		    //动态数据对象，用于调用者动态生成界面等
		    if(actionContext.get("pageInfo") != null){
		        pageInfo.put("dynamicDataObject", dataObject);
		    }

		    //----------------执行查询-----------------------
		    List<Thing> attributes = self.getChilds("attribute");
		    if(actionContext.get("pageInfo") != null && pageInfo.getLimit() != 0){
		        //有分页查询
		        String dbType = (String) actionContext.get("dbType");
		        if(dbType != null && !"".equals(dbType)){
		        	dbType = dbType.toLowerCase();
		            List<DataObject> ds = null;
		            if(!iterator){
			            if(dbType.startsWith("oracle")){
			                ds = (List<DataObject>) dataObject.doAction("queryOraclePage", actionContext, UtilMap.toMap("cds", cds, "sql", sql, "attributes", attributes));
			            }else if("derby".equals(dbType)){
			                ds = (List<DataObject>) dataObject.doAction("queryDerbyPage", actionContext, UtilMap.toMap("cds", cds, "sql" , sql, "attributes", attributes));
			            }else if("mysql".equals(dbType)){
			                ds = (List<DataObject>) dataObject.doAction("queryMysqlPage", actionContext, UtilMap.toMap("cds", cds, "sql", sql, "attributes", attributes));
			            }
			            
			            if(self.getBoolean("afterQueryed")){
			                self.doAction("afterQueryed", actionContext, UtilMap.toMap("datas", ds));
			            }
			            return ds;
		            }else{
		            	if(dbType.startsWith("oracle")){
			                dataObject.doAction("iteratorOraclePage", actionContext, UtilMap.toMap("cds", cds, "sql", sql, "attributes", attributes));
			            }else if("derby".equals(dbType)){
			                dataObject.doAction("iteratorDerbyPage", actionContext, UtilMap.toMap("cds", cds, "sql" , sql, "attributes", attributes));
			            }else if("mysql".equals(dbType)){
			                dataObject.doAction("iteratorMysqlPage", actionContext, UtilMap.toMap("cds", cds, "sql", sql, "attributes", attributes));
			            }
			            
			            if(self.getBoolean("afterQueryed")){
			                self.doAction("afterQueryed", actionContext, UtilMap.toMap("datas", ds));
			            }
			            return ds;
		            }
		        }
		        throw new ActionException("dbType=" + dbType + ", not supported page query now");
		    }else{
		        //没有分页查询
		        //设置参数值和查询
		    	Connection con = (Connection) actionContext.get("con");
		        PreparedStatement pst = con.prepareStatement(sql);
		        ResultSet rs = null;
		        try{
		            //设置查询参数
		        	dataObject.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes" ,attributes, "index", 1));
		            
		            //执行sql
		            rs = pst.executeQuery();
		            List<DataObject> ds = new ArrayList<DataObject>();
		            attributes = dataObject.getChilds("attribute"); //取查询数据对象实际的列
		            int index = 1;
		            while(rs.next()){
		                //构造对象
		                DataObject data = new DataObject(dataObject);
		                data.setInited(false);
		                
		                //设置属性值
		                for(int i=0; i<attributes.size(); i++){
		                    data.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
		                }
		                
		                data.setInited(true);
		                
		                if(iterator){
		                	action.run(actionContext, UtilMap.toMap("data", data, "index", index));
		                }else{
		                	ds.add(data);
		                }
		                index++;
		            }
		            
		            if(self.getBoolean("afterQueryed")){
		                self.doAction("afterQueryed", actionContext, UtilMap.toMap("datas", ds));
		            }
		            return ds;
		        }finally{
		            if(rs != null){
		                rs.close();
		            }
		            if(pst != null){
		                pst.close();
		            }
		        }
		    }
		}
	}
	
	/**
	 * 返回有效的分组列。
	 */
	public static List<Thing> getValidGroupColumns(Thing dataObject, String groupColumns){
	    List<Thing> myGroupColumns = dataObject.getChilds("group");
	    List<Thing> validGroupColumns = new ArrayList<Thing>();
	    if(groupColumns != null &&  !"".equals(groupColumns)){
	        //获取匹配的聚合列
	        String[] ags = groupColumns.split("[,]");
	        for(int i=0; i<ags.length; i++){
	            String ag = ags[i];
	            for(int n=0; n<myGroupColumns.size(); n++){
	                if(ag.equals(myGroupColumns.get(n).getString("name"))){
	                    validGroupColumns.add(myGroupColumns.get(n));
	                }
	            }
	        }
	    }
	    
	    return validGroupColumns;
	}
	

	/**
	 * 返回有效的聚合列。
	 */
	public static List<Thing> getValidAggregateColumns(Thing dataObject, String aggregateColumns){
	    List<Thing> myAggregateColumns = dataObject.getChilds("aggregate");
	    List<Thing> validAggregateColumns = new ArrayList<Thing>();
	    if(aggregateColumns == null || "".equals(aggregateColumns)){
	        //在没有指定的情况下，返回第一个，如果有
	        if(myAggregateColumns.size() > 0){            
	            validAggregateColumns.add(myAggregateColumns.get(0));
	        }
	    }else{
	        //获取匹配的聚合列
	        String[] ags = aggregateColumns.split("[,]");
	        for(int i=0; i<ags.length; i++){
	            String ag = ags[i];
	            for(int n=0; n<myAggregateColumns.size(); n++){
	                if(ag.equals(myAggregateColumns.get(n).getString("name"))){
	                    validAggregateColumns.add(myAggregateColumns.get(n));
	                }
	            }
	        }
	    }
	    
	    return validAggregateColumns;
	}
}
