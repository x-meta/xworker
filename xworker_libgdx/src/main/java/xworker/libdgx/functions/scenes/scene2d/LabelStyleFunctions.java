package xworker.libdgx.functions.scenes.scene2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class LabelStyleFunctions {
	public static Object createLabelStyle(ActionContext actionContext){
		return new LabelStyle();
	}
	
	public static Object createLabelStyle_font_fontColor(ActionContext actionContext){
		BitmapFont font = (BitmapFont) actionContext.get("font");
		Color fontColor = (Color) actionContext.get("fontColor");
		return new LabelStyle(font, fontColor);
	}
	
	public static Object createLabelStyle_style(ActionContext actionContext){
		LabelStyle style = (LabelStyle) actionContext.get("style");
		return new LabelStyle(style);
	}
}
