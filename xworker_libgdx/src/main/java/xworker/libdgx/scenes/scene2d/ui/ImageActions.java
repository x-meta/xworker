package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

import ognl.OgnlException;
import xworker.libdgx.ConstantsUtils;

public class ImageActions {
	public static Image create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getStringBlankAsNull("constructor");
		boolean drawableSeted = false;
		boolean scalingSeted = false;
		boolean alignSeted = false;
		
		Image image = null;
		if("drawable".equals(constructor)){
			Drawable drawable = StyleActions.getDrawable(self, "drawable", actionContext);
			drawableSeted = true;
			image = new Image(drawable);
		}else if("drawable_scaling".equals(constructor)){
			Drawable drawable = StyleActions.getDrawable(self, "drawable", actionContext);
			drawableSeted = true;
			Scaling scaling = ConstantsUtils.getScaling(self.getString("scaling"));
			scalingSeted = true;
			image = new Image(drawable, scaling);
		}else if("drawable_scaling_align".equals(constructor)){
			Drawable drawable = StyleActions.getDrawable(self, "drawable", actionContext);
			drawableSeted = true;
			Scaling scaling = ConstantsUtils.getScaling(self.getString("scaling"));
			scalingSeted = true;
			int align = ConstantsUtils.getAlign(self.getString("align"));
			alignSeted = true;
			image = new Image(drawable, scaling, align);
		}else if("ninePatch".equals(constructor)){
			NinePatch ninePatch = (NinePatch) actionContext.get(self.getString("ninePatch"));
			image = new Image(ninePatch);
		}else if("skin_drawableName".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			String drawableName = self.getString("drawableName");
			image = new Image(skin, drawableName);
		}else if("texture".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			image = new Image(texture);
		}else if("textureRegion".equals(constructor)){
			TextureRegion textureRegion = (TextureRegion) actionContext.get(self.getString("textureRegion"));
			image = new Image(textureRegion);
		}else{
			image = new Image();
		}

		if(!scalingSeted && self.getStringBlankAsNull("scaling") != null){
			Scaling scaling = ConstantsUtils.getScaling(self.getString("scaling"));
			image.setScaling(scaling);
		}
		
		if(!alignSeted && self.getStringBlankAsNull("align") != null){
			image.setAlign(ConstantsUtils.getAlign(self.getString("align")));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), image);
		init(self, image, actionContext);			
		
		return image;
	}
	
	public static void init(Thing self, Image item, ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
	}
}
