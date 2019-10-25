package xworker.lang.actions.utils;

import java.io.IOException;

import org.apache.commons.io.FileSystemUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FileSystemUtilsActions {
	public static long freeSpaceKb(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		Long timeout = (Long) self.doAction("getTimeout", actionContext);
		String path = (String) self.doAction("getPath", actionContext);
		if(path == null || "".equals(path)){
			path = ".";
		}
		
		return FileSystemUtils.freeSpaceKb(path, timeout);
	}
}
