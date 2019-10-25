package xworker.net.jsch;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.net.jsch.functions.JschFunctions;

public class AppSession {
	/**
	 * 创建JSCH会话。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException
	 */
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取服务器的数据对象
		if(self.getStringBlankAsNull("serverId") != null){
			DataObject server = new DataObject("xworker.app.server.dataobjects.Server");
			server.put("id", self.getLong("serverId"));
			server = server.load(actionContext);
			if(server == null){
				throw new ActionException("Server is not exists, id=" + self.getLong("serverId") + ",path=" + self.getMetadata().getPath());
			}
			
			//转化为会话事物
			actionContext.peek().put("server", server);
		}else{
			Object server = UtilData.getData(self, "server", actionContext);
			if(server != null){
				actionContext.peek().put("server", server);
			}else{
				throw new ActionException("Server is not exists, server=" + self.getString("server") + ",path=" + self.getMetadata().getPath());
			}
		}
		
		Thing sessionThing = JschFunctions.getSessonThingFromServer(actionContext);
		return sessionThing.doAction("create", actionContext);
	}
}
