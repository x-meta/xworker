package xworker.libdgx.graphics;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.PerspectiveCamera;

public class PerspectiveCameraActions {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String fieldOfViewY = self.getStringBlankAsNull("fieldOfViewY");
		String viewportWidth = self.getStringBlankAsNull("viewportWidth");
		String viewportHeight = self.getStringBlankAsNull("viewportHeight");

		PerspectiveCamera item = null;
		
		if(fieldOfViewY != null && viewportWidth != null && viewportHeight != null){
			item = new PerspectiveCamera(self.getFloat("fieldOfViewY"), self.getFloat("viewportWidth"), self.getFloat("viewportHeight"));
		}else {
			item = new PerspectiveCamera();
		}

		//item.update();
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
