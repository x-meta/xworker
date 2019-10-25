package xworker.app.monitor.monitordata;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.dataObject.utils.DbUtil;
import xworker.util.UtilTemplate;

public class CompareLastTwoValues {
	@SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext) throws SQLException, OgnlException, IOException, TemplateException{
		//DataObject monitor = (DataObject) actionContext.get("monitor");
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
			for(DataObject resource : resources){
				String resId = resource.getString("resId");
				//目标监控任务
				DataObject taskObj = new DataObject("xworker.app.monitor.dataobjects.ResMonitorTaskItems");
				taskObj.set("id", resId);
				taskObj = taskObj.load(actionContext);
				if(taskObj == null){
					monitorContext.appendContent(monitorTask, resource, resource.getString("resName"), "监控任务不存在，taskId=" + resId);
					continue;
				}
				if(taskObj.getBoolean("status") == false){
					monitorContext.appendContent(monitorTask, resource, resource.getString("resName"), "监控任务已停止，不检查数据，taskId=" + resId);
					continue;
				}
							
				//目标监控
				DataObject monitorObj = new DataObject("xworker.app.monitor.dataobjects.ResMonitor");
				monitorObj.set("id", taskObj.get("monitorId"));
				monitorObj = monitorObj.load(actionContext);
				if(monitorObj == null){
					monitorContext.appendContent(monitorTask, resource, resource.getString("resName"), "监控不存在，监控可能已删除，taskId=" + resId + ",monitorId=" + taskObj.get("monitorId"));
					continue;
				}
				if(monitorObj.getBoolean("status") == false){
					monitorContext.appendContent(monitorTask, resource, resource.getString("resName"), "监控已停止，不检查数据，taskId=" + resId + ",monitorId=" + taskObj.get("monitorId"));
					continue;
				}
				long interval = getInterval(monitorObj);
				
				//目标监控任务
				List<DataObject> targetResources = DataObjectUtil.query("xworker.app.monitor.dataobjects.ResMonitorResItems", 
						UtilMap.toMap("taskId", resId), actionContext);
				for(DataObject targetResource : targetResources){
					long monitorId = monitorObj.getLong("id");
					long taskId = taskObj.getLong("id");
					long resourceId = targetResource.getLong("id");
					String dataName = resource.getString("param6");
					
					pst = con.prepareStatement("select * from tblmonitordata where monitorId=? and monitorTaskId=? and monitorTaskResId=? and grabStartTime > ? and dataName =? order by grabStartTime desc");
					pst.setLong(1, monitorId);
					pst.setLong(2, taskId);
					pst.setLong(3, resourceId);
					pst.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis() - 5 * interval));
					pst.setString(5, dataName);
					rs = pst.executeQuery();
					
					double v2 = 0;
					boolean v2Setted = false;
					double v1 = 0;
					boolean v1Setted = false;
					while(rs.next()){
						String value = rs.getString("value");
						Date time = rs.getTimestamp("grabStartTime");
						long currentTime = System.currentTimeMillis();
						long subTime = currentTime - time.getTime();
						if(!v2Setted){
							if(subTime < 2 * interval){
								//第一个值可以作为最后一个记录
								v2 = Double.parseDouble(value);
								v2Setted = true;
							}else if(subTime < 3 * interval){
								//第一个值可作为倒数第二条记录
								v1 = Double.parseDouble(value);
								v1Setted = true;
								break;
							}else{
								//值都是过期的数据
								break;
							}
						}else if(currentTime - time.getTime() < 3 * interval){
							v1 = Double.parseDouble(value);
							v1Setted = true;
							break;
						}else{
							break;
						}
					}
					
					//System.out.println(v1Setted)
					
					String ognlExp = resource.getString("param1");
					String message = resource.getString("param2");
					String errType = resource.getString("param3");
					String errMessage = resource.getString("param4");
					String actionPath = resource.getString("param5");
					actionContext.peek().put("v2", v2);
					actionContext.peek().put("v1", v1);
					actionContext.peek().put("v2Have", v2Setted);
					actionContext.peek().put("v2Have", v1Setted);
					if(v1Setted && v2Setted){
						if(ognlExp != null){
							
							if(UtilData.getBoolean(OgnlUtil.getValue(ognlExp, actionContext), false)){
								//发送消息
								message = UtilTemplate.processString(actionContext, message);
								if(message != null){
									monitorContext.appendContent(monitorTask, resource, resource.getString("resName"), message);
								}
								
								//执行动作
								if(actionPath != null && !"".equals(actionPath)){
									Action action = world.getAction(actionPath);
									if(action != null){
										action.run(actionContext, UtilMap.toMap("monitorTaskResource", resource, "targetResource", targetResource));
									}else{
										monitorContext.appendContent(monitorTask, resource, resource.getString("resName"), "action is null, path=" + actionPath);
									}
								}
							}							
						}
					}else if("sendMessage".equals(errType)){
						if(errMessage != null && !"".equals(errMessage)){
							errMessage = UtilTemplate.processString(actionContext, errMessage);
						}else{
							errMessage = "数据不齐全，无法比较，v2Have=" + v2Setted + ",v1Have=" + v1Setted; 
						}
					}
				}
			}
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
		
	}
	
	/**
	 * 取得监控周期的毫秒数。
	 * 
	 * @param monitor
	 */
	public static long getInterval(DataObject monitor){
		long period = monitor.getLong("period");
		String timeUnit = monitor.getString("timeUnit");
		if("DAYS".equals(timeUnit)){
			return period * 24 * 36000 * 1000;
		}else if("HOURS".equals(timeUnit)){
			return period * 36000 * 1000;
		}else if("MINUTES".equals(timeUnit)){
			return period * 60 * 1000;
		}else if("SECONDS".equals(timeUnit)){
			return period * 1000;
		}else{
			return period;
		}
	}
}
