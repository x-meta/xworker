package xworker.swt.util.graphics;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ImageActions {
	public static int getImageType(String type){
		if("BMP".equals(type)){
			return SWT.IMAGE_BMP;
		}else if("GIF".equals(type)){
			return SWT.IMAGE_GIF;
		}else if("ICO".equals(type)){
			return SWT.IMAGE_ICO;
		}else if("JPEG".equals(type)){
			return SWT.IMAGE_JPEG;
		}else if("PNG".equals(type)){
			return SWT.IMAGE_PNG;
		}else if("TIFF".equals(type)){
			return SWT.IMAGE_TIFF;
		}else{
			return SWT.IMAGE_PNG;
		}
	}
	
	public static boolean saveImageToFile(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		int type = getImageType((String) self.doAction("getType", actionContext));
		ImageData imageData = (ImageData) self.doAction("getImageData", actionContext);
		if(imageData == null){
			throw new ActionException("ImageData is null, action=" + self.getMetadata().getPath());
		}
		Object filePath = self.doAction("getFilePath", actionContext);
		if(filePath == null){
			throw new ActionException("ImageData is null, action=" + self.getMetadata().getPath());
		}
		File file = null;
		if(filePath instanceof File){
			file = (File) filePath;
		}else{
			file = new File(String.valueOf(filePath));
		}
		
	    ImageLoader loader = new ImageLoader();
	    loader.data = new ImageData[1];
	    loader.data[0] = imageData;
	    loader.save(file.getAbsolutePath(), type);
	    
	    return true;
	}
}
