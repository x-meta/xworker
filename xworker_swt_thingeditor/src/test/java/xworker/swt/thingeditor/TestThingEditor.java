package xworker.swt.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.ide.SimpleThingEditor;
import xworker.swt.SwtThingEditor;

public class TestThingEditor {
	public static void main(String[] args) {
		try {
			World world = World.getInstance();
			world.init("./xworker/");

			SwtThingEditor.run();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
