package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ognl.OgnlException;
import xworker.libdgx.ConstantsUtils;

public class LabelActions {
	public static Label create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getString("constructor");
		Label label = null;		
		String text = self.getString("text");
		
		if("text_style".equals(constructor)){
			Label.LabelStyle style = (Label.LabelStyle) actionContext.get(self.getString("style"));
			label = new Label(text, style);
		}else if("text_skin".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			label = new Label(text, skin);
		}else if("text_skin_styleName".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			String styleName = self.getString("styleName");
			label = new Label(text, skin, styleName);
		}else if("text_skin_fontName_color".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			String fontName = self.getString("fontName");
			Color color = (Color) actionContext.get(self.getString("color"));
			label = new Label(text, skin, fontName, color);
		}else if("text_skin_fontName_colorName".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			String fontName = self.getString("fontName");
			String colorName = self.getString("colorName");
			label = new Label(text, skin, fontName, colorName);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), label);
		init(self, label, actionContext);		
		
		return label;
	}
	
	public static void init(Thing self, Label item, ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("wrapAlign") != null){
			item.setAlignment(ConstantsUtils.getAlign(self.getString("wrapAlign", "", actionContext)));
		}
		
		if(self.getStringBlankAsNull("labelAlign") != null && self.getStringBlankAsNull("lineAlign") != null){
			item.setAlignment(ConstantsUtils.getAlign(self.getString("labelAlign", null, actionContext)),
					ConstantsUtils.getAlign(self.getString("lineAlign", null, actionContext)));
		}
		
		if(self.getStringBlankAsNull("fontScale") != null){
			item.setFontScale(self.getFloat("fontScale", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("fontScaleX") != null){
			item.setFontScaleX(self.getFloat("fontScaleX", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("fontScaleY") != null){
			item.setFontScaleX(self.getFloat("fontScaleY", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("style") != null){
			Label.LabelStyle style = (Label.LabelStyle) actionContext.get(self.getString("style", null, actionContext));
			item.setStyle(style);
		}
		
		if(self.getStringBlankAsNull("wrap") != null){
			item.setWrap(self.getBoolean("wrap", false, actionContext));
		}
	}
}
