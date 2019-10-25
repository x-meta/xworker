package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class StyleActions {
	public static WindowStyle createWindowStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		BitmapFont titleFont = getBitmapFont(self, "titleFont", actionContext);
		Color titleFontColor = getColor(self, "titleFontColor", actionContext);
		Drawable background = getDrawable(self, "background", actionContext);
		
		WindowStyle style = new WindowStyle( titleFont, titleFontColor, background);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static TreeStyle createTreeStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable plus = getDrawable(self, "plus", actionContext);
		Drawable minus = getDrawable(self, "minus", actionContext);
		Drawable selection = getDrawable(self, "selection", actionContext);
		
		TreeStyle style = new TreeStyle( plus, minus, selection);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static TouchpadStyle createTouchpadStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable background = getDrawable(self, "background", actionContext);
		Drawable knob = getDrawable(self, "knob", actionContext);
		
		TouchpadStyle style = new TouchpadStyle( background, knob);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static TextFieldStyle createTextFieldStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable upcursor = getDrawable(self, "cursor", actionContext);
		Drawable selection = getDrawable(self, "selection", actionContext);
		Drawable background = getDrawable(self, "background", actionContext);
		BitmapFont font = getBitmapFont(self, "font", actionContext);
		Color fontColor = getColor(self, "fontColor", actionContext);
		
		TextFieldStyle style = new TextFieldStyle(font, fontColor, upcursor, selection, background);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static TextButtonStyle createTextButtonStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable up = getDrawable(self, "up", actionContext);
		Drawable down = getDrawable(self, "down", actionContext);
		Drawable checked = getDrawable(self, "checked", actionContext);
		BitmapFont font = getBitmapFont(self, "font", actionContext);
		
		TextButtonStyle style = new TextButtonStyle( up, down, checked, font);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static SplitPaneStyle createSplitPaneStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable handle = getDrawable(self, "handle", actionContext);
			
		SplitPaneStyle style = new SplitPaneStyle( handle);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static SliderStyle createSliderStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable background = getDrawable(self, "background", actionContext);
		Drawable knob = getDrawable(self, "knob", actionContext);
			
		SliderStyle style = new SliderStyle( background,  knob);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static SelectBoxStyle createSelectBoxStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Color fontColor = getColor(self, "fontColor", actionContext);
		BitmapFont font = getBitmapFont(self, "font", actionContext);
		Drawable background = getDrawable(self, "background", actionContext);
		ScrollPaneStyle scrollStyle = UtilData.getObjectByType(self, "scrollStyle", ScrollPaneStyle.class, actionContext);
		ListStyle sslistStyle = UtilData.getObjectByType(self,  "listStyle", ListStyle.class, actionContext);
			
		SelectBoxStyle style = new SelectBoxStyle(font, fontColor, background, scrollStyle, sslistStyle);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static ScrollPaneStyle createScrollPaneStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable background = getDrawable(self, "background", actionContext);
		Drawable hScroll = getDrawable(self, "hScroll", actionContext);
		Drawable hScrollKnob = getDrawable(self, "hScrollKnob", actionContext);
		Drawable vScroll = getDrawable(self, "vScroll", actionContext);
		Drawable vScrollKnob = getDrawable(self, "vScrollKnob", actionContext);
			
		ScrollPaneStyle style = new ScrollPaneStyle(background, hScroll, hScrollKnob, vScroll, vScrollKnob);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static ProgressBarStyle createProgressBarStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable background = getDrawable(self, "background", actionContext);
		Drawable knob = getDrawable(self, "knob", actionContext);
			
		ProgressBarStyle style = new ProgressBarStyle(background, knob);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static LabelStyle createLabelStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Color fontColor = getColor(self, "fontColor", actionContext);
		BitmapFont font = getBitmapFont(self, "font", actionContext);
			
		LabelStyle style = new LabelStyle(font, fontColor);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static ImageTextButtonStyle createImageTextButtonStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable up = getDrawable(self, "up", actionContext);
		Drawable down = getDrawable(self, "down", actionContext);
		Drawable checked = getDrawable(self, "checked", actionContext);
		BitmapFont font = getBitmapFont(self, "font", actionContext);
			
		ImageTextButtonStyle style = new ImageTextButtonStyle(up, down, checked, font);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static ImageButtonStyle createImageButtonStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getStringBlankAsNull("constructor");
		ImageButtonStyle style = null;
		if("default".equals(constructor)){
			style = new ImageButtonStyle();
		}else if("buttonStyle".equals(constructor)){
			ButtonStyle s = UtilData.getObjectByType(self, "buttonStyle", ButtonStyle .class, actionContext);
			style = new ImageButtonStyle(s);
		}else if("imageButtonStyle".equals(constructor)){
			ImageButtonStyle s = UtilData.getObjectByType(self, "imageButtonStyle", ImageButtonStyle.class, actionContext);
			style = new ImageButtonStyle(s);
		}else if("fromSkin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String skinStyleName = self.getStringBlankAsNull("skinStyleName");
			if(skinStyleName != null){
				style = skin.get(skinStyleName, ImageButtonStyle.class);
			}else{
				style = skin.get(ImageButtonStyle.class);
			}
		}else{
			throw new ConstructException(self);
		}
		
		Drawable up = getDrawable(self, "up", actionContext);
		Drawable down = getDrawable(self, "down", actionContext);
		Drawable checked = getDrawable(self, "checked", actionContext);
		Drawable imageUp = getDrawable(self, "imageUp", actionContext);
		Drawable imageDown = getDrawable(self, "imageDown", actionContext);
		Drawable imageChecked = getDrawable(self, "imageChecked", actionContext);
		if(up != null){
			style.up = up;
		}
		if(down != null){
			style.down = down;
		}
		if(checked != null){
			style.checked = checked;
		}
		if(imageUp != null){
			style.imageUp = imageUp;
		}
		if(imageDown != null){
			style.imageDown = imageDown;
		}
		if(imageChecked != null){
			style.imageChecked = imageChecked;
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static CheckBoxStyle createCheckBoxStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Drawable checkboxOff = getDrawable(self, "checkboxOff", actionContext);
		Drawable checkboxOn = getDrawable(self, "checkboxOn", actionContext);
		BitmapFont font = getBitmapFont(self, "font", actionContext);
		Color fontColor = getColor(self, "fontColor", actionContext);
			
		CheckBoxStyle style = new CheckBoxStyle(checkboxOff, checkboxOn, font, fontColor);
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
	public static ButtonStyle createButtonStyle(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");

		String constructor = self.getStringBlankAsNull("constructor");
		ButtonStyle style = null;
		if("default".equals(constructor)){
			style = new ButtonStyle();
		}else if("buttonStyle".equals(constructor)){
			ButtonStyle s = UtilData.getObjectByType(self, "buttonStyle", ButtonStyle .class, actionContext);
			style = new ButtonStyle(s);
		}else if("fromSkin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String skinStyleName = self.getStringBlankAsNull("skinStyleName");
			if(skinStyleName != null){
				style = skin.get(skinStyleName, ButtonStyle.class);
			}else{
				style = skin.get(ButtonStyle.class);
			}
		}else{
			throw new ConstructException(self);
		}
		Drawable up = getDrawable(self, "up", actionContext);		
		Drawable down = getDrawable(self, "down", actionContext);		
		Drawable checked = getDrawable(self, "checked", actionContext);
		if(up != null){
			style.up = up;
		}
		if(down != null){
			style.down = down;
		}
		if(checked != null){
			style.checked = checked;
		}
		actionContext.getScope(0).put(self.getMetadata().getName(), style);
		return style;
	}
	
    public static Drawable getDrawable(Thing thing, String name, ActionContext actionContext) throws OgnlException{
    	Object obj = UtilData.getObject(thing, name, actionContext);
    	if(obj instanceof Drawable){
    		return (Drawable) obj;
    	}else if(obj instanceof Texture){
    		TextureRegion region = new TextureRegion((Texture) obj);
    		return new TextureRegionDrawable(region);
    	}else if(obj instanceof TextureRegion){
    		return new TextureRegionDrawable((TextureRegion) obj);
    	}else if(obj instanceof NinePatch){
    		return new NinePatchDrawable((NinePatch) obj);
    	}else if(obj instanceof Sprite){
    		return new SpriteDrawable((Sprite) obj);
    	}else{
    		return null;
    	}
    }
    
    public static BitmapFont getBitmapFont(Thing thing, String name, ActionContext actionContext) throws OgnlException{
    	Object obj = UtilData.getObject(thing, name, actionContext);
    	if(obj instanceof BitmapFont){
    		return (BitmapFont) obj;
    	}else{
    		return null;
    	}
    }
    
    public static Color getColor(Thing thing, String name, ActionContext actionContext) throws OgnlException{
    	Object obj = UtilData.getObject(thing, name, actionContext);
    	if(obj instanceof Color){
    		return (Color) obj;
    	}else{
    		return null;
    	}
    }
}
