package xworker.libdgx.functions.files;

import java.io.File;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;

public class FileFunctions {
	public Object createFileHandleWithFile(ActionContext actionContext){
		File file = (File) actionContext.get("file");
		return new FileHandle(file);
	}
	
	public Object createFileHandleWithFileName(ActionContext actionContext){
		String fileName = (String) actionContext.get("fileName");
		return new FileHandle(fileName);
	}
}
