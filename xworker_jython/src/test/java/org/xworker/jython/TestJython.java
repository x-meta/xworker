package org.xworker.jython;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class TestJython {
	public static void main(String args[]) {
		try {
			World world = World.getInstance();
			world.init(".");
			
			String[] things = new String[] {
					"org.xworker.jython.TestJava",
					"org.xworker.jython.TestContinue",
					"org.xworker.jython.TestFunction",
					"org.xworker.jython.TestOO",
					"org.xworker.jython.TestPyFile",
			};
			
			ActionContext actionContext = new ActionContext();
			actionContext.put("name", "xworker_jython");
			for(String path : things) {
				Thing thing = world.getThing(path);
				
				System.out.println("---------------test " + path + "--------------------------");				
				thing.getAction().run(actionContext);
				System.out.println("---------------end test " + path + "--------------------------\n");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
