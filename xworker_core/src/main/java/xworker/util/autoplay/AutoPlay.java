package xworker.util.autoplay;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AutoPlay extends AutoPlayNodeGroup implements Runnable{

	public AutoPlay(Thing thing, ActionContext actionContext) {
		super(null, thing, actionContext);
	}
	
	public void run() {
		run(false);
	}
	
	/**
	 * 启动线程执行。
	 */
	public void start() {
		new Thread(this).start();
	}
	
	public static AutoPlay create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		AutoPlay autoPlay = new AutoPlay(self, actionContext);
		return autoPlay;
	}
	
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		AutoPlay autoPlay = new AutoPlay(self, actionContext);
		autoPlay.run(false);
	}
}
