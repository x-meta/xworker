package xworker.swt.xworker;

import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.ThingGroup;
import xworker.util.XWorkerUtils;

public class ThingRegistMenu {
	private static final String key = "ThingRegistMenu_Regist_Thing";
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//查询注册的事物列表
		Thing thing = self.doAction("getThing", actionContext);
		String registType = self.doAction("getRegistType", actionContext);
		List<Thing> things = XWorkerUtils.searchRegistThings(thing, registType, null, actionContext);
		
		//分组和创建QuickMenu事物
		ThingGroup thingGroup = new ThingGroup();
		thingGroup.addThings(things, thing);
		thingGroup.sort();
		
		String itemPrefix = self.doAction("getItemPrefix", actionContext);
		
		Thing quickMenu = new Thing("xworker.swt.xwidgets.QuickMenu");
		quickMenu.set("name", self.getMetadata().getName());
		quickMenu.set("label", self.getMetadata().getLabel());
		
		for(ThingGroup child : thingGroup.getChilds()) {
			quickMenu.addChild(createMenuItem(child, itemPrefix));
		}
		
		return quickMenu.doAction("create", actionContext);
	}
	
	/**
	 * 处理MenuItem的事件。
	 * 
	 * @param actionContext
	 */
	public static void handleEvent(ActionContext actionContext) {
		SelectionEvent event = actionContext.getObject("event");
		Thing itemThing = (Thing) event.widget.getData();
		Thing thing = (Thing) itemThing.get(key);
		thing.doAction("run", actionContext);
	}
	
	private static Thing createMenuItem(ThingGroup thingGroup, String itemPrefix) {
		Thing menuItem = new Thing("xworker.swt.xwidgets.QuickMenu/@Menu");
		if(thingGroup.getThing() != null) {
			Thing thing = thingGroup.getThing();
			menuItem.getAttributes().putAll(thing.getAttributes());
			String name = thing.getMetadata().getName() + "MenuItem";
			if(itemPrefix != null) {
				name = itemPrefix + name;
			}
			
			menuItem.set("name", name);
			menuItem.set(key, thing);
			menuItem.set("actionThing", "xworker.swt.xworker.ThingRegistMenu/@actions/@handleEvent");
			return menuItem;
		}else {
			String name = thingGroup.getGroup();
			if(itemPrefix != null) {
				name = itemPrefix + name;
			}
			
			menuItem.set("name", name);
			menuItem.set("label", thingGroup.getGroup());
			
			for(ThingGroup child : thingGroup.getChilds()) {
				menuItem.addChild(createMenuItem(child, itemPrefix));
			}
			
			return menuItem;
		}
	}
}
