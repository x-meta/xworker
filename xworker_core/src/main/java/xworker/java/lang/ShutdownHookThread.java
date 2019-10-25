package xworker.java.lang;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ShutdownHookThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(ShutdownHookThread.class);
	
	List<Hook> hooks = new ArrayList<Hook>();
	
	public ShutdownHookThread(String name){
		super(name);
	}
	
	public void addHook(Thing thing, ActionContext actionContext){
		hooks.add(new Hook(thing, actionContext));
	}
	
	public void run(){
		for(Hook hook : hooks){
			try{
				hook.thing.doAction("doAction", hook.actionContext);
			}catch(Exception e){
				logger.error("shutdown hook error, thing=" + hook.thing, e);
			}
		}
	}
	
	static class Hook{
		Thing thing;
		ActionContext actionContext;
		
		public Hook(Thing thing, ActionContext actionContext){
			this.thing = thing;
			this.actionContext = actionContext;
		}
	}
}
