package xworker.javafx.scene.image;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.NodeActions;
import xworker.javafx.util.JavaFXUtils;

public class ImageViewActions {
    public static void init(ImageView node, Thing thing, ActionContext actionContext){
        NodeActions.init(node, thing, actionContext);

        if(thing.valueExists("fitHeight")){
            node.setFitHeight(thing.getDouble("fitHeight"));
        }
        if(thing.valueExists("fitWidth")){
            node.setFitWidth(thing.getDouble("fitWidth"));
        }
        if(thing.valueExists("image")){
            Image image = JavaFXUtils.getImage(thing, "image", actionContext);
            if(image != null) {
                node.setImage(image);
            }
        }
        if(thing.valueExists("preserveRatio")){
            node.setPreserveRatio(thing.getBoolean("preserveRatio"));
        }
        if(thing.valueExists("smooth")){
            node.setSmooth(thing.getBoolean("smooth"));
        }
        if(thing.valueExists("viewport")){
            Rectangle2D viewport = JavaFXUtils.getRectangle2D(thing, "viewport", actionContext);
            if(viewport != null) {
                node.setViewport(viewport);
            }
        }
        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static ImageView create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        ImageView node = new ImageView();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Image){
                node.setImage((Image) obj);
            }
        }

        return node;
    }
}
