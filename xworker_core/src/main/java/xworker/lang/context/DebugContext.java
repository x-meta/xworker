package xworker.lang.context;


import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class DebugContext {
	private static Logger logger = LoggerFactory.getLogger("Debug");
	
	public static void init(ActionContext actionContext){		
		ActionContext acContext = ContextUtil.getActionContext(actionContext);
		Thing actionThing = ContextUtil.getActionThing(actionContext);
		Thing debugContextThing = World.getInstance().getThing("xworker.ide.debug.context.DebugContext/@actions/@onDoAction");
		if(debugContextThing == actionThing){
			return;
		}
		Thing self = actionContext.getObject("self");
		
		//放到变量上下文作为局部全局动作上下文
		StringBuffer msg = new StringBuffer();
		boolean have = false;
		for(Bindings bindings : acContext.getScopes()){
			if(bindings.getContextThing() == self){
				have = true;
				continue;
			}
			
			if(have && bindings.getCaller() instanceof Action){
				msg.append("    ");
				//ident = ident + "  ";
			}
		}
		if(!have){
			if(acContext.peek().getContextThing() == null){
				acContext.peek().setContextThing(self);
			}
		}
		actionContext.g().put("startTime", System.nanoTime());
		actionContext.g().put("ident", msg.toString());
		
		msg.append(actionThing.getMetadata().getLabel());
		msg.append(", action: ");
		msg.append(actionThing.getMetadata().getPath());
		
		printLog(msg.toString());
	}
	
	/**
	 * 为了在日志中对齐，使用该方法最后打印。
	 * 
	 * @param msg
	 */
	private static void printLog(String msg) {
		logger.info(msg);
	}

	public static void success(ActionContext actionContext){
		//ActionContext acContext = ContextUtil.getActionContext(actionContext);
		Thing actionThing = ContextUtil.getActionThing(actionContext);
		Thing debugContextThing = World.getInstance().getThing("xworker.ide.debug.context.DebugContext/@actions/@onDoAction");
		if(debugContextThing == actionThing){
			return;
		}
		
		String ident = actionContext.getObject("ident");
		StringBuffer msg = new StringBuffer();
		msg.append(ident);
		msg.append("/");
		msg.append(actionThing.getMetadata().getLabel());
		msg.append(", time: ");
		DecimalFormat df = new DecimalFormat("#.####");
		msg.append(df.format(getTime(actionContext)));
		//msg.append(getTime(actionContext) );
		//msg.append(c)
		//msg = msg +  + ", time: " + getTime(actionContext) 
		//		+ ", return: " + acContext.get("action-result");
		
		printLog(msg.toString());
	}
	
	public static void exception(ActionContext actionContext){
		ActionContext acContext = ContextUtil.getActionContext(actionContext);
		Thing actionThing = ContextUtil.getActionThing(actionContext);
		Thing debugContextThing = World.getInstance().getThing("xworker.ide.debug.context.DebugContext/@actions/@onDoAction");
		if(debugContextThing == actionThing){
			return;
		}
		
		String ident = actionContext.getObject("ident");
		StringBuffer msg = new StringBuffer();
		msg.append(ident);
		msg.append("/");
		msg.append(actionThing.getMetadata().getLabel());
		msg.append(", exception, time: ");
		DecimalFormat df = new DecimalFormat("#.####");
		msg.append(df.format(getTime(actionContext)));
		msg.append(", exception: ");
		msg.append(acContext.get("action-exception"));
		
		printLog(msg.toString());
	}
		
	public static void onDoAction(ActionContext actionContext){
		Thing thing = actionContext.getObject("thing");
		String actionName = actionContext.getObject("actionName");
		Thing self = actionContext.getObject("self");
		
		StringBuffer msg = new StringBuffer();		
		int index = 0;
		for(Bindings bindings : actionContext.getScopes()){
			if(bindings.getContextThing() == self){
				continue;
			}else if(bindings.getCaller() instanceof Action){
				index ++;
				if(index < 3) {
					continue;
				}
				
				msg.append("    ");
			}
		}
		
		msg.append("actionName： ");
		msg.append(actionName);
		msg.append(", thing：" );
		msg.append(thing.getMetadata().getPath());
		msg.append(",label:");
		msg.append(thing.getMetadata().getLabel());
		
		printLog(msg.toString());
	}
	
	public static double getTime(ActionContext actionContext){
		return 1d * (System.nanoTime() - actionContext.getLong("startTime")) / 1000000;
	}
}
