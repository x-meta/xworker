package xworker.javafx.scene.shape;

import javafx.scene.shape.PathElement;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PathElementActions {
    public static void init(PathElement node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("absolute")){
            node.setAbsolute(thing.getBoolean("absolute"));
        }
    }
}
