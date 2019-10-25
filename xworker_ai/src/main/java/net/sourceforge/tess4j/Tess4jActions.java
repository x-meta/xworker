package net.sourceforge.tess4j;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

public class Tess4jActions {
	public static Object createTesseract(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ITesseract instance = new Tesseract1();
		String datapath = self.getStringBlankAsNull("datapath");
		if(datapath != null){
			instance.setDatapath(datapath);
		}else{
			//instance.setDatapath("c:\\Program Files (x86)\\Tesseract-OCR\\");
		}
		String language = self.getStringBlankAsNull("language");
		if(language != null){
			instance.setLanguage(language);
		}else{
			instance.setLanguage("eng");
		}
		String ocrEngineMode = self.getStringBlankAsNull("ocrEngineMode");
		if(ocrEngineMode != null){
			instance.setOcrEngineMode(self.getInt("ocrEngineMode"));
		}
		String pageSegMode = self.getStringBlankAsNull("pageSegMode");
		if(pageSegMode != null){
			instance.setPageSegMode(self.getInt("pageSegMode"));
		}
		
		return instance;
	}
	
	public static Rectangle getRect(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
				
		String rect = self.getStringBlankAsNull("rect");
		if(rect == null){
			return null;
		}
		
		String rs[] = rect.split("[,]");
		if(rs.length == 4){
			return new Rectangle(Integer.parseInt(rs[0]), Integer.parseInt(rs[1]), Integer.parseInt(rs[2]), Integer.parseInt(rs[3]));
		}else{
			return (Rectangle) UtilData.getObject(self, "rect", actionContext);
		}
	}
	
	public static ITesseract getInstance(Thing self, ActionContext actionContext){
		ITesseract instance = (ITesseract) self.doAction("getTesseract", actionContext);
		if(instance == null){
			instance = new Tesseract1();
			instance.setDatapath("c:\\Program Files (x86)\\Tesseract-OCR\\");
			instance.setLanguage("eng");
		}
		
		return instance;
	}
	
	public static String doOCRBufferedImage(ActionContext actionContext) throws TesseractException{
		Thing self = actionContext.getObject("self");
		
		//OCR引擎
		ITesseract instance = getInstance(self, actionContext);
		
		//要识别的图像
		BufferedImage image = (BufferedImage) self.doAction("getImage", actionContext);
		
		//区域
		Rectangle rect = (Rectangle) self.doAction("getRect", actionContext);
		
		if(image == null){
			throw new ActionException("Image is null, action=" + self.getMetadata().getPath());
		}
		
		if(rect == null){
			return instance.doOCR(image);
		}else{
			return instance.doOCR(image, rect);
		}
	}
	
	public static String doOCRImageFile(ActionContext actionContext) throws TesseractException{
		Thing self = actionContext.getObject("self");
		
		//OCR引擎
		ITesseract instance = getInstance(self, actionContext);
		
		//要识别的图像问价
		File image = (File) self.doAction("getImageFile", actionContext);
		
		//区域
		Rectangle rect = (Rectangle) self.doAction("getRect", actionContext);
		
		if(image == null){
			throw new ActionException("Image is null, action=" + self.getMetadata().getPath());
		}
		
		if(rect == null){
			return instance.doOCR(image);
		}else{
			return instance.doOCR(image, rect);
		}
	}
}
