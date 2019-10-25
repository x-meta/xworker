package xworker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.Thing;

/**
 * 事物分组的工具类。
 * 
 * 事物分组是XWorker种常用的功能，比如在事物注册的树控件中显示等。
 * 
 * @author zyx
 *
 */
public class ThingGroup implements Comparable<ThingGroup>{
	String group;
	String oldGroup;
	Thing thing;
	int sortWeight = 0;
	int weightCount = 0;
	
	List<ThingGroup> childs = new ArrayList<ThingGroup>();
	
	/**
	 * 创建一个根分组。
	 */
	public ThingGroup() {
		
	}
	
	/**
	 * 创建要给节点分组。
	 * 
	 * @param thing
	 * @param group
	 */
	public ThingGroup(Thing thing, String group){
		this.thing = thing;
		this.group = group;
		this.oldGroup  = group;
		
		if(thing == null && group != null && !"".equals(group)){
			String[] ss = group.split("[|]");
			if(ss.length > 1){
				try{
					sortWeight = Integer.parseInt(ss[0]);
					weightCount++;
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
	
	/**
	 * 添加一组事物到分组中。
	 * 
	 * @param things
	 */
	public void addThings(List<Thing> things) {
		addThings(things, null);
	}
	
	/**
	 * 添加一组事物到分组中。
	 * 
	 * @param things
	 * @param owner
	 */
	public void addThings(List<Thing> things, Thing owner) {
		for(Thing thing : things) {
			addThing(thing, owner);
		}
	}
	
	/**
	 * 添加一个事物到分组中。
	 * 
	 * @param thing
	 */
	public void addThing(Thing thing) {
		addThing(thing, null);
	}
	
	/**
	 * 添加一个事物到分组中，这个方法通常在根分组中调用。
	 * 
	 * @param thing
	 */
	public void addThing(Thing thing, Thing owner) {
		String groups = ThingGroupUtils.getGroup(thing, owner);
		if(groups == null || "".equals(groups.trim())) {
			addThingByGroup(thing, groups);
			return;
		}
				
		for(String group : groups.split("[,]")) {
			group = group.trim();
			addThingByGroup(thing , group);
		}
	}
	
	private void addThingByGroup(Thing thing, String group) {
		if(group == null || "".equals(group.trim())) {
			this.childs.add(new ThingGroup(thing, null));
		}else {
			ThingGroup current = this;
			for(String gs : group.split("[.]")) {
				current = current.findGroup(gs);				
			}
			
			current.childs.add(new ThingGroup(thing, null));
		}
	}
	
	/**
	 * 查找Group，如果不存在则增加一个。
	 * 
	 * @param group
	 * @return
	 */
	private ThingGroup findGroup(String group) {
		String g = filteGroup(group);
		for(ThingGroup child : childs) {
			if(child.getThing() == null && child.group.equals(g)) {
				child.addWeight(group);
				return child;
			}
		}
		
		ThingGroup thingGroup = new ThingGroup(null, group);
		childs.add(thingGroup);
		return thingGroup;
	}
	
	/**
	 * 过滤掉权重等，只剩下group字符串。
	 * 
	 * @param group
	 * @return
	 */
	private String filteGroup(String group) {
		if(group != null) {
			String[] ss = group.split("[|]");
			if(ss.length > 1){				
				return ss[1];				
			}			
		}
		
		return group;
	}
	
	public void addWeight(String group){
		String[] ss = group.split("[|]");
		if(ss.length > 1){
			try{
				sortWeight += Integer.parseInt(ss[0]);
				weightCount++;
			}catch(Exception e){						
			}			
		}		
	}
	
	public void sort(){
		Collections.sort(childs);
		
		for(ThingGroup group : childs){
			group.sort();
		}
	}
	
	public List<ThingGroup> getChilds(){
		return childs;
	}

	@Override
	public int compareTo(ThingGroup o) {
		if(weightCount > 0){
			//计算sortWeight的平均值
			sortWeight = sortWeight / weightCount;
			weightCount = 0;
		}
		
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
	
	public Thing getThing(){
		return thing;
	}
	
	public String getGroup(){
		return group;
	}
	public int compareSortWeight(ThingGroup o){
		if(o.sortWeight < sortWeight){
			return 1;
		}else if(o.sortWeight > sortWeight){
			return -1;
		}else{
			return 0;
		}
	}

	public String getOldGroup() {
		return oldGroup;
	}

}
