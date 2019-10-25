package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import ognl.OgnlException;

public class TextButtonActions {
	public static TextButton create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getStringBlankAsNull("constructor");
		TextButton item = null;
		if("text_skin".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			
			item = new TextButton(text, skin);
		}else if("text_skin_styleName".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			item = new TextButton(text, skin, styleName);
		}else if("text_style".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			TextButtonStyle style = UtilData.getObjectByType(self, "style", TextButtonStyle.class, actionContext);
			item = new TextButton(text, style);
		}else{
			throw new ActionException("Must select a constructor, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
	}
	
	public static void init(Thing self, TextButton item, ActionContext actionContext) throws OgnlException{
		ButtonActions.init(self, item, actionContext);
	}
}
