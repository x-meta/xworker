package xworker.net.netty.buffer.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.codes.PropertyCoder;
import org.xmeta.codes.XmlCoder;
import org.xml.sax.SAXException;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;

public class ThingCoder {
	public static void encode(ActionContext actionContext) throws IOException, OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		//Thing thing = self.doAction("getThing", actionContext);
		Thing thing = (Thing) CodecUtils.getValue(self, actionContext);
		String coder = self.doAction("getCoder", actionContext);
		if(coder == null) {
			coder = "properties";
		}
		
		//首先压缩编码方式
		buffer.writeByte(coder.getBytes().length);
		buffer.writeBytes(coder.getBytes());
				
		//存放事物的数据
		if(thing == null) {
			buffer.writeInt(0);
		}else {
			byte[] bytes = null;
			if("xml".equals(coder)) {
				bytes = XmlCoder.encodeToString(thing).getBytes(Charset.forName("utf-8"));
			}else {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(bout));
				PropertyCoder.encode(thing, writer, new HashMap<Thing, String>());
				bytes = bout.toByteArray();
			}
			buffer.writeInt(bytes.length);
			buffer.writeBytes(bytes);
		}
	
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException, IOException, SAXException, ParserConfigurationException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
				
		int length = buffer.readByte() & 0xFF;
		
		//编码方式
		byte[] bytes = new byte[length];
		buffer.readBytes(bytes);
		String coder = new String(bytes);
		
		//模型
		length = buffer.readInt();
		Thing thing = null;
		if(length > 0) {
			bytes = new byte[length];
			buffer.readBytes(bytes);
			
			thing = new Thing();
			if("xml".equals(coder)) {
				XmlCoder.parse(thing, new ByteArrayInputStream(bytes));
			}else {
				PropertyCoder.decode(thing, new ByteArrayInputStream(bytes), true, System.currentTimeMillis());
			}
		}
		
		CodecUtils.setValue(self, thing, actionContext); 
	}
}
