package xworker.swt.xwidgets;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ActionExecutorService {
	ExecutorService executorService;
	
	public ActionExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public Object runAction(Action action, ActionContext actionContext) {
		Executor.push(executorService);
		try {
			return action.run(actionContext);
		}catch(Throwable e) {
			Executor.warn(ActionExecutorService.class.getSimpleName(), ExceptionUtil.toString(e));
			return null;
		}finally {
			Executor.pop();
		}
	}
	
	public Object runAction(Action action, ActionContext actionContext, Object ... params) {
		Executor.push(executorService);
		try {
			return action.run(actionContext, params);
		}catch(Throwable e) {
			Executor.warn(ActionExecutorService.class.getSimpleName(), ExceptionUtil.toString(e));
			return null;
		}finally {
			Executor.pop();
		}
	}
	
	public Object runAction(Thing actionThing, ActionContext actionContext) {
		return runAction(actionThing.getAction(), actionContext);
	}
	
	public Object runAction(Thing actionThing, ActionContext actionContext, Object ... params) {
		return runAction(actionThing.getAction(), actionContext, params);
	}
	
	public Object doAction(Thing thing, String actionName, ActionContext actionContext) {
		Executor.push(executorService);
		try {
			return thing.doAction(actionName, actionContext);
		}catch(Throwable e) {
			Executor.warn(ActionExecutorService.class.getSimpleName(), ExceptionUtil.toString(e));
			return null;
		}finally {
			Executor.pop();
		}
	}
	
	public Object doAction(Thing thing, String actionName, ActionContext actionContext, Object ... params) {
		Executor.push(executorService);
		try {
			return thing.doAction(actionName, actionContext, params);
		}catch(Throwable e) {
			Executor.warn(ActionExecutorService.class.getSimpleName(), ExceptionUtil.toString(e));
			return null;
		}finally {
			Executor.pop();
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ActionExecuteServiceShell/@executorService"));
		
		Object result = cc.create();
		ExecutorService executorService = cc.getNewActionContext().getObject("executorService");
		
		actionContext.g().put(self.getMetadata().getName(), new ActionExecutorService(executorService));
		
		return result;
	}
}
