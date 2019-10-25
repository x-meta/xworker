package xworker.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class InputStreamActions {
	public static int available(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		return in.available();
	}
	
	public static void close(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		in.close();
	}
	
	public static void mark(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		int readlimit = self.doAction("getReadlimit", actionContext);
		
		in.mark(readlimit);
	}
	
	public static boolean markSupported(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		return in.markSupported();
	}
	
	public static int read(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		if(in == null) {
			throw new ActionException("InputStream is null, action" + self.getMetadata().getPath());
		}
		boolean loop = self.doAction("isLoop", actionContext);
		if(loop) {
			while(true) {
				int data = in.read();			
				Object result = self.doAction("onRead", actionContext, "data", data);
								
				if(data == -1 || UtilData.isTrue(result) == false) {
					return data;
				}
			}
		}else {
			int data = in.read();
			self.doAction("onRead", actionContext, "data", data);
			return data;
		}		
	}
	
	public static int readBytes(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		if(in == null) {
			throw new ActionException("InputStream is null, action" + self.getMetadata().getPath());
		}
		
		byte[] bytes = self.doAction("getBytes", actionContext);		
		int offset = self.doAction("getOffset", actionContext);
		if(offset < 0) {
			offset = 0;
		}
		int length = self.doAction("getLength", actionContext);
		if(length < 0) {
			length = bytes.length;
		}
		
		if(bytes == null && UtilData.isTrue(self.doAction("isCreateBytes", actionContext))) {
			bytes = new byte[length];
		}
		
		boolean loop = self.doAction("isLoop", actionContext);
		if(loop) {
			int total = 0;
			while(true) {
				length = in.read(bytes, offset, length);
				if(length >=0) {
					total += length;
				}
				Object r = self.doAction("onRead", actionContext, "bytes", bytes, "length", length, "offset", offset);
				if(length == -1 || UtilData.isTrue(r) == false) {
					break;
				}
			}
			
			return total;
		}else {
			length = in.read(bytes, offset, length);
			self.doAction("onRead", actionContext, "bytes", bytes, "length", length, "offset", offset);
			return length;
		}
	}
	
	public static void reset(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		in.reset();
	}
	
	public static long skip(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		int n = self.doAction("getN", actionContext);
		
		return in.skip(n);
	}
	
	public static ByteArrayInputStream createByteArrayInputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		byte[] bytes = self.doAction("getBytes", actionContext);
		int offset = self.doAction("getOffset", actionContext);
		if(offset < 0) {
			offset = 0;
		}
		int length = self.doAction("getLength", actionContext);
		if(length < 0) {
			length = bytes.length;
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(bytes, offset, length);
		inputStreamReader(self, in, actionContext);
		
		return in;
	}
	
	public static FileInputStream createFileInputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		FileInputStream in = new FileInputStream(file);
		inputStreamReader(self, in, actionContext);
		return in;
	}
	
	public static Object createPipedInputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		PipedOutputStream src = self.doAction("getSrc", actionContext);
		int pipeSize = self.doAction("getPipeSize", actionContext);
		PipedInputStream in = null;
		if(src != null && pipeSize > 0) {
			in = new PipedInputStream(src, pipeSize);
		}else if(src != null) {
			in =  new PipedInputStream(src);
		}else if(pipeSize > 0) {
			in = new PipedInputStream(pipeSize);
		}else {
			in = new PipedInputStream();
		}
		
		inputStreamReader(self, in, actionContext);
		return in;
	}
	
	public static Object createGZIPInputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		int size = self.doAction("getSize", actionContext);
		GZIPInputStream gin = null;
		if(size > 0) {
			gin = new GZIPInputStream(in, size);
		}else {
			gin = new GZIPInputStream(in);
		}
		
		inputStreamReader(self, gin, actionContext);
		return gin;
	}
	
	public static void inputStreamReader(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		
		inputStreamReader(self, in, actionContext);
	}
	
	public static void inputStreamReader(Thing thing, InputStream inputStream, ActionContext actionContext) throws IOException {
		String varName = thing.doAction("getVarName", actionContext);
		if(varName != null && !"".equals(varName)) {
			actionContext.peek().put(varName, inputStream);
		}
		
		boolean loop = thing.doAction("isLoop", actionContext);
		boolean close = thing.doAction("isClose", actionContext);
		try {
			while(true) {
				for(Thing child : thing.getChilds()) {
					if("actions".equals(child.getThingName())) {
						continue;
					}
					
					Object result = child.getAction().run(actionContext);
					if(result instanceof byte[]) {
						byte[] bytes = (byte[]) result;
						inputStream.read(bytes);
					}else if(result instanceof OutputStream) {
						IOUtils.copy(inputStream, (OutputStream) result);
					}
				}
				
				if(!loop || inputStream.available() <= 0) {
					break;
				}
			}
		}finally {
			if(close) {
				inputStream.close();
			}
		}
	}
	
	
}
