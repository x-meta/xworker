package xworker.httpclient.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class DownloadAndDecompress {
	private static Logger logger = LoggerFactory.getLogger(DownloadAndDecompress.class);
	
	public static void downloadAndDecompress(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		//获取下载的文件流
		InputStream in = getUrlInputStream(self, actionContext);
		if(in == null){
			throw new ActionException("Can not open url, action=" + self.getMetadata().getPath());
		}
		
		File dir = self.doAction("getTargetDirectory", actionContext);
		
		String type = self.doAction("getArchiveType", actionContext);
		if(type == null || "".equals(type)){
			type = "zip";
		}
		
		type = type.toLowerCase();
		self.doAction(type, actionContext, "in", in, "directory", dir);
	}
	
	public static void checkOrDownloadAndDecompress(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getCheckFile", actionContext);
		if(file.exists() == false){
			downloadAndDecompress(actionContext);
		}
	}
		
	public static InputStream getUrlInputStream(Thing self, ActionContext actionContext){
		InputStream in = getUrlInputStream((String) self.doAction("getUrl", actionContext), self);
		if(in == null){
			in = getUrlInputStream((String) self.doAction("getBackUrl", actionContext), self);
		}
		
		return in;
	}
	
	public static InputStream getUrlInputStream(String url, Thing self){
		if(url != null && !"".equals(url)){
			return null;
		}

		try{
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			uc.setDoInput(true);
			uc.connect();
			return uc.getInputStream();
		}catch(Exception e){
			logger.warn("open url error, url=" + url + ", action=" + self.getMetadata().getPath());
			return null;
		}
	}
}
