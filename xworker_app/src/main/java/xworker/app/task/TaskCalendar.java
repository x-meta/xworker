package xworker.app.task;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;

public class TaskCalendar {
	private static Logger logger = LoggerFactory.getLogger(TaskCalendar.class);
	
	/**
	 * 日期任务定时器，执行顶确定好了的日期的任务。
	 */
	@SuppressWarnings({"unchecked" })
	public static void run(ActionContext actionContext){
		//从数据库中读取要执行的任务
		Thing dbThing = World.getInstance().getThing("xworker.app.task.dataobjects.TaskCalandar");
		List<DataObject> datas = (List<DataObject>) dbThing.doAction("query", actionContext);
		
		//遍历执行任务
		for(DataObject data : datas){
			Date runDate = data.getDate("runDate");
			
			if(System.currentTimeMillis() > runDate.getTime()){
				if(System.currentTimeMillis() - runDate.getTime() > 60000){
					
				}else{
					//执行任务，并删除任务
					Thing actionThing = World.getInstance().getThing(data.getString("thingPath"));
					if(actionThing != null){
						try{
							actionThing.doAction("run", actionContext);
						}catch(Exception e){
							logger.error("Execute TaskCalendar error, path=" + actionThing.getMetadata().getPath(), e);
						}
					}
				}
				
				//对于超时的任务，不执行直接删除
				data.doAction("delete", actionContext);
			}
		}
				
	}
}
