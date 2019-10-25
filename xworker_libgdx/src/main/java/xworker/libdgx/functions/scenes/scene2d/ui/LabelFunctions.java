package xworker.libdgx.functions.scenes.scene2d.ui;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelFunctions {
	public static Object createLabel_text_style(ActionContext actionContext){
		CharSequence text = (CharSequence) actionContext.get("text");
		Label.LabelStyle  style = (Label.LabelStyle) actionContext.get("style");
		return new Label(text, style);
	}
	
	public static Object createLabel_text_skin(ActionContext actionContext){
		CharSequence text = (CharSequence) actionContext.get("text");
		Skin skin = (Skin) actionContext.get("skin");
		return new Label(text, skin);
	}
	
	public static Object createLabel_text_skin_styleName(ActionContext actionContext){
		CharSequence text = (CharSequence) actionContext.get("text");
		Skin skin = (Skin) actionContext.get("skin");
		String styleName = (String) actionContext.get("styleName");
		return new Label(text, skin, styleName);
	}
	
	public static Object createLabel_text_skin_fontName_color(ActionContext actionContext){
		CharSequence text = (CharSequence) actionContext.get("text");
		Skin skin = (Skin) actionContext.get("skin");
		String fontName = (String) actionContext.get("fontName");
		Color color = (Color) actionContext.get("color");
		return new Label(text, skin, fontName, color);
	}
	
	public static Object createLabel_text_skin_fontName_colorName(ActionContext actionContext){
		CharSequence text = (CharSequence) actionContext.get("text");
		Skin skin = (Skin) actionContext.get("skin");
		String fontName = (String) actionContext.get("fontName");
		String colorName = (String) actionContext.get("colorName");
		return new Label(text, skin, fontName, colorName);
	}
}
