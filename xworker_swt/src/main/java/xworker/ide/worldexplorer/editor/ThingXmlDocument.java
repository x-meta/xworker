package xworker.ide.worldexplorer.editor;

import org.xmeta.Thing;

/**
 * 把事物转化为
 * 
 * @author Administrator
 *
 */
public class ThingXmlDocument {
	ThingXmlLine root;
	Thing thing;
	
	public ThingXmlDocument(Thing thing){
		setThing(thing);
	}
	
	public void setThing(Thing thing){
		this.thing = thing;
		
		root = new ThingXmlLine(thing, 0);
	}
	
	public Thing getThingAtLine(int line){
		ThingXmlLine l = root.getAtLine(line);
		if(l != null){
			return l.thing;
		}else{
			return null;
		}
	}
	
	public ThingXmlLine getLineByThing(Thing athing){
		ThingXmlLine l = root.getByThing(athing);
		if(l != null){
			return l;
		}else{
			return null;
		}
	}
	
	public String toString(){
		return root.toString("");
	}
	
	public String getXml(){
		return root.toString("");
	}
}
