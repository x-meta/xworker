package xworker.util.filesync.http;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.filesync.FileSync;

public class HttpFileSyncClient {
	@SuppressWarnings("unchecked")
	public static void upload(ActionContext actionContext) throws NoSuchAlgorithmException, IOException{
		Thing self = actionContext.getObject("self");
		
		//根目录
		String fileDir = (String) self.doAction("getFileDir", actionContext);
		String fileListFilePath = (String) self.doAction("getFileListFile", actionContext);
		Map<String, String> userMap = (Map<String, String>) self.doAction("getUser", actionContext);
		String serverUrl = (String) self.doAction("serverUrl", actionContext);
		
		File rootDir = new File(fileDir);
		
		String fileListUrl = addAction(serverUrl, "getFileList");
		String uploadUrl = addAction(serverUrl, "upload");
		String user = userMap.get("name");
		String password = userMap.get("password");
		String filter = (String) self.doAction("getFilter", actionContext);
		FileSync fileSync = new FileSync(new File(fileListFilePath));		
		fileSync.upload(fileListUrl, uploadUrl, user, password, rootDir, filter);
	}
	
	public static void download(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		//根目录
		String fileDir = (String) self.doAction("getFileDir", actionContext);
		String fileListFilePath = (String) self.doAction("getFileListFile", actionContext);
		//Map<String, String> userMap = (Map<String, String>) self.doAction("getUser", actionContext);
		String serverUrl = (String) self.doAction("serverUrl", actionContext);
		
		File rootDir = new File(fileDir);
		File fileListFile = new File(fileListFilePath);
		String downloadUrl = addAction(serverUrl, "download");
		
		FileSync fileSync = new FileSync(fileListFile);	
		fileSync.download(downloadUrl, rootDir, fileListFile);
	}
	
	public static String addAction(String url, String action){
		if(url.indexOf("?") == -1){
			return url + "?action=" + action;
		}else{
			return url + "&action=" + action;
		}
	}
	
	public static Map<String, String> getUser(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Map<String, String> user = new HashMap<String, String>();
		
		user.put("name", self.getStringBlankAsNull("userName"));
		user.put("password", self.getStringBlankAsNull("password"));
		
		return user;
	}
}
