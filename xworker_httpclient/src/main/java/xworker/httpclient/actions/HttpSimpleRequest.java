package xworker.httpclient.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.codes.TxtCoder;
import org.xmeta.codes.XmlCoder;
import org.xml.sax.SAXException;

import xworker.httpclient.HttpClientManager;

public class HttpSimpleRequest {
	public static Object run(ActionContext actionContext) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException{
		Thing self = (Thing) actionContext.get("self");
		
		//URL
		String url = (String) self.doAction("getUrl", actionContext);
		if(url == null){
			throw new ActionException("url is null, path=" + self.getMetadata().getPath());
		}
		
		//构建request
		String method = self.getStringBlankAsNull("method");
		HttpUriRequest request = createRequest(method, url);
		
		//headers
		setHeaders(self, request, actionContext);
		
		//entity
		if("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)){
			setEntity(self, (HttpEntityEnclosingRequestBase) request, actionContext);
		}
		
		//执行请求
		return doRequest(self, request, actionContext);
		
	}	
	
	public static Object doRequest(Thing self, HttpUriRequest request, ActionContext actionContext) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException{
		HttpClient httpclient = HttpClientManager.getDefaultHttpClient();
		HttpResponse response = null;
		HttpEntity entity = null;
		try{		    
		    response = httpclient.execute(request);
		    entity = response.getEntity();
		    
		    Map<String, Object> result = new HashMap<String, Object>();
		    result.put("status",  response.getStatusLine().getStatusCode());
		    
		    String returnType = self.getStringBlankAsNull("returnType");
		    if("byte[]".equals(returnType)){
		    	result.put("content", EntityUtils.toByteArray(entity));
		    }else{
		    	String content = EntityUtils.toString(entity);
		    	if("jsonObject".equals(returnType)){
		    		Object obj = self.doAction("parseJson", actionContext, "jsonText", content);
		    		result.put("content", obj);
		    		/*
		    		ObjectMapper mapper = new ObjectMapper();
		    		content = content.trim();
		    		if(content.startsWith("[")){
		    			result.put("content", mapper.readValue(content, List.class));
		    		}else{
		    			result.put("content", mapper.readValue(content, Map.class));
		    		}*/
		    	}else if("xmlThing".equals(returnType)){
		    		Thing thing = new Thing();
		    		XmlCoder.parse(thing, content);
		    		result.put("content", thing);
		    	}else if("textThing".equals(returnType)){
		    		Thing thing = new Thing();
		    		TxtCoder.decode(thing, new ByteArrayInputStream(content.getBytes()), true, 0);
		    		result.put("content", thing);
		    	}else{
		    		result.put("content", content);
		    	}
		    }
		    
		    return result;
		}finally{
		    if(entity != null){
		        EntityUtils.consume(entity);
		    }
		}
	}
	
	public static void setEntity(Thing self, HttpEntityEnclosingRequestBase request, ActionContext actionContext) throws UnsupportedEncodingException{
		String parametersEntity = (String) self.doAction("getParamtersEntity", actionContext);
		Object otherEntity = self.doAction("getOtherEntity", actionContext);
		if(parametersEntity != null){
			parametersEntity = parametersEntity.trim();
		}
		if("".equals(parametersEntity)){
			parametersEntity = null;
		}
		
		if(parametersEntity != null && otherEntity != null){
			MultipartEntity entity = new MultipartEntity();
			
			ContentBody body = null;
			if(otherEntity instanceof String){
				body = new StringBody((String) otherEntity);
			}else if(otherEntity instanceof InputStream){
				body = new InputStreamBody((InputStream) otherEntity, "input");
			}else if(otherEntity instanceof byte[]){
				byte[] bytes = (byte[]) otherEntity;
				body = new ByteArrayBody(bytes, "input");
			}else if(otherEntity instanceof File){
				File file = (File) otherEntity;
				body = new FileBody(file);
			}
			
			FormBodyPart form = new FormBodyPart("params", body);
			entity.addPart(form);
			request.setEntity(entity);
		}else if(parametersEntity != null){
			//参数enttiy
			UrlEncodedFormEntity paramEntity = null;
			if(parametersEntity != null){
				List<BasicNameValuePair> lists = new ArrayList<BasicNameValuePair>();
				for(String line : parametersEntity.split("[\n]")){
					line = line.trim();
					if("".equals(line)){
						continue;
					}
					
					int index = line.indexOf(":");
					if(index == -1){
						continue;
					}
					
					BasicNameValuePair p = new BasicNameValuePair(line.substring(0, index), line.substring(index + 1, line.length()));
					lists.add(p);
				}
				
				paramEntity = new UrlEncodedFormEntity(lists);
				request.setEntity(paramEntity);
			}
		}else if(otherEntity != null){		
			//其他entity
			HttpEntity other = null;
			if(otherEntity instanceof String){
				other = new StringEntity((String) otherEntity);
			}else if(otherEntity instanceof InputStream){
				other = new InputStreamEntity((InputStream) otherEntity, -1);
			}else if(otherEntity instanceof byte[]){
				byte[] bytes = (byte[]) otherEntity;
				other = new ByteArrayEntity(bytes, 0, bytes.length);
			}else if(otherEntity instanceof File){
				File file = (File) otherEntity;
				other = new FileEntity(file);
			}
			
			if(other != null){
				request.setEntity(other);
			}
		}
	}
	
	public static void setHeaders(Thing self, HttpUriRequest request, ActionContext actionContext){
		String headers = (String) self.doAction("getHeaders", actionContext);
		if(headers != null && !"".equals(headers)){
			for(String line : headers.split("[\n]")){
				line = line.trim();
				if("".equals(line)){
					continue;
				}
				
				int index = line.indexOf(":");
				if(index == -1){
					continue;
				}
				
				request.addHeader(line.substring(0, index), line.substring(index + 1, line.length()));
			}
		}
	}
	
	public static HttpUriRequest createRequest(String method, String url){
		HttpUriRequest request = null;
		if("DELETE".equals(method)){
			request = new HttpDelete(url);
		}else if("POST".equals(method)){
			request = new HttpPost(url);
		}else if("PATCH".equals(method)){
			request = new HttpPatch(url);
		}else if("PUT".equals(method)){
			request = new HttpPut(url);
		}else if("TRACE".equals(method)){
			request = new HttpTrace(url);
		}else{
			request = new HttpGet(url);
		}
		
		return request;
	}
}
