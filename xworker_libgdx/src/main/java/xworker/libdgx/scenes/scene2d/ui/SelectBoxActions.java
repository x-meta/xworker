package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import ognl.OgnlException;

public class SelectBoxActions {
	public static SelectBox<Object> create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		SelectBox<Object>  item = null;
		String constructor = self.getStringBlankAsNull("constructor");
		if("style".equals(constructor)){
			SelectBoxStyle style = UtilData.getObjectByType(self, "style", SelectBoxStyle.class, actionContext);
			item = new SelectBox<Object>(style);
		}else if("skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new SelectBox<Object>(skin);
		}else if("skin_styleName".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = UtilString.getString(self, "styleName", actionContext);
			item = new SelectBox<Object>(skin, styleName);
		}else{
			throw new ActionException("No matched constructors, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);		
		return item;		
	}
	
	@SuppressWarnings("unchecked")
	public static void init(Thing self, SelectBox<Object> item , ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
		
		Object items = self.doAction("getItems", actionContext);
		if(items instanceof Array){
			item.setItems((Array<Object>) items);
		}else if(items instanceof Object[]){
			item.setItems((Object[]) items);
		}
		
		if(self.getStringBlankAsNull("selectItem") != null){
			Object selectItem = UtilData.getObject(self, "selectItem", actionContext);
			if(selectItem != null){
				item.setSelected(selectItem);
			}else{
				item.setSelected(self.getString("selectItem"));
			}
		}
		
		if(self.getStringBlankAsNull("selectIndex") != null){
			item.setSelectedIndex(self.getInt("selectIndex", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxListCount") != null){
			item.setMaxListCount(self.getInt("maxListCount", 0, actionContext));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createItems(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		SelectBox<Object> parent = (SelectBox<Object>) actionContext.get("parent");
		
		Array<Object> items = new Array<Object>();
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj != null){
				items.add(obj);
			}
		}
		
		parent.setItems(items);
	}
	
	public static Object getItems(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		return UtilData.getObjectByType(self, "items", Object.class, actionContext);
	}
}
