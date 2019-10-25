package xworker.app.monitor;

import org.xmeta.ActionContext;
import org.xmeta.util.UtilData;

public class MonitorFunctions {
	public static void sendMessage(ActionContext actionContext){
		long messageId = UtilData.getLong(actionContext.get("messageId"), 0);
		String subject = UtilData.getString(actionContext.get("subject"), null);
		String content = UtilData.getString(actionContext.get("content"), null);
		Object object = actionContext.get("object");
		
		MessageCenter.sendMessage(messageId, subject, content, object, actionContext);
		
	}
}
