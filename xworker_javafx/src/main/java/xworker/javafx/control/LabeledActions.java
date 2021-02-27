package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import xworker.javafx.util.JavaFXUtils;

public class LabeledActions {
	public static void init(Labeled labeled, Thing thing, ActionContext actionContext) {
		ControlActions.init(labeled, thing, actionContext);
		
        if(thing.valueExists("alignment")){
            labeled.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
        if(thing.valueExists("contentDisplay")){
            labeled.setContentDisplay(ContentDisplay.valueOf(thing.getString("contentDisplay")));
        }
        if(thing.valueExists("ellipsisString")){
            labeled.setEllipsisString(thing.getString("ellipsisString"));
        }
        if(thing.valueExists("font")){
        	Font font = JavaFXUtils.getFont(thing.getString("font"), actionContext);
        	if(font != null) {
        		labeled.setFont(font);
        	}
        }
        if(thing.valueExists("graphic")){
        	Node graphic = thing.doAction("getGraphic", actionContext);
        	if(graphic != null) {
        		labeled.setGraphic(graphic);
        	}
        }
        if(thing.valueExists("graphicTextGap")){
            labeled.setGraphicTextGap(thing.getDouble("graphicTextGap"));
        }
        if(thing.valueExists("lineSpacing")){
            labeled.setLineSpacing(thing.getDouble("lineSpacing"));
        }
        if(thing.valueExists("mnemonicParsing")){
            labeled.setMnemonicParsing(thing.getBoolean("mnemonicParsing"));
        }
        if(thing.valueExists("textAlignment")){
            labeled.setTextAlignment(TextAlignment.valueOf(thing.getString("textAlignment")));
        }
        if(thing.valueExists("textFill")){
        	Paint textFill = thing.doAction("getTextFill", actionContext);
        	if(textFill != null) {
        		labeled.setTextFill(textFill);
        	}
        }
        if(thing.valueExists("textOverrun")){
            labeled.setTextOverrun(OverrunStyle.valueOf(thing.getString("textOverrun")));
        }
        if(thing.valueExists("text")){
            labeled.setText(thing.getString("text"));
        }
        if(thing.valueExists("underline")){
            labeled.setUnderline(thing.getBoolean("underline"));
        }
        if(thing.valueExists("wrapText")){
            labeled.setWrapText(thing.getBoolean("wrapText"));
        }
	}
}
