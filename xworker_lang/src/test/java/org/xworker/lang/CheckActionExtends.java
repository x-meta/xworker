package org.xworker.lang;

import java.io.File;

import org.xmeta.Thing;
import org.xmeta.World;

public class CheckActionExtends {
	public static void main(String args[]) {
		try {
			World world = World.getInstance();
			world.init(".");
			
			world.addFileThingManager("xworker_lang", new File("./src/main/resources"), false, true);
			Thing actions = world.getThing("xworker.lang.actions.Actions");
			Thing coreActions = world.getThing("xworker.lang.actions.CoreActions");


			for(Thing thing : actions.getChilds()) {
				Thing ext = world.getThing(thing.getString("extends"));
				if(ext == null) {
					System.out.println(thing.getMetadata().getPath() + " : " + thing.getString("extends"));
					thing.set("modifier", "private");
					coreActions.addChild(thing.detach());
				}
			}

			actions.save();
			coreActions.save();

			System.out.println(actions.getMetadata().getThingManager());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
