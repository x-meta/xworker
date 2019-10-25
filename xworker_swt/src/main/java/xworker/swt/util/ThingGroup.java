package xworker.swt.util;

import org.xmeta.Thing;

/**
 * 请参看和使用xworker.util.ThingGroup。本对象已经过期。
 * 
 * @author zyx
 *
 */
public class ThingGroup extends xworker.util.ThingGroup{//implements Comparable<ThingGroup>{
	public ThingGroup() {
		super();
	}
	
	public ThingGroup(Thing thing, String group){
		super(thing, group);
	}
		
	/*
	String group;
	String oldGroup;
	Thing thing;
	int sortWeight = 0;
	
	List<ThingGroup> childs = new ArrayList<ThingGroup>();
	
	public ThingGroup(Thing thing, String group){
		this.thing = thing;
		this.group = group;
		this.oldGroup  = group;
		
		if(thing == null && group != null && !"".equals(group)){
			String[] ss = group.split("[|]");
			if(ss.length > 1){
				try{
					sortWeight = Integer.parseInt(ss[0]);
				}catch(Exception e){						
				}
				this.group = ss[1];
			}				
		}else if(thing != null){
			String sw = thing.getStringBlankAsNull("th_sortWeight");
			if(sw != null){
				try{
					sortWeight = Integer.parseInt(sw);
				}catch(Exception e){
					
				}
			}
		}
	}

	@Override
	public int compareTo(ThingGroup o) {
		if(o.thing != null && thing != null){
			if(o.sortWeight == sortWeight){
				return thing.getMetadata().getLabel().compareTo(o.thing.getMetadata().getLabel());
			}else{
				return compareSortWeight(o);
			}				
		}else if(o.thing != null && thing == null){
			return -1;
		}else if(o.thing == null && thing != null){
			return 1;
		}else{
			if(o.sortWeight == sortWeight){
				return group.compareTo(o.group);
			}else{
				return compareSortWeight(o);
			}
		}
	}
	
	public int compareSortWeight(ThingGroup o){
		if(o.sortWeight < sortWeight){
			return 1;
		}else if(o.sortWeight > sortWeight){
			return -1;
		}else{
			return 0;
		}
	}*/
}
