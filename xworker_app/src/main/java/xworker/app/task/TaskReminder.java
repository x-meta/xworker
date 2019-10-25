package xworker.app.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

/**
 * 任务提醒。
 * 
 * @author zyx
 *
 */
public class TaskReminder {
	private static Logger logger = LoggerFactory.getLogger(TaskReminder.class);
	private static long lastCheckTime = 0;
	
	public static void run(ActionContext actionContext){
		List<DataObject> tasks = DataObjectUtil.query("xworker.app.task.dataobjects.TaskReminders", null, actionContext);
		long checkTime = System.currentTimeMillis();
		
		try{
			for(DataObject task : tasks){
				try{
					Date date = null;
					Date nextTime = task.getDate("nextTime");
					boolean fixDate = false;
					String remindeDate = task.getString("remindeDate");
					if(remindeDate == null || "".equals(remindeDate)){
						continue;
					}else if(remindeDate.length() == 19){
						fixDate = true;
					}else if(remindeDate.length() == 11){
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
						remindeDate = sf.format(new Date()) + "-" + remindeDate;
					}else if(remindeDate.length() == 14){
						SimpleDateFormat sf = new SimpleDateFormat("yyyy");
						remindeDate = sf.format(new Date()) + "-" + remindeDate;				
					}else if(remindeDate.length() == 8){
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
						remindeDate = sf.format(new Date()) + " " + remindeDate;					
					}else if(remindeDate.length() == 5){
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH");
						remindeDate = sf.format(new Date()) + ":" + remindeDate;					
					}	else if(remindeDate.length() == 2){
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						remindeDate = sf.format(new Date()) + ":00";					
					}
					
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					date = sf.parse(remindeDate);
					
					//lastCheckTime < date.getTime()是为了避免重复校验
					if(date != null) {
						if((checkTime >= date.getTime()  && lastCheckTime < date.getTime()) ||
								(nextTime != null && checkTime >= nextTime.getTime()  && lastCheckTime < nextTime.getTime())) {
							//发送系统消息
							task.doAction("notify", actionContext);
							
							Action action = World.getInstance().getAction(task.getString("actionPath"));
							if(action != null){
								action.run(new ActionContext(), UtilMap.toMap("task", task));
							}
						}
						
						if(fixDate && ((lastCheckTime - date.getTime()) > 7 * 24 * 3600000)){
							//删除已失效7天的数据
							task.delete(actionContext);
						}
					}					
				}catch(Exception e){
					logger.error("check remind task error, task=" + task, e);
				}
			}
		}finally{
			lastCheckTime = checkTime;
		}
	}
		
}
