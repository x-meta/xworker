package xworker.httpclient.actions;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.httpclient.HttpClientManager;
import xworker.util.StringUtils;

public class HttpTest {
	/**
	 * 执行Http测试。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public static HttpTestInfo run(ActionContext actionContext) throws OgnlException, IOException, TemplateException{
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		//URI
		String uri = StringUtils.getString(self, "url", actionContext);
		//log.info(uri);

		//HttpGet
		HttpGet httpGet = new HttpGet(uri);

		/*
		//headers
		if(self.headers != null && self.headers != ""){
		    def headers = OgnlUtil.getValue(self.headers, actionContext);
		    if(headers instanceof Header[]){
		        httpGet.setHeaders(headers);
		    }
		}

		//params
		if(self.params != null && self.params != ""){
		    def params = OgnlUtil.getValue(self.params, actionContext);
		    if(params instanceof HttpParams){
		        httpGet.setParams(params);
		    }
		}*/

		//httpClient
		HttpClient httpclient = null;
		if(self.getStringBlankAsNull("httpclient") != null){
			Object obj = UtilData.getData(self, "httpclient", actionContext);
			if(obj instanceof HttpClient){
				httpclient = (HttpClient) obj;
			}else if(obj instanceof String){
			    Thing httpclientThing = world.getThing((String) obj);
			    if(httpclientThing != null){
			        httpclient = (HttpClient) httpclientThing.doAction("getHttpClient", actionContext);
			    }	
			}
		}
		
		if(httpclient == null){
		    httpclient = HttpClientManager.getDefaultHttpClient();
		    //throw new XMetaException("no httpclient");
		}

		HttpResponse response = null;
		HttpEntity entity = null;
		long start = System.currentTimeMillis();
		HttpTestInfo info = new HttpTestInfo();
		info.url = uri;
		
		try{
		    response = httpclient.execute(httpGet);
		    entity = response.getEntity();
		    String head = null;
		    for(Header h : response.getAllHeaders()){
		    	if(head == null){
		    		head = h.getName() + "=" + h.getValue();
		    	}else{
		    		head = head + "\n" + h.getName() + "=" + h.getValue();
		    	}
		    }
		    info.heads = head;
		    
		    info.statusCode = response.getStatusLine().getStatusCode();
		    info.contentLength = entity.getContentLength();
		    info.contentType = entity.getContentType().getValue();
		    
		    info.content = EntityUtils.toString(entity);		    

		    info.success = true;
		    long totalTime = System.currentTimeMillis() - start;
		    info.totalTime = totalTime;
		    if(info.contentLength > 0){
		    	info.speed = (int) (info.contentLength * 1000 / (totalTime));
		    	info.speedLabel = UtilData.getSizeInfo(info.speed);
		    }
		    
		}catch(Exception e){
			info.success = false;
			long totalTime = System.currentTimeMillis() - start;
		    info.totalTime = totalTime;
		    info.exception = e;
		}finally{
		    if(entity != null){
		        try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		}
		
		return info;
	}
}
