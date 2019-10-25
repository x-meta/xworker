package xworker.app.monitor.res;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;

import xworker.app.monitor.MonitorUtils;
import xworker.dataObject.DataObject;

/**
 * 监控任务的超时，处理任务已结束和超时。
 * 
 * @author Administrator
 *
 */
public class CheckResMonitorTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(CheckResMonitorTask.class);
	
	public List<MonitorContext> contexts = new ArrayList<MonitorContext>();
		
	public void run(){
		try{
			for(int i=0; i<contexts.size(); i++){
				final MonitorContext context = contexts.get(i);
				if(context == null){
					contexts.remove(i);
					i--;
				}else if(context.isFinished()){
					context.sendMail();
					
					contexts.remove(i);
					i--;
				}else if(context.isTimeout()){
					MonitorUtils.getScheduledExecutorService().execute(new Runnable(){
						public void run(){
							try{
								//停止任务
								DataObject monitor = context.monitor;
								Future<?> future = (Future<?>) monitor.get("___future___");
								if(future != null){
									future.cancel(true);
								}
								
								//监控改为不激活
								monitor.put("status", 0);
								monitor.update(new ActionContext());
								
								context.sendMail();
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
					
					contexts.remove(i);
					i--;
				}
			}
		}catch(Exception e){
			logger.error("CheckResMonitorTask error", e);
		}
	}
}
