package xworker.libdgx.scenes.scene2d.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import ognl.OgnlException;
import xworker.libdgx.utils.AlignActions;

public class TextFieldActions {
	private static Logger logger = LoggerFactory.getLogger(TextFieldActions.class);
	
	public static TextField create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getString("constructor");
		TextField text = null;
		String txt = self.getString("text");
		if(txt == null){
			txt = "";
		}
		if("text_skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self,  "skin", Skin.class, actionContext);
			text = new TextField(txt, skin);
		}else if("text_skin_styleName".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self,  "skin", Skin.class, actionContext);
			String styleName = self.getString("styleName");
			text = new TextField(txt, skin, styleName);
		}else if("text_style".equals(constructor)){
			TextField.TextFieldStyle style = UtilData.getObjectByType(self,  "style", TextFieldStyle.class, actionContext);
			text = new TextField(txt, style);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), text);
		init(self, text, actionContext);		
		
		return text;
	}
	
	public static void createTextFieldListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TextField parent = (TextField) actionContext.get("parent");
		
		ThingTextFieldListener l = new ThingTextFieldListener(self, actionContext);
		parent.setTextFieldListener(l);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
	}
	
	public static void createTextFieldFilter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TextField parent = (TextField) actionContext.get("parent");
		
		ThingTextFieldFilter l = new ThingTextFieldFilter(self, actionContext);
		parent.setTextFieldFilter(l);		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
	}
	
	public static void init(Thing self, TextField item, ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("alignment") != null){
			item.setAlignment(AlignActions.getAlign(self.getString("alignment")));
		}
		
		if(self.getStringBlankAsNull("blinkTime") != null){
			item.setBlinkTime(self.getFloat("blinkTime", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("cursorPosition") != null){
			item.setCursorPosition(self.getInt("cursorPosition", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("disabled") != null){
			item.setDisabled(self.getBoolean("disabled", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("focusTraversal") != null){
			item.setFocusTraversal(self.getBoolean("focusTraversal", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxLength") != null){
			item.setMaxLength(self.getInt("maxLength", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("messageText") != null){
			item.setMessageText(self.getString("messageText", null, actionContext));
		}
		
		if(self.getStringBlankAsNull("onlyFontChars") != null){
			item.setOnlyFontChars(self.getBoolean("onlyFontChars", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("passwordCharacter") != null){
			item.setPasswordCharacter(self.getString("passwordCharacter", "*", actionContext).charAt(0));
		}
		
		if(self.getStringBlankAsNull("passwordMode") != null){
			item.setPasswordMode(self.getBoolean("passwordMode", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("rightAligned") != null){
			//item.setsetRightAligned(self.getBoolean("rightAligned"));
		}
	}
	
	public static class ThingTextFieldListener implements TextFieldListener{
		Thing thing;
		ActionContext actionContext;
		
		public ThingTextFieldListener(Thing thing, ActionContext actionContext){
			this.thing = thing;
			this.actionContext = actionContext;
		}
		
		@Override
		public void keyTyped(TextField textField, char c) {
			try{
				thing.doAction("keyTyped", actionContext, UtilMap.toMap("textField", textField, "c", c));
			}catch(Exception e){
				logger.error("Handle keyTyped event eror, thing=" + thing.getMetadata().getPath(), e);
			}
		}
		
	}
	
	public static class ThingTextFieldFilter implements TextFieldFilter{
		Thing thing;
		ActionContext actionContext;
		
		public ThingTextFieldFilter(Thing thing, ActionContext actionContext){
			this.thing = thing;
			this.actionContext = actionContext;
		}
		
		@Override
		public boolean acceptChar(TextField textField, char c) {
			try{
				Object obj = thing.doAction("acceptChar", actionContext, UtilMap.toMap("textField", textField, "c", c));
				if(obj instanceof Boolean){
					return (Boolean) obj;
				}else{
					return true;
				}
			}catch(Exception e){
				logger.error("Handle acceptChar event eror, thing=" + thing.getMetadata().getPath(), e);
				return false;
			}
		}
		
	}
}
