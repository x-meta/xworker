package xworker.ide.worldexplorer.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;

public class ThingXmlLine {
	Thing thing;
	int lineStart;
	int lineEnd;
	
	List<ThingXmlAttribute> attributes = new ArrayList<ThingXmlAttribute>();
	List<ThingXmlLine> childs = new ArrayList<ThingXmlLine>();
	
	public ThingXmlLine(Thing thing, int lineStart){
		this.thing = thing;
		this.lineStart = lineStart;
		
		init();
	}
	
	public ThingXmlLine getAtLine(int line){
		if(line == lineStart || line == lineEnd){
			return this;
		}
		
		for(ThingXmlLine child : childs){
			ThingXmlLine l = child.getAtLine(line);
			if(l != null){
				return l;
			}
		}
		
		return null;
	}
	
	public ThingXmlLine getByThing(Thing athing){
		if(thing.getMetadata().getPath().equals(athing.getMetadata().getPath())){
			return this;
		}
		
		for(ThingXmlLine child : childs){
			ThingXmlLine l = child.getByThing(athing);
			if(l != null){
				return l;
			}
		}
		
		return null;
	}
	
	public void init(){
		this.lineEnd = this.lineStart;
		
		//初始化属性
		Map<String, String> context = new HashMap<String, String>();
		
		for(Thing attrThing : thing.getAllAttributesDescriptors()){
			String name = attrThing.getMetadata().getName();
			if(context.get(name) != null){
				continue;
			}else{
				context.put(name, name);
			}
			
			ThingXmlAttribute attr = new ThingXmlAttribute(thing, attrThing, name);
			attributes.add(attr);
		}
		
		Collections.sort(attributes);
		
		int line = lineStart + 1;
		for(Thing child : thing.getChilds()){
			ThingXmlLine childLine = new ThingXmlLine(child, line);
			childs.add(childLine);
			
			this.lineEnd = childLine.lineEnd; 
			line = this.lineEnd + 1;
		}
		
		if(thing.getChilds().size() > 0){
			this.lineEnd = this.lineEnd + 1;
		}
	}
	
	public String toString(String ident){
		String xml  = ident + "<" + thing.getThingName();
		xml = xml + " name=\"" + thing.getMetadata().getName() + "\"";
		if(thing.getStringBlankAsNull("label") != null){
			xml = xml +	" label=\"" + thing.getMetadata().getLabel() + "\"";
		}
		for(ThingXmlAttribute attr : attributes){
			if(attr.name.equals("name") || attr.name.equals("label")){
				continue;
			}
			
			String str = attr.toString();
			if(str != null){
				if(xml.length() > 150){
					xml = xml + " more=\"...\"";
					break;
				}
				xml = xml + " " + str;
			}
			
			
		}
		
		if(childs.size() > 0){
			xml = xml + ">";
			for(ThingXmlLine line : childs){
				xml = xml + "\n" + line.toString(ident + "    ");
			}
			xml = xml + "\n" + ident + "</" + thing.getThingName() + ">";					
		}else{
			xml = xml + "/>";
		}
		
		return xml;
	}

	public Thing getThing() {
		return thing;
	}

	public int getLineStart() {
		return lineStart;
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public List<ThingXmlAttribute> getAttributes() {
		return attributes;
	}

	public List<ThingXmlLine> getChilds() {
		return childs;
	}
}
