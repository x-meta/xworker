package xworker.app.userflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.dataObject.utils.DbUtil;
import xworker.util.UtilDate;

public class UserFlowActions {
	public static void start(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		for(Thing task : self.getChilds()){
			if(task.getBoolean("preventStartByParnet") == false){
				startTask(task, actionContext);
			}
		}
	}
	
	public static void finish(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DataObject task = getTask(self, actionContext);
		if(task != null){
			task.put("finishedCount", task.getInt("finishedCount") + 1);
			task.put("lastFinishedTime", new Date());
			task.put("status", 2);
			task.update(actionContext);				
			
			UserFlowManager.fireTaskFinished(task);
			
			//启动后续子节点
			List<Thing> nextTaskThings = self.doAction("getNextTaskThings", actionContext);
			for(Thing nextTaskThing : nextTaskThings){
				startTask(nextTaskThing, actionContext);
			}			
		}
	}
	
	public static DataObject getTask(Thing thing, ActionContext actionContext){
		List<DataObject> datas = DataObjectUtil.query("xworker.app.userflow.dataobjects.UserFlow", UtilMap.toMap("thingPath", thing.getMetadata().getPath()), actionContext);
		if(datas != null && datas.size() > 0){
			return datas.get(0);
		}else{
			return null;
		}
	}
	
	public static void startTask(ActionContext actionContext) throws SQLException{
		Thing self = actionContext.getObject("self");
		startTask(self, actionContext);
	}
	
	public static synchronized void  startTask(Thing taskThing , ActionContext actionContext) throws SQLException{
		//判断是否是被引用的任务
		if(taskThing.getStringBlankAsNull("referenceTask") != null){
			Thing refTask = World.getInstance().getThing(taskThing.getString("referenceTask"));
			if(refTask != null){
				taskThing = refTask;
			}
		}
		
		//排除非用户任务节点，比如启动子节点时也会把actions节点启动，需要排除这样的非任务子节点		
		Boolean isTask = (Boolean) taskThing.doAction("isUserFlowTask", actionContext);
		if(isTask == null || isTask == false){
			return;
		}
		
		//判断任务是否存在
		DataObject task = getTask(taskThing, actionContext);
		if(task == null){
			//不存在，新建一条记录
			task = new DataObject("xworker.app.userflow.dataobjects.UserFlow");
			task.put("thingPath", taskThing.getMetadata().getPath());
			task.put("status", 0);
			task.put("label", taskThing.getMetadata().getLabel());
			task.put("finishedCount", 0);
			task.put("daily", taskThing.getString("daily"));
			task = task.create(actionContext);
		}
		
		if(task == null){
			throw new ActionException("UserFlowTask not exists, path=" + taskThing.getMetadata().getPath());
		}
		
		//正在执行
		if(task.getInt("status") == UserTask.RUNNING){
			return;
		}
		
		//剩下的状态未启动或已结束，现在重新启动
		checkPreAndStart(taskThing, task, actionContext);
	}
	
	/**
	 * 自动检查任务的一个执行。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 */
	public static void autoCheckTasks(ActionContext actionContext) throws SQLException{
		//检查任务状态为0的任务，如果任务过多且有很多未执行的任务，应该通过某种方式清理掉
		List<DataObject> unstartTasks = DataObjectUtil.query("xworker.app.userflow.dataobjects.UserFlow", 
				UtilMap.toMap("status", 0), actionContext);
		
		for(DataObject task : unstartTasks){
			Thing flow = World.getInstance().getThing(task.getString("thingPath"));
			if(flow == null){
				task.delete(actionContext);
			}else{
				checkPreAndStart(flow, task, actionContext);
			}
		}
		
		//启动每日任务
		List<DataObject> dailyTasks = DataObjectUtil.query("xworker.app.userflow.dataobjects.UserFlow", 
				UtilMap.toMap("status", 2, "daily", "true"), actionContext);
		
		for(DataObject task : dailyTasks){
			Thing flow = World.getInstance().getThing(task.getString("thingPath"));
			if(flow == null){
				task.delete(actionContext);
			}else{
				//检查是否是今天
				if(task.getDate("lastFinishedTime") != null && !UtilDate.isToday(task.getDate("lastFinishedTime"))){				
					checkPreAndStart(flow, task, actionContext);
				}
			}
		}
	}
	
	/**
	 * 检查前置任务， 如果满足那么就启动。
	 * @throws SQLException 
	 */
	private static void checkPreAndStart(Thing flow , DataObject task, ActionContext actionContext) throws SQLException{
		//检查前置条件
		String preTasks = flow.getStringBlankAsNull("preTasks");
		if(preTasks != null){
			Thing dbFlow = World.getInstance().getThing("xworker.app.userflow.dataobjects.UserFlow");
			Thing dataSource = World.getInstance().getThing(dbFlow.getString("dataSource"));
			
			//获取数据库连接
			Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
			PreparedStatement pst = null;
			try{
				for(String pre : preTasks.split("[\n]")){
					pre = pre.trim();
					if("".equals(pre)){
						continue;
					}
					
					ResultSet rs = null;
					try{
						pst = con.prepareStatement("select finishedCount from tblAppUserFlow where thingPath=?");
						pst.setString(1, pre);
						rs = pst.executeQuery();
					
						if(rs.next()){
							int finishedCount = rs.getInt("finishedCount");
							if(finishedCount == 0){
								return;
							}
						}else{
							//没有记录，前置条件未完成
							return;
						}
					}finally{
						DbUtil.close(rs);
						DbUtil.close(pst);
					}
				}
				
			}finally{
				DbUtil.close(con);
			}
		}
		//判断前置条件，如果没有符合条件的返回false
		for(Thing preCondition : flow.getChilds("PreTasksCondition")){
			if(UtilData.isTrue(preCondition.doAction("isTrue", actionContext)) == false){
				return;
			}
		}
		
		
		//任务可以执行
		task.put("status", 1);
		task.put("lastStartTime", new Date());
		task.update(actionContext);
		
		UserFlowManager.fireTaskStarted(task);
		
		//检查是否是自动执行
		Boolean auto = (Boolean) flow.doAction("isAutoRun", actionContext);
		if(auto != null && auto == true){
			flow.doAction("run", actionContext);
		}
	}
	
	public static void taskRun(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//执行任务
		self.doAction("doTask", actionContext);
		
		//如果自动结束，则结束
		if(UtilData.isTrue(self.doAction("isAutoFinish", actionContext))){
			self.doAction("finish", actionContext);
		}
	}
	
	public static Boolean isAutoRun(ActionContext actionContext){
		return true;
	}
	
	public static Boolean unAutoRun(ActionContext actionContext){
		return false;
	}
	
	public static Boolean isUserFlowTask(ActionContext actionContext){
		return true;
	}
}
