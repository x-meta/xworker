package xworker.httpclient;

import org.apache.http.HttpMessage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class HeaderActions {
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void addHeader(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HttpMessage httpMessage = (HttpMessage) actionContext.get("httpMessage");
		httpMessage.addHeader(self.getString("name"), self.getString("value"));
	}
	
	public static void setHeader(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HttpMessage httpMessage = (HttpMessage) actionContext.get("httpMessage");
		httpMessage.setHeader(self.getString("name"), self.getString("value"));
	}
	
	public static void addHeaders(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HttpMessage httpMessage = (HttpMessage) actionContext.get("httpMessage");
		String value = self.getStringBlankAsNull("headers");
		if(value != null){
			for(String v : value.split("[,]")){
				String[] vs = v.split("=");
				if(vs.length >= 2){
					httpMessage.addHeader(vs[0], vs[1]);
				}
			}
		}
	}
	
	public static void setHeaders(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HttpMessage httpMessage = (HttpMessage) actionContext.get("httpMessage");
		String value = self.getStringBlankAsNull("headers");
		if(value != null){
			for(String v : value.split("[,]")){
				String[] vs = v.split("=");
				if(vs.length >= 2){
					httpMessage.setHeader(vs[0], vs[1]);
				}
			}
		}
	}
}
