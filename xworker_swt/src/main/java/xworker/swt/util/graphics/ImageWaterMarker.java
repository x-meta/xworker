package xworker.swt.util.graphics;

import java.io.File;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.swt.graphics.ColorCreator;
import xworker.swt.graphics.FontCreator;
import xworker.swt.util.SwtUtils;

public class ImageWaterMarker {
	public static Image run(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取图片
		Object imageObj = self.doAction("getImage", actionContext);
		Image image = null;
		Image targetImage = null;
		Device device = null;
		
		boolean disposeImage = false;
		boolean disposeTargetImage = false;
		boolean disposeDevice = false;
		
		try{
			if(imageObj instanceof Image){
				image = (Image) imageObj;
				device = image.getDevice();
			}else{
				device = Display.getCurrent();
				if(device == null){
					device = new Display();
					disposeDevice = true;
				}
				
				disposeImage = true;
				if(imageObj instanceof String){
					image = new Image(device, (String) imageObj);
				}else if(imageObj instanceof File){
					image = new Image(device, ((File) imageObj).getAbsolutePath());
				}else{
					throw new ActionException("Image is null, plase set a image or image filePaht, thing=" + self.getMetadata().getPath());
				}
			}
			
			//制作图片
			targetImage = new Image(image.getDevice(), (ImageData) image.getImageData().clone());
			GC gc = new GC(targetImage);
			Font font = null;
			Color color = null;
			try{
				//设置字体
				Object f = UtilData.getData(self, "font", actionContext);
				if(f instanceof Font){
					gc.setFont((Font) f);
				}else if(f instanceof String){
					Thing fontThing = new Thing("xworker.swt.graphics.Font");
					fontThing.set("fontData", f);
					actionContext.peek().put("self", fontThing);
					font = (Font) FontCreator.createResource(actionContext);
					if(font != null){
						gc.setFont(font);
					}
				}
				
				//设置颜色
				Object c = UtilData.getData(self, "color", actionContext);
				if(c instanceof Color){
					gc.setForeground((Color) c);
				}else if(c instanceof String){
					Thing colorThing = new Thing("xworker.swt.graphics.Color");
					colorThing.set("rgb", c);
					actionContext.peek().put("self", colorThing);
					color = (Color) ColorCreator.createResource(actionContext);
					if(color != null){
						gc.setForeground(color);
					}
				}
				
				gc.setAlpha(self.getInt("alpha"));
				float degree = self.getFloat("degree");
				if(degree != 0){
					Transform t = new Transform(targetImage.getDevice());
					t.rotate(1f * degree);
					gc.setTransform(t);
				}
				int width = targetImage.getBounds().width;
				int height = targetImage.getBounds().height;
				String text = UtilData.getString(self, "text", actionContext);
				if(text == null || "".equals(text.trim())){
					text = "Hello World!";
				}
				int charWidth = 0;			
				for(char ch : text.toCharArray()){
					charWidth += gc.getCharWidth(ch);
				}
				int charHeight = gc.getFontMetrics().getHeight();
				
				int widthSpace = self.getInt("hgap");
				int hegithSpace = self.getInt("vgap");
				int i = - 4 * width / (charWidth *  widthSpace);			
				while(true){
					int n = - 4 * height / (charHeight * hegithSpace);
					while(true){					
						gc.drawText(text, i * charWidth * widthSpace, n * charHeight * hegithSpace, true);
						
						n++;
						if(n * charHeight * hegithSpace > height * 4){
							break;
						}
					}
					i++;
					
					if(i * charWidth * widthSpace > width * 4){
						break;
					}
				}
				
				//保存图片
				Object saveFileObj = self.doAction("getSaveFile", actionContext);
				File file = null;
				if(saveFileObj instanceof File){
					file = (File) saveFileObj;
				}else if(saveFileObj instanceof String){
					file = new File((String) saveFileObj);
					if(!file.getParentFile().exists()){
						file.getParentFile().mkdirs();
					}
				}
				
				if(file != null){
					SwtUtils.saveImage(targetImage, file);
				}
				
			}finally{
				gc.dispose();
				
				if(font != null){
					font.dispose();
				}
				
				if(color != null){
					color.dispose();
				}
			}
			
			//返回值
			if(self.getBoolean("returnImage") && !disposeDevice){
				return targetImage;
			}else{
				disposeTargetImage = true;
				return null;
			}
		}finally{
			if(disposeImage && image != null){
				image.dispose();
			}
			
			if(disposeTargetImage && targetImage != null){
				targetImage.dispose();
			}
			
			if(disposeDevice && device != null){
				device.dispose();
			}
		}
	}


}
