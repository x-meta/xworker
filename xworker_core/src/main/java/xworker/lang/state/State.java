package xworker.lang.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.UtilData;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.task.TaskManager;

public class State extends StateEvent{
	private static final String TAG = State.class.getName();
	
	ThingEntry thingEntry;
	ActionContext actionContext;
	//Map<String, Object> datas = new HashMap<String, Object>();
	State parent;
	State child;
	ScheduledFuture<?> taskFuture;
	boolean running = false;
	boolean pause = false;
	ExecutorService executorService;
	Object result;	
	
	public State(Thing thing, ActionContext actionContext) {
		this.thingEntry = new ThingEntry(thing);
		this.actionContext = new ActionContext();
		this.actionContext.put("parentContext", actionContext);
		
		Map<String, Object> variables = thing.doAction("getVariables", actionContext, "state", this);
		if(variables != null) {
			actionContext.g().putAll(variables);
		}
		
		thing.doAction("onInit", actionContext, "state", this);
	}
	
	public Thing getThing() {
		Thing thing = thingEntry.getThing();
		if(thing == null) {
			exit();
		}
		
		return thing;
	}
	
	/**
	 * 使用新的State替换自己，如果自己是根节点，那么执行push操作。
	 * 
	 * @param stateThing
	 */
	public void replace(Thing stateThing) {		
		//进入新的状态
		if(stateThing == null) {
			throw new ActionException("Can not enter new state, state thing is null, path=" + thingEntry.getPath());
		}
		
		this.pause = true;
		State state = new State(stateThing, actionContext);
		if(this.parent == null) {
			state.parent = this;
			this.child = state;
			child.run();
		} else {
			this.doit = false;
			this.parent.child = state;
			child.run();
		}
	}
	
	/**
	 * 执行一个新的State， 新的State作为自己子，子执行完毕后理论上还会继续执行当前State。
	 * 
	 * @param stateThing
	 */
	public void push(Thing stateThing) {
		//进入新的状态
		if(stateThing == null) {
			throw new ActionException("Can not push new state, state thing is null, path=" + thingEntry.getPath());
		}
		
		this.pause = true;
		State state = new State(stateThing, actionContext);
		state.parent = this;
		this.child = state;
		child.run();		
	}
	
	public void exit() {
		Executor.info(TAG, "Exitting state, path=" + thingEntry.getPath());
		StateManager.unregist(State.this);
		this.running = false;
		if(taskFuture != null && taskFuture.isDone() == false) {
			taskFuture.cancel(false);			
			Executor.info(TAG, "State task has finished, path=" + thingEntry.getPath());
		}
		
		if(parent != null) {
			parent.set(getThing().getMetadata().getName(), result);
			parent.setPause(false);
		}
		
		getThing().doAction("onExit", actionContext, "state", this);
	}
	
