package xworker.io;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class WriterActions {
	public static Object createBufferedWriter(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Writer writer = self.doAction("getWriter", actionContext);
		if(writer == null) {
			throw new ActionException("Writer is null, action=" + self.getMetadata().getPath());
		}
		int size = self.doAction("getSize", actionContext);
		if(size <= 0) {
			return new BufferedWriter(writer);
		}else {
			return new BufferedWriter(writer, size);
		}
	}
	
	public static Object createCharArrayWriter(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int initialSize = self.doAction("getInitialSize", actionContext);
		if(initialSize <= 0) {
			return new CharArrayWriter();
		}else {
			return new CharArrayWriter(initialSize);
		}
	}
	
	public static Object createStringWriter(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int initialSize = self.doAction("getInitialSize", actionContext);
		if(initialSize <= 0) {
			return new StringWriter();
		}else {
			return new StringWriter(initialSize);
		}
	}
	
	public static Object createOutputStreamWriter(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		OutputStream out = self.doAction("getOutputStream", actionContext);
		if(out == null) {
			throw new ActionException("OutputStream is null, action=" + self.getMetadata().getPath());
		}		
		String charset = self.doAction("getCharset", actionContext);
		if(charset == null) {
			return new OutputStreamWriter(out);
		}else {
			return new OutputStreamWriter(out, Charset.forName(charset));
		}
	}
	
	public static Object createPipedWriter(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		PipedReader reader = self.doAction("getPipedReader", actionContext);
		if(reader == null) {
			return new PipedWriter();
		}else {
			return new PipedWriter(reader);
		}
	}
	
	private static PrintWriter getPrintWriter(Thing self, ActionContext actionContext) throws IOException {
		Object out = self.doAction("getOut", actionContext);
		if(out == null) {
			throw new ActionException("Out is null, action=" + self.getMetadata().getPath());
		}
		
		PrintWriter writer = null;
		if(out instanceof OutputStream) {
			writer = new PrintWriter((OutputStream) out, UtilData.isTrue(self.doAction("isAutoFlush", actionContext)));
		}else if(out instanceof PrintWriter) {
			writer = (PrintWriter) writer;
		}else if(out instanceof Writer) {
			writer = new PrintWriter((Writer) out);
		}else if(out instanceof File){
			String charset = self.doAction("getCharset", actionContext);
			if(charset != null && !"".equals(charset)) {
				writer = new PrintWriter((File) out, Charset.forName(charset));
			}else {
				writer = new PrintWriter((File) out);
			}
		}else if(out instanceof String) {
			String charset = self.doAction("getCharset", actionContext);
			if(charset != null && !"".equals(charset)) {
				writer = new PrintWriter((String) out, Charset.forName(charset));
			}else {
				writer = new PrintWriter((String) out);
			}
		}else {
			throw new ActionException("Unsupport out type, class=" + out.getClass().getName() + ", action=" + self.getMetadata().getPath());
		}
		
		return writer;
	}
	
	public static void print(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		PrintWriter writer = getPrintWriter(self, actionContext);
		String varName = self.doAction("getWriterWar", actionContext);
		if(varName != null && !"".equals(varName)) {
			actionContext.peek().put(varName, writer);
		}
		for(Thing child : self.getChilds()) {
			if(!"actions".equals(child.getThingName())) {
				Object result = child.getAction().run(actionContext);
				if(result == null && UtilData.isTrue(self.doAction("isIgnoreNull", actionContext))) {
					continue;
				}
				writer.print(result);
			}
		}
		
		if(UtilData.isTrue(self.doAction("isClose", actionContext))) {
			writer.close();
		}
	}
	
	public static void println(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		PrintWriter writer = getPrintWriter(self, actionContext);
		String varName = self.doAction("getWriterWar", actionContext);
		if(varName != null && !"".equals(varName)) {
			actionContext.peek().put(varName, writer);
		}
		for(Thing child : self.getChilds()) {
			if(!"actions".equals(child.getThingName())) {
				Object result = child.getAction().run(actionContext);
				if(result == null && UtilData.isTrue(self.doAction("isIgnoreNull", actionContext))) {
					continue;
				}
				writer.println(result);
			}
		}
		
		if(UtilData.isTrue(self.doAction("isClose", actionContext))) {
			writer.close();
		}
	}
	
	public static void printf(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		String format = self.doAction("getFormat", actionContext);		
		PrintWriter writer = getPrintWriter(self, actionContext);
		String varName = self.doAction("getWriterWar", actionContext);
		if(varName != null && !"".equals(varName)) {
			actionContext.peek().put(varName, writer);
		}
		List<Object> results = new ArrayList<Object>();
		for(Thing child : self.getChilds()) {
			if(!"actions".equals(child.getThingName())) {
				Object result = child.getAction().run(actionContext);
				if(result == null && UtilData.isTrue(self.doAction("isIgnoreNull", actionContext))) {
					continue;
				}
				results.add(result);
			}
		}
		
		Object[] args = new Object[results.size()];
		results.toArray(args);
		writer.printf(format, args);
		
		if(UtilData.isTrue(self.doAction("isClose", actionContext))) {
			writer.close();
		}
	}
}
