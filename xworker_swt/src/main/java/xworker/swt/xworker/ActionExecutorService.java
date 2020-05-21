package xworker.swt.xworker;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ActionExecutorService {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//创建控件
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xworker.prototype.ActionExecutorService/@mainComposite"));
		Object result = cc.create();
		
		//保存的是ActionContainer
		ActionContainer actions = cc.getNewActionContext().getObject("actions");
		cc.getNewActionContext().g().put("thing", self);
		actionContext.g().put(self.getMetadata().getName(), actions);
		
		ActionContext ac = self.doAction("getActionContext", actionContext);
		if(ac != null) {
			actionContext.g().put("ac", ac);
		}
		
		//设置动作
		Thing action = self.doAction("getAction", actionContext);
		if(action != null) {
			actions.doAction("setAction", actionContext, "action", action);
		}
		
		return result;
	}
	
	public static void setAction(ActionContext actionContext) {
		Thing action = actionContext.getObject("action");
		actionContext.g().put("action", action);
		
		Thing thing = actionContext.getObject("thing");
		if(thing.getBoolean("execute")) {
			ActionContainer actions = actionContext.getObject("actions");
			actions.doAction("execute", actionContext);
		}		
	}
	
	public static void execute(ActionContext actionContext) {
		Thing action = actionContext.getObject("action");		
		ExecutorService executorService = actionContext.getObject("executorService");
		
		new Thread(new Runnable() {
			public void run() {
				String TAG = ActionExecutorService.class.getName();
				try {
					Executor.push(executorService);
					
					if(action == null) {
						Executor.info(TAG, "Can not execute action, the action is not setted!");
						return;
					}
					
					Executor.info(TAG, "Start execute action, path=" + action.getMetadata().getPath());
					
					//变量上下文
					ActionContext ac = actionContext.getObject("ac"); //ac在上面的create方法中设置的
					if(ac == null) {
						ac = new ActionContext();
					}
					
					Object result = action.getAction().run(ac);
					Executor.info(TAG, "Action is executed, result=" + result);
				}catch(Exception e){
					Executor.error(TAG, "execute action error");
				}finally {
					Executor.pop();
				}
			}
		}).start();
		
	}
}
