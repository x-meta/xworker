package xworker.ide;

import org.xmeta.Thing;
import org.xmeta.World;

public class TestSwtDesign {
	public static void main(String[] args){
		try{
			World world = World.getInstance();			
			world.init("./xworker/");
			
			//启动编辑器			
			Thing thing = World.getInstance().getThing("_local.test.swt.TestShell");
			//Thing thing = World.getInstance().getThing("xworker.example.swt.xwidgets.ai.TestAlice");
			thing.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
