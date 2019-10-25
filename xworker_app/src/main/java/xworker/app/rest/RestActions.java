package xworker.app.rest;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.httpclient.HttpClientManager;

public class RestActions {
	/**
	 * RestResource的getUrl方法的实现。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object get(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//获得Get
		Thing getThing = self.getThing("Get@0");
		if(getThing == null){
			throw new ActionException("Get not defiend, path=" + self.getMetadata().getPath());
		}
		
		//获取URL
		String url = (String) self.doAction("getUrl", actionContext);
		
		HttpGet get = new HttpGet(url);
		
		return doRequest(self, getThing, get, actionContext);
	}
	
	public static Object doRequest(Thing self, Thing requestThing, HttpUriRequest request, ActionContext actionContext){
		//httpClient
		HttpClient httpclient = HttpClientManager.getDefaultHttpClient();
		
		HttpResponse response = null;
		HttpEntity entity = null;
	    
	    
		try{		    
		    response = httpclient.execute(request);
		    entity = response.getEntity();
		    
		    int status = response.getStatusLine().getStatusCode();
		    String content = EntityUtils.toString(entity);
		    
		    Thing responseThing = getResponseBodyThing(requestThing, status);
		    
		    
		    return self.doAction("onSuccess", actionContext);
		}catch(Exception e){
			throw new ActionException("Execute http request error, " + request + ", path=" + self.getMetadata().getPath(), e);			
		}finally{
		    if(entity != null){
		        try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		}
	}
	
	public static Thing parseResponse(Thing responseThing, String content){
		Thing bodyThing = responseThing.getThing("ResponseBody@0");
		if(bodyThing == null){
			return null;
		}
		
		String format = responseThing.getString("format");
		if("json".equals(format)){
			
		}
		
		return null;
	}
	
	public static Thing getResponseBodyThing(Thing requestThing, int httpStatus){
		for(Thing thing : requestThing.getChilds("Response")){
			if(thing.getInt("status") == httpStatus){
				return thing;
			}
		}

		return null;
	}
}

