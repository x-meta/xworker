package xworker.swt.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

/**
 * 事件相关的一些动作。
 * 
 * @author zyx
 *
 */
public class EventActions {
	public static void keySwitch(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Event event = actionContext.getObject("event");
		
		KeySwitch ks = (KeySwitch) self.getData("__KeySwitch__");
		if(ks == null){
			ks = new KeySwitch(self, actionContext);
			self.setData("__KeySwitch__", ks);
		}
		
		ks.handleKeyEvent(event, actionContext);
	}
	
	public static class KeySwitch{
		ThingEntry thingEntry;
		ActionContext actionContext;
		Thing defaultThing;
		List<KeyCase> keyCases;
		
		public KeySwitch(Thing thing, ActionContext actionContext){
			thingEntry = new ThingEntry(thing);
			this.actionContext = actionContext;
			
			init();
		}
		
		public void init(){
			Thing thing = thingEntry.getThing();
			keyCases = new ArrayList<KeyCase>();
			for(Thing caseThing : thing.getChilds("Case")){
				keyCases.add(new KeyCase(caseThing, actionContext));
			}
			
			defaultThing = thing.getThing("Default@0");
		}
		
		public void handleKeyEvent(Event event, ActionContext actionContext){
			if(thingEntry.isChanged()){
				init();
			}
			
			for(KeyCase ca : keyCases){
				if(ca.handleKeyEvent(event, actionContext)){
					return;
				}
			}
			
			if(defaultThing != null){
				defaultThing.doAction("doEvent", actionContext);
			}
		}
	}
	
	
	
	public static class KeyCase{
		Thing thing;
		boolean ctrl;
		boolean shift;
		boolean alt;
		char[] keys;
		
		public KeyCase(Thing thing, ActionContext actionContext){
			this.thing = thing;
			ctrl = thing.getBoolean("Ctrl");
			shift = thing.getBoolean("Shift");
			alt = thing.getBoolean("Alt");
		
			String ks = thing.getStringBlankAsNull("key");
			if(ks != null){
				keys = ks.toCharArray();
			}
		}
		
		public boolean handleKeyEvent(Event event, ActionContext actionContext){
			if(ctrl && (event.stateMask & SWT.CTRL) != SWT.CTRL){
				return false;
			}
			
			if(alt && (event.stateMask & SWT.ALT) != SWT.ALT){
				return false;
			}
			
			if(shift && (event.stateMask & SWT.SHIFT) != SWT.SHIFT){
				return false;
			}
			
			if(keys != null){
				boolean ok = false;
				for(char k : keys){
					if(k == event.keyCode){
						ok = true;
						break;
					}
				}
				
				if(!ok){
					return false;
				}
			}
			
			thing.doAction("doEvent", actionContext);
			return true;
		}
	}
}
