package xworker.doc.thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;

public class ThingDoc extends Doc{
	List<AttributeDoc> attributes = new ArrayList<AttributeDoc>();
	List<ActionDoc> actions = new ArrayList<ActionDoc>();
	List<ChildDoc> childs = new ArrayList<ChildDoc>();
	
	public ThingDoc(ThingDocContext context, Thing thing){
		super(context, null, thing);
		
		List<Thing> allAttributes = getAllChilds("attribute");
		for(Thing attr : allAttributes){
			attributes.add(new AttributeDoc(context, thing, attr));
		}
		
		List<Thing> actionThings = thing.getActionThings();
		for(Thing act : actionThings){
			actions.add(new ActionDoc(context, thing, act));
		}
		
		List<Thing> childThings = getAllChilds("thing");
		for(Thing child : childThings){
			childs.add(new ChildDoc(context, thing, child));
		}
		
		Collections.sort(attributes);
		Collections.sort(actions);
		Collections.sort(childs);
	}
	
	public List<Thing> getAllChilds(String name){
		Map<String, String> context = new HashMap<String, String>();
		List<Thing> allChilds = new ArrayList<Thing>();
		
		List<Thing> childs = thing.getChilds(name);
		for(Thing child : childs){
			allChilds.add(child);
			context.put(child.getMetadata().getName(), child.getMetadata().getName());
		}
		
		for(Thing ext : thing.getAllExtends()){
			List<Thing> echilds = ext.getChilds(name);
			for(Thing child : echilds){
				String cname = child.getMetadata().getName();
				if(context.get(cname) != null){
					continue;
				}else{
					allChilds.add(child);
					context.put(cname, cname);
				}
			}
		}
		
		return allChilds;
	}		
	
	public List<AttributeDoc> getAttributes(){
		return attributes;
	}
	
	public List<ActionDoc> getActions(){
		return actions;
	}
	
	public List<ChildDoc> getChilds(){
		return childs;
	}
	
	@Override
	public int compareTo(Doc o) {
		return getName().compareTo(o.getName());
	}
}
