package xworker.libdgx.scenes.scene2d.utils;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.utils.Selection;

public class SelectionActions {
	@SuppressWarnings("rawtypes")
	public static void init(Thing self, Selection item, ActionContext actionContext){
		if(self.getStringBlankAsNull("isDisabled") != null){
			item.setDisabled(self.getBoolean("isDisabled"));
		}
		
		if(self.getStringBlankAsNull("multiple") != null){
			item.setMultiple(self.getBoolean("multiple"));
		}
		
		
		if(self.getStringBlankAsNull("required") != null){
			item.setRequired(self.getBoolean("required"));
		}
		
		if(self.getStringBlankAsNull("toggle") != null){
			item.setToggle(self.getBoolean("toggle"));
		}

	}
}
