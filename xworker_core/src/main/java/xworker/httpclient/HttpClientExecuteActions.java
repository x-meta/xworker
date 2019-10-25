package xworker.httpclient;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;

public class HttpClientExecuteActions {
	private static Logger logger = LoggerFactory.getLogger(HttpClientExecuteActions.class);
	
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		try{
			//获取HttpClient
			HttpClient httpClient = null;
			String httpClientPath = self.getStringBlankAsNull("httpclient");
			Thing httpClientThing = null;
			if(httpClientPath != null){
				httpClientThing = World.getInstance().getThing(httpClientPath);			
			}
			if(httpClientThing == null){
				httpClientThing = self.getThing("HttpClient@0");
			}
			if(httpClientThing != null){
				httpClient = (HttpClient) httpClientThing.doAction("getHttpClient", actionContext);
			}
			if(httpClient == null){
				throw new ActionException("HttpClinet is null, HttpClientExecute=" + self.getMetadata().getPath());
			}
			
			//执行Httpmessage
			Thing httpMessages = self.getThing("HttpUriRequests@0");
			if(httpMessages != null){
				int exceptionStatusCode = self.getInt("exceptionStatusCode", 0);
				Object result = null;
				for(Thing message : httpMessages.getChilds()){
					HttpUriRequest httpMessage = (HttpUriRequest) message.doAction("create", actionContext, UtilMap.toMap(new Object[]{
							"httpClinet", httpClient,
							"requestThing", message
					}));
					HttpResponse httpResponse = null;
					try{
						
						httpResponse = httpClient.execute(httpMessage);
						if(exceptionStatusCode != 0 && httpResponse.getStatusLine().getStatusCode() > exceptionStatusCode){
							result = fireEvent(self, "onException", 
									UtilMap.toMap(new Object[]{"exception", new ActionException("Response status code is error, " + httpResponse.getStatusLine().toString()),
											"response", httpResponse,
											"entity", httpResponse.getEntity(),
											"requestThing", message,
											"request", httpMessage}), 
									actionContext);
						}else{
							result = fireEvent(self, "onSuccess", 
									UtilMap.toMap(new Object[]{
											"response", httpResponse,
											"entity", httpResponse.getEntity(),
											"requestThing", message,
											"request", httpMessage}), 
									actionContext);
						}
						
						if(actionContext.getStatus() != ActionContext.RUNNING){
							actionContext.setStatus(ActionContext.RUNNING);
							break;
						}
					}finally{
						if(httpResponse != null){
							EntityUtils.consume(httpResponse.getEntity());
						}
					}
				}
				
				return result;
			}else{
				return null;
			}
		}catch(Exception e){
			//总异常处理
			return fireEvent(self, "onException", UtilMap.toMap(new Object[]{"exception", e,
					"response", null,
					"entity", null,
					"requestThing", null,
					"request", null}), actionContext);
		}
	}
	
	public static Object fireEvent(Thing self, String method, Map<String, Object> params, ActionContext actionContext){
		try{
			Bindings bindings = actionContext.push(null);
			bindings.putAll(params);
			return self.doAction(method, actionContext);
		}finally{
			actionContext.pop();
		}
	}
	
	public static Object getUri(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		return UtilTemplate.processString(actionContext, self.getString("uri"));
	}
	
	public static Object createHttpGet(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String uri = (String) self.doAction("getUri", actionContext);
		HttpGet httpGet = new HttpGet(uri);
		createHeader(self, httpGet, actionContext);
		return httpGet;
	}
	
	public static void createHeader(Thing self, HttpMessage httpMessage, ActionContext actionContext){
		Thing headers = self.getThing("Headers@0");
		if(headers != null){
			try{
				Bindings bindings = actionContext.push(null);
				bindings.put("httpMessage", httpMessage);
				headers.doAction("create", actionContext);
			}finally{
				actionContext.pop();
			}
		}
	}
	
	public static Object createHttpPost(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String uri = (String) self.doAction("getUri", actionContext);
		HttpPost httpPost = new HttpPost(uri);
		createHeader(self, httpPost, actionContext);
		
		HttpEntity entity = (HttpEntity) self.doAction("createEntity", actionContext);
		if(entity != null){
			httpPost.setEntity(entity);
		}
		
		return httpPost;
	}
	
	public static Object createHttpEntity(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		if(self.getChilds().size() > 0){
			return self.getChilds().get(0).doAction("create", actionContext);
		}else{
			return null;
		}
	}
	
	public static Object createHttpPostEntity(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing entity = self.getThing("Entity@0");
		if(entity != null){
			return entity.doAction("create", actionContext);
		}else{
			return null;
		}		
	}
	
	public static void onSuccess(ActionContext actionContext) throws ParseException, IOException{
		HttpUriRequest request = (HttpUriRequest) actionContext.get("request");
		HttpEntity entity = (HttpEntity) actionContext.get("entity");
		HttpResponse response = (HttpResponse) actionContext.get("response");
		logger.info(request.toString() + "\r\n" + response.getStatusLine() + "\r\n" + EntityUtils.toString(entity));
	}
	
	public static void onException(ActionContext actionContext) throws Exception{
		HttpUriRequest request = (HttpUriRequest) actionContext.get("request");
		HttpEntity entity = (HttpEntity) actionContext.get("entity");
		HttpResponse response = (HttpResponse) actionContext.get("response");
		if(request != null){
			logger.info(request.toString() + "\r\n" + response.getStatusLine() + "\r\n" + EntityUtils.toString(entity));
		}
		
		throw (Exception) actionContext.get("exception");
	}
}
