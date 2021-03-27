package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.event.ThingEventHandler;
import xworker.javafx.util.JavaFXUtils;

public class DialogActions {
    public static void init(Dialog node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("title")){
            String title = JavaFXUtils.getString(thing, "title", actionContext);
            if(title != null) {
                node.setTitle(title);
            }
        }
        if(thing.valueExists("headerText")){
            String headerText = JavaFXUtils.getString(thing, "headerText", actionContext);
            if(headerText != null) {
                node.setHeaderText(headerText);
            }
        }
        if(thing.valueExists("contentText")){
            String contentText = JavaFXUtils.getString(thing, "contentText", actionContext);
            if(contentText != null) {
                node.setContentText(contentText);
            }
        }
        if(thing.valueExists("dialogPane")){
            DialogPane dialogPane = JavaFXUtils.getObject(thing, "dialogPane", actionContext);
            if(dialogPane != null) {
                node.setDialogPane(dialogPane);
            }
        }
        if(thing.valueExists("graphic")){
            Node graphic = JavaFXUtils.getObject(thing, "graphic", actionContext);
            if(graphic != null) {
                node.setGraphic(graphic);
            }
        }
        if(thing.valueExists("resizable")){
            node.setResizable(thing.getBoolean("resizable"));
        }
        if(thing.valueExists("resultConverter")){
            Callback<ButtonType,Object> resultConverter = JavaFXUtils.getObject(thing, "resultConverter", actionContext);
            if(resultConverter != null) {
                node.setResultConverter(resultConverter);
            }
        }
        if(thing.valueExists("result")){
            node.setResult(JavaFXUtils.getObject(thing, "result", actionContext));
        }
    }

    public static Dialog<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Dialog<Object> node = new Dialog<>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof ButtonType){
                node.getDialogPane().getButtonTypes().add((ButtonType) obj);
            }
        }

        if(node.getDialogPane().getButtonTypes().size() == 0){
            node.getDialogPane().getButtonTypes().add(ButtonType.OK);
        }
        return node;

    }

    public static void createGraphic(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node && parent instanceof Dialog){
                ((Dialog) parent).setGraphic((Node) obj);
            }
        }
    }

    public static void createResultConverter(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Dialog<Object> dialog = actionContext.getObject("parent");

        dialog.setResultConverter(new ThingResultConverter(self, actionContext));
    }

    public static void createContent(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Dialog<Object> dialog = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                dialog.getDialogPane().setContent((Node) obj);
                return;
            }
        }
    }

    public static void createEvent(ActionContext actionContext) {
        ThingEventHandler.create(actionContext);
        /*
        Thing self = actionContext.getObject("self");
        Dialog<Object> obj = actionContext.getObject("parent");
        String eventName = self.getString("type");
        EventHandler eventHandler = new ThingEventHandler(self, actionContext);

        if("onCloseRequest".equals(eventName)) {
            obj.setOnCloseRequest(eventHandler);
        }else if("onHidden".equals(eventName)) {
            obj.setOnHidden(eventHandler);
        }else if("onHiding".equals(eventName)) {
            obj.setOnHiding(eventHandler);
        }else if("onShowing".equals(eventName)) {
            obj.setOnShowing(eventHandler);
        }else if("onShown".equals(eventName)) {
            obj.setOnShown(eventHandler);
        }
        */
    }

    public static class ThingResultConverter implements Callback<ButtonType,Object>{
        Thing thing;
        ActionContext actionContext;

        public ThingResultConverter(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }

        @Override
        public Object call(ButtonType param) {
            Object result = null;
            for(Thing child : thing.getChilds()){
                result = child.getAction().run(actionContext, "param", param);
            }
            return result;
        }
    }

}
