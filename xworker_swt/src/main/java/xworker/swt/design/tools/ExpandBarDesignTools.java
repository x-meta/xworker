package xworker.swt.design.tools;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.widgets.ExpandBarExpandItemCreator;

public class ExpandBarDesignTools implements IDesignTool<ExpandBar>{
	public ItemInfo getItemIndex(ExpandBar parent, Control control){
		int index = -1;
		for(ExpandItem item : parent.getItems()){
			index++;
			if(item.getControl() == control){
				return new ItemInfo(index, Designer.getThing(item));
			}
		}
		
		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(ExpandBar parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			return insert(parent, thing);
		}else{
			Thing itemThing = new Thing("xworker.swt.widgets.ExpandBar/@ExpandItem");
			itemThing.set("name", thing.getMetadata().getName() + "Item");
			itemThing.set("text", thing.getMetadata().getLabel());
			itemThing.addChild(thing);
			
			DesignTools.addToParentThing(parentThing, itemThing, itemInfo.itemThing, actionType);
			if(actionType == DesignTools.BELOW){
				itemInfo.index ++;
			}
			ExpandItem newItem = (ExpandItem) ExpandBarExpandItemCreator.create(itemThing, actionContext, itemInfo.index);
			newItem.setExpanded(true);
			return newItem.getControl();
		}
	}

	@Override
	public Control update(ExpandBar parent, Control control) {
		ExpandItem item = null;
		Thing itemThing = null;
		for(			ExpandItem citem : parent.getItems()){
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
		
		return item.getControl();
	}

	@Override
	public Control remove(ExpandBar parent, Control control) {
		for(ExpandItem item : parent.getItems()){
			if(item.getControl() == control){
				Thing parentThing = Designer.getThing(parent);
				Thing itemThing = Designer.getThing(item);				
				parentThing.removeChild(itemThing);
				parentThing.save();
				
				item.dispose();
				control.dispose();
				
				//返回正打开的Item
				for(ExpandItem eitem : parent.getItems()){
					if(eitem.getExpanded()){
						return eitem.getControl();
					}
				}
				
				if(parent.getItemCount() > 0){
					parent.getItems()[0].setExpanded(true);
					return parent.getItems()[0].getControl();
				}
				
				return parent;
			}
		}
		
		return null;
	}

	@Override
	public Control insert(ExpandBar parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		Thing itemThing = new Thing("xworker.swt.widgets.ExpandBar/@ExpandItem");
		itemThing.set("name", thing.getMetadata().getName() + "Item");
		itemThing.set("text", thing.getMetadata().getLabel());
		if("TabItem".equals(thing.getThingName())){
			itemThing = thing;
		}else{
			itemThing.addChild(thing);
		}
		parentThing.addChild(itemThing);
		parentThing.save();
		
		actionContext.peek().put("parent", parent);
		ExpandItem newItem = itemThing.doAction("create", actionContext);
		newItem.setExpanded(true);
		
		return newItem.getControl();
	}

	@Override
	public Control replace(ExpandBar parent, Control control, Thing thing) {
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		for(ExpandItem item : parent.getItems()){
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
				
				return newControl;
			}
		}		
		
		return null;
	}

}
