package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;

import ognl.OgnlException;

public class ButtonGroupActions {
	public static ButtonGroup<Button> create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ButtonGroup<Button> group = new ButtonGroup<Button>();
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Button){
				group.add((Button) obj);
			}
		}
		
		String checkTextButton = UtilString.getString(self, "checkTextButton", actionContext);
		if(checkTextButton != null){
			group.setChecked(checkTextButton);
		}
		
		if(self.getStringBlankAsNull("maxCheckCount") != null){
			group.setMaxCheckCount(self.getInt("maxCheckCount", 0,actionContext));
		}
		
		if(self.getStringBlankAsNull("minCheckCount") != null){
			group.setMinCheckCount(self.getInt("minCheckCount", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("uncheckLast") != null){
			group.setUncheckLast(self.getBoolean("uncheckLast", false, actionContext));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), group);
		return group;
		
	}
}
