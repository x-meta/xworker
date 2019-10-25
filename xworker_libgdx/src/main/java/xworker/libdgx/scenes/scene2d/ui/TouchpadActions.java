package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import ognl.OgnlException;

public class TouchpadActions {
	public static Touchpad create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Touchpad item = null;
		String constructor = self.getString("constructor");
		float deadzoneRadius = self.getFloat("deadzoneRadius");
		
		if("deadzoneRadius_skin".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			item = new Touchpad(deadzoneRadius, skin);
		}else if("deadzoneRadius_skin_styleName".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			String styleName = self.getString("styleName");
			item = new Touchpad(deadzoneRadius, skin, styleName);
		}else if("deadzoneRadius_style".equals(constructor)){
			Touchpad.TouchpadStyle style = (Touchpad.TouchpadStyle) actionContext.get(self.getString("style"));
			item = new Touchpad(deadzoneRadius, style);
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
	}
	
	public static void init(Thing self, Touchpad item, ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
	}
}
