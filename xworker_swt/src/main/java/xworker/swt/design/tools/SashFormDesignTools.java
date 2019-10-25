package xworker.swt.design.tools;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.util.SwtUtils;

public class SashFormDesignTools implements IDesignTool<SashForm>{

	public ItemInfo getItemIndex(SashForm parent, Control control){
		int index = -1;
		for(Control item : parent.getChildren()){
			index++;
			if(item == control){
				return new ItemInfo(index, Designer.getThing(item));
			}
		}
		
		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(SashForm parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		Control newControl = null;
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			newControl = insert(parent, thing);
		}else{
			DesignTools.addToParentThing(parentThing, thing, itemInfo.itemThing, actionType);
			
			newControl = thing.doAction("create", actionContext);
			if(actionType == DesignTools.ABOVE){
				newControl.moveAbove(control);
			}else{
				newControl.moveBelow(control);
			}
		}
		SwtUtils.layout(parent);
		saveWeights(parent);
		
		return newControl;
	}

	@Override
	public Control update(SashForm parent, Control control) {		
		int weights[] = parent.getWeights();
		ActionContext actionContext = Designer.getActionContext(control);
		Thing thing = Designer.getThing(control);
		
		actionContext.peek().put("parent", parent);
		Control newControl = thing.doAction("create", actionContext);
		newControl.moveAbove(control);
		control.dispose();
		parent.setWeights(weights);
		SwtUtils.layout(parent);		
		
		return newControl;
	}

	@Override
	public Control remove(SashForm parent, Control control) {
		Thing parentThing = Designer.getThing(parent);
		Thing itemThing = Designer.getThing(control);				
		parentThing.removeChild(itemThing);
		parentThing.save();
		
		int index = -1;
		boolean last = false;
		for(int i=0; i<parent.getChildren().length; i++){
			index++;
			if(parent.getChildren()[i] == control){
				if(i == parent.getChildren().length - 1){
					last = true;
				}
				break;
			}
		}
		control.dispose();		
		parent.update();
		SwtUtils.layout(parent);
		saveWeights(parent);
		if(index != -1){
			if(last && index > 0){
				return parent.getChildren()[index - 1];
			}else if(parent.getChildren().length > index){
				//System.out.println(parent.getChildren()[index]);
				return parent.getChildren()[index];
			}else if(parent.getChildren().length > index - 1 && index != 0){
				return parent.getChildren()[index - 1];
			}else{
				return parent;
			}
		}else{
			return parent;
		}
	}

	@Override
	public Control insert(SashForm parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		parentThing.addChild(thing);		
		
		actionContext.peek().put("parent", parent);
		Control newControl = thing.doAction("create", actionContext);	
		
		saveWeights(parent);
		parentThing.save();
		SwtUtils.layout(parent);
		
		return newControl;
	}

	@Override
	public Control replace(SashForm parent, Control control, Thing thing) {
		int weights[] = parent.getWeights();
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);
		
		//拷贝LayoutData
		ItemInfo itemInfo = getItemIndex(parent, control);
		DesignTools.addToParentThing(parentThing, thing, itemInfo.itemThing, DesignTools.REPLACE);
		
		//创建新的控件
		actionContext.peek().put("parent", parent);
		Control newControl = thing.doAction("create", actionContext);
		newControl.moveAbove(control);
		
		//parent.getItem(index).dispose();
		control.dispose();		
		parent.setWeights(weights);
		
		SwtUtils.layout(parent);
		return newControl;
	}

	public void saveWeights(SashForm parent){
		int[] weights = parent.getWeights();
		Thing thing = Designer.getThing(parent);
		
		String s = null;
		for(int i=0; i<weights.length; i++){		
			if(i == 0){
				s = "" + weights[i];
			}else{
				s = s + "," + weights[i];
			}
		}
		
		thing.set("weights", s);
		thing.save();
	}
}
