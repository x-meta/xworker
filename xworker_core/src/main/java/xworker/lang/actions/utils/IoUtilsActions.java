package xworker.lang.actions.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class IoUtilsActions {
	public static Object buffer(ActionContext actionContext) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Integer size = (Integer) self.doAction("getSize", actionContext);
		if(size != null && size > 0){
			return MethodUtils.invokeExactStaticMethod(IOUtils.class, "buffer", input, size);
		}else{
			return MethodUtils.invokeExactStaticMethod(IOUtils.class, "buffer", input);
		}
	}
	
	public static Object closeQuietly(ActionContext actionContext) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		return MethodUtils.invokeExactStaticMethod(IOUtils.class, "closeQuietly", input);
	}
	
	public static Object contentEquals(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input1 = self.doAction("getInput1", actionContext);
		Object input2 = self.doAction("getInput2", actionContext);
		
		if(input1 instanceof InputStream){
			return IOUtils.contentEquals((InputStream) input1,  (InputStream) input2); 
		}else{
			return IOUtils.contentEquals((Reader) input1,  (Reader) input2);
		}		
	}
	
	public static Object contentEqualsIgnoreEOL(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input1 = self.doAction("getInput1", actionContext);
		Object input2 = self.doAction("getInput2", actionContext);
		
		return IOUtils.contentEqualsIgnoreEOL((Reader) input1,  (Reader) input2);
	}
	
	public static Object copyInToOut(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		InputStream intput = (InputStream) self.doAction("getInput", actionContext);
		OutputStream output = (OutputStream) self.doAction("getOutput", actionContext);
		//Integer bufferSize = (Integer) self.doAction("getBufferSize", actionContext);
		return IOUtils.copy(intput, output);
		/*
		if(bufferSize == null || bufferSize <= 0){
			return IOUtils.copy(input1, input2);
		}else{
			return IOUtils.copy(input1, input2, bufferSize);
		}*/
	}
	
	public static void copyInToWriter(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		InputStream intput = (InputStream) self.doAction("getInput", actionContext);
		Writer output = (Writer) self.doAction("getOutput", actionContext);
		String inputEncoding = (String) self.doAction("getInputEncoding", actionContext);
		
		IOUtils.copy(intput, output, inputEncoding);
	}
	
	public static void copy(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Object output = self.doAction("getOutput", actionContext);
		if(input instanceof InputStream){
			if(output instanceof OutputStream){
				//Integer bufferSize = (Integer) self.doAction("getBufferSize", actionContext);
				//if(bufferSize == null || bufferSize <= 0){
				IOUtils.copy((InputStream) input, (OutputStream) output);
				//}else{
				//	IOUtils.copy((InputStream) input, (OutputStream) output, bufferSize);
				//}
			}else if(output instanceof Writer){
				String encoding = (String) self.doAction("getEncoding", actionContext);
				IOUtils.copy((InputStream) input, (Writer) output, encoding);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}
		}else if(input instanceof Reader){
			if(output instanceof OutputStream){
				String encoding = (String) self.doAction("getEncoding", actionContext);
				IOUtils.copy((Reader) input, (OutputStream) output, encoding);
			}else if(output instanceof Writer){				
				IOUtils.copy((Reader) input, (Writer) output);
			}else{
				throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
			}
		}
	}
	
	public static void copyLarge(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Object output = self.doAction("getOutput", actionContext);
		Integer bufferSize = (Integer) self.doAction("getBufferSize", actionContext);
		Long inputOffset = (Long) self.doAction("getInputOffset", actionContext);
		Long length = (Long) self.doAction("getLength", actionContext);
		if(input instanceof InputStream){
			if(output instanceof OutputStream){
				if(bufferSize != null && bufferSize > 0){
					IOUtils.copyLarge((InputStream) input, (OutputStream) output, inputOffset, length, new byte[bufferSize]);
				}else{
					IOUtils.copyLarge((InputStream) input, (OutputStream) output, inputOffset, length);
				}
				
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}
		}else if(input instanceof Reader){
			if(output instanceof Writer){				
				//if(bufferSize != null && bufferSize > 0){
				//	IOUtils.copyLarge((Reader) input, (Writer) output, inputOffset, length, new byte[bufferSize]);
				//}else{
					IOUtils.copyLarge((Reader) input, (Writer) output, inputOffset, length);
				//}
			}else{
				throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
			}
		}
	}
	
	public static void lineIterator(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		InputStream in = null;
		try{
			String encoding = (String) self.doAction("getEncoding", actionContext);
			Object input = self.doAction("getInput", actionContext);
			 LineIterator iterator = null;
			if(input instanceof InputStream){			
				iterator = IOUtils.lineIterator((InputStream) input, encoding);
			}else if(input instanceof Reader){
				iterator = IOUtils.lineIterator((Reader) input);
			}else if(input instanceof String){
				File file = new File((String) input);
				if(file.exists()){
					in = new FileInputStream(file);
					iterator = IOUtils.lineIterator(in, encoding);
				}else{
					iterator = IOUtils.lineIterator(new ByteArrayInputStream(((String) input).getBytes()), encoding);
				}
			}else if(input instanceof File){
				in = new FileInputStream((File) input);
				iterator = IOUtils.lineIterator(in, encoding);
			}else if(input instanceof URI){
				iterator = IOUtils.lineIterator(new ByteArrayInputStream(IOUtils.toByteArray((URI) input)), encoding);
			}else if(input instanceof URL){
				iterator = IOUtils.lineIterator(new ByteArrayInputStream(IOUtils.toByteArray((URL) input)), encoding);
			}else if(input instanceof URLConnection){
				iterator = IOUtils.lineIterator(new ByteArrayInputStream(IOUtils.toByteArray((URLConnection) input)), encoding);
			}else{
				throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
			}
			
			int index = 0;
			while(iterator.hasNext()){
				String line = iterator.next();
				for(Thing child : self.getChilds()) {
					child.getAction().run(actionContext, UtilMap.toMap("line", line, "line_index", index, "line_has_next", iterator.hasNext()), true);
					int status = actionContext.getStatus();
					if(status != ActionContext.RUNNING) {
						break;
					}
				}
				
				int status = actionContext.getStatus();
				if(status == ActionContext.CONTINUE) {
					actionContext.setStatus(ActionContext.RUNNING);
					continue;
				}else if(status == ActionContext.BREAK) {
					actionContext.setStatus(ActionContext.RUNNING);
					break;
				}else if(status != ActionContext.RUNNING){
					break;
				}
				
				index++;
			}
		}finally{
			if(in != null){
				in.close();
			}
		}
	}
	
	public static int read(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Object buffer = (Object) self.doAction("getBuffer", actionContext);
		Integer offset = (Integer) self.doAction("getOffset", actionContext);
		Integer length = (Integer) self.doAction("getLength", actionContext);
		if(input instanceof InputStream){
			if(buffer instanceof byte[]){
				return IOUtils.read((InputStream) input, (byte[]) buffer, offset, length); 
			}else{
				throw new ActionException("Unsupport buffer " + buffer + ", action=" + self.getMetadata().getPath());
			}			
		}else if(input instanceof Reader){
			if(buffer instanceof char[]){
				return IOUtils.read((Reader) input, (char[]) buffer, offset, length); 
			}else{
				throw new ActionException("Unsupport buffer " + buffer + ", action=" + self.getMetadata().getPath());
			}
		}else if(input instanceof ReadableByteChannel){
			if(buffer instanceof ByteBuffer){
				return IOUtils.read((ReadableByteChannel) input, (ByteBuffer) buffer); 
			}else{
				throw new ActionException("Unsupport buffer " + buffer + ", action=" + self.getMetadata().getPath());
			}
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static void readFully(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Object buffer = (Object) self.doAction("getBuffer", actionContext);
		Integer offset = (Integer) self.doAction("getOffset", actionContext);
		Integer length = (Integer) self.doAction("getLength", actionContext);
		if(input instanceof InputStream){
			if(buffer instanceof byte[]){
				IOUtils.readFully((InputStream) input, (byte[]) buffer, offset, length); 
			}else{
				throw new ActionException("Unsupport buffer " + buffer + ", action=" + self.getMetadata().getPath());
			}			
		}else if(input instanceof Reader){
			if(buffer instanceof char[]){
				IOUtils.readFully((Reader) input, (char[]) buffer, offset, length); 
			}else{
				throw new ActionException("Unsupport buffer " + buffer + ", action=" + self.getMetadata().getPath());
			}
		}else if(input instanceof ReadableByteChannel){
			if(buffer instanceof ByteBuffer){
				IOUtils.readFully((ReadableByteChannel) input, (ByteBuffer) buffer); 
			}else{
				throw new ActionException("Unsupport buffer " + buffer + ", action=" + self.getMetadata().getPath());
			}
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static List<String> readLines(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		if(input instanceof InputStream){
			String encoding = (String) self.doAction("getEncoding", actionContext);
			return IOUtils.readLines((InputStream) input, encoding); 
		}else if(input instanceof Reader){
			return IOUtils.readLines((Reader) input);
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static long skip(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Long skipSize = (Long) self.doAction("getSkipSize", actionContext);
		if(input instanceof InputStream){
			return IOUtils.skip((InputStream) input, skipSize); 
		}else if(input instanceof Reader){
			return IOUtils.skip((Reader) input, skipSize);
		}else if(input instanceof ReadableByteChannel){
			return IOUtils.skip((ReadableByteChannel) input, skipSize);
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static void skipFully(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Long skipSize = (Long) self.doAction("getSkipSize", actionContext);
		if(input instanceof InputStream){
			IOUtils.skipFully((InputStream) input, skipSize); 
		}else if(input instanceof Reader){
			IOUtils.skipFully((Reader) input, skipSize);
		}else if(input instanceof ReadableByteChannel){
			IOUtils.skipFully((ReadableByteChannel) input, skipSize);
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static byte[] toByteArray(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		Long size = (Long) self.doAction("getSize", actionContext);
		if(input instanceof InputStream){
			if(size != null && size > 0){
				return IOUtils.toByteArray((InputStream) input, size);
			}else{
				return IOUtils.toByteArray((InputStream) input);
			}
		}else if(input instanceof Reader){
			String encoding = (String) self.doAction("getEncoding", actionContext);
			return IOUtils.toByteArray((Reader) input, encoding);
		}else if(input instanceof URI){
			return IOUtils.toByteArray((URI) input);
		}else if(input instanceof URL){
			return IOUtils.toByteArray((URL) input);
		}else if(input instanceof URLConnection){
			return IOUtils.toByteArray((URLConnection) input);
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static char[] toCharArray(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		if(input instanceof InputStream){
			String encoding = (String) self.doAction("getEncoding", actionContext);
			return IOUtils.toCharArray((InputStream) input, encoding);
		}else if(input instanceof Reader){			
			return IOUtils.toCharArray((Reader) input);
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static InputStream toInputStream(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		String encoding = (String) self.doAction("getEncoding", actionContext);
		if(input instanceof String){			
			return IOUtils.toInputStream((String) input, encoding) ;
		}else if(input instanceof CharSequence){			
			return IOUtils.toInputStream((CharSequence) input, encoding) ;
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static String toString(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object input = self.doAction("getInput", actionContext);
		String encoding = (String) self.doAction("getEncoding", actionContext);
		if(input instanceof InputStream){
			return IOUtils.toString((InputStream) input, encoding);
		}else if(input instanceof Reader){			
			return IOUtils.toString((Reader) input);
		}else if(input instanceof URI){
			return IOUtils.toString((URI) input, encoding);
		}else if(input instanceof URL){
			return IOUtils.toString((URL) input, encoding);
		}else if(input instanceof byte[]){
			return IOUtils.toString((byte[]) input, encoding);
		}else{
			throw new ActionException("Unsupport input " + input + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static void write(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object data = self.doAction("getData", actionContext);
		Object output = self.doAction("getOutput", actionContext);
		String encoding = (String) self.doAction("getEncoding", actionContext);
		if(data instanceof byte[]){
			if(output instanceof OutputStream){
				IOUtils.write((byte[]) data, (OutputStream) output);
			}else if(output instanceof Writer){
				IOUtils.write((byte[]) data, (Writer) output, encoding);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}	
		}else if(data instanceof char[]){			
			if(output instanceof OutputStream){
				IOUtils.write((char[]) data, (OutputStream) output, encoding);
			}else if(output instanceof Writer){
				IOUtils.write((char[]) data, (Writer) output);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}
		}else if(data instanceof CharSequence){
			if(output instanceof OutputStream){
				IOUtils.write((CharSequence) data, (OutputStream) output, encoding);
			}else if(output instanceof Writer){
				IOUtils.write((CharSequence) data, (Writer) output);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}
		}else if(data instanceof String){
			if(output instanceof OutputStream){
				IOUtils.write((String) data, (OutputStream) output, encoding);
			}else if(output instanceof Writer){
				IOUtils.write((String) data, (Writer) output);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}
		}else{
			throw new ActionException("Unsupport data " + data + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static void writeChunked(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
		
		Object data = self.doAction("getData", actionContext);
		Object output = self.doAction("getOutput", actionContext);
		if(data instanceof byte[]){
			if(output instanceof OutputStream){
				IOUtils.writeChunked((byte[]) data, (OutputStream) output);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}	
		}else if(data instanceof char[]){			
			if(output instanceof Writer){
				IOUtils.writeChunked((char[]) data, (Writer) output);
			}else{
				throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
			}
		}else{
			throw new ActionException("Unsupport data " + data + ", action=" + self.getMetadata().getPath());
		}		
	}
	
	public static void writeLines(ActionContext actionContext) throws IOException {
		Thing  self = actionContext.getObject("self");
				
		Object output = self.doAction("getOutput", actionContext);
		String encoding = (String) self.doAction("getEncoding", actionContext);
		String lineEnding = (String) self.doAction("getLineEnding", actionContext);
		lineEnding = StringEscapeUtils.escapeJava(lineEnding);
		Collection<?> lines = (Collection<?>) self.doAction("getLines", actionContext);
		if(output instanceof OutputStream){
			IOUtils.writeLines(lines, lineEnding, (OutputStream) output, encoding);
		}else if(output instanceof Writer){
				IOUtils.writeLines(lines, lineEnding, (Writer) output);			
		}else{
			throw new ActionException("Unsupport output " + output + ", action=" + self.getMetadata().getPath());
		}		
	}
}
