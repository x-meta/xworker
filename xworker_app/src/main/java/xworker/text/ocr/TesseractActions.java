package xworker.text.ocr;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

public class TesseractActions {
	public static String runOcr(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		String tesseract = (String) self.doAction("getTesseract", actionContext);
		if(tesseract == null || !new File(tesseract).exists()){
			throw new ActionException("Tesseract not found, action=" + self.getMetadata().getPath());
		}
		
		String imageFile = (String) self.doAction("getImageFile", actionContext);
		String outFile = (String) self.doAction("getOutFile", actionContext);
		String lang = (String) self.doAction("getLang", actionContext);
		String pagesegmode = (String) self.doAction("getPagesegmode", actionContext);

		String cmd = tesseract + " " + imageFile + " " + outFile;
		if(lang != null && !"".equals(lang)){
			cmd = cmd  + " -l " + lang;
		}
		if(pagesegmode != null && !"".equals(pagesegmode)){
			cmd = cmd + " -psm " + pagesegmode;
		}
		
		Process p = Runtime.getRuntime().exec(cmd);
		return IOUtils.toString(p.getInputStream());		
	}
	
	public static String getTesseract(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String tesseract = self.getStringBlankAsNull("tesseract");
		if(tesseract == null){
			Thing config = World.getInstance().getThing("local.xworker.config.orc.TessercatConfig");
			if(config != null){
				tesseract = config.getStringBlankAsNull("filePath");
			}
		}
		if(tesseract == null){
			tesseract = "C:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe";
		}
		
		return tesseract;
		
	}
}
