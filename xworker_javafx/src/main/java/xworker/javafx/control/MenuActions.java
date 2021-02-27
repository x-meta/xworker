package xworker.javafx.control;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import ognl.OgnlException;

public class MenuActions {
	public static void init(Menu menu, Thing thing, ActionContext actionContext) throws OgnlException, IOException {
		MenuItemActions.init(menu, thing, actionContext);		
	}
	
	public static Menu create(ActionContext actionContext) throws OgnlException, IOException {
		Thing self = actionContext.getObject("self");
		
		Menu menu = new Menu();
		init(menu, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), menu);
		
		actionContext.peek().put("parent", menu);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof MenuItem) {
				menu.getItems().add((MenuItem) obj);
			}
		}
		
		return menu;
	}
}
