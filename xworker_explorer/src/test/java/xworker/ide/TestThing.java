package xworker.ide;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.debug.Debuger;

public class TestThing {
	
	public static void run(ActionContext actionContext){
		System.out.println();		
	}
	
	public static void main(String[] args){
		try{
			World world = World.getInstance();			
			world.init("./xworker/");
			
			Debuger.getDebuger().setEnabled(true);
			
			//启动编辑器			
			Thing thing = World.getInstance().getThing("_local.test.web.extjs.TestDataObjectGrid/@view/@ExtJs/@DataObjectGridPanel/@dataObjects/@CurrencyCode");
			//Thing thing = World.getInstance().getThing("xworker.example.swt.xwidgets.ai.TestAlice");
			ActionContext actionContext = new ActionContext();
			//actionContext.peek().setContextThing(world.getThing("xworker.ide.debug.context.TraceContext"));
			System.out.println((Object) thing.doAction("query", actionContext));
			
			System.out.println("system end");
			ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
			while (root.getParent() != null) {
			    root = root.getParent();
			}
			
			printThread(root, "");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void printThread(ThreadGroup group, String ident){
		ThreadGroup tg[] = new ThreadGroup[1000];
		Thread[] ts = new Thread[1000];
		
		group.enumerate(tg);
		group.enumerate(ts);
		
		for(int i=0; i<ts.length; i++){
			if(ts[i] == null){
				break;
			}
			
			System.out.println(ident + ts[i].isDaemon() + ":" + ts[i].getName());
		}
		
		for(int i=0; i<tg.length; i++){
			if(tg[i] == null){
				break;
			}
			
			System.out.println(ident + tg[i].isDaemon() + ":" + tg[i].getName());
			printThread(tg[i], ident + "    ");
		}
	}
}
