package xworker.swt.design.tools;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.design.IDesignTool;
import xworker.swt.design.ItemInfo;
import xworker.swt.nebula.PShelfActions;

public class PShelfTools implements IDesignTool<PShelf>{
	
	public ItemInfo getItemIndex(PShelf parent, Control control){
		int index = -1;
		for(PShelfItem item : parent.getItems()){
			index++;
			
			Control[] cs = item.getBody().getChildren();
			if(cs != null && cs.length > 0 && cs[0] == control){
				return new ItemInfo(index, Designer.getThing(item));
			}
		}
		
		return null;
	}
	
	@Override
	public Control insertAboveOrBelow(PShelf parent, Control control,
			Thing thing, int actionType) {
		Thing parentThing = Designer.getThing(parent);	
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		ItemInfo itemInfo = getItemIndex(parent, control);					
		if(itemInfo == null){
			return insert(parent, thing);
		}else{
			Thing itemThing = new Thing("xworker.swt.nubula.PShelf/@PShelfItem");
			itemThing.set("name", thing.getMetadata().getName() + "Item");
			itemThing.set("text", thing.getMetadata().getLabel());
			itemThing.addChild(thing);
			
			DesignTools.addToParentThing(parentThing, itemThing, itemInfo.itemThing, actionType);
			if(actionType == DesignTools.BELOW){
				itemInfo.index ++;
			}
								
			PShelfItem newItem = (PShelfItem) PShelfActions.createItem(actionContext, itemThing, itemInfo.index);
			parent.setSelection(newItem);			
			return getItemControl(newItem);
		}
	}
	
	public Control getItemControl(PShelfItem item) {
		Control[] cs = item.getBody().getChildren();
		if(cs != null && cs.length > 0) {
			return cs[0];
		}else {
			return null;
		}
	}

	@Override
	public Control update(PShelf parent, Control control) {
		PShelfItem item = null;
		Thing itemThing = null;
		for(PShelfItem citem : parent.getItems()){
			if(getItemControl(citem) == control){
				item = citem;
				itemThing = Designer.getThing(item);
				break; 
			}
		}
				
		ActionContext actionContext = Designer.getActionContext(control);
				
		Thing controlThing = Designer.getThing(control);
		control.dispose();
		
		actionContext.peek().put("parent", item.getBody());
		if(itemThing != null){			
			for(Thing child : itemThing.getChilds()){
				child.doAction("create", actionContext);
				
			}		
		}else{
			controlThing.doAction("create", actionContext);			
		}
		
		return getItemControl(item);
	}

	@Override
	public Control remove(PShelf parent, Control control) {
		for(PShelfItem item : parent.getItems()){
			if(getItemControl(item) == control){
				Thing parentThing = Designer.getThing(parent);
				Thing itemThing = Designer.getThing(item);				
				parentThing.removeChild(itemThing);
				parentThing.save();
				
				item.dispose();
				control.dispose();
				
				item = parent.getSelection();
				if(item != null){
					return getItemControl(item);
				}else{
					return parent;
				}
			}
		}
		
		return null;
	}

	@Override
	public Control insert(PShelf parent, Thing thing) {
		ActionContext actionContext = Designer.getActionContext(parent);
		Thing parentThing = Designer.getThing(parent);	
		Thing itemThing = new Thing("xworker.swt.nubula.PShelf/@PShelfItem");
		itemThing.set("name", thing.getMetadata().getName() + "item");
		itemThing.set("text", thing.getMetadata().getLabel());
		if("PShelfItem".equals(thing.getThingName())){
			itemThing = thing;
		}else{
			itemThing.addChild(thing);
		}
		parentThing.addChild(itemThing);
		parentThing.save();
		
		actionContext.peek().put("parent", parent);
		PShelfItem newItem = itemThing.doAction("create", actionContext);
		parent.setSelection(newItem);
		return getItemControl(newItem);
	}

	@Override
	public Control replace(PShelf parent, Control control, Thing thing) {
		ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		
		actionContext.peek().put("parent", parent);
		for(PShelfItem item : parent.getItems()){
			if(getItemControl(item) == control){
				Thing itemThing = Designer.getThing(item);
				//清空
				itemThing.getChilds().clear();
				itemThing.addChild(thing);
				itemThing.save();
				
				actionContext.peek().put("parent", item.getBody());
				thing.doAction("create", actionContext);
				
				//parent.getItem(index).dispose();
				control.dispose();
				
				return getItemControl(item);
			}
		}		
		
		return null;
	}

}
