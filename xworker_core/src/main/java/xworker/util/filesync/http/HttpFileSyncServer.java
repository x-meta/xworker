package xworker.util.filesync.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.filesync.FileSync;
import xworker.util.filesync.FileUploadChecker;

public class HttpFileSyncServer {
	private static Logger logger = LoggerFactory.getLogger(HttpFileSyncServer.class);
	
	public static void httpDo(ActionContext actionContext) throws IOException{
		HttpServletRequest request = actionContext.getObject("request");
		String action = request.getParameter("action");
		if("upload".equals(action)){
			doUpload(actionContext);
		}else if("download".equals(action)){
			download(actionContext);
		}else if("getFileList".equals(action)){
			getFileList(actionContext);
		}
		
	}
	
	public static void getFileList(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		//HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		String fileListFilePath = (String) self.doAction("getFileListFile", actionContext);
		FileSync fileSync = new FileSync(new File(fileListFilePath));

		String fileList = fileSync.getServerFileList();

		response.setContentType("text/plain; charset=utf-8");
		response.setContentLength(fileList.length());
		response.getOutputStream().write(fileList.getBytes());
	}
	
	public static void download(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		//根目录
		String fileDir = (String) self.doAction("getFileDir", actionContext);
		String fileListFilePath = (String) self.doAction("getFileListFile", actionContext);

		HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		//根目录
		File rootDir = new File(fileDir);
		InputStream input = request.getInputStream();
		OutputStream output = response.getOutputStream();

		//强制读取所有字节
		int length = request.getContentLength();
		ByteArrayInputStream bin = FileSync.readComplete(input, length);

		//处理上传文件
		response.setContentType("application/octet-stream");
		
		FileSync fileSync = new FileSync(new File(fileListFilePath));
		fileSync.serverDownload(rootDir, bin, output);
	}
	
	public static void doUpload(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		//根目录
		String fileDir = (String) self.doAction("getFileDir", actionContext);
		String fileListFilePath = (String) self.doAction("getFileListFile", actionContext);
		
		if(fileDir == null || fileListFilePath == null){
			logger.info("fileDir or fileListFilePath is null, server=" + self.getMetadata().getPath());
			return;
		}
		
		HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		File rootDir = new File(fileDir);
		File fileListFile = new File(fileListFilePath);
		InputStream input = request.getInputStream();
		OutputStream output = response.getOutputStream();

		//强制读取所有字节
		int length = request.getContentLength();
		ByteArrayInputStream bin = FileSync.readComplete(input, length);

		//checkder
		FileUploadChecker checker = null;
		if(self.getBoolean("uploadPermissionCheck")){
			checker = new ThingCheck(self, actionContext, true);
		}else{
			checker = new TrueCheck();
		}
		
		//处理上传文件
		response.setContentType("application/octet-stream");
		FileSync fileSync = new FileSync(new File(fileListFilePath));
		fileSync.serverUpload(rootDir, fileListFile, bin, output, checker);
	}
	
	static class TrueCheck implements FileUploadChecker{
		@Override
		public boolean checkUser(String user, String password) {
			return true;
		}
		
	}
	
	static class ThingCheck implements FileUploadChecker{
		Thing thing;
		ActionContext actionContext;
		boolean upload = true;
		
		public ThingCheck(Thing thing, ActionContext actionContext, boolean upload){
			this.thing = thing;
			this.actionContext = actionContext;
			this.upload = upload;
		}
		
		@Override
		public boolean checkUser(String user, String password) {
			if(upload){
				return (Boolean) thing.doAction("checkUpload", actionContext, "user", user, "password", password);
			}else{
				return (Boolean) thing.doAction("checkDownload", actionContext, "user", user, "password", password);
			}			
		}
		
	}
	
}
