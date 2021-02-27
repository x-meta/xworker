package xworker.javafx.scene;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.ParallelCamera;
import javafx.scene.Scene;

public class ParallelCameraActions {
	public static void init(ParallelCamera camera, Thing thing, ActionContext actionContext) {
        if(thing.valueExists("farClip")){
            camera.setFarClip(thing.getDouble("farClip"));
        }
        if(thing.valueExists("nearClip")){
            camera.setNearClip(thing.getDouble("nearClip"));
        }
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.get("parent");
		
		ParallelCamera camera = new ParallelCamera();
		init(camera, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), camera);
		
		if(parent instanceof Scene) {
			((Scene) parent).setCamera(camera);
		}
		
		return camera;
	}
}
