package xworker.lang.util;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.doc.schema.ThingDocument;

public class GenerateTestSchema {
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init(".");
			
			Thing test = world.getThing("xworker.doc.schema.test.Test");
			ThingDocument doc = new ThingDocument(false, false, test);
			doc.write("./src/test/java/test.xsd");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
