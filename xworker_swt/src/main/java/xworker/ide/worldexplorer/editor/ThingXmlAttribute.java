package xworker.ide.worldexplorer.editor;

import org.xmeta.Thing;

public class ThingXmlAttribute implements Comparable<ThingXmlAttribute> {
	String name;
	Thing thing;
	Thing attrThing;
	
	public ThingXmlAttribute(Thing thing, Thing attrThing, String name){
		this.thing = thing;
		this.attrThing = attrThing;
		this.name = name;
	}

	public Object getValue(){
		return thing.get(name);
	}
	
	public Thing getAttributeThing(){
		return attrThing;
	}

	public String toString(){
		Object value = thing.get(name);
		if(value == null){
			return null;
		}
		
		String v = value instanceof String ? (String) value : "...";
		int index = v.indexOf("\n");
		if(index != -1){
			v = v.substring(0, index);
		}
		v = v.trim();
		if(v.length() > 50){
			v = v.substring(0, 50) + "...";
		}
		return name + "=\"" + v + "\"";		
	}

	@Override
	public int compareTo(ThingXmlAttribute o) {		
		return name.compareTo(o.name);
	}
	
}
