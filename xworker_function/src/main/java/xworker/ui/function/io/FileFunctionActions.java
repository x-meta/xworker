package xworker.ui.function.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;

public class FileFunctionActions {
	/**
	 * 获取文件的函数。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static File getFile(ActionContext actionContext){
		String filePath = (String) actionContext.get("filePath");
		return new File(filePath);
	}
	
	public static String getAbsolutePath(ActionContext actionContext){
		File file = (File) actionContext.get("file");
		return file.getAbsolutePath();
	}
	
	/**
	 * 把文件转化为字符串。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException 
	 */
	public static String fileToText(ActionContext actionContext) throws IOException{
		Object fileObj = actionContext.get("file");
		Object charset = actionContext.get("charset");
		
		File file = null;
		if(fileObj instanceof String){
			file = new File((String) fileObj);
		}else if(fileObj instanceof File){
			file = (File) fileObj;
		}else if(fileObj instanceof URL){
			file = new File(((URL) fileObj).getFile());
		}else if(fileObj instanceof URI){
			file = new File(((URI) fileObj));
		}else{
			throw  new ActionException("fileToText not suppor fileType, fileObj=" + fileObj);
		}
	
		FileInputStream fin = new FileInputStream(file);
		try{
			byte[] bytes = new byte[fin.available()];
			fin.read(bytes);
			
			if(charset != null){
				if(charset instanceof String){
					return new String(bytes, (String) charset);
				}else if(charset instanceof Charset){
					return new String(bytes, (Charset) charset);
				}
			}
			
			return new String(bytes);
		}finally{
			fin.close();
		}
	}
}
