package xworker.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.dataObject.utils.JacksonFormator;
import xworker.util.UtilData;
import freemarker.template.TemplateException;

public class URLActions {
	public static URL getUrl(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = actionContext.getObject("self");
		
		String url = UtilData.getString(self, "url", actionContext);
		if(url == null){
			return null;
		}
		
		return new URL(url);
	}
	
	public static InputStream openInputStream(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		URL url = (URL) self.doAction("getUrl", actionContext);
		if(url == null){
			throw new ActionException("url can not be null, action=" + self.getMetadata().getPath());
		}
		
		return url.openStream();
	}
	
	public static Object getContent(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		URL url = (URL) self.doAction("getUrl", actionContext);
		if(url == null){
			throw new ActionException("url can not be null, action=" + self.getMetadata().getPath());
		}
		
		return url.getContent();
	}
	
	public static Object getJson(ActionContext actionContext) throws Exception{
		Thing self = actionContext.getObject("self");
		
		URL url = (URL) self.doAction("getUrl", actionContext);
		if(url == null){
			throw new ActionException("url can not be null, action=" + self.getMetadata().getPath());
		}
		
		
		String json = IOUtils.toString(url);
		return JacksonFormator.parseObject(json);
	}
	
	public static Object getString(ActionContext actionContext) throws Exception{
		Thing self = actionContext.getObject("self");
		
		URL url = (URL) self.doAction("getUrl", actionContext);
		if(url == null){
			throw new ActionException("url can not be null, action=" + self.getMetadata().getPath());
		}
		
		
		return IOUtils.toString(url);
	}
	
	public static Object getBytes(ActionContext actionContext) throws Exception{
		Thing self = actionContext.getObject("self");
		
		URL url = (URL) self.doAction("getUrl", actionContext);
		if(url == null){
			throw new ActionException("url can not be null, action=" + self.getMetadata().getPath());
		}
		
		
		return IOUtils.toByteArray(url);
	}
}

