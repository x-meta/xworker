package xworker.com.google.zxing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ognl.OgnlException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class ZXingActions {
	public static Object run(ActionContext actionContext) throws WriterException, OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		String message = (String) self.doAction("getMessage", actionContext);
		BarcodeFormat barcodeFormat = getBarcodeFormat((String) self.doAction("getBarcodeFormat", actionContext));
		String outputFormat = (String) self.doAction("getOutputFormat", actionContext);
		
		MultiFormatWriter encoder = new MultiFormatWriter();
		BitMatrix bitMatrix = encoder.encode(message, barcodeFormat, 
				self.getInt("width", -1, actionContext), self.getInt("height", -1, actionContext));
		
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
		
		
		MatrixToImageWriter.writeToStream(bitMatrix, outputFormat, out);
		
		if(closeOut){
			out.close();
		}
		
		if(out instanceof ByteArrayOutputStream){
			return ((ByteArrayOutputStream) out).toByteArray();
		}else{			
			return null;
		}
	}
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Composite parent = (Composite) actionContext.get("parent");
		Canvas canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		ZXingPainListener listener = new ZXingPainListener(canvas, self, actionContext);
		//canvas.addPaintListener(listener);
		canvas.addListener(SWT.Resize, listener);
		
		actionContext.peek().put("parent", canvas);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);			
		}
		
		Designer.attach(canvas, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return canvas;
	}
	
	public static BarcodeFormat getBarcodeFormat(String format){
		if("AZTEC".equals(format)){
			return BarcodeFormat.AZTEC;
		}else if("CODABAR".equals(format)){
			return BarcodeFormat.CODABAR;
		}else if("CODE_128".equals(format)){
			return BarcodeFormat.CODE_128;
		}else if("CODE_39".equals(format)){
			return BarcodeFormat.CODE_39;
		}else if("CODE_93".equals(format)){
			return BarcodeFormat.CODE_93;
		}else if("DATA_MATRIX".equals(format)){
			return BarcodeFormat.DATA_MATRIX;
		}else if("EAN_13".equals(format)){
			return BarcodeFormat.EAN_13;
		}else if("EAN_8".equals(format)){
			return BarcodeFormat.EAN_8;
		}else if("ITF".equals(format)){
			return BarcodeFormat.ITF;
		}else if("MAXICODE".equals(format)){
			return BarcodeFormat.MAXICODE;
		}else if("PDF_417".equals(format)){
			return BarcodeFormat.PDF_417;
		}else if("QR_CODE".equals(format)){
			return BarcodeFormat.QR_CODE;
		}else if("RSS_14".equals(format)){
			return BarcodeFormat.RSS_14;
		}else if("RSS_EXPANDED".equals(format)){
			return BarcodeFormat.RSS_EXPANDED;
		}else if("UPC_A".equals(format)){
			return BarcodeFormat.UPC_A;
		}else if("UPC_E".equals(format)){
			return BarcodeFormat.UPC_E;
		}else if("UPC_EAN_EXTENSION".equals(format)){
			return BarcodeFormat.UPC_EAN_EXTENSION;
		}else{
			return BarcodeFormat.QR_CODE;
		}
	}
}
