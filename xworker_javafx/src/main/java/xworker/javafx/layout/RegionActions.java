package xworker.javafx.layout;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.layout.Region;
import xworker.javafx.scene.NodeActions;
import xworker.javafx.util.JavaFXUtils;

public class RegionActions {
	public static void init(Region region, Thing thing, ActionContext actionContext) {
		NodeActions.init(region, thing, actionContext);
		
        if(thing.valueExists("cacheShape")){
            region.setCacheShape(thing.getBoolean("cacheShape"));
        }
        if(thing.valueExists("centerShape")){
            region.setCenterShape(thing.getBoolean("centerShape"));
        }
        if(thing.valueExists("opaqueInsets")){
            Insets opaqueInsets = JavaFXUtils.getInsets(thing, "opaqueInsets", actionContext);
            if(opaqueInsets != null){
                region.setOpaqueInsets(opaqueInsets);
            }
        }
        if(thing.valueExists("padding")){
            Insets padding = JavaFXUtils.getInsets(thing, "padding", actionContext);
            if(padding != null){
                region.setPadding(padding);
            }
        }
        if(thing.valueExists("maxHeight")){
            region.setMaxHeight(thing.getDouble("maxHeight"));
        }
        if(thing.valueExists("maxWidth")){
            region.setMaxWidth(thing.getDouble("maxWidth"));
        }
        if(thing.valueExists("minHeight")){
            region.setMinHeight(thing.getDouble("minHeight"));
        }
        if(thing.valueExists("minWidth")){
            region.setMinWidth(thing.getDouble("minWidth"));
        }
        if(thing.valueExists("prefHeight")){
            region.setPrefHeight(thing.getDouble("prefHeight"));
        }
        if(thing.valueExists("prefWidth")){
            region.setPrefWidth(thing.getDouble("prefWidth"));
        }
        if(thing.valueExists("scaleShape")){
            region.setScaleShape(thing.getBoolean("scaleShape"));
        }
        if(thing.valueExists("snapToPixel")){
            region.setSnapToPixel(thing.getBoolean("snapToPixel"));
        }
	}

	public static void createBackground(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Region parent = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Background){
                parent.setBackground((Background) obj);
            }
        }
    }
}
