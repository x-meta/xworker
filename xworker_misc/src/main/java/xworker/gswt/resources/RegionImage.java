package xworker.gswt.resources;

import org.eclipse.swt.graphics.Image;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.graphics.ImageUtils;
import xworker.swt.util.ResourceManager;

public class RegionImage {
	public static Image create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Image image = actionContext.getObject(self.getString("image"));
		int x = self.getInt("x");
		int y = self.getInt("y");
		int width = self.getInt("width");
		int height = self.getInt("height");
		
		Image newImage = ImageUtils.clip(image, x, y, width, height);
		ResourceManager.putResource(self.getMetadata().getPath(), newImage, actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), newImage);
		return newImage;
	}
}
