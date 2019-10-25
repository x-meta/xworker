package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import ognl.OgnlException;

public class StackActions {
	public static Stack create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Stack item = new Stack();
		
		actionContext.put(self.getMetadata().getName(), item);
		WidgetGroupActions.init(self, item, actionContext);		
		
		return item;
	}
}
