package xworker.swt.design.tools;

import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.util.SwtUtils;

public class PGroupTools implements IDesignTool<PGroup>{

	public ItemInfo getItemIndex(PGroup parent, Control control){
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
	public Control insertAboveOrBelow(PGroup parent, Control control,
			Thing thing, int actionType) {
		if(!checkLayout(parent)){
			return null;
		}
		
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
		
		return newControl;
	}

	@Override
	public Control update(PGroup parent, Control control) {
		if(!checkLayout(parent)){
			return null;
		}
		
		ActionContext actionContext = Designer.getActionContext(control);
		Thing thing = Designer.getThing(control);
		
		actionContext.peek().put("parent", parent);
		Control newControl = thing.doAction("create", actionContext);
		newControl.moveAbove(control);
		control.dispose();
		SwtUtils.layout(parent);
		return newControl;
	}

	@Override
	public Control remove(PGroup parent, Control control) {
		Thing parentThing = Designer.getThing(parent);
		Thing itemThing = Designer.getThing(control);				
		parentThing.removeChild(itemThing);
		parentThing.save();
		
		int index = -1;
		for(int i=0; i<parent.getChildren().length; i++){
			index++;
			if(parent.getChildren()[i] == control){
				break;
			}
		}
		control.dispose();		
		SwtUtils.layout(parent);
		
		if(index != -1){
			if(parent.getChildren().length > index){
				return parent.getChildren()[index];
			}else if(parent.getChildren().length > index - 1  && index != 0){
				return parent.getChildren()[index - 1];
			}else{
				return parent;
			}
		}else{
			return parent;
		}
	}

	@Override
	public Control insert(PGroup parent, Thing thing) {
		if(!checkLayout(parent)){
			return null;
		}
		
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		parentThing.addChild(thing);
		parentThing.save();
		
		actionContext.peek().put("parent", parent);
		Control newControl = thing.doAction("create", actionContext);	
		
		SwtUtils.layout(parent);
		return newControl;
	}

	@Override
	public Control replace(PGroup parent, Control control, Thing thing) {
		if(!checkLayout(parent)){
			return null;
		}
		
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing itemThing = Designer.getThing(control);
		Thing parentThing = Designer.getThing(parent);
		
		//拷贝LayoutData
		Thing ndata = thing.getThing("LaoyutData@0");
		Thing odata = itemThing.getThing("LaoyutData@0");
		if(ndata == null && odata != null){
			thing.addChild(odata);
		}
		ItemInfo itemInfo = getItemIndex(parent, control);
		DesignTools.addToParentThing(parentThing, thing, itemInfo.itemThing, DesignTools.REPLACE);
		
		//清空
		actionContext.peek().put("parent", parent);
		Control newControl = thing.doAction("create", actionContext);
		newControl.moveAbove(control);
		
		//parent.getItem(index).dispose();
		control.dispose();		
		SwtUtils.layout(parent);
		return newControl;
	}

	public boolean checkLayout(Composite parent){
		if(parent.getLayout() == null){
			//如果没有布局，默认添加一个FillLayout
			Thing fillLayout = new Thing("xworker.swt.layout.FillLayout");
			Thing parentThing = Designer.getThing(parent);	
			parentThing.addChild(fillLayout);
			parentThing.save();
			parent.setLayout(new FillLayout());
			return true;
		}else{
			return true;
		}
	}

}
