package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ognl.OgnlException;

public class ImageTextButtonActions {
	public static ImageTextButton create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ImageTextButton item = null;
		String constructor = self.getStringBlankAsNull("constructor");
		if("text_style_imageLeft".equals(constructor)){
			String text = UtilString.getString(self, "self", actionContext);
			ImageTextButtonStyle style = UtilData.getObjectByType(self, "style", ImageTextButtonStyle.class, actionContext);
			//boolean imageLeft = self.getBoolean("imageLeft");
			item = new ImageTextButton(text, style);
		}else if("text_skin".equals(constructor)){
			String text = UtilString.getString(self, "self", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ImageTextButton(text, skin);
		}else if("text_skin_styleName".equals(constructor)){
			String text = UtilString.getString(self, "self", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			item = new ImageTextButton(text, skin, styleName);
		}else if("text_skin_styleName_imageLeft".equals(constructor)){
			String text = UtilString.getString(self, "self", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			//boolean imageLeft = self.getBoolean("imageLeft");
			item = new ImageTextButton(text, skin, styleName);
		}else{
			throw new ActionException("No matched constructor, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);		
		
		return item;
	}
	
	public static void init(Thing self, ImageTextButton item, ActionContext actionContext) throws OgnlException{
		ButtonActions.init(self, item, actionContext);
	}
}
