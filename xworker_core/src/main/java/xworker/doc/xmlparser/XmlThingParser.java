package xworker.doc.xmlparser;

import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.codes.XmlCoder;

public class XmlThingParser {
	public static void parse(String xml){
		
	}
	
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init("./xworekr");
			
			String xml = "<Shell name=";
			Thing thing = new Thing("");
			XmlCoder.parse(thing, xml);
					
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
