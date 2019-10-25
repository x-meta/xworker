package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import ognl.OgnlException;
import xworker.libdgx.scenes.scene2d.GroupActions;

public class VerticalGroupActions {
	public static VerticalGroup create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
	
		VerticalGroup group = new VerticalGroup();
		
		init(self, group, actionContext);		
		
		actionContext.getScope(0).put(self.getMetadata().getName(), group);
		GroupActions.init(self, group, actionContext);
				
		return group;
	}
	

	public static void init(Thing self, VerticalGroup item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		String alignment = self.getStringBlankAsNull("alignment");
		if(alignment != null){
		//	item.setAlignment(ConstantsUtils.getAlign(alignment));
		}
		
		if(self.getStringBlankAsNull("reverse") != null){
		//	item.setReverse(self.getBoolean("reverse"));
		}
	}
}
