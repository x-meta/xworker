package xworker.javafx.control;

import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.stage.PopupWindowActions;
import xworker.javafx.util.JavaFXUtils;

public class PopupControlActions {
    public static void init(PopupControl node, Thing thing, ActionContext actionContext){
        PopupWindowActions.init(node, thing, actionContext);

        if(thing.valueExists("id")){
            node.setId(thing.getString("id"));
        }
        if(thing.valueExists("maxHeight")){
            node.setMaxHeight(thing.getDouble("maxHeight"));
        }
        if(thing.valueExists("maxWidth")){
            node.setMaxWidth(thing.getDouble("maxWidth"));
        }
        if(thing.valueExists("minHeight")){
            node.setMinHeight(thing.getDouble("minHeight"));
        }
        if(thing.valueExists("minWidth")){
            node.setMinWidth(thing.getDouble("minWidth"));
        }
        if(thing.valueExists("prefHeight")){
            node.setPrefHeight(thing.getDouble("prefHeight"));
        }
        if(thing.valueExists("prefWidth")){
            node.setPrefWidth(thing.getDouble("prefWidth"));
        }
        if(thing.valueExists("skin")){
            Skin<?> skin = JavaFXUtils.getObject(thing, "skin", actionContext);
            if(skin != null) {
                node.setSkin(skin);
            }
        }
    }
}
