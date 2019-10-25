package xworker.swt.design.tools;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.widgets.CoolBarCoolItemCreator;
import xworker.swt.widgets.CoolBarCreator;

public class CoolBarDesignTools implements IDesignTool<CoolBar>{

	public ItemInfo getItemIndex(CoolBar parent, Control control){
		int index = -1;
		for(CoolItem item : parent.getItems()){
			index++;
			if(item.getControl() == control){
				
				return new ItemInfo(index, Designer.getThing(item));
			}
		}

		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(CoolBar parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			return (Control) insert(parent, thing);
		}else{
			Thing itemThing = new Thing("xworker.swt.widgets.CoolBar/@CoolItem");
			itemThing.set("name", thing.getMetadata().getName() + "Item");
			itemThing.set("text", thing.getMetadata().getName());
			itemThing.addChild(thing);
			
			
			DesignTools.addToParentThing(parentThing, itemThing, itemInfo.itemThing, actionType);
			if(actionType == DesignTools.BELOW){
				itemInfo.index ++;
			}
			CoolBarCreator.initCoolBar(parent);
			actionContext.peek().put("self", itemThing);
			CoolItem newItem = (CoolItem) CoolBarCoolItemCreator.create(actionContext, itemInfo.index);
			
			if(parent.getParent() != null){
				parent.getParent().layout();
			}
			return newItem.getControl();
		}
	}

	@Override
	public Control update(CoolBar parent, Control control) {
		CoolItem item = null;
		Thing itemThing = null;
		for(CoolItem citem : parent.getItems()){
			if(citem.getControl() == control){
				item = citem;
				itemThing = Designer.getThing(item);
				break; 
			}
		}
				
		ActionContext actionContext = Designer.getActionContext(control);
		control.dispose();
		actionContext.peek().put("parent", parent);		
		for(Thing child : itemThing.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Control){
				item.setControl((Control) obj);
			}
		}		
		
		CoolBarCreator.initCoolBar(parent);
		
		if(parent.getParent() != null){
			parent.getParent().layout();
		}
		return item.getControl();
	}

	@Override
	public Control remove(CoolBar parent, Control control) {
		for(CoolItem item : parent.getItems()){
			if(item.getControl() == control){
				Thing parentThing = Designer.getThing(parent);
				Thing itemThing = Designer.getThing(item);				
				parentThing.removeChild(itemThing);
				parentThing.save();
				
				ItemInfo itemInfo = getItemIndex(parent, control);
				int index = itemInfo != null ? itemInfo.index : -1;
				item.dispose();
				control.dispose();

				CoolBarCreator.initCoolBar(parent);
				if(parent.getParent() != null){
					parent.getParent().layout();
				}
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
	public Control insert(CoolBar parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		Thing itemThing = new Thing("xworker.swt.widgets.CoolBar/@CoolItem");
		itemThing.set("name", thing.getMetadata().getName() + "item");
		itemThing.set("text", thing.getMetadata().getName());
		if("CoolItem".equals(thing.getThingName())){
			itemThing = thing;
		}else{
			itemThing.addChild(thing);
		}
		parentThing.addChild(itemThing);
		parentThing.save();
		
		actionContext.peek().put("parent", parent);
		CoolItem newItem = (CoolItem) itemThing.doAction("create", actionContext);	
		
		CoolBarCreator.initCoolBar(parent);
		if(parent.getParent() != null){
			parent.getParent().layout();
		}
		return newItem.getControl();
	}

	@Override
	public Control replace(CoolBar parent, Control control, Thing thing) {
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		for(CoolItem item : parent.getItems()){
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
				
				CoolBarCreator.initCoolBar(parent);
				
				if(parent.getParent() != null){
					parent.getParent().layout();
				}
				return newControl;
			}
		}		
		
		return null;
	}

}
