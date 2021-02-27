package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.CheckBox;

public class CheckBoxActions {
	public static void init(CheckBox box, Thing thing, ActionContext actionContext) {
		ButtonBaseActions.init(box, thing, actionContext);
		 
		 if(thing.valueExists("allowIndeterminate")) {
			 box.setAllowIndeterminate(thing.getBoolean("allowIndeterminate"));
		 }
		 
		 if(thing.valueExists("indeterminate")) {
			 box.setIndeterminate(thing.getBoolean("indeterminate"));
		 }
		 
		 if(thing.valueExists("selected")) {
			 box.setSelected(thing.getBoolean("selected"));
		 }
	}
	
	public static CheckBox create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		CheckBox box = new CheckBox();
		init(box, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), box);
		
		actionContext.peek().put("parent", box);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return box;
	}
}
