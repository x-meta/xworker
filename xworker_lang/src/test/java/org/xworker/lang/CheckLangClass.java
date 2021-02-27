package org.xworker.lang;

import java.io.File;
import java.util.Iterator;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

/**
 * xworker_lang是从xworker_core中独立的，检查模型所使用的java都已拷入本项目。
 * @author zhangyuxiang
 *
 */
public class CheckLangClass {
	public static void checkThing(Thing thing) {
		if("JavaAction".equals(thing.getThingName())) {
			String clsName = thing.getStringBlankAsNull("outerClassName");
			if(clsName != null) {
				try {
					Class.forName(clsName);
				}catch(Exception e) {
					System.out.println(clsName + "  :  " + thing.getMetadata().getPath());
				}				
			}
		}
		
		for(Thing child : thing.getChilds()) {
			checkThing(child);
		}
	}
	
	public static void main(String[] args) {
		try {
			World world = World.getInstance();
			world.init(".");
			
			world.addFileThingManager("xworker_lang", new File("./src/main/resources"), false, true);
			ThingManager tm = world.getThingManager("xworker_lang");
			
			Iterator<Thing> iter = tm.iterator(null, true); 
			while(iter.hasNext()) {
				Thing thing = iter.next();
				checkThing(thing);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
