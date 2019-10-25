package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import ognl.OgnlException;
import xworker.libdgx.scenes.scene2d.utils.ArraySelectionActions;

public class ListActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getString("constructor");
		List list = null;
		if("style".equals(constructor)){
			List.ListStyle style = (List.ListStyle) actionContext.get(self.getString("style"));
			list = new List(style);			
		}else if("skin".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			list = new List(skin);
		}else if("skin_styleName".equals(constructor)){
			Skin skin = (Skin) actionContext.get(self.getString("skin"));
			String styleName = self.getString("styleName");
			list = new List(skin, styleName);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), list);
		init(self, list, actionContext);		
		
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public static void createSelection(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List parent = (List) actionContext.get("parent");
		
		ArraySelectionActions.init(self, parent.getSelection(), actionContext);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void init(Thing self, List item, ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
		
		Object items = self.doAction("getItems", actionContext);
		if(items instanceof Array){
			item.setItems((Array<Object>) items);
		}else if(items instanceof Object[]){
			item.setItems((Object[]) items);
		}
		
		if(self.getStringBlankAsNull("selectable") != null){
			//item.setSelectable(self.getBoolean("selectable"));
		}
		
		if(self.getStringBlankAsNull("selectedIndex") != null){
			item.setSelectedIndex(self.getInt("selectedIndex", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("selectedItem") != null){
			item.setSelected(self.getString("selectedItem", "", actionContext));
		}
	}
}
