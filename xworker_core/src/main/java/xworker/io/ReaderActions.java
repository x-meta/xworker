package xworker.io;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ReaderActions {
	public static Object createBufferedReader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		if(reader == null) {
			throw new ActionException("Reader is null, action=" + self.getMetadata().getPath());
		}
		
		int size = self.doAction("getSize", actionContext);
		if(size > 0) {
			return new BufferedReader(reader, size);
		}else {
			return new BufferedReader(reader);
		}
	}
	
	public static Object createLineNumberReader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		if(reader == null) {
			throw new ActionException("Reader is null, action=" + self.getMetadata().getPath());
		}
		
		int size = self.doAction("getSize", actionContext);
		if(size > 0) {
			return new LineNumberReader(reader, size);
		}else {
			return new LineNumberReader(reader);
		}
	}
	
	public static Object createPushbackReader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		if(reader == null) {
			throw new ActionException("Reader is null, action=" + self.getMetadata().getPath());
		}
		
		int size = self.doAction("getSize", actionContext);
		if(size > 0) {
			return new PushbackReader(reader, size);
		}else {
			return new PushbackReader(reader);
		}
	}
	
	public static Object createCharArrayReader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = self.doAction("getCharArray", actionContext);
		char[] chars = null;
		if(obj instanceof char[]) {
			chars = (char[]) obj;
		}else if(obj instanceof String) {
			chars = ((String) obj).toCharArray();
		}else if(obj != null) {
			String str = String.valueOf(obj);
			chars = str.toCharArray();
		}
		
		if(chars == null) {
			throw new ActionException("char[] is null, action=" + self.getMetadata().getPath());
		}
		
		int offset = self.doAction("getOffset", actionContext);
		if(offset < 0) {
			offset = 0;
		}
		int length = self.doAction("getLength", actionContext);
		if(length <= 0) {
			length = chars.length;
		}
		
		return new CharArrayReader(chars, offset, length);
	}
	
	public static Object createInputStreamReader(ActionContext actionContext) throws UnsupportedEncodingException {
		Thing self = actionContext.getObject("self");
		InputStream in = self.doAction("getInputStream", actionContext);
		if(in == null) {
			throw new ActionException("InputStream is null, action=" + self.getMetadata().getName());
			
		}
		String charset = self.doAction("getCharset", actionContext);
		if(charset == null || "".equals(charset)) {
			return new InputStreamReader(in);
		}else {
			return new InputStreamReader(in, charset);
		}
	}
	
	public static Object createFileReader(ActionContext actionContext) throws FileNotFoundException {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		if(file == null) {
			throw new ActionException("File is null, action=" + self.getMetadata().getPath());
		}
		
		return new FileReader(file);	
	}
	
	public static Object createPipedReader(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		PipedWriter writer = self.doAction("getPipedWriter", actionContext);
		int size = self.doAction("getSize", actionContext);
		if(writer != null && size > 0) {
			return new PipedReader(writer, size);
		}else if(writer != null) {
			return new PipedReader(writer);
		}else if(size > 0) {
			return new PipedReader(size);
		}else {
			return new PipedReader();
		}
	}
	
	public static Object createStringReader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String value = self.doAction("getValue", actionContext);
		if(value == null) {
			throw new ActionException("Sting is null, action=" + self.getMetadata().getPath());
		}
		
		return new StringReader(value);
	}
	
	public static void close(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		if(reader != null) {
			reader.close();
		}
	}
	
	public static void mark(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		if(reader != null) {
			int readAheadLimit = self.doAction("getReadAheadLimit", actionContext);
			reader.mark(readAheadLimit);
		}
	}
	
	public static int read(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		int c = reader.read();
		self.doAction("onRead", actionContext, "char", c);
		return c;
	}
	
	public static int readChars(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Reader reader = self.doAction("getReader", actionContext);
		int offset = self.doAction("getOffset", actionContext);
		if(offset == -1) {
			offset = 0;
		}
		int length = self.doAction("getLength", actionContext);
		
		char[] chars = null;
		if(UtilData.isTrue(self.doAction("isCreateChars", actionContext))) {
			chars = new char[length];
		}else {
			chars = self.doAction("getChars", actionContext);
		}
		
		int readLength = reader.read(chars, offset, length);
		self.doAction("onRead", actionContext, "readLength", readLength, "chars", chars,
				"offset", offset, "length", length);
		return readLength;
	}
	
	public static Object readLine(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Object reader = self.doAction("getReader", actionContext);
		
		BufferedReader br = null;
		if(reader instanceof BufferedReader) {
			br = (BufferedReader) br;
		} else if (reader instanceof InputStream){
			br = new BufferedReader(new InputStreamReader((InputStream) reader));
		}else if(reader instanceof Reader){
			br = new BufferedReader((Reader) reader);
		}
		if(reader == null) {
			throw new ActionException("Reader is null, action=" + self.getMetadata().getPath());
		}
				
		boolean loop = self.doAction("isLoop", actionContext);
		boolean close = self.doAction("isClose", actionContext);
		try {
			String line = null;
			if(loop) {
				while(true) {
					line = br.readLine();
					self.doAction("onRead", actionContext, "line", line);
					if(line == null) {
						break;
					}
				}
			}else {
				line = br.readLine();
			}
			
			return line;
		}finally {
			if(close) {
				br.close();
			}
		}
		
	}
}
