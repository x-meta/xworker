package xworker.doc.thing;

import org.xmeta.Thing;

public class ActionDoc extends Doc{

	public ActionDoc(ThingDocContext context, Thing owner, Thing thing) {
		super(context, owner, thing);

	}
	
	public boolean isDefinedBySelf(){
		Thing parent = thing.getParent();
		if(parent != null){
			parent = parent.getParent();
			
			return parent == owner;
		}

		return false;
	}
	
	public Thing getDefiendThing(){
		Thing parent = thing.getParent();
		if(parent != null){
			parent = parent.getParent();
		}
		
		return parent;
	}
	
	public String getDefiendThingUrl(){
		Thing d = getDefiendThing();
		if(d == null){
			return "";
		}
		
		String name = d.getMetadata().getName();
		String label = d.getMetadata().getLabel();
		if(!name.equals(label)){
			label = name + "(" + label + ")";
		}
		if(d != owner){
			Thing parent = owner.getParent();
			if(parent == null){
				parent = owner;
			}
			String url = context.getThingUrl(parent, d);
			if(url != null){
				label = "<a href=\"" + url + "\"><b>" + label + "</b></a>";
			}
		}
		
		return label;
	}
	
}
