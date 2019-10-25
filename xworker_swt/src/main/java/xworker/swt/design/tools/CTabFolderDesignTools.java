package xworker.swt.design.tools;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.custom.CTabFolderCTabItemCreator;
import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;

public class CTabFolderDesignTools implements IDesignTool<CTabFolder>{

	public ItemInfo getItemIndex(CTabFolder parent, Control control){
		int index = -1;
		for(CTabItem item : parent.getItems()){
			index++;
			if(item.getControl() == control){
				return new ItemInfo(index, Designer.getThing(item));
			}
		}
		
		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(CTabFolder parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			return insert(parent, thing);
		}else{
			Thing itemThing = new Thing("xworker.swt.custom.CTabFolder/@CTabItem");
			itemThing.set("name", thing.getMetadata().getName() + "Item");
			itemThing.set("text", thing.getMetadata().getLabel());
			itemThing.addChild(thing);
			
			DesignTools.addToParentThing(parentThing, itemThing, itemInfo.itemThing, actionType);
			if(actionType == DesignTools.BELOW){
				itemInfo.index ++;
			}
								
			CTabItem newItem = (CTabItem) CTabFolderCTabItemCreator.create(actionContext, itemThing, itemInfo.index);
			parent.setSelection(newItem);
			return newItem.getControl();
		}
	}

	@Override
	public Control update(CTabFolder parent, Control control) {
		CTabItem item = null;
		Thing itemThing = null;
		for(CTabItem citem : parent.getItems()){
			if(citem.getControl() == control){
				item = citem;
				itemThing = Designer.getThing(item);
				break; 
			}
		}
				
		ActionContext actionContext = Designer.getActionContext(control);
		actionContext.peek().put("parent", parent);
		
		Thing controlThing = Designer.getThing(control);
		control.dispose();
		if(itemThing != null){
			for(Thing child : itemThing.getChilds()){
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof Control){
					item.setControl((Control) obj);
				}
			}		
		}else{
			Control ctrl = controlThing.doAction("create", actionContext);
			item.setControl(ctrl);
		}
		
		return item.getControl();
	}

	@Override
	public Control remove(CTabFolder parent, Control control) {
		for(CTabItem item : parent.getItems()){
			if(item.getControl() == control){
				Thing parentThing = Designer.getThing(parent);
				Thing itemThing = Designer.getThing(item);				
				parentThing.removeChild(itemThing);
				parentThing.save();
				
				item.dispose();
				control.dispose();
				
				item = parent.getSelection();
				if(item != null){
					return item.getControl();
				}else{
					return parent;
				}
			}
		}
		
		return null;
	}

	@Override
	public Control insert(CTabFolder parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		Thing itemThing = new Thing("xworker.swt.custom.CTabFolder/@CTabItem");
		itemThing.set("name", thing.getMetadata().getName() + "item");
		itemThing.set("text", thing.getMetadata().getLabel());
		if("CTabItem".equals(thing.getThingName())){
			itemThing = thing;
		}else{
			itemThing.addChild(thing);
		}
		parentThing.addChild(itemThing);
		parentThing.save();
		
		actionContext.peek().put("parent", parent);
		CTabItem newItem = itemThing.doAction("create", actionContext);
		parent.setSelection(newItem);
		return newItem.getControl();
	}

	@Override
	public Control replace(CTabFolder parent, Control control, Thing thing) {
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		for(CTabItem item : parent.getItems()){
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
