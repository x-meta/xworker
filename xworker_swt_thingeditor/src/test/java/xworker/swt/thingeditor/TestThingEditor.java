package xworker.swt.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class TestThingEditor {
	public static void main(String[] args) {
		try {
			World world = World.getInstance();
			world.init(".");
			
			Thing simpleEditor = world.getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor");
			simpleEditor.doAction("run", new ActionContext());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
