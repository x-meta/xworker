package xworker.lang.actions;

import java.io.InputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

/**
 * 进程控制台，目前是简单的打印输出留信息。
 * 
 * @author Administrator
 *
 */
public class ProcessConsole {
	//Process process;	
	
	public static void startPrintThread(Thing thing, ActionContext actionContext, Process process){
		//this.process = process;
		String console = thing.doAction("getConsole", actionContext);
		if("shell".equals(console) && XWorkerUtils.getIDEShell() != null){
			ActionContext ac = new ActionContext();
			ac.put("shell", XWorkerUtils.getIDEShell());
			ac.put("process", process);
			ac.put("thing", thing);
			
			Thing shellThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ProcessConsoleIns");
			shellThing.doAction("run", ac);
		}else{
			new Thread(new PrintInputstreamRunnable(process.getErrorStream())).start();
			new Thread(new PrintInputstreamRunnable(process.getInputStream())).start();
		}
	}
	
	static class PrintInputstreamRunnable implements Runnable{
		InputStream in;
		
		public PrintInputstreamRunnable(InputStream in){
			this.in = in;
		}
		
		public void run(){
			try{
				byte[] bytes = new byte[2048];
				int length = -1;
				while((length = in.read(bytes)) != -1){
					System.out.print(new String(bytes, 0, length));
				}
			}catch(Exception e){				
			}
		}
	}
}
