package xworker.groovy.test;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class GroovyTest {
	public static void main(String[] args) {
		try {
			World world = World.getInstance();
			world.init(".");
			
			Thing test = world.getThing("xworker.groovy.test.GroovyTest");
			test.getAction().run(new ActionContext());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
