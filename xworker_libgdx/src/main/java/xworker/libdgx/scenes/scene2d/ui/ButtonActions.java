package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import ognl.OgnlException;

public class ButtonActions {
	public static Actor getChildActor(Thing self, ActionContext actionContext) throws OgnlException{
		if(self.getStringBlankAsNull("child") != null){
			return UtilData.getObjectByType(self, "child", Actor.class, actionContext);
		}else{
			Thing child = self.getThing("ChildActor@0");
			if(child != null && child.getChilds().size() > 0){
				return (Actor) child.getChilds().get(0).doAction("create", actionContext);
			}
			
			return null;
		}
	}
	
	public static Button create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getString("constructor");
		Button button = null;
		if("child_style".equals(constructor)){
			Actor child = getChildActor(self, actionContext);
			ButtonStyle style = UtilData.getObjectByType(self, "style", ButtonStyle.class, actionContext);
			button = new Button(child, style);
		}else if("child_skin".equals(constructor)){
			Actor child = getChildActor(self, actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			button = new Button(child, skin);
		}else if("child_skin_styleName".equals(constructor)){
			Actor child = getChildActor(self, actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			button = new Button(child, skin, styleName);
		}else if("style".equals(constructor)){
			ButtonStyle style = UtilData.getObjectByType(self, "style", ButtonStyle.class, actionContext);
			button = new Button(style);
		}else if("up".equals(constructor)){
			Drawable up = StyleActions.getDrawable(self, "up", actionContext);
			button = new Button(up);
		}else if("up_down".equals(constructor)){
			Drawable up = StyleActions.getDrawable(self, "up", actionContext);
			Drawable down = StyleActions.getDrawable(self, "down", actionContext);
			button = new Button(up, down);
		}else if("up_down_checked".equals(constructor)){
			Drawable up = StyleActions.getDrawable(self, "up", actionContext);
			Drawable down = StyleActions.getDrawable(self, "down", actionContext);
			Drawable checked = StyleActions.getDrawable(self, "checked", actionContext);
			button = new Button(up, down, checked);
		}else if("skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			button = new Button(skin);
		}else if("skin_styleName".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			button = new Button(skin, styleName);
		}else{
			button = new Button();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), button);
		init(self, button, actionContext);		
		
		return button;
	}
	
	public static void init(Thing self, Button item,  ActionContext actionContext) throws OgnlException{
		TableActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("isChecked") != null){
			item.setChecked(self.getBoolean("isChecked", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("isDisabled") != null){
			item.setDisabled(self.getBoolean("isDisabled", false, actionContext));
		}
	}
}

