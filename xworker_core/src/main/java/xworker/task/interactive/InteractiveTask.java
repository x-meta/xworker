package xworker.task.interactive;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 可交互的任务，是一个系统后台的任务。
 * 
 * @author zhangyuxiang
 *
 */
public class InteractiveTask {
	Thing thing;
	ActionContext actionContext;
	
	List<Parameter> parameters = new ArrayList<Parameter>();
	Parameter onException;
	Parameter onResult;
	
	/** 是否已经执行了 */
	boolean executed = false;
	Object result;
	Exception exception;
	InteractiveTask parent;	
	
	public InteractiveTask(Thing thing, ActionContext actionContext, InteractiveTask parent) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.parent = parent;
		
		
	}
	
	public void execute() {
		if(executed) {
			
		}
	}
	
	private void executeResult() {
		if(exception != null) {
			if(onException != null) {
				onException.execute();
			} else {
				edit();
			}
		} else {
			if(onResult != null) {
				onResult.execute();
			}
		}
	}
	
	/**
	 * 请求编辑当前的任务。
	 */
	public void edit() {
		
	}
}
