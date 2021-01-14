package xworker.swt.xwidgets.dataitems;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;
import xworker.util.ThingGroup;
import xworker.util.XWorkerUtils;

public class ThingRegistorItems extends DataItem{
	boolean dynamic;
	
	public ThingRegistorItems(DataItemContainer dataItemContainer, DataItem parentItem, boolean createChilds,
			Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, createChilds, thing, actionContext);

		Thing registThing = thing.doAction("getThing", actionContext);
		String type = thing.doAction("getType", actionContext);
		List<String> keys = thing.doAction("getKeys", actionContext);
		String groupRegexStr = thing.doAction("getGroupRegex", actionContext);
		dynamic = thing.getBoolean("dynamic");
		
		if(registThing != null) {
			List<Thing> things = XWorkerUtils.searchRegistThings(registThing, type, keys, actionContext);
			if(things != null) {
				List<ThingGroup> groups = XWorkerUtils.getThingGroups(things);
				boolean noRoot = thing.getBoolean("noRoot");
				if(noRoot) {
					this.visible = false;
				}
				
				Pattern groupFilter = null;
				if(groupRegexStr != null && !"".equals(groupRegexStr)) {
					groupFilter = Pattern.compile(groupRegexStr);
				}
				
				Collections.sort(groups);
				for(ThingGroup group : groups) {
					if(groupFilter != null && group.getGroup() != null && !"".equals(group.getGroup()) 
							&& !groupFilter.matcher(group.getGroup()).matches()) {
						//不符合group的过滤条件
						continue;
					}
					
					DataItem item = new ThingGroupDataItem(group, dynamic, groupFilter, dataItemContainer, parentItem,							
							true, thing, actionContext);
					if(noRoot) {
						if(parentItem  != null) {
							parentItem.addChild(item);
						}else {
							dataItemContainer.addItem(item);
						}
					}else {
						this.addChild(item);
					}
				}
				
				
			}
		}
	}

	@Override
	public boolean isDynamic() {
		return dynamic;
	}

	@Override
	public Object getData() {
		return null;
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
				
		return new ThingRegistorItems(dataItemContainer, parentItem, true, self, actionContext);
	}
}
	