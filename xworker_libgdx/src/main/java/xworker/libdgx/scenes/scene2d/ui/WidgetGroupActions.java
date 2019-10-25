package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import ognl.OgnlException;
import xworker.libdgx.scenes.scene2d.GroupActions;

public class WidgetGroupActions {
	public static WidgetGroup create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
	
		WidgetGroup group = new WidgetGroup();
		
		actionContext.getScope(0).put(self.getMetadata().getName(), group);
		
		init(self, group, actionContext);		
		GroupActions.initChilds(self, group, actionContext);
				
		
		return group;
	}
	
	public static void init(Thing self, WidgetGroup group, ActionContext actionContext) throws OgnlException{
		GroupActions.init(self, group, actionContext);
		
		if(self.getStringBlankAsNull("fillParent") != null){
			group.setFillParent(self.getBoolean("fillParent", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("layoutEnabled") != null){
			group.setLayoutEnabled(self.getBoolean("layoutEnabled", false, actionContext));
		}
	}
}
