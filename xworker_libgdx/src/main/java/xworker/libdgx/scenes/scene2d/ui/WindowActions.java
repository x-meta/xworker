package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

import ognl.OgnlException;

public class WindowActions {
	public static Window create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Window item = null;
		String constructor = self.getStringBlankAsNull("constructor");
		if("title_skin".equals(constructor)){
			String title = UtilString.getString(self, "title", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);			
			item = new Window(title, skin);
		}else if("title_skin_styleName".equals(constructor)){
			String title = UtilString.getString(self, "title", actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);			
			String styleName = UtilString.getString(self,"styleName", actionContext);
			item = new Window(title, skin, styleName);
		}else if("title_style".equals(constructor)){
			String title = UtilString.getString(self, "title", actionContext);
			WindowStyle style = UtilData.getObjectByType(self, "style", WindowStyle.class, actionContext);			
			item = new Window(title, style);
		}
				
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
				
		return item;
	}
	
	public static void createTitleTable(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Window parent = (Window) actionContext.get("parent");
		
		Table table = parent.getTitleTable();
		TableActions.init(self, table, actionContext);
	}
	
	public static void init(Thing self, Window item, ActionContext actionContext) throws OgnlException{
		TableActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("isModal") != null){
			item.setModal(self.getBoolean("isModal", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("isMovable") != null){
			item.setMovable(self.getBoolean("isMovable", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("isResizable") != null){
			item.setResizable(self.getBoolean("isResizable", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("resizeBorder") != null){
			item.setResizeBorder(self.getInt("resizeBorder", 0, actionContext));
		}
	}
}
