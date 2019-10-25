package xworker.swt.design.tools;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.widgets.TabFolderTabItemCreator;

public class TabFolderDesignTools implements IDesignTool<TabFolder>{
	public ItemInfo getItemIndex(TabFolder parent, Control control){
		int index = -1;
		for(TabItem item : parent.getItems()){
			index++;
			if(item.getControl() == control){
				return new ItemInfo(index, Designer.getThing(item));
			}
		}
		
		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(TabFolder parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			return insert(parent, thing);
		}else{
			Thing itemThing = new Thing("xworker.swt.widgets.TabFolder/@TabItem");
			itemThing.set("name", thing.getMetadata().getName() + "Item");
			itemThing.set("text", thing.getMetadata().getLabel());
			itemThing.addChild(thing);
			
			DesignTools.addToParentThing(parentThing, itemThing, itemInfo.itemThing, actionType);
			if(actionType == DesignTools.BELOW){
				itemInfo.index ++;
			}
			TabItem newItem = (TabItem) TabFolderTabItemCreator.create(actionContext, itemThing, itemInfo.index);
			parent.setSelection(newItem);
			return newItem.getControl();
		}
	}

	@Override
	public Control update(TabFolder parent, Control control) {
		TabItem item = null;
		Thing itemThing = null;
		for(TabItem citem : parent.getItems()){
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
	public Control remove(TabFolder parent, Control control) {
		for(TabItem item : parent.getItems()){
			if(item.getControl() == control){
				Thing parentThing = Designer.getThing(parent);
				Thing itemThing = Designer.getThing(item);				
				parentThing.removeChild(itemThing);
				parentThing.save();
				
				item.dispose();
				control.dispose();
				
				if(parent.getSelectionIndex() != -1){
					return parent.getSelection()[0].getControl();
				}else{
					return parent;
				}
			}
		}
		
		return null;
	}

	@Override
	public Control insert(TabFolder parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		Thing itemThing = new Thing("xworker.swt.widgets.TabFolder/@TabItem");
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
		TabItem newItem = itemThing.doAction("create", actionContext);
		parent.setSelection(newItem);
		
		return newItem.getControl();
	}

	@Override
	public Control replace(TabFolder parent, Control control, Thing thing) {
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		for(TabItem item : parent.getItems()){
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
