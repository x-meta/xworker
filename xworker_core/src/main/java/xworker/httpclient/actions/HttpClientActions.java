package xworker.httpclient.actions;

import java.io.IOException;

import ognl.OgnlException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;

import xworker.httpclient.HttpClientManager;
import xworker.util.StringUtils;
import freemarker.template.TemplateException;

public class HttpClientActions {
	private static Logger logger = LoggerFactory.getLogger(HttpClientActions.class);
			
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
		
		for(Thing child : self.getChilds()){
			child.doAction("initRequest", actionContext);
		}
	}
	
	public static Object post(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);

		//httpPost
		HttpPost httpPost = new HttpPost(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpPost.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpPost.setParams(params);
	    }

		//entity
	    HttpEntity httpEntity = (HttpEntity) self.doAction("getHttpEntity", actionContext);
	    if(httpEntity != null){
	    	httpPost.setEntity(httpEntity);
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
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpGet
		HttpGet httpGet = new HttpGet(uri);
		
		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	        httpGet.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	        httpGet.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpGet, actionContext);
	    
		return doRequest(self, httpGet, actionContext);
	}
	
	public static Object delete(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpRequest
		HttpDelete httpRequest = new HttpDelete(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpRequest.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpRequest.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object head(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpRequest
		HttpHead httpRequest = new HttpHead(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpRequest.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpRequest.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object options(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpRequest
		HttpOptions httpRequest = new HttpOptions(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpRequest.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpRequest.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object patch(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpRequest
		HttpPatch httpRequest = new HttpPatch(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpRequest.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpRequest.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object put(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpRequest
		HttpPut httpRequest = new HttpPut(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpRequest.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpRequest.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object trace(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String  uri = (String) self.doAction("getUri", actionContext);
		//log.info(uri);

		//HttpRequest
		HttpTrace httpRequest = new HttpTrace(uri);

		//headers	
		Header[] headers = (Header[]) self.doAction("getHeaders", actionContext);
	    if(headers != null){
	    	httpRequest.setHeaders(headers);
	    }
		
		//params
	    HttpParams params = (HttpParams) self.doAction("getHttpParams", actionContext);
	    if(params != null){
	    	httpRequest.setParams(params);
	    }

	    HttpClientActions.initRequest(self, httpRequest, actionContext);
	    
		return doRequest(self, httpRequest, actionContext);
	}
	
	public static Object doRequest(Thing self, HttpUriRequest request, ActionContext actionContext) throws Exception{
		//httpClient
		HttpClient httpclient = (HttpClient) self.doAction("getHttpClient", actionContext);
		if(httpclient == null){
			throw new ActionException("HttpClint is null, path=" + self.getMetadata().getPath());
		}
		
		HttpResponse response = null;
		HttpEntity entity = null;
		Bindings bindings = actionContext.push(null);
		bindings.put("httpClient", httpclient);
	    bindings.put("request", request);
		try{		    
		    response = httpclient.execute(request);
		    entity = response.getEntity();		    
		    bindings.put("response", response);
		    bindings.put("entity", entity);
		    
		    return self.doAction("onSuccess", actionContext);
		}catch(Exception e){
			bindings.put("exception", e);
			
			Object r = self.doAction("onException", actionContext);
			if(r instanceof Exception){
				throw e;
			}else{
				return r;
			}
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
	
	public static void printLog(ActionContext actionContext, boolean isSuccess){
		Thing self = (Thing) actionContext.get("self");
		
		if(UtilAction.getDebugLog(self, actionContext)){
			HttpUriRequest request = (HttpUriRequest) actionContext.get("request");
			HttpEntity entity = (HttpEntity) actionContext.get("entity");
			
			logger.info("HttpRequest method=" + request.getMethod() + ", url=" + request.getURI() + ", path=" + self.getMetadata().getPath());			
			if(isSuccess){
				HttpResponse response = (HttpResponse) actionContext.get("response");
				logger.info("    StatusLine=" + response.getStatusLine());
				logger.info("    ContentLength=" + entity.getContentLength());
				logger.info("    ContentType=" + entity.getContentType());
			}else{
				logger.info("    exception", (Exception) actionContext.get("exception"));
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

