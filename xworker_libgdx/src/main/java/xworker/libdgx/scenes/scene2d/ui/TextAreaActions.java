package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import ognl.OgnlException;

public class TextAreaActions {
	public static TextArea create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		TextArea item = null;
		String constructor  = self.getString("constructor");
		if("text_skin".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new TextArea(text, skin);
		}else if("text_skin_styleName".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			item = new TextArea(text, skin, styleName);
		}else if("text_style".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			TextFieldStyle style = UtilData.getObjectByType(self, "style", TextFieldStyle.class, actionContext);
			item = new TextArea(text, style);
		}else{
			throw new ActionException("No matched constructors, thing=" + self.getMetadata().getPath());
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
		
	}
	
	public static void init(Thing self, TextArea item, ActionContext actionContext) throws OgnlException{
		TextFieldActions.init(self, item, actionContext);
	}
}
