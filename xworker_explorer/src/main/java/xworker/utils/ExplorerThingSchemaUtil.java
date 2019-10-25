package xworker.utils;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.doc.schema.ThingDocument;

public class ExplorerThingSchemaUtil {
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init("./xworker");
			
			//生成WEB的Schema
			Thing webThing = world.getThing("xworker.http.controls.SimpleControl");
			ThingDocument tdoc = new ThingDocument(webThing);
			tdoc.write("./target/web.xsd");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
