package xworker.javafx.scene;

import javafx.scene.Camera;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.paint.Paint;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class SubSceneActions {
    public static void init(SubScene node, Thing thing, ActionContext actionContext) {
        if(thing.valueExists("camera")){
            Camera camera = JavaFXUtils.getObject(thing, "camera", actionContext);
            if(camera != null) {
                node.setCamera(camera);
            }
        }
        if(thing.valueExists("fill")){
            Paint fill = JavaFXUtils.getObject(thing, "fill", actionContext);
            if(fill != null) {
                node.setFill(fill);
            }
        }
        if(thing.valueExists("height")){
            node.setHeight(thing.getDouble("height"));
        }
        if(thing.valueExists("root")){
            Parent root = JavaFXUtils.getObject(thing, "root", actionContext);
            if(root != null) {
                node.setRoot(root);
            }
        }
        if(thing.valueExists("userAgentStylesheet")){
            node.setUserAgentStylesheet(thing.getString("userAgentStylesheet"));
        }
    }

    public static SubScene create(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");
        Parent parent = (Parent) actionContext.get("parent");

        SubScene subScene = new SubScene(parent, self.getDouble("width"), self.getDouble("height"));
        init(subScene, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), subScene);

        actionContext.peek().put("parent", subScene);
        for(Thing child : self.getChilds()) {
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Paint){
                subScene.setRoot((Parent) obj);
            }
        }

        return subScene;
    }
}
