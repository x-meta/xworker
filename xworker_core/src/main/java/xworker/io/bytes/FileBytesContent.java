package xworker.io.bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.xmeta.ActionException;

import xworker.io.BytesContent;

public class FileBytesContent extends BytesContent{
	File file;
	FileInputStream fin = null;
	
	public FileBytesContent(File file) {
		if(file == null) {
			throw new ActionException("File is null.");
		}
		if(file.isDirectory()) {
			throw new ActionException("File is a directory, file=" + file.getPath());			
		}
		if(file.exists() == false) {
			throw new ActionException("File not exists, file=" + file.getPath());
		}
		this.file = file;
	}
	
	@Override
	protected void doRead() {
		if(listener == null) {
			return;
		}
		
		try {
			fin = new FileInputStream(file);
			byte[] bytes = new byte[4096];
			int l = -1;
			while((l = fin.read(bytes)) != -1){
				listener.onRead(bytes, 0, l);
			}
		}catch(Exception e) {
			throw new ActionException("FileBytesContent doRead error", e);
		}
	}

	@Override
	protected void close() {
		if(fin != null) {
			try {
				fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
