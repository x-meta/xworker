package xworker.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;



public class OutputStreamActions {
	/**
	 * 写入数据。
	 * 
	 * @param actionContext
	 * @throws OgnlException 
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public static void write(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		OutputStream out = getOutputStream(actionContext);
		
		//要写入的对象
		Object obj = UtilData.getData(self, "object", actionContext);
		if(obj instanceof byte[]){
			byte[] bytes = (byte[]) obj;
			if(self.getStringBlankAsNull("off") != null && self.getStringBlankAsNull("len") != null){
				out.write(bytes, getInt(self.getString("off"), actionContext), getInt(self.getString("len"), actionContext));
			}else{
				out.write(bytes);
			}
		}else if(obj instanceof String){
			String str = (String) obj;
			String charset = self.getStringBlankAsNull("charset");
			if(charset != null){
				out.write(str.getBytes(charset));
			}else{
				out.write(str.getBytes());
			}
		}else if(obj instanceof Number){
			ByteBuffer buf = ByteBuffer.allocate(128);
			Number num = (Number) obj;
			if(obj instanceof Byte){
				buf.put(num.byteValue());
			}else if(obj instanceof Short){
				buf.putShort(num.shortValue());
			}else if(obj instanceof Character){
				buf.putChar((char) num.intValue());
			}else if(obj instanceof Integer){
				buf.putInt(num.intValue());
			}else if(obj instanceof Long){
				buf.putLong(num.longValue());
			}else if(obj instanceof Float){
				buf.putFloat(num.floatValue());
			}else if(obj instanceof Double){
				buf.putDouble(num.doubleValue());
			}else if(obj instanceof BigDecimal){
				BigDecimal bd = (BigDecimal) obj;
				out.write(bd.toString().getBytes());
			}else if(obj instanceof BigInteger){
				BigInteger bd = (BigInteger) obj;
				out.write(bd.toString().getBytes());
			}else{
				buf.putDouble(num.doubleValue());
			}
			
			if(buf.position() != 0){
				out.write(buf.array(), 0, buf.position());
			}
		}else if(obj instanceof Buffer){
			//Buffer buf = (Buffer) obj;
			//
			throw new ActionException("current not supported java.nio.Buffer, path=" + self.getMetadata().getPath());
		}else if(obj instanceof InputStream){
			InputStream in = (InputStream) obj;
			byte[] bytes = new byte[2048];
			int length = -1;
			while((length = in.read(bytes)) != -1){
				out.write(bytes, 0, length);
			}
		}else if(obj instanceof java.io.Serializable){
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.close();
		}else if(obj != null){
			String str = obj.toString();
			if(str != null){
				out.write(str.getBytes());
			}
		}
	}
	
	public static int getInt(String var, ActionContext actionContext) throws OgnlException{
		Object obj = UtilData.getData(var, actionContext);
		
		if(obj instanceof Integer){
			return (Integer) obj;
		}else if(obj instanceof Number){
			return ((Number) obj).intValue();
		}else if(obj instanceof String){
			return Integer.parseInt((String) obj);
		}else{
			return 0;
		}
	}
	
	public static OutputStream getOutputStream(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取输出流
		Object outObj = UtilData.getData(self, "outputStream", actionContext);
		if(outObj == null){
			throw new ActionException("outputStream is null, path=" + self.getMetadata().getPath());
		}
		if(!(outObj instanceof OutputStream)){
			throw new ActionException("Is not a outputStream, currentIs " + outObj.getClass() + ", path=" + self.getMetadata().getPath());
		}
		
		return (OutputStream) outObj; 
	}
	
	public static void flush(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		OutputStream out = self.doAction("getOutputStream", actionContext);
		if(out != null) {
			out.flush();
		}
	}
	
	public static void close(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		OutputStream out = self.doAction("getOutputStream", actionContext);
		if(out != null) {
			out.close();
		}
	}
	
	public static void writeBytes(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		OutputStream out = self.doAction("getOutputStream", actionContext);
		
		byte[] bytes = self.doAction("getBytes", actionContext);
		int offset = self.doAction("getOffset", actionContext);
		if(offset < 0) {
			offset = 0;
		}
		int length = self.doAction("getLength", actionContext);
		if(length < 0) {
			length = bytes.length;
		}
		
		out.write(bytes, offset, length);
	}
	
	public static Object createByteArrayOutputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		int size = self.doAction("getSize", actionContext);
		ByteArrayOutputStream bout = null;
		if(size > 0) {
			bout = new ByteArrayOutputStream(size);
		}else {
			bout = new ByteArrayOutputStream();
		}
		
		OutputStreamUtils.writeChildDatas(self, bout, actionContext);
		return bout;
	}
	
	public static Object createFileOutputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		boolean append = self.doAction("isAppend", actionContext);
		FileOutputStream fout = new FileOutputStream(file, append);
		OutputStreamUtils.writeChildDatas(self, fout, actionContext);
		return fout;
	}
	
	public static Object createPipedOutputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		PipedInputStream snk = self.doAction("getSnk", actionContext);
		PipedOutputStream pout = null;
		if(snk != null) {
			pout = new PipedOutputStream(snk);
		}else {
			pout = new PipedOutputStream();
		}
		
		OutputStreamUtils.writeChildDatas(self, pout, actionContext);
		return pout;
	}
	
	public static Object createGZIPOutputStream(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		OutputStream out = self.doAction("getOutputStream", actionContext);
		int size = self.doAction("getSize", actionContext);
		GZIPOutputStream gout = null;
		if(size > 0) {
			gout = new GZIPOutputStream(out, size);
		}else {
			gout = new GZIPOutputStream(out);
		}
		
		OutputStreamUtils.writeChildDatas(self, gout, actionContext);
		return gout;
	}
	
	public static void writer(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		OutputStream bout = self.doAction("getOutputStream", actionContext);
		OutputStreamUtils.writeChildDatas(self, bout, actionContext);
	}
}
