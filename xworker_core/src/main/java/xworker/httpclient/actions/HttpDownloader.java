package xworker.httpclient.actions;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.httpclient.HttpClientManager;
import xworker.task.TaskManager;


public class HttpDownloader {
	private static Logger logger = LoggerFactory.getLogger(HttpDownloader.class);
	
	@SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		List<String> urls = (List<String>) self.doAction("getUrls", actionContext);
		if(urls != null){
			for(String url : urls){
				HttpDownloadTask task = new HttpDownloadTask(url, self, actionContext);
				TaskManager.getExecutorService().execute(task);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addUrls(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		List<String> urls = (List<String>) actionContext.get("urls");
		if(urls != null){
			for(String url : urls){
				HttpDownloadTask task = new HttpDownloadTask(url, self, actionContext);
				TaskManager.getExecutorService().execute(task);
			}
		}
	}
	
	/**
	 * 获取要下载的URL列表。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static List<String> getUrls(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String urls = self.getStringBlankAsNull("urls");
		if(urls == null){
			return Collections.emptyList();
		}else{
			List<String> list = new ArrayList<String>();
			for(String url : urls.split("[\n]")){
				url = url.trim();
				if(!"".equals(url)){
					list.add(url);
				}
			}
			return list;
		}
	}
	
	/**
	 * 获取HttpClient。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static HttpClient getHttpClient(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Object obj = UtilData.getData(self, "httpClient", actionContext);
		if(obj instanceof HttpClient){
			return (HttpClient) obj;
		}else if(obj instanceof String){
			Thing thing = World.getInstance().getThing((String) obj);
			if(thing != null){
				return (HttpClient) thing.doAction("getHttpClient", actionContext);
			}else{
				throw new ActionException("Can not get Httpclient, path=" + self.getMetadata().getPath());
			}
		}else{
			return HttpClientManager.getDefaultHttpClient();
		}
	}
	
	/**
	 * 返回下载文件的文件名。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getFileName(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HttpResponse response = (HttpResponse) actionContext.get("response");
		URI uri = (URI) actionContext.get("uri");
		//String url = (String) actionContext.get("url");
		
		String path = uri.getPath();
		
		String fileName = null;
		Header header = response.getLastHeader("Content-Disposition");
		if(header != null){
			for(HeaderElement el : header.getElements()){
				if("filename".equals(el.getName())){
					fileName = el.getValue();
				}
			}
		}
		if(fileName != null){
			int index = path.lastIndexOf("/");
			if(index != -1){
				path = path.substring(0, index) + "." + fileName;
			}
		}
		
		if("".equals(path)){
			path = "index.html";
		}
		
		String filePath = self.getStringBlankAsNull("filePath");
		if(filePath != null){
			return filePath + "/" + path;
		}else{
			return "./" + path;
		}
	}
	
	public static void onFailure(ActionContext actionContext){	
		Thing self = (Thing) actionContext.get("self");
		Exception exception = (Exception) actionContext.get("exception");
		logger.error("Download http error, path=" + self.getMetadata().getPath(), exception);
	}
	
	public static void onFinished(ActionContext actionContext){
		URI uri = (URI) actionContext.get("uri");
		File file = (File) actionContext.get("file");
		logger.info("HttpDownlaoder: " + uri + " -> " + file.getAbsolutePath());
	}
}
