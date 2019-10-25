package xworker.app.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class MessageCenter {
	/** 
	 * 通过tblMonitorMessage注册的对应于消息标识的Handler。
	 */
	private static Map<Long, Thing> handlers = new HashMap<Long, Thing>();
	
	private static long lastLoadHandlerTime = 0;
	
	private static synchronized void reloadHandlers(){
		Thing dataObject = World.getInstance().getThing("xworker.app.monitor.dataobjects.Message");
		Thing conditionConfig = DataObjectUtil.mapToConditionThing(UtilMap.toMap(new Object[]{
				"attributeName", "status", "dataName", "status"
		}));
		Map<String, Object> conditionData = UtilMap.toMap(new Object[]{"status", 1});
		
		List<DataObject> datas = DataObjectUtil.query(dataObject, conditionConfig, conditionData, null);
		Map<Long, Thing> handlers_ = new HashMap<Long, Thing>();
		for(DataObject data : datas){
			Long id = (Long) data.get("id");
			String thingPath = (String) data.get("handler");
			Thing handler = World.getInstance().getThing(thingPath);
			if(handler != null){
				handlers_.put(id, handler);
			}
		}
		
		handlers = handlers_;
	}
	
	/**
	 * 通过消息标识发送一个消息。
	 * 
	 * @param messageId
	 * @param subject
	 * @param content
	 * @param actionContext
	 */
	public static void sendMessage(long messageId, String subject, String content, Object object, ActionContext actionContext){
		if(System.currentTimeMillis() - lastLoadHandlerTime > 300000){
			//5分钟刷新一次
			reloadHandlers();
		}
		
		//消息处理器
		Thing handler = handlers.get(messageId);
		
		if(handler != null){
			handler.doAction("run", actionContext, UtilMap.toMap(new Object[]{
					"subject", subject, "content", content, "object", object
			}));
		}
	}
	
	/**
	 * 动作xworker.app.monitor.MonitorActions/@SendMessage的实现。
	 * 
	 * @param actionContext
	 * @throws OgnlException
	 */
	public static void sendMessage(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		long messageId = UtilData.getLong(UtilData.getData(self, "messageId", actionContext), -1);
		if(messageId != -1){
			String subject = UtilString.getString(self, "subject", actionContext);
			String content = UtilString.getString(self, "content", actionContext);
			Object object = UtilData.getData(self, "object", actionContext);
			
			sendMessage(messageId, subject, content, object, actionContext);
		}
	}
}
