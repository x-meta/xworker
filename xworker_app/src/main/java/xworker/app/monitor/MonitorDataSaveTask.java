package xworker.app.monitor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;

import xworker.dataObject.DataObject;

/**
 * 专门用于保存监控数据的线程。
 * 
 * @author Administrator
 *
 */
public class MonitorDataSaveTask {
	private static Logger logger = LoggerFactory.getLogger(MonitorDataSaveTask.class);
	
	private static List<DataObject> datas = new ArrayList<DataObject>();
	private static Runnable saveRun = new Runnable(){
		ActionContext actionContext = new ActionContext();
		public void run(){
			while(datas.size() > 0){
				DataObject data = null;
				synchronized(datas){
					data = datas.remove(0);
				}
				
				//logger.info("insert montior data :" + data);
				try{
					data.create(actionContext);
				}catch(Exception e){
					logger.error("save  monitor data error, data=" + data, e);
				}
								
			}
		}
	};
	
	private static List<DataObject> updateDatas = new ArrayList<DataObject>();
	private static Runnable updateRun = new Runnable(){
		ActionContext actionContext = new ActionContext();
		public void run(){
			while(updateDatas.size() > 0){
				DataObject data = null;
				synchronized(updateDatas){
					data = updateDatas.remove(0);
				}
				
				//logger.info("insert montior data :" + data);
				try{
					data.update(actionContext);
				}catch(Exception e){
					logger.error("save  monitor data error, data=" + data, e);
				}
								
			}
		}
	};
	
    public static void addCreateData(DataObject monitorData){
    	synchronized(datas){
		    datas.add(monitorData);
				
			if(datas.size() == 1){
				MonitorUtils.getScheduledExecutorService().execute(saveRun);
			}
		}
	}
    
    public static void addUpdateData(DataObject monitorData){
    	synchronized(updateDatas){
    		updateDatas.add(monitorData);
				
			if(updateDatas.size() == 1){
				MonitorUtils.getScheduledExecutorService().execute(updateRun);
			}
		}
	}
    
}
