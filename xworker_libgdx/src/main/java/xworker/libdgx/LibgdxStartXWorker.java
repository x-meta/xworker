package xworker.libdgx;

import org.xmeta.Thing;
import org.xmeta.World;

public class LibgdxStartXWorker {
	public static void main(String[] args){
		try{
			World world = World.getInstance();			
			world.init("E:\\git\\xworker\\xworker\\xworker");

			//启动编辑器
			Thing worldExplorer = World.getInstance().getThing("xworker.ide.worldExplorer.swt.SimpleExplorerRunner");			
			worldExplorer.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
