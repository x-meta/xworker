package xworker.app.monitor.res;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;

public class MessageSender {
	private static Logger logger = LoggerFactory.getLogger(MessageSender.class);
	
	public static void sendMessage(DataObject monitor, String subject, String content){
		try{
			logger.info("发送监控邮件，监控标识："  + monitor.get("id"));
			String mailSessionThingStr = monitor.getString("mailSessionThing");
			if(mailSessionThingStr != null){
				Thing mailSessionThing = World.getInstance().getThing(mailSessionThingStr);
				long receiveUserGroupId = monitor.getLong("mailReceiveUserGroupId");
				if(mailSessionThing != null && receiveUserGroupId != 0){
					Action sendMail = World.getInstance().getAction("xworker.app.monitor.MonitorActions/@actions/@SendMail");
					ActionContext ac = new ActionContext();
					ac.put("sessionThing", mailSessionThing);
					ac.put("userGroupId", receiveUserGroupId);
					ac.put("subject", subject);
					ac.put("content", content);
					sendMail.run(ac);
				}
			}	
		}catch(Exception e){
			logger.error("监控发送邮件失败，监控标识：" + monitor.get("id") + "，内容：" + content, e);
		}
	}
}
