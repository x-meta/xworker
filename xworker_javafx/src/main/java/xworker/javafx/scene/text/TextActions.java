package xworker.javafx.scene.text;

import javafx.geometry.VPos;
import javafx.scene.text.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.shape.ShapeActions;
import xworker.javafx.util.JavaFXUtils;

public class TextActions {
    public static void init(Text node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("boundsType")){
            node.setBoundsType(TextBoundsType.valueOf(thing.getString("boundsType")));
        }
        if(thing.valueExists("font")){
            Font font = JavaFXUtils.getObject(thing, "font", actionContext);
            if(font != null) {
                node.setFont(font);
            }
        }
        if(thing.valueExists("fontSmoothingType")){
            node.setFontSmoothingType(FontSmoothingType.valueOf(thing.getString("fontSmoothingType")));
        }
        if(thing.valueExists("lineSpacing")){
            node.setLineSpacing(thing.getDouble("lineSpacing"));
        }
        if(thing.valueExists("strikethrough")){
            node.setStrikethrough(thing.getBoolean("strikethrough"));
        }
        if(thing.valueExists("textAlignment")){
            node.setTextAlignment(TextAlignment.valueOf(thing.getString("textAlignment")));
        }
        if(thing.valueExists("textOrigin")){
            node.setTextOrigin(VPos.valueOf(thing.getString("textOrigin")));
        }
        if(thing.valueExists("underline")){
            node.setUnderline(thing.getBoolean("underline"));
        }
        if(thing.valueExists("wrappingWidth")){
            node.setWrappingWidth(thing.getDouble("wrappingWidth"));
        }
        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
        if(thing.valueExists("text")){
            String text = JavaFXUtils.getString(thing, "text", actionContext);
            if(text != null) {
                node.setText(text);
            }
        }
    }

    public static Text create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Text node = new Text();
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
