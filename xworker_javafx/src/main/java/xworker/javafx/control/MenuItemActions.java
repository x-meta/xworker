package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import xworker.javafx.util.JavaFXUtils;

public class MenuItemActions {
	public static void init(MenuItem item, Thing thing, ActionContext actionContext) {
        if(thing.valueExists("accelerator")){
            item.setAccelerator(JavaFXUtils.getKeyCombination(thing.getString("accelerator")));
        }
        if(thing.valueExists("disable")){
            item.setDisable(thing.getBoolean("disable"));
        }
        if(thing.valueExists("graphic")){
        	Node graphic = JavaFXUtils.getGraphic(thing, "graphic", actionContext);
        	if(graphic != null) {
        		item.setGraphic(graphic);
        	}
        }
        if(thing.valueExists("id")){
            String id = JavaFXUtils.getString(thing, "id", actionContext);
            if(id != null) {
                item.setId(thing.getString("id"));
            }
        }
        if(thing.valueExists("mnemonicParsing")){
            item.setMnemonicParsing(thing.getBoolean("mnemonicParsing"));
        }
        if(thing.valueExists("style")){
            String style = JavaFXUtils.getString(thing, "style", actionContext);
            if(style != null) {
                item.setStyle(style);
            }
        }
        if(thing.valueExists("text")){
            String text = JavaFXUtils.getString(thing, "text", actionContext);
            if(text != null) {
                item.setText(text);
            }
        }
        if(thing.valueExists("visible")){
            item.setVisible(thing.getBoolean("visible"));
        }
	}
	
	public static MenuItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		MenuItem item = new MenuItem();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}

    public static void createGraphic(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node && parent instanceof MenuItem){
                ((MenuItem) parent).setGraphic((Node) obj);
            }
        }
    }
}
