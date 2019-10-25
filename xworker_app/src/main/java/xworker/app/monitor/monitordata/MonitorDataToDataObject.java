package xworker.app.monitor.monitordata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.dataObject.query.UtilCondition;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.dataObject.utils.DbUtil;

public class MonitorDataToDataObject {
	private static Logger logger = LoggerFactory.getLogger(MonitorDataToDataObject.class);
	
	/**
	 * 监控数据的归类。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		//DataObject monitorTaskTask = (DataObject) actionContext.get("monitorTaskTask");
		List<DataObject> resources = (List<DataObject>) actionContext.get("resources");
		MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		
		World world = World.getInstance();
		Thing monitorDataThing = World.getInstance().getThing("xworker.app.monitor.dataobjects.MonitorData");
		Thing datasourceThing = world.getThing(monitorDataThing.getString("dataSource"));
		
		Connection con = (Connection) datasourceThing.doAction("getConnection", actionContext);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//查找最后一次任务执行的时间
			Date updateTime = null;
			pst = con.prepareStatement("select updateTime from tblmonitordatacategorylog where monitorDataCategory=?");
			pst.setString(1, self.getMetadata().getPath());
			rs = pst.executeQuery();
			if(rs.next()){
				updateTime = rs.getTimestamp("updateTime");
			}
			rs.close();
			pst.close();
			
			
			Date lastToDataObjectTime = null; //最后一次存入数据对象的记录			
			for(DataObject resource : resources){
				long targetMonitorId = resource.getLong("resId");
				String dataObjectPath = resource.getString("param1");
				String startTimeName = resource.getString("param3");
				String endTimeName = resource.getString("param4");
				String durationTimeName = resource.getString("param5");
				String mappingStr = resource.getString("param2");
				String resourceName = resource.getString("param6");
				
				//删除更新日期之后的数据
				DataObjectUtil.deleteBatch(dataObjectPath,
						UtilMap.toMap(startTimeName, updateTime == null ? new Date(0) : updateTime, 
										"op_" + startTimeName, UtilCondition.gt), actionContext);
				
				Map<String, String> mappings = new HashMap<String, String>();
				for(String m : mappingStr.split("[,]")){
					String ms[] = m.split("[:]");
					if(ms.length >= 2){
						mappings.put(ms[0], ms[1]);
					}
					
				}
				//从监控数据中获取数据
				String sql = "select * from tblmonitordata where monitorId=? and dataName in(";
				Iterator<String> keys = mappings.keySet().iterator();
				int index = 0;
				while(keys.hasNext()){
					if(index != 0){
						sql = sql + ",";
					}
					
					sql = sql + "?";
					keys.next();
					index++;
				}
				sql = sql + ")";
				
				if(updateTime != null){
					sql = sql + " and grabStartTime > ?";
				}
				sql = sql + " order by resourceId, grabStartTime asc";
				pst = con.prepareStatement(sql);
				index = 1;
				pst.setLong(1, targetMonitorId);
				index++;
				for(String key : mappings.keySet()){
					pst.setString(index, key);
					index++;
				}
				
				if(updateTime != null){
					pst.setTimestamp(index, new java.sql.Timestamp(updateTime.getTime()));
					index++;
				}
				rs = pst.executeQuery();
				
				//最后一条数据的时间，用来区分是否是一条新的记录
				Date lastDataTime = null;
				DataObject dataObject = null;
				Date maxGrabEndTime = null;
				String resourceId = null;
				
				while(rs.next()){
					String currentResourceId = rs.getString("resourceId");
					String dataName = rs.getString("dataName");
					String value = rs.getString("value");
					Date grabStartTime = rs.getTimestamp("grabStartTime");
					Date grabEndTime = rs.getTimestamp("grabEndTime");
					if(maxGrabEndTime == null || maxGrabEndTime.getTime() < grabEndTime.getTime()){
						maxGrabEndTime = grabEndTime;
					}				
					
					if(resourceId == null || !currentResourceId.equals(resourceId) || lastDataTime == null || !grabStartTime.equals(lastDataTime)){
						resourceId = currentResourceId;
						//新的记录
						if(dataObject != null){
							//如果数据对象已经存在，那么执行插入操作
							putValue(dataObject, endTimeName, maxGrabEndTime);
							putValue(dataObject, durationTimeName, maxGrabEndTime.getTime() - lastDataTime.getTime());
							
							Date time = dataObject.getDate(startTimeName);
							if(lastToDataObjectTime == null || time.getTime() > lastToDataObjectTime.getTime()){
								lastToDataObjectTime = time;
							}
							dataObject.create(actionContext);
						}
						
						//新建记录
						dataObject = new DataObject(dataObjectPath);					
						maxGrabEndTime = grabEndTime;
					    lastDataTime = grabStartTime;
					    
					    putValue(dataObject, startTimeName, grabStartTime);
					    putValue(dataObject, resourceName, resourceId);

					}

					//放入映射的数据
					if(mappings.containsKey(dataName)){
						putValue(dataObject, mappings.get(dataName), value);
					}					
				}
			}
			rs.close();
			pst.close();
			
			//插入或更新最后存储的日期
			if(updateTime == null && lastToDataObjectTime != null){
				pst = con.prepareStatement("insert into tblmonitordatacategorylog(monitorDataCategory, updateTime) values(?, ?)");
				pst.setString(1, self.getMetadata().getPath());
				pst.setTimestamp(2, new java.sql.Timestamp(lastToDataObjectTime.getTime()));
				pst.execute();				
			}else if(lastToDataObjectTime != null){
				pst = con.prepareStatement("update tblmonitordatacategorylog set updateTime=? where monitorDataCategory=?");
				pst.setTimestamp(1, new java.sql.Timestamp(lastToDataObjectTime.getTime()));
				pst.setString(2, self.getMetadata().getPath());				
				pst.execute();
			}
			
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}	
	}
	
	private static  void putValue(DataObject dataObject, String name, Object value){
		if(name != null){
			dataObject.put(name, value);
		}
	}
	
	static class Mapping{
		String sourceName;
		String targetName;
		
		public Mapping(String sourceName, String targetName){
			this.sourceName = sourceName;
			this.targetName = targetName;
		}
	}
	
	static class TaskMapping{
		public String taskPath;
		public String resourceIdMappingName;
		public String dataIdMappingName;
		
		Map<String, DataMapping> dataMappings = new HashMap<String, DataMapping>();
		
		public TaskMapping(Thing thing){
			this.resourceIdMappingName = thing.getStringBlankAsNull("resourceIdMappingName");
			this.taskPath = thing.getStringBlankAsNull("taskPath");
			this.dataIdMappingName = thing.getStringBlankAsNull("dataIdMappingName");
			
			for(Thing child : thing.getChilds("DataNameMapping")){
				DataMapping dataMapping = new DataMapping(child);
				dataMappings.put(dataMapping.sourceDataName, dataMapping);				
			}
		}
	}
	
	static class DataMapping{
		public String sourceDataName;
		public String targetDataName;
		
		public DataMapping(Thing thing){
			this.sourceDataName = thing.getStringBlankAsNull("sourceDataName");
			this.targetDataName = thing.getStringBlankAsNull("targetDataName");
		}
	}
}
