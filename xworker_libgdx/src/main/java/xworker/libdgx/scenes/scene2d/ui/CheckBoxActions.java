package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ognl.OgnlException;

public class CheckBoxActions {
	public static CheckBox create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getStringBlankAsNull("constructor");
		CheckBox item = null;
		if("text_skin".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			
			item = new CheckBox(text, skin);
		}else if("text_skin_styleName".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			item = new CheckBox(text, skin, styleName);
		}else if("text_style_checkBoxLeft".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			CheckBoxStyle style = UtilData.getObjectByType(self, "style", CheckBoxStyle.class, actionContext);
			//boolean checkBoxLeft = self.getBoolean("checkBoxLeft");			
			item = new CheckBox(text, style);
		}else if("text_skin_styleName_checkBoxLeft".equals(constructor)){
			String text = UtilString.getString(self, "text", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			//boolean checkBoxLeft = self.getBoolean("checkBoxLeft");	
			item = new CheckBox(text, skin, styleName);
		}else{
			throw new ActionException("Must select a constructor, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
	}
	
	public static void init(Thing self, CheckBox item, ActionContext actionContext) throws OgnlException{
		TextButtonActions.init(self, item, actionContext);
	}
}
