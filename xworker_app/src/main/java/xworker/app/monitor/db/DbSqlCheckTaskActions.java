package xworker.app.monitor.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.app.monitor.MonitorDataSaveTask;
import xworker.app.monitor.MonitorUtils;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DbUtil;
import xworker.util.UtilTemplate;

public class DbSqlCheckTaskActions {
	public static Object run(ActionContext actionContext) throws IOException, TemplateException, SQLException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//DataObject monitor = (DataObject) actionContext.get("monitor");
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		//DataObject task = (DataObject) actionContext.get("task");
		DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		DataObject datasource = (DataObject) actionContext.get("datasource");
		MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		Thing taskThing = (Thing) actionContext.get("taskThing");
		Connection con = (Connection) actionContext.get("con");
		
		String sqlTemplate = self.getStringBlankAsNull("sql");
		if(sqlTemplate == null){
			return "sql is null, path=" + self.getMetadata().getPath();
		}
		String sql = UtilTemplate.processString(actionContext, sqlTemplate);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			String message = null;
			int columnCount = rs.getMetaData().getColumnCount();
			String ognlExp = monitorTaskResource.getString("param1");
			String ognlMessage = monitorTaskResource.getString("param2");
			String dataNames = monitorTaskResource.getString("param3");
			int dataIdIndex = monitorTaskResource.getInt("param4");
			
			if(rs.next()){
				Map<String, Object> values = new HashMap<String, Object>();
				for(int i=1; i<=columnCount; i++){
					Object value = getValue(rs, i);
					values.put("v" + i, value);
					actionContext.peek().put("v" + i, value);
				}
				actionContext.peek().put("values", values);
				
				//比较Ognl表达式
				if(ognlExp != null && !"".equals(ognlExp)){
					if(UtilData.getBoolean(OgnlUtil.getValue(ognlExp, actionContext), false) && ognlMessage != null){
						message = UtilTemplate.processString(actionContext, ognlMessage);
					}
				}
				
				//是否保存数据
				if(dataNames != null && !"".equals(dataNames)){
					String[] dts = dataNames.split("[,]");
					
					for(int i=0; i<dts.length; i++){
						if(!"".equals(dts[i])){
							DataObject data = new DataObject("xworker.app.monitor.dataobjects.MonitorData");
							data.put("taskThingPath", taskThing.getMetadata().getPath());
							data.put("resourceId", datasource.getString("id"));
							data.put("dataName", dts[i]);
							data.put("value", values.get("v" + (i + 1)));
							data.put("grabStartTime", monitorContext.getStartTime());
							data.put("grabEndTime", new Date());
							data.put("monitorId", monitorContext.monitor.get("id"));
							data.put("monitorTaskId", monitorTask.get("id"));
							data.put("monitorTaskResId", monitorTaskResource.get("id"));
							if(dataIdIndex > 0){
								data.put("dataId", values.get("v" + dataIdIndex));
							}
							
							MonitorDataSaveTask.addCreateData(data);
						}
					}
				}
	
				String mes = self.getStringBlankAsNull("message");
				if(message == null && mes != null){
					message = UtilTemplate.processString(actionContext, mes);
				}
			}
						
