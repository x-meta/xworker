package xworker.javafx.control;

import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TextAreaActions {
    public static void init(TextArea node, Thing thing, ActionContext actionContext){
        TextInputControlActions.init(node, thing, actionContext);

        if(thing.valueExists("prefColumnCount")){
            node.setPrefColumnCount(thing.getInt("prefColumnCount"));
        }
        if(thing.valueExists("prefRowCount")){
            node.setPrefRowCount(thing.getInt("prefRowCount"));
        }
        if(thing.valueExists("scrollLeft")){
            node.setScrollLeft(thing.getDouble("scrollLeft"));
        }
        if(thing.valueExists("scrollTop")){
            node.setScrollTop(thing.getDouble("scrollTop"));
        }
        if(thing.valueExists("wrapText")){
            node.setWrapText(thing.getBoolean("wrapText"));
        }
    }

    public static TextArea create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TextArea node = new TextArea();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Font){
                node.setFont((Font) obj);
            }
        }

        return node;
    }
}
