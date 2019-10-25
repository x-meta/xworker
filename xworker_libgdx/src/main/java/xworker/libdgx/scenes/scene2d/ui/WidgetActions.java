package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import ognl.OgnlException;
import xworker.libdgx.scenes.scene2d.ActorActions;

public class WidgetActions {
	public static Widget create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Widget widget = new Widget();

		actionContext.getScope(0).put(self.getMetadata().getName(), widget);
		init(self, widget, actionContext);
		
		return widget;
	}
	
	public static void init(Thing self, Widget item, ActionContext actionContext) throws OgnlException{
		ActorActions.initActor(self, item, actionContext);
		
		if(self.getStringBlankAsNull("fillParent") != null){
			item.setFillParent(self.getBoolean("fillParent", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("layoutEnabled") != null){
			item.setLayoutEnabled(self.getBoolean("layoutEnabled", false, actionContext));
		}		
	}
}
