package xworker.doc.thing;

import org.xmeta.Thing;

public class ChildDoc extends Doc{

	public ChildDoc(ThingDocContext context, Thing owner, Thing thing) {
		super(context, owner, thing);
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
			String url = context.getThingUrl(owner, d);
			if(url != null){
				label = "<a href=\"" + url + "\"><b>" + label + "</b></a>";
			}
		}
		
		return label;
	}
}
