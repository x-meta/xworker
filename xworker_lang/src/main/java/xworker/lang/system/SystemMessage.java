package xworker.lang.system;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SystemMessage {
	//private static Logger logger = LoggerFactory.getLogger(SystemMessage.class);
	
	public static void sendMessage(String title, String htmlContent){
		//logger.info("System message, title={}, content={}", title, htmlContent);
		Thing notify = new Thing("xworker.notification.Notification");
		
		notify.put("label", title);
		notify.put("description", htmlContent);
		
		notify.doAction("run", new ActionContext());
	}
}
