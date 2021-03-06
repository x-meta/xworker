package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TooltipActions {
    public static void init(Tooltip node, Thing thing, ActionContext actionContext) {
        PopupControlActions.init(node, thing, actionContext);

        if(thing.valueExists("contentDisplay")){
            node.setContentDisplay(ContentDisplay.valueOf(thing.getString("contentDisplay")));
        }
        if(thing.valueExists("font")){
            Font font = JavaFXUtils.getObject(thing, "font", actionContext);
            if(font != null) {
                node.setFont(font);
            }
        }
        if(thing.valueExists("graphic")){
            Node graphic = JavaFXUtils.getObject(thing, "graphic", actionContext);
            if(graphic != null) {
                node.setGraphic(graphic);
            }
        }
        if(thing.valueExists("graphicTextGap")){
            node.setGraphicTextGap(thing.getDouble("graphicTextGap"));
        }
        if(thing.valueExists("textAlignment")){
            node.setTextAlignment(TextAlignment.valueOf(thing.getString("textAlignment")));
        }
        if(thing.valueExists("textOverrun")){
            node.setTextOverrun(OverrunStyle.valueOf(thing.getString("textOverrun")));
        }
        if(thing.valueExists("text")){
            node.setText(thing.getString("text"));
        }
        if(thing.valueExists("wrapText")){
            node.setWrapText(thing.getBoolean("wrapText"));
        }
    }

    public static Tooltip create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Tooltip node = new Tooltip();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()) {
            child.doAction("create", actionContext);
        }

        return node;
    }
}
