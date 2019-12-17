package xworker.swt.xwidgets.dataitems;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;
import xworker.util.ThingGroup;

public class ThingGroupDataItem extends DataItem{
	ThingGroup thingGroup;
	boolean dynamic;
	boolean inited = false;
	Pattern groupFilter;
	
	public ThingGroupDataItem(ThingGroup thingGroup, boolean dynamic, Pattern groupFilter, DataItemContainer dataItemContainer, DataItem parentItem, boolean createChilds,
			Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, createChilds, thing, actionContext);
		
		this.thingGroup = thingGroup;
		this.groupFilter = groupFilter;
		
		//创建子节点
		if(dynamic == false) {
			initChilds();
		}
		
	}

	private void initChilds() {
		if(inited == false) {
			inited = true;
			for(ThingGroup childGroup : thingGroup.getChilds()) {
				if(groupFilter != null && !groupFilter.matcher(thingGroup.getGroup()).matches()) {
					continue;
				}
				
				ThingGroupDataItem item = new ThingGroupDataItem(childGroup, dynamic, groupFilter, 
						dataItemContainer, this, true, thing, actionContext);
				this.addChild(item);
			}
			
		}
	}
	
	@Override
	public List<DataItem> getChilds() {
		initChilds();
		
		return childs;
	}
	
	@Override
	public Object getData() {
		if(thingGroup.getThing() == null) {
			return null;
		}else {
			return thingGroup.getThing();
		}
	}

	@Override
	public Image getIcon(Control control) {
		if(thingGroup.getThing() == null) {
			return SwtUtils.createImage(control, "icons/folder.gif", actionContext);
		}else {
			return SwtUtils.getIcon(thingGroup.getThing(), control, actionContext);
		}
	}

	@Override
	public String getLabel() {
		if(thingGroup.getThing() == null) {
			return thingGroup.getGroup();
		}else {
			return thingGroup.getThing().getMetadata().getLabel();
		}
	}
	
	
}
