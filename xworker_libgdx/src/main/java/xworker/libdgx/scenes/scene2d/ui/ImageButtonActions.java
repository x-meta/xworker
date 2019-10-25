package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import ognl.OgnlException;

public class ImageButtonActions {
	public static ImageButton create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getStringBlankAsNull("constructor");
		ImageButton item = null;
		if("imageUp".equals(constructor)){
			Drawable imageUp = StyleActions.getDrawable(self, "imageUp", actionContext);
			item = new ImageButton(imageUp);
		}else if("imageUp_imageDown".equals(constructor)){
			Drawable imageUp = StyleActions.getDrawable(self, "imageUp", actionContext);
			Drawable imageDown = StyleActions.getDrawable(self, "imageDown", actionContext);
			item = new ImageButton(imageUp, imageDown);
		}else if("imageUp_imageDown_imageChecked".equals(constructor)){
			Drawable imageUp = StyleActions.getDrawable(self, "imageUp", actionContext);
			Drawable imageDown = StyleActions.getDrawable(self, "imageDown", actionContext);
			Drawable imageChecked = StyleActions.getDrawable(self, "imageChecked", actionContext);
			item = new ImageButton(imageUp, imageDown, imageChecked);
		}else if("style".equals(constructor)){
			ImageButtonStyle style = UtilData.getObjectByType(self, "style", ImageButtonStyle.class, actionContext);
			item = new ImageButton(style);
		}else if("skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ImageButton(skin);
		}else if("skin_styleName".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			item = new ImageButton(skin, styleName);
		}else{
			throw new ActionException("no matched constructor, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
				
		return item;
		
	}
	
	public static void init(Thing self, ImageButton item, ActionContext actionContext) throws OgnlException{
		ButtonActions.init(self, item, actionContext);		
	}
}
