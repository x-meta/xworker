package xworker.httpclient.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.task.UserTask;
import xworker.task.UserTaskManager;

public class HttpDownloadTask implements Runnable{
	Thing thing;
	ActionContext actionContext;
	String url;
	
	public HttpDownloadTask(String url, Thing thing, ActionContext actionContext){
		this.url = url;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void run(){	
		Thing urlThing = new Thing(thing.getDescriptor().getMetadata().getPath());
		urlThing.set("label", url);
		urlThing.getMetadata().setPath(thing.getMetadata().getPath());
		urlThing.set("description", thing.get("description"));
		
		UserTask task = UserTaskManager.createTask(urlThing, true);
		try{
			task.start();
			task.setCurrentLabel("Get HttpClient");
			HttpClient httpclient = (HttpClient) thing.doAction("getHttpClient", actionContext);
			if(httpclient == null){
				return;
			}
			
			HttpGet request = new HttpGet(url);
			HttpResponse response = null;
			HttpEntity entity = null;
			Bindings bindings = actionContext.push(null);
			bindings.put("httpClient", httpclient);
		    bindings.put("request", request);
			try{		    
				task.setCurrentLabel("Execute request...");
			    response = httpclient.execute(request);
			    int statusCode = response.getStatusLine().getStatusCode(); 
			    if(statusCode != 200){
			    	throw new ActionException("Http response status is " + statusCode);
			    }
			    
			    entity = response.getEntity();		    
			    bindings.put("response", response);
			    bindings.put("entity", entity);
			    bindings.put("uri", request.getURI());
			    bindings.put("url", url);
			    
			    task.setCurrentLabel("GetFileName...");
			    String fileName = (String) thing.doAction("getFileName", actionContext);
			    
			    if(fileName != null){
			    	File file = new File(fileName);
			    	if(!file.exists()){
			    		file.getParentFile().mkdirs();
			    	}
			    	
			    	task.setCurrentLabel("downloading...");
			    	FileOutputStream fout = new FileOutputStream(file);
			    	long length = entity.getContentLength();
			    	InputStream in = entity.getContent();
			    	int l = -1;
			    	long readed = 0;
			    	byte[] bytes = new byte[2048];
			    	while((l = in.read(bytes)) != -1){
			    		readed += l;
			    		
			    		fout.write(bytes, 0, l);
			    		
			    		if(length > 0){
			    			int percent = (int) (100d * readed / length);
			    			task.setProgress(percent);			    			
			    		}
			    	}
			    	fout.close();
				    
				    thing.doAction("onFinished", actionContext, UtilMap.toMap("file", file));
			    }else{
			    	throw new ActionException("FileName is null");
			    }			    
			}finally{
			    actionContext.pop();
			    if(entity != null){
			        EntityUtils.consume(entity);
			    }
			}
		}catch(Exception e){			
			task.setLabel("Exception:" + e.getMessage());
			
			thing.doAction("onFailure", actionContext, UtilMap.toMap("exception", e));
		}finally{
			task.finished();	
		}
	}
}
