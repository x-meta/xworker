package xworker.swt.actions;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GraphicsActions {
	
	public static ImageData gifToOther(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File inputFile = self.doAction("getInputFile", actionContext);
		File outputFile = self.doAction("getOutputFile", actionContext);
		String format = self.doAction("getOutputFormat", actionContext);
		int frame = self.doAction("getFrame", actionContext);
		
		ImageLoader loader = new ImageLoader();
		loader.load(inputFile.getAbsolutePath());
		
		if(frame < 0){
			frame = loader.data.length + frame;
		}
		
		if(frame < 0){
			frame = 0;
		}
		if(frame >= loader.data.length){
			frame = loader.data.length - 1;
		}
				
		mergeFromTo(loader.data, frame);
		ImageData data = loader.data[0];
		loader.data = new ImageData[]{data};
		
		int f = "JPEG".equals(format) ? SWT.IMAGE_JPEG : SWT.IMAGE_PNG;
		loader.save(outputFile.getAbsolutePath(), f);
		
		return data;
	}

	public static void mergeFromTo(ImageData[] datas, int frame){
		for(int i=0; i<=frame; i++){
			merge(datas[0], datas[i]);
		}
	}
	public static void merge(ImageData data1, ImageData data2){		
		for(int x=data2.x; x<data2.x + data2.width; x++){
			for(int y=data2.y; y<data2.y + data2.height; y++){
				data1.setPixel(x, y, data2.getPixel(x - data2.x, y - data2.y));
			}
		}
	}
}
