package xworker.javafx.scene.shape;

import javafx.scene.paint.Material;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.java.assist.JavaCacheItem;
import xworker.javafx.scene.NodeActions;
import xworker.javafx.util.JavaFXUtils;

public class Shape3DActions {
    public static void init(Shape3D node, Thing thing, ActionContext actionContext){
        NodeActions.init(node, thing, actionContext);

        if(thing.valueExists("cullFace")){
            node.setCullFace(CullFace.valueOf(thing.getString("cullFace")));
        }
        if(thing.valueExists("drawMode")){
            node.setDrawMode(DrawMode.valueOf(thing.getString("drawMode")));
        }
        if(thing.valueExists("material")){
            Material material = JavaFXUtils.getObject(thing, "material", actionContext);
            if(material != null) {
                node.setMaterial(material);
            }
        }
    }
}
