package xworker.httpclient.actions;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.xmeta.*;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;
import xworker.httpclient.HttpClientManager;
import xworker.lang.executor.Executor;
import xworker.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpClientActions {
	private static final String TAG = HttpClientActions.class.getName();
			
	public static void addHeader(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		HttpRequestBase request = (HttpRequestBase) actionContext.get("request");
		String name = (String) self.doAction("getName", actionContext);
		String value = (String) self.doAction("getValue", actionContext);
		
		if(name != null && value != null && !"".equals(value.trim())){
			request.addHeader(name, value);
		}
	}
	
	public static void initRequest(Thing self, HttpRequestBase request, ActionContext actionContext){
		actionContext.peek().put("request", request);

		//headers
		Map<String, Object> headers = self.doAction("getHeaders", actionContext);
		if(headers != null){
			for(String key : headers.keySet()){
				request.addHeader(key, String.valueOf(headers.get(key)));
			}
		}
		for(Thing header : self.getChilds("Header")){
			header.doAction("initRequest", actionContext);
		}

		//requestConfig
		RequestConfig requestConfig = self.doAction("getRequestConfig", actionContext);
		if(requestConfig != null){
			request.setConfig(requestConfig);
		}else{
			Thing requestConfigThing = self.doAction("getRequestConfigThing", actionContext);
			if(requestConfigThing != null){
				requestConfig = requestConfigThing.doAction("create", actionContext);
				if(requestConfig != null){
					request.setConfig(requestConfig);
				}
			}
		}

		self.doAction("initRequest", actionContext);
	}
	
	public static Object post(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");

		//httpPost
		HttpPost httpPost = new HttpPost(getURI(self, actionContext));

		//entity
		HttpEntity entity = self.doAction("getEntity", actionContext);
		if(entity == null){
			Thing entityThing = self.doAction("getEntityThing", actionContext);
			if(entityThing != null){
				entity = entityThing.doAction("create", actionContext);
			}
		}

		if(entity != null){
			httpPost.setEntity(entity);
		}

	    HttpClientActions.initRequest(self, httpPost, actionContext);
	    return doRequest(self, httpPost, actionContext);
	}
	
	/**
	 * Httpclient的GET方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws Exception 
	 */
	public static Object get(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");

		//HttpGet
		HttpGet httpGet = new HttpGet(getURI(self, actionContext));

	    HttpClientActions.initRequest(self, httpGet, actionContext);
	    
		return doRequest(self, httpGet, actionContext);
	}
	
	public static Object delete(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//HttpRequest
		HttpDelete httpRequest = new HttpDelete(getURI(self, actionContext));

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object head(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//HttpRequest
		HttpHead httpRequest = new HttpHead(getURI(self, actionContext));

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object options(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//HttpRequest
		HttpOptions httpRequest = new HttpOptions(getURI(self, actionContext));

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object patch(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//HttpRequest
		HttpPatch httpRequest = new HttpPatch(getURI(self, actionContext));

		HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object put(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//HttpRequest
		HttpPut httpRequest = new HttpPut(getURI(self, actionContext));

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}

	private static URI getURI(Thing self, ActionContext actionContext) throws URISyntaxException {
		Object uriObj = self.doAction("getUri", actionContext);
		URIBuilder builder = null;
		if(uriObj instanceof  String){
			builder = new URIBuilder((String) uriObj);
		}else if(uriObj instanceof  URI){
			builder = new URIBuilder((URI) uriObj);
		}else{
			builder = new URIBuilder();
		}

		String path = self.doAction("getUriPath", actionContext);
		if(path != null){
			builder.setPath(path);
		}

		String fragment = self.doAction("getUriFragment", actionContext);
		if(fragment != null){
			builder.setFragment(fragment);
		}

		String charset = self.doAction("getUriCharset", actionContext);
		if(charset != null && !charset.isEmpty()){
			builder.setCharset(Charset.forName(charset));
		}

		Map<String, Object> params = self.doAction("getUriParams", actionContext);
		if(params != null){
			for(String key : params.keySet()){
				builder.addParameter(key, String.valueOf(params.get(key)));
			}
		}

		return builder.build();
	}

	public static Object trace(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//HttpRequest
		HttpTrace httpRequest = new HttpTrace(getURI(self, actionContext));

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object doRequest(Thing self, HttpUriRequest request, ActionContext actionContext) throws Exception{
		//httpClient
		HttpClient httpClient = null;
		Thing httpClientThing = self.doAction("getHttpClient", actionContext);
		if(httpClientThing != null){
			httpClient = httpClientThing.doAction("getHttpClient", actionContext);
		}
		if(httpClient == null){
			httpClient = HttpClientManager.getDefaultHttpClient();
		}
		
		HttpResponse response = null;
		HttpEntity entity = null;
		Bindings bindings = actionContext.push(null);
		bindings.put("httpClient", httpClient);
	    bindings.put("request", request);
		try{		    
		    response = httpClient.execute(request);
		    entity = response.getEntity();		    
		    bindings.put("response", response);
		    bindings.put("entity", entity);

		    Object result = null;
		    for(Thing onSuccess : self.getChilds("OnSuccess")){
		    	for(Thing child : onSuccess.getChilds()){
		    		result = child.getAction().run(actionContext);
				}
			}

		    return result;
		}catch(Exception e){
			bindings.put("exception", e);

			Object result = null;
			boolean handled = false;
			for(Thing onSuccess : self.getChilds("OnException")){
				for(Thing child : onSuccess.getChilds()){
					result = child.getAction().run(actionContext);
					handled = true;
				}
			}

			if(!handled){
				throw e;
			}
			return result;
		}finally{
		    actionContext.pop();
		    if(entity != null){
		        EntityUtils.consume(entity);
		    }
		}
	}
	
	public static Object getHeaders(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//headers				
		if(self.getStringBlankAsNull("headers") != null){
			return UtilData.getData(self,  "headers", actionContext);		    
		}
		
		return null;
	}
	
	public static Object getHttpParams(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		if(self.getStringBlankAsNull("params") != null){
		    return UtilData.getData(self,  "params", actionContext);
		}else{
			return null;
		}
	}
	
	public static Object getHttpClient(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		if(self.getStringBlankAsNull("httpclient") != null){
			Object client = UtilData.getData(self, "httpclient", actionContext);
			if(client instanceof String){
				Thing thing = World.getInstance().getThing((String) client);
				if(thing != null){
					return thing.doAction("getHttpClient", actionContext);
				}
			}else if(client instanceof Thing){
				Thing thing = (Thing) client;
				if(thing != null){
					return thing.doAction("getHttpClient", actionContext);
				}
			}else if(client instanceof HttpClient){
				return (HttpClient) client;
			}
		
			throw new ActionException("HttpClient not exist, httpclient" + self.getString("httpclient") 
					+ ", path=" + self.getMetadata().getPath());
		}else{
			Thing client = self.getThing("HttpClient@0");
			if(client != null){
				return client.doAction("getHttpClient", actionContext);				
			}else{
				return HttpClientManager.getDefaultHttpClient();
			}
		}		
	}
	
	public static Object onSuccess(ActionContext actionContext) throws ParseException, IOException{
		printLog(actionContext, true);
		
		HttpEntity entity = (HttpEntity) actionContext.get("entity");
		return EntityUtils.toString(entity);
	}
	
	public static Object onException(ActionContext actionContext){
		printLog(actionContext, false);
		
		return actionContext.get("exception");
	}
	
	public static String getUri(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		return StringUtils.getString(self, "uri", actionContext);
	}

	public static void httpClientLog(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		boolean detail = self.getBoolean("detail");

		HttpRequest request = actionContext.getObject("request");
		Executor.info(TAG, request.getRequestLine().toString());
		if(detail){
			for(Header header: request.getAllHeaders()){
				Executor.info(TAG, header.getName() + ":" + header.getValue());
			}
		}

		HttpResponse response = actionContext.getObject("response");
		Exception exception = actionContext.getObject("exception");
		if(response != null){
			Executor.info(TAG, response.getStatusLine().toString());

			if(detail){
				for(Header header : response.getAllHeaders()){
					Executor.info(TAG, header.getName() + ":" + header.getValue());
				}
			}

			HttpEntity entity = actionContext.getObject("entity");
			Executor.info(TAG, "ContentType: " + entity.getContentType());
			Executor.info(TAG, "ContentLength: " + entity.getContentLength());

			Executor.info(TAG, EntityUtils.toString(entity));
		}else if(exception != null){
			Executor.info(TAG, "Execute request exception", exception);
		}
	}
	public static void printLog(ActionContext actionContext, boolean isSuccess){
		Thing self = (Thing) actionContext.get("self");
		
		if(UtilAction.getDebugLog(self, actionContext)){
			HttpUriRequest request = (HttpUriRequest) actionContext.get("request");
			HttpEntity entity = (HttpEntity) actionContext.get("entity");
			
			Executor.info(TAG, "HttpRequest method=" + request.getMethod() + ", url=" + request.getURI() + ", path=" + self.getMetadata().getPath());
			if(isSuccess){
				HttpResponse response = (HttpResponse) actionContext.get("response");
				Executor.info(TAG, "    StatusLine=" + response.getStatusLine());
				Executor.info(TAG, "    ContentLength=" + entity.getContentLength());
				Executor.info(TAG, "    ContentType=" + entity.getContentType());
			}else{
				Executor.info(TAG, "    exception", (Exception) actionContext.get("exception"));
			}
		}
	}
	
	public static HttpEntity getHttpEntity(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing entityThing = self.getThing("entity@0");
		if(entityThing != null && entityThing.getChilds().size() > 0){
		    return (HttpEntity) entityThing.getChilds().get(0).doAction("create", actionContext);		    
		}
		
		return null;
	}
	
	public static void main(String args[]){
		try{
			HttpClient httpclient = HttpClientManager.getDefaultHttpClient();
			HttpGet get = new HttpGet("http://192.168.20.181:50070/webhdfs/v1/?op=LISTSTATUS");
			
			HttpResponse response = null;
			HttpEntity entity = null;
			try{		    
			    response = httpclient.execute(get);
			    entity = response.getEntity();
			    
			    System.out.println(response.getStatusLine());
			    System.out.println(EntityUtils.toString(entity));
			}finally{
			    if(entity != null){
			        EntityUtils.consume(entity);
			    }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

