package xworker.ide;

import org.xmeta.Thing;
import org.xmeta.World;

public class TestSimpleThingEditor {
	public static void main(String args[]){
		try{
			//读取所有的事物，看看内存占用
			World world = World.getInstance();
			world.init("xworker");
			
			Thing editor = world.getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor");
			editor.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