	public void runAsTread() {
		Executor.info(TAG, "Start state as thread, path=" + thingEntry.getPath());
		if(running) {
			Executor.info(TAG, "State is running, do nothing, path=" + thingEntry.getPath());
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				StateManager.regist(State.this);
				try {
					Executor.info(TAG, "State thread started, path=" + thingEntry.getPath());
					
					State.this.run();
					
					Executor.info(TAG, "State thread finished, path=" + thingEntry.getPath());
				}finally {
					StateManager.unregist(State.this);
				}
			}
		}, getThing().getMetadata().getLabel()).start();
	}
	
	public void runAsTask() {
		Executor.info(TAG, "Start state as task, path=" + thingEntry.getPath());
		if(running) {
			Executor.info(TAG, "State is running, do nothing, path=" + thingEntry.getPath());
			return;
		}
		
		if(taskFuture != null && taskFuture.isDone() == false) {
			Executor.info(TAG, "State task is running, do nothing, path=" + thingEntry.getPath());
			//避免重复执行
			return;
		}
		
		StateManager.regist(State.this);
		doit = true;
		running = true;
		Long interval = getThing().doAction("getInterval", actionContext);
		if(interval == null) {
			interval = 200l;
		}
		taskFuture = TaskManager.getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {
			public void run() {
				if(pause) {
					return;
				}
				
				if(running == false) {
					State.this.taskFuture.cancel(false);
					Executor.info(TAG, "State task has finished, path=" + thingEntry.getPath());					
					return;
				}
				
				doit = true;
				Bindings bindings = actionContext.push();
				ExecutorService es = State.this.getExecutorService();
				try {
					if(es != null) {
						Executor.push(es);
					}
					bindings.put("state", State.this);
					for(Thing child : getThing().getChilds()) {
						try {
							result = child.doAction("exec", actionContext);													
						}catch(Exception e) {
							result = e;
							Executor.info(TAG, "Exec exception, path=" + child.getMetadata().getPath(), e);
						}
						actionContext.g().put(child.getMetadata().getName(), result);
						if(doit == false) {
							break;
						}
					}
				}finally {
					if(es != null) {
						Executor.pop();
					}
					actionContext.pop();
				}
			}
		}, 0, interval, TimeUnit.MILLISECONDS);		
	}
	
	private void run() {
		if(running) {
			return;
		}
		
		running = true;
		this.doit = true;
	
		while(true) {
			Bindings bindings = actionContext.push();
			ExecutorService es = this.getExecutorService();
			try {
				if(es != null) {
					Executor.push(es);
				}
				bindings.put("state", this);
				if(!pause) {
					doit = true;
					for(Thing child : getThing().getChilds()) {
						try {
							result = child.doAction("exec", actionContext);													
						}catch(Exception e) {
							result = e;
							Executor.info(TAG, "Exec exception, path=" + child.getMetadata().getPath(), e);
						}
						actionContext.g().put(child.getMetadata().getName(), result);
						if(doit == false) {
							break;
						}
					}

					if(running == false) {
						break;
					}
				}
								
				//休眠
				Long interval = getThing().doAction("getInterval", actionContext);
				if(interval == null) {
					interval = 200l;
				}
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}finally {
				running = false;
				if(es != null) {
					Executor.pop();
				}
				actionContext.pop();
			}
		}
	}
	
	/**
	 * 判断一个保存在State中的属性是否为真，如果属性无法判定为布尔类型，那么非null也会判断为真。
	 * 
	 * @param name
	 * @return
	 */
	public boolean is(String name) {
		return UtilData.isTrue(get(name));
	}
	
	/**
	 * 调用is(name)方法，返回相反的结果。
	 * 
	 * @param name
	 * @return
	 */
	public boolean not(String name) {
		return is(name) ? false : true;
	}
	
	/**
	 * 判断一个变量是否存在。
	 * 
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		if(actionContext.g().containsKey(name)) {
			return true;
		}
		
		if(parent != null) {
			return parent.exists(name);
		}
		
		return false;
	}
	
	/**
	 * 获取一个已保存的值，如果当前State没有保存该值，尝试从父State上获取。
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		Object value = actionContext.g().get(name);
		
		if(value == null && !actionContext.g().containsKey(name)) {
			if(parent != null) {
				value = parent.get(name);
			}
		}
		
		return (T) value;
	}
	
	/**
	 * 设置一个值到State，优先保存到父State上。
	 * 
	 * @param name
	 * @param value
	 */
	public void set(String name, Object value) {
		actionContext.g().put(name, value);
	}
	
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		State state = new State(self, actionContext);
		state.runAsTask();
	}
	
	public static void exec(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State parent = actionContext.getObject("state"); 
		
		if(parent == null) {
			throw new ActionException("Can not exec state, parent state is null, path=" + self.getMetadata().getPath());
		}
		
		Thing stateThing = self.doAction("getRef", actionContext);
		if(stateThing == null) {
			stateThing = self;
		}
		String enterType = self.doAction("getEnterType", actionContext);
		if("push".equals(enterType)) {
			parent.push(stateThing);
		}else {
			parent.replace(stateThing);
		}
	}
	
	public State getParent() {
		return parent;
	}
	
	public State getRoot() {
		if(parent == null) {
			return this;
		}else {
			return parent.getRoot();
		}
	}
	
	public State getChild() {
		return child;
	}

	/**
	 * 总是设置到根节点上。
	 * 
	 * @param executorService
	 */
	public void setExecutorService(ExecutorService executorService) {
		getRoot().executorService = executorService;
	}

	/**
	 * 从根节点上获取。
	 * 
	 * @return
	 */
	public ExecutorService getExecutorService() {
		return getRoot().executorService;
	}

	public Object getResult() {
		return result;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Map<String, Object> getDatas() {
		return actionContext.g();
	}

	public ScheduledFuture<?> getTaskFuture() {
		return taskFuture;
	}

	public boolean isRunning() {
		return running;
	}
	
}
