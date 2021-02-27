package org.xworker.jython;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class TestPerformance {
	public static void main(String args[]) {
		try {
			World world = World.getInstance();
			world.init(".");
			
			ActionContext actionContext = new ActionContext();
			Thing thing = world.getThing("org.xworker.jython.TestPerformance");
			
			long start = System.currentTimeMillis();
			thing.getAction().run(actionContext, "x", 0);
			System.out.println("First execute time : " + (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			
			int count = 1000000;
			int x = 0;
			for(int i = 0; i<count; i++) {
				x = thing.getAction().run(actionContext, "x", x);
			}
			System.out.println("Eexecute " + count + " time : " + (System.currentTimeMillis() - start));
			System.out.println("x=" + x);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
