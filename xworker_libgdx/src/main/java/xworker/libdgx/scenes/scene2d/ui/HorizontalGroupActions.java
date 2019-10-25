package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

import ognl.OgnlException;
import xworker.libdgx.utils.AlignActions;

public class HorizontalGroupActions {
	public static HorizontalGroup create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		HorizontalGroup item = new HorizontalGroup();
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
	}
	
	public static void init(Thing self, HorizontalGroup item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("align") != null){
			item.align(AlignActions.getAlign(self.getString("align", null, actionContext)));
		}
		
		if(self.getStringBlankAsNull("fill") != null){
			item.fill(self.getFloat("fill", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padBottom") != null){
			item.padBottom(self.getFloat("padBottom", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padLeft") != null){
			item.padLeft(self.getFloat("padLeft", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padRight") != null){
			item.padRight(self.getFloat("padRight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padBottom") != null){
			item.padBottom(self.getFloat("padBottom", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padTop") != null){
			item.padTop(self.getFloat("padTop", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("reverse") != null){
			item.reverse(self.getBoolean("reverse", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("round") != null){
			item.setRound(self.getBoolean("round", false, actionContext));
		}
		if(self.getStringBlankAsNull("spacing") != null){
			item.space(self.getFloat("spacing", 0, actionContext));
		}
	}
}
