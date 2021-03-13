package xworker.javafx.control;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import ognl.OgnlException;
import xworker.util.UtilData;

public class CustomMenuItemActions {
	public static void init(CustomMenuItem item, Thing thing, ActionContext actionContext) throws OgnlException, IOException {
		MenuItemActions.init(item, thing, actionContext);
		
		if(thing.valueExists("content")) {
			Node content = (Node) UtilData.getData(thing.getString("content"), actionContext);
			if(content != null) {
				item.setContent(content);
			}
		}
		
		if(thing.valueExists("hideOnClick")) {
			item.setHideOnClick(thing.getBoolean("hideOnClick"));
		}
	}
	
	public static CustomMenuItem create(ActionContext actionContext) throws OgnlException, IOException {
		Thing self = actionContext.getObject("self");
		
		CustomMenuItem item = new CustomMenuItem();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}

	public static void createContent(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		CustomMenuItem parent = actionContext.getObject("parent");

		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Node){
				parent.setContent((Node) obj);
			}
		}
	}
}
