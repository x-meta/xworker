package xworker.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class RandomAccessFileActions {
	@SuppressWarnings("resource")
	public static Object read(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		int length = self.doAction("getLength", actionContext);
		long offset = self.doAction("getOffset", actionContext);
		File file = self.doAction("getFile", actionContext);
		if(file == null) {
			throw new ActionException("File is null, path=" + self.getMetadata().getPath());
		}
		
		RandomAccessFile rf = new RandomAccessFile(file, "r");
		try {
			if(offset >= 0) {
				rf.seek(offset);
			}
			
			Object out = self.doAction("getOut", actionContext);
			if(out instanceof byte[]) {
				byte[] bytes = (byte[]) out;
				if(length >= 0) {
					rf.read(bytes, 0, length);
				}else {
					rf.read(bytes);
				}
				
				return bytes;
			}else if(out instanceof OutputStream) {
				OutputStream oout = (OutputStream) out;
				if(length >= 0) {
					byte[] bytes = new byte[1024 * 56];
					int l = -1;
					int count = 0;
					while((l = rf.read(bytes)) != -1) {						
						if(l + count > length) {
							l = length - count;
							
						}
						
						oout.write(bytes, 0 , l);
						count += l;
						if(count >= length) {
							break;
						}
					}
				}else {
					byte[] bytes = new byte[1024 * 56];
					int l = -1;
					while((l = rf.read(bytes)) != -1) {						
						oout.write(bytes, 0 , l);
					}
				}
			}
		}finally {
			rf.close();
		}
		
		return null;
	}
	
	public static Object append(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		if(file == null) {
			throw new ActionException("File is null, path=" + self.getMetadata().getPath());
		}
		
		RandomAccessFile rf = new RandomAccessFile(file, "rw");
		boolean fix = UtilData.isTrue(self.doAction("isFix", actionContext));
		FileInfo fileInfo = null;
		if(fix) {
			fileInfo = FileInfoManager.getFileInfo(file);
		}
		try {
			long offset = -1;
			Object content = self.doAction("getContent", actionContext);
			if(fix) {
				offset = write(rf, fileInfo, content);
			}else {
				offset = write(rf, content);
			}
			
			for(Thing child : self.getChilds()) {
				if("actions".equals(child.getThingName())) {
					continue;
				}
				
				content = child.getAction().run(actionContext);
				if(fix) {
					offset = write(rf, fileInfo, content);
				}else {
					offset = write(rf, content);
				}
			}
			
			return offset;
		}finally {
			rf.close();
		}
	}
	
	
	private static long write(RandomAccessFile rf, Object content) throws IOException {
		long offset  = -1;
		if(content instanceof byte[]) {
			byte[] bytes = (byte[]) content;
			offset = rf.length();
			rf.seek(offset);
			rf.write(bytes);
		}else if(content instanceof File) {
			File f = (File) content;
			offset = rf.length();
			rf.seek(offset);
			byte[] bytes = new byte[1024 * 56];
			FileInputStream fin = new FileInputStream(f);
			try {
				int l = -1;
				while((l = fin.read(bytes)) != -1) {
					rf.write(bytes, 0, l); 
				}
			}finally {
				fin.close();
			}
		}else if(content instanceof InputStream) {
			InputStream in = (InputStream) content;
			int length = in.available();
			if(length > 0) {
				offset = rf.length();
				rf.seek(offset);
				byte[] bytes = new byte[1024 * 56];
				int total = length;
				int count = 0;
				//只写入可以读到的长度
				while((length = in.read(bytes)) != -1) {					
					if(count + length > total) {
						length = total - count;
					}
					
					rf.write(bytes, 0, length);
					count += length;
					if(count >= total) {
						break;
					}
				}
			}
		}else if(content instanceof String) {
			String str = (String) content;
			byte[] bytes = str.getBytes();
			offset = rf.length();
			rf.seek(offset);
			rf.write(bytes);
		}
		
		return offset;
	}
	
	private static long write(RandomAccessFile rf, FileInfo fileInfo, Object content) throws IOException {
		long offset  = -1;
		if(content instanceof byte[]) {
			byte[] bytes = (byte[]) content;
			offset = fileInfo.getNextOffset(bytes.length);
			rf.seek(offset);
			rf.write(bytes);
		}else if(content instanceof File) {
			File f = (File) content;
			long length = f.length();
			offset = fileInfo.getNextOffset(length);
			rf.seek(offset);
			byte[] bytes = new byte[1024 * 56];
			FileInputStream fin = new FileInputStream(f);
			try {
				int l = -1;
				while((l = fin.read(bytes)) != -1) {
					rf.write(bytes, 0, l); 
				}
			}finally {
				fin.close();
			}
		}else if(content instanceof InputStream) {
			InputStream in = (InputStream) content;
			int length = in.available();
			if(length > 0) {
				offset = fileInfo.getNextOffset(length);
				rf.seek(offset);
				byte[] bytes = new byte[1024 * 56];
				int total = length;
				int count = 0;
				//只写入可以读到的长度
				while((length = in.read(bytes)) != -1) {					
					if(count + length > total) {
						length = total - count;
					}
					
					rf.write(bytes, 0, length);
					count += length;
					if(count >= total) {
						break;
					}
				}
			}
		}else if(content instanceof String) {
			String str = (String) content;
			byte[] bytes = str.getBytes();
			offset = fileInfo.getNextOffset(bytes.length);
			rf.seek(offset);
			rf.write(bytes);
		}
	
		return offset;
	}
	
}
