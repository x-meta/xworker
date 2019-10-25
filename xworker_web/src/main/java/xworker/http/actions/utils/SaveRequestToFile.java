package xworker.http.actions.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SaveRequestToFile {
	private static Logger logger = LoggerFactory.getLogger(SaveRequestToFile.class);
	
	public static void run(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		Object fileObj = self.doAction("getFile", actionContext);
		File file = null;
		if(fileObj instanceof File){
			file = (File) fileObj;
		}else if(fileObj instanceof String){
			file = new File((String) fileObj);
		}else{
			logger.warn("SaveRequestToFile: File not exists, thing=" + self.getMetadata().getPath());
			return;
		}
		
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(file);
		try{
			writeToOutputStream(actionContext, fout);
		}finally{
			fout.close();
		}
	}
	
	public static void writeToOutputStream(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		OutputStream out = self.doAction("getOutputStream", actionContext);
		
		writeToOutputStream(actionContext, out);
	}
			
	public static void writeToOutputStream(ActionContext actionContext, OutputStream out) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
				
		String queryString = request.getQueryString();
		String url = request.getRequestURL().toString();
		if(queryString != null){
			url = url + "?" + queryString;
		}
		out.write(url.getBytes());
		out.write("\n".getBytes());
				
		if(self.getBoolean("saveHeader")){
			Enumeration<String> headers = request.getHeaderNames();
			while(headers.hasMoreElements()){
				String name = headers.nextElement();
				out.write((name + ":" + request.getHeader(name) + "\n").getBytes());
			}				
		}
		
		if(self.getBoolean("saveContent")){				
			InputStream in = request.getInputStream();
			int length = -1;
			byte[] bytes = new byte[2048];
			while((length = in.read(bytes)) != -1){
				out.write(bytes, 0, length);
			}
		}	
	}
	
}