			return message;
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);			
		}	
	}
	
	public static Object getValue(ResultSet rs, int column) throws SQLException{
		ResultSetMetaData  metadata = rs.getMetaData();
		int type = metadata.getColumnType(column);
		switch(type){
		case Types.BIGINT:
			return rs.getBigDecimal(column);
		case Types.BIT:
			return rs.getInt(column);
		case Types.BOOLEAN:
			return rs.getBoolean(column);
		case Types.CHAR:
			return rs.getString(column);
		case Types.DATE:
			return rs.getTimestamp(column);
		case Types.DECIMAL:
			return rs.getDouble(column);
		case Types.DOUBLE:
			return rs.getDouble(column);
		case Types.FLOAT:
			return rs.getFloat(column);
		case Types.INTEGER:
			return rs.getInt(column);
		case Types.NUMERIC:
			return rs.getDouble(column);
		case Types.SMALLINT:
			return rs.getInt(column);
		case Types.TIME:
			return rs.getTime(column);
		case Types.TIMESTAMP:
			return rs.getTimestamp(column);
		case Types.TINYINT:
			return rs.getInt(column);
			default:
			return rs.getString(column);
		}
	}
	
	public static Object dbSqlRun(ActionContext actionContext) throws IOException, TemplateException, SQLException, OgnlException{
		DataObject paramObject = (DataObject) actionContext.get("monitorTaskResource");
		return executeSqlTask(paramObject, actionContext);
		
	}
	
	public static Object taskDbSqlRun(ActionContext actionContext) throws IOException, TemplateException, SQLException, OgnlException{
		DataObject paramObject = (DataObject) actionContext.get("monitorTask");
		return executeSqlTask(paramObject, actionContext);
	}
	
	/**
	 * 执行SQL任务，其中SQL等是在参数中设置的。
	 * 
	 * @param paramObject
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws SQLException
	 * @throws OgnlException
	 */
	public static Object executeSqlTask(DataObject paramObject, ActionContext actionContext) throws IOException, TemplateException, SQLException, OgnlException{
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		DataObject datasource = (DataObject) actionContext.get("datasource");
		MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		Thing taskThing = (Thing) actionContext.get("taskThing");
		Connection con = (Connection) actionContext.get("con");
		String sql = paramObject.getString("param1");
		String sqlMessage = paramObject.getString("param2");
		String ognlExp = paramObject.getString("param3");
		String ognlMessage = paramObject.getString("param4");
		String dataNames = paramObject.getString("param5");
		int dataIdIndex = paramObject.getInt("param6");		
		//boolean subTaskSame = paramObject.getBoolean("param7");
		boolean saveMonitorDAta = paramObject.getBoolean("param8");
		String actionPath = paramObject.getString("param9");
		
		sql = UtilTemplate.processString(actionContext, sql);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			
			if(actionPath != null && !"".equals(actionPath)){
				Action action = World.getInstance().getAction(actionPath);
				if(action != null){
					return action.run(actionContext, UtilMap.toMap("resultSet", rs));
				}else{
					return "action not exists, actionPath=" + actionPath;
				}
			}
			
			String message = null;
			int columnCount = rs.getMetaData().getColumnCount();
			
			Map<String, Map<String, Object>> mapDatas = new HashMap<String, Map<String, Object>>();
			List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();
			actionContext.peek().put("vmap", mapDatas);
			actionContext.peek().put("vlist", listDatas);
			
			String[] columnMappings = null;
			if(dataNames != null && !"".equals(dataNames)){
				columnMappings = dataNames.split("[,]");
			}
			
			int rowcount = 1;
			while(rs.next()){
				//从结果集中取数据
				Map<String, Object> values = new HashMap<String, Object>();
				for(int i=1; i<=columnCount; i++){
					Object value = getValue(rs, i);
					values.put("v" + i, value);
					actionContext.peek().put("v" + i, value);
					
					if(columnMappings != null && i <= columnMappings.length){
						String name = columnMappings[i - 1].trim();
						if(name != null && !"".equals(name)){
							values.put(name, value);
						}
					}
				}
				
				//放入数据
				actionContext.peek().put("values", values);
				listDatas.add(values);
				mapDatas.put("r" + rowcount, values);
				rowcount ++;
				if(dataIdIndex > 0  && values.get("v" + dataIdIndex) != null){
					Object key = values.get("v" + dataIdIndex);
					mapDatas.put(key.toString(), values);
				}
			}
			
			//比较Ognl表达式
			if(ognlExp != null && !"".equals(ognlExp)){
				if(UtilData.getBoolean(OgnlUtil.getValue(ognlExp, actionContext), false) && ognlMessage != null){
					message = UtilTemplate.processString(actionContext, ognlMessage);
				}
			}
			
			//是否保存数据
			if(saveMonitorDAta){
				MonitorUtils.saveMonitorData(monitorContext, monitorTask, monitorTaskResource, taskThing, 
						datasource.get("id"), columnMappings, dataIdIndex, listDatas);
			}
			if(message == null && sqlMessage != null && !"".equals(sqlMessage)){
				message = UtilTemplate.processString(actionContext, sqlMessage);
			}
						
			//执行子任务
			if(monitorTask.getBoolean("childSameRes")){
				MonitorUtils.executeSubResTasks(monitorTaskResource, actionContext);
			}else{
				MonitorUtils.executeSubResTasks(null, actionContext);
			}
			return message;
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
		}	
	}
}
