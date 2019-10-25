package xworker.http;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class REST {
	public static void httpDo(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String requestMethod = (String) actionContext.get("requestMethod");
		for(Thing child : self.getChilds()){
			if(child.getThingName().equals(requestMethod)){
				child.doAction("httpDo", actionContext);
				return;
			}
		}
		
		//未找到对应的资源
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		response.setStatus(404);
	}
}
