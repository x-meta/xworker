package xworker.libdgx.scenes.scene2d.utils;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;

public class ArraySelectionActions {
	@SuppressWarnings("rawtypes")
	public static void init(Thing self, ArraySelection item, ActionContext actionContext){
		if(self.getStringBlankAsNull("rangeSelect") != null){
			item.setRangeSelect(self.getBoolean("rangeSelect"));
		}
	}
}
