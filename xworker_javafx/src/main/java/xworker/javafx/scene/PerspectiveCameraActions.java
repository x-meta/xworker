package xworker.javafx.scene;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;

public class PerspectiveCameraActions {
	public static void init(PerspectiveCamera camera, Thing thing, ActionContext actionContext) {
        if(thing.valueExists("farClip")){
            camera.setFarClip(thing.getDouble("farClip"));
        }
        if(thing.valueExists("nearClip")){
            camera.setNearClip(thing.getDouble("nearClip"));
        }
        if(thing.valueExists("fieldOfView")){
            camera.setFieldOfView(thing.getDouble("fieldOfView"));
        }
        if(thing.valueExists("verticalFieldOfView")){
            camera.setVerticalFieldOfView(thing.getBoolean("verticalFieldOfView"));
        }
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.get("parent");
		
		PerspectiveCamera camera = new PerspectiveCamera();
		init(camera, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), camera);
		
		if(parent instanceof Scene) {
			((Scene) parent).setCamera(camera);
		}
		
		return camera;
	}
}
