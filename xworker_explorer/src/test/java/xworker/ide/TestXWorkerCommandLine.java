package xworker.ide;

import org.xmeta.Thing;
import org.xmeta.World;

public class TestXWorkerCommandLine {
	public static void main(String args[]){
		try{
			//读取所有的事物，看看内存占用
			World world = World.getInstance();
			world.init("xworker");
			
			Thing xworker = world.getThing("XWorker");
			xworker.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
