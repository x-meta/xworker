import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;


public class TestShell {
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init("./xworker/");
			
			Thing editor = World.getInstance().getThing("TestThingEditor");
			editor.doAction("run", new ActionContext());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
