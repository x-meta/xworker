package xworker.javafx.scene;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import xworker.javafx.util.JavaFXUtils;

public class SceneActions {
	public static void init(Scene scene, Thing thing, ActionContext actionContext) {
        if(thing.valueExists("cursor")){
        	Cursor cursor = JavaFXUtils.getCursor(thing.getString("cursor"));
        	if(cursor != null) {
        		scene.setCursor(cursor);
        	}
        }
        if(thing.valueExists("nodeOrientation")) {
			scene.setNodeOrientation(NodeOrientation.valueOf(thing.getString("nodeOrientation")));
		}
	}
	
	public static Scene create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.get("parent");
		
		Parent root = self.doAction("getRoot", actionContext);
		Paint fill = self.doAction("getFill", actionContext);
		Scene scene = null;
		
		if(self.valueExists("antiAliasing")) {
			scene = new Scene(root, self.getDouble("width"), self.getDouble("height"), 
					self.getBoolean("depthBuffer"), JavaFXUtils.getSceneAntialiasing(self.getString("antiAliasing")));
		}else if(self.valueExists("width") && self.valueExists("height") &&  self.valueExists("depthBuffer")) {
			scene = new Scene(root, self.getDouble("width"), self.getDouble("height"),self.getBoolean("depthBuffer"));
		}else if(self.valueExists("width") && self.valueExists("height") && fill != null) {
			scene = new Scene(root, self.getDouble("width"), self.getDouble("height"), fill);
		}else if(self.valueExists("width") && self.valueExists("height")) {
			scene = new Scene(root, self.getDouble("width"), self.getDouble("height"));
		}else if(fill != null) {
			scene = new Scene(root, fill);
		}else {
			scene = new Scene(root);
		}
		
		init(scene, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), scene);
		
		actionContext.peek().put("parent", scene);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		if(parent instanceof Stage) {
			((Stage) parent).setScene(scene);
		}
		
		return scene;
	}
}
