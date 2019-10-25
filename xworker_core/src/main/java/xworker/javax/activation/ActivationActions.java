package xworker.javax.activation;

import java.io.File;
import java.net.URL;

import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActivationActions {
	public static FileDataSource createFileDataSource(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		if(file != null) {
			return new FileDataSource(file);
		}else {
			return null;
		}
	}
	
	public static URLDataSource createURLDataSource(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		URL url = self.doAction("getUrl", actionContext);
		if(url != null) {
			return new URLDataSource(url);
		}else {
			return null;
		}
	}
}
