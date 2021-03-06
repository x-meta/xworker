package xworker.javafx.scene.web;

import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.NodeActions;

public class WebViewActions {
    public static void init(WebView node, Thing thing, ActionContext actionContext){
        NodeActions.init(node, thing, actionContext);

        if(thing.valueExists("contextMenuEnabled")){
            node.setContextMenuEnabled(thing.getBoolean("contextMenuEnabled"));
        }
        if(thing.valueExists("fontScale")){
            node.setFontScale(thing.getDouble("fontScale"));
        }
        if(thing.valueExists("fontSmoothingType")){
            node.setFontSmoothingType(FontSmoothingType.valueOf(thing.getString("fontSmoothingType")));
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
        if(thing.valueExists("zoom")){
            node.setZoom(thing.getDouble("zoom"));
        }
        WebEngineActions.init(node.getEngine(), thing, actionContext);
        if(thing.valueExists("url")){
            node.getEngine().load(thing.getString("url"));
        }
        if(thing.valueExists("content")){
            node.getEngine().loadContent(thing.getString("content"));
        }

    }

    public static WebView create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        WebView node = new WebView();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
