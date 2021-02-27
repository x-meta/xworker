package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
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
        	Node graphic = (Node) UtilData.getData(thing.getString("graphic"), actionContext);
        	if(graphic != null) {
        		item.setGraphic(graphic);
        	}
        }
        if(thing.valueExists("id")){
            item.setId(thing.getString("id"));
        }
        if(thing.valueExists("mnemonicParsing")){
            item.setMnemonicParsing(thing.getBoolean("mnemonicParsing"));
        }
        if(thing.valueExists("style")){
            item.setStyle(thing.getString("style"));
        }
        if(thing.valueExists("text")){
            item.setText(thing.getString("text"));
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
}
