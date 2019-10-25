package xworker.libdgx.graphics;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class OrthographicCameraActions {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String yDown = self.getStringBlankAsNull("yDown");
		String viewportWidth = self.getStringBlankAsNull("viewportWidth");
		String viewportHeight = self.getStringBlankAsNull("viewportHeight");
		String rotateAngle = self.getStringBlankAsNull("rotateAngle");

		OrthographicCamera item = new OrthographicCamera();
		
		if(yDown != null && viewportWidth != null && viewportHeight != null){
			item.setToOrtho(self.getBoolean("yDown"), self.getFloat("viewportWidth"), self.getFloat("viewportHeight"));
		}else if(yDown != null){
			item.setToOrtho(self.getBoolean("yDown"));
		}
		
		if(rotateAngle != null){
			item.rotate(self.getFloat("rotateAngle"));
		}
		
		if(self.getStringBlankAsNull("zoom") != null){
			item.zoom = self.getFloat("zoom");
		}
		
		item.update();
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
