package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class DialogActions {
	private static final String TAG = DialogActions.class.getName();
	
	public static Dialog create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getStringBlankAsNull("constructor");
		ThingDialog item = null;
		
		if("title_skin".equals(constructor)){
			String title = UtilString.getString(self, "title", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ThingDialog(title, skin);			
		}else if("title_skin_windowStyleName".equals(constructor)){
			String title = UtilString.getString(self, "title", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String windowStyleName = UtilString.getString(self, "windowStyleName", actionContext);
			item = new ThingDialog(title, skin, windowStyleName);
		}else if("title_windowStyle".equals(constructor)){
			String title = UtilString.getString(self, "title", actionContext);
			WindowStyle windowStyle = UtilData.getObjectByType(self, "windowStyle", WindowStyle.class, actionContext);
			item = new ThingDialog(title, windowStyle);		
		}else{
			throw new ActionException("Must have as constructor, thing=" + self.getMetadata().getPath());
		}
		
		item.thing = self;
		item.actionContext = actionContext;
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
				
		return item;
	}
	
	public static void createButtons(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createTextButtonWithObject(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Dialog parent = (Dialog) actionContext.get("parent");
		
		String text = UtilString.getString(self, "text", actionContext);
		Object object = UtilData.getData(self, "object", actionContext);
		TextButtonStyle buttonStyle = UtilData.getObjectByType(self, "buttonStyle", TextButtonStyle.class, actionContext);
		
		if(buttonStyle != null){
			parent.button(text, object, buttonStyle);
		}else{
			parent.button(text, object);
		}
	}
	
	public static void createKeys(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createKey(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Dialog parent = (Dialog) actionContext.get("parent");
		Object object = UtilData.getData(self, "object", actionContext);
		int key = Keys.valueOf(self.getString("key"));
		parent.key(key, object);
		
	}
	
	public static void createButtonWithObject(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Dialog parent = (Dialog) actionContext.get("parent");
		
		Object object = UtilData.getData(self, "object", actionContext);
		Button button = UtilData.getObjectByType(self, "button", Button.class, actionContext);
		
		if(button != null){
			parent.button(button, object);
		}
	}
	
	public static void init(Thing self, Dialog item, ActionContext actionContext) throws OgnlException{
		WindowActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("text") != null){
			item.text(self.getString("text"));
		}
	}
	
	public static class ThingDialog extends Dialog{
		Thing thing;
		ActionContext actionContext;
		
		public ThingDialog(String title, Skin skin) {
			super(title, skin);
		}

		public ThingDialog(String title, Skin skin, String windowStyleName) {
			super(title, skin, windowStyleName);
		}

		public ThingDialog(String title, WindowStyle windowStyle) {
			super(title, windowStyle);
		}

		@Override
		protected void result(Object object) {
			try{
				thing.doAction("result", actionContext, UtilMap.toMap("object", object));
			}catch(Exception e){
				Executor.error(TAG, "Dialog handler result error, thing=" + thing.getMetadata().getPath(), e);
			}
		}
		
	}
}
