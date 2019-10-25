package xworker.util.deploy;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.filesync.FileSync;

public class XWorkerModuleActions {
	private static Logger logger = LoggerFactory.getLogger(XWorkerModuleActions.class);
	
	public static void run(ActionContext actionContext) throws NoSuchAlgorithmException, IOException {
		Thing self = actionContext.getObject("self");
		String name = self.getMetadata().getName();
		
		Object r = self.doAction("copyResources", actionContext);
		if(r != null) {
			File dir = null;
			if(r instanceof String) {
				dir = new File((String) r);
			}else if(r instanceof File) {
				dir = (File) r;
			}
			
			if(dir != null) {
				World world = World.getInstance();
				Thing userObj = world.getThing("_local.manong.User");

				File rootDir = dir;
				String fileListUrl = "https://www.xworker.org/do?sc=xworker.tools.update.GetFileList&project=" + name;
				String uploadUrl = "https://www.xworker.org/do?sc=xworker.tools.update.Upload&project=" + name;
				String user = userObj.getString("userName");
				String password = userObj.getString("password");
				String filter = "actionClasses\n" + 
							"actionSources\n" + 
							"updateindex\n" +
							"log\n" +
						"projects/_local\n" + "projects/_share";
						
				FileSync fileSync = new FileSync();
				fileSync.upload(fileListUrl, uploadUrl, user, password, rootDir, filter);
			}else {
				logger.info("dist dir is null, path=" + self.getMetadata().getPath());
			}
		}else {
			logger.info("dist dir is null, path=" + self.getMetadata().getPath());
		}
	}
}
