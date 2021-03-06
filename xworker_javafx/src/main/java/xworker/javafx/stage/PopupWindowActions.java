package xworker.javafx.stage;

import javafx.stage.PopupWindow;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PopupWindowActions {
    public static void init(PopupWindow node, Thing thing, ActionContext actionContext){
        WindowActions.init(thing, node, actionContext);

        if(thing.valueExists("anchorLocation")){
            node.setAnchorLocation(PopupWindow.AnchorLocation.valueOf(thing.getString("anchorLocation")));
        }
        if(thing.valueExists("autoFix")){
            node.setAutoFix(thing.getBoolean("autoFix"));
        }
        if(thing.valueExists("autoHide")){
            node.setAutoHide(thing.getBoolean("autoHide"));
        }
        if(thing.valueExists("consumeAutoHidingEvents")){
            node.setConsumeAutoHidingEvents(thing.getBoolean("consumeAutoHidingEvents"));
        }
        if(thing.valueExists("hideOnEscape")){
            node.setHideOnEscape(thing.getBoolean("hideOnEscape"));
        }
    }
}
