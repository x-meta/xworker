package xworker.project.index;

import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

/**
 * 
 * @author zyx
 * @deprecated
 *
 */

public class ProjectThingIndex {
	ThingEntry thingEntry;
	
	public ProjectThingIndex(Thing thing){
		thingEntry = new ThingEntry(thing);
	}
	
	public Thing getThing(){
		return thingEntry.getThing();
	}
	
	public String getPath(){
		return getThing().getMetadata().getPath();
	}
	
	public String getName(){
		return getThing().getMetadata().getName();
	}
	
	public String getLabel(){
		return getThing().getMetadata().getLabel();
	}
	
	public boolean exists(){
		return getThing() != null;
	}
}
