package xworker.org.krysalis.barcode4j;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.Document;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xml.sax.SAXException;

import xworker.lang.executor.Executor;
import xworker.xml.XmlUtils;

public class Barcode4jActions {
	private static final String TAG = Barcode4jActions.class.getName();
	
	public static Object run(ActionContext actionContext) throws ConfigurationException, SAXException, IOException, BarcodeException, TransformerException{
		Thing self = (Thing) actionContext.get("self");
		String xml = "<barcode>\n" + XmlUtils.encodeToXml(self) + "\n</barcode>";
		
		//创建生成器
		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
		Configuration cfg = builder.build(new ByteArrayInputStream(xml.getBytes()));		
		BarcodeGenerator gen = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
		
		//获取要生生成的消息
		String message = (String) self.doAction("getMessage", actionContext);
		if(message == null || "".equals(message)){
			Executor.warn(TAG, "message is null, thing=" + self.getMetadata().getPath());
			return null;
		}
		
		String format = (String) self.doAction("getFormat", actionContext);
		if(format == null){
			format = "BMP";
		}
		
		//输出类型
		Object output = self.doAction("getOutput", actionContext);
		OutputStream out = null;
		boolean closeOut = false;
		if(output instanceof OutputStream){
			out = (OutputStream) output;
		}else if(output instanceof File){
			out = new FileOutputStream((File) output);
			closeOut = true;
		}else if(output instanceof String){
			out = new FileOutputStream(new File((String) output));
			closeOut = true;
		}else{
			out = new ByteArrayOutputStream();
		}
		
		if("SVG".equals(format)){
			SVGCanvasProvider provider = new SVGCanvasProvider(false, 0);
			gen.generateBarcode(provider, message);
			Document doc = provider.getDOM();
			
		     //开始把Document映射到文件
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transFormer = transFactory.newTransformer();
			             
			//设置输出结果
			DOMSource domSource = new DOMSource(doc);
			StreamResult xmlResult = new StreamResult(out);
			xmlResult.setSystemId("1111");
			 
			//输出xml文件
			transFormer.transform(domSource, xmlResult);
		}else if("EPS".equals(format)){
			EPSCanvasProvider provider = new EPSCanvasProvider(out, 0);
			gen.generateBarcode(provider, message);
			provider.finish();
		}else{			
			BitmapCanvasProvider provider = new BitmapCanvasProvider(out, format, 300, BufferedImage.TYPE_BYTE_GRAY, true, 0);
			gen.generateBarcode(provider, message);
			provider.finish();
		}
		
		out.flush();
		if(closeOut){
			out.close();
		}
		
		if(out instanceof ByteArrayOutputStream){
			return out;
		}else{
			return null;
		}
	}
	
	
}
