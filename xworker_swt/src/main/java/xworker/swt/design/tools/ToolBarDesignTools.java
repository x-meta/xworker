package xworker.swt.design.tools;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.widgets.ToolBarCreator;
import xworker.swt.widgets.ToolBarToolItemCreator;

public class ToolBarDesignTools implements IDesignTool<ToolBar>{

	public ItemInfo getItemIndex(ToolBar parent, Control control){
		int index = -1;
		for(ToolItem item : parent.getItems()){
			index++;
			if(item.getControl() == control){
				return new ItemInfo(index, Designer.getThing(item));
			}
		}
		
		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(ToolBar parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			return insert(parent, thing);
		}else{
			Thing itemThing = null;
			if("ToolItem".equals(thing.getThingName())) {
				itemThing = thing;
			}else {
				itemThing = new Thing("xworker.swt.widgets.ToolBar/@ToolItem");
				itemThing.set("name", thing.getMetadata().getName() + "Item");
				itemThing.set("text", thing.getMetadata().getName());
				itemThing.set("type", "SEPARATOR");
				itemThing.addChild(thing);
			}
			
			DesignTools.addToParentThing(parentThing, itemThing, itemInfo.itemThing, actionType);
			if(actionType == DesignTools.BELOW){
				itemInfo.index ++;
			}
			
			actionContext.peek().put("self", itemThing);
			ToolItem newItem = (ToolItem) ToolBarToolItemCreator.create(actionContext, itemThing, itemInfo.index);
			ToolBarCreator.initToolBar(parent);
			return newItem.getControl();
		}
	}

	@Override
	public Control update(ToolBar parent, Control control) {
		ToolItem item = null;
		Thing itemThing = null;
		for(ToolItem citem : parent.getItems()){
			if(citem.getControl() == control){
				item = citem;
				itemThing = Designer.getThing(item);
				break; 
			}
		}
				
		ActionContext actionContext = Designer.getActionContext(control);
		actionContext.peek().put("parent", parent);
		control.dispose();
		for(Thing child : itemThing.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Control){
				item.setControl((Control) obj);
			}
		}		
		
		ToolBarCreator.initToolBar(parent);
		return item.getControl();
	}

	@Override
	public Control remove(ToolBar parent, Control control) {
		for(ToolItem item : parent.getItems()){
			if(item.getControl() == control){
				Thing parentThing = Designer.getThing(parent);
				Thing itemThing = Designer.getThing(item);				
				parentThing.removeChild(itemThing);
				parentThing.save();
				
				ItemInfo itemInfo = getItemIndex(parent, control);
				int index = itemInfo != null ? itemInfo.index : -1;
				item.dispose();
				control.dispose();

				ToolBarCreator.initToolBar(parent);
				
				
				if(index != -1){
					if(index > 0 && parent.getItemCount() > index - 1){
						return parent.getItem(index - 1).getControl();
					}if(parent.getItemCount() > index){
						return parent.getItem(index).getControl();
					}
				}
				
				return parent;		
			}
		}
		
		return null;
	}

	@Override
	public Control insert(ToolBar parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		Thing itemThing = new Thing("xworker.swt.widgets.ToolBar/@ToolItem");
		itemThing.set("name", thing.getMetadata().getName() + "item");
		itemThing.set("text", thing.getMetadata().getName());
		itemThing.set("type", "SEPARATOR");
		if("CoolItemItem".equals(thing.getThingName())){
			itemThing = thing;
		}else{
			itemThing.addChild(thing);
		}
		parentThing.addChild(itemThing);
		parentThing.save();
		
		actionContext.peek().put("parent", parent);
		ToolItem newItem = itemThing.doAction("create", actionContext);	
		
		ToolBarCreator.initToolBar(parent);
		
		return newItem.getControl();
	}

	@Override
	public Control replace(ToolBar parent, Control control, Thing thing) {
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		for(ToolItem item : parent.getItems()){
			if(item.getControl() == control){
				Thing itemThing = Designer.getThing(item);
				//清空
				itemThing.getChilds().clear();
				itemThing.addChild(thing);
				itemThing.save();
				
				Control newControl = thing.doAction("create", actionContext);
				item.setControl(newControl);
				
				//parent.getItem(index).dispose();
				control.dispose();
				
				ToolBarCreator.initToolBar(parent);
				return newControl;
			}
		}		
		
		return null;
	}

}
