/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.debug;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class ActionRecorder {
	private static Logger logger = LoggerFactory.getLogger(ActionRecorder.class);
	
	/** 动作列表 */
	List<ActionRecord> records = new java.util.concurrent.CopyOnWriteArrayList<ActionRecord>();
	/** 是否停止的标志 */
	boolean stop = true;
	/** 是否正在运行 */
	boolean running = false;
	/** 监听器 */
	Thing listener = null;
	/** 监听器所使用的动作上下文 */
	ActionContext ac = new ActionContext();
	/** 调试器 */
	Debuger debuger = null;
	/** 缓存的最大记录数 */
	int maxSize  = 15000;
	/** 线程上下文，如果一个线程的记录第一个进入，那么可以分析其堆栈 */
	Map<String, String> threadContext = new HashMap<String, String>();
	
	public ActionRecorder(Debuger debuger){
		listener = World.getInstance().getThing("xworker.ide.debug.action.ActionListener");
		this.debuger = debuger;
		
		/*
		new Thread("XWorekr actionRecord"){
			public void run(){				
				while(true){
					try{
						if (records.size() == 0) {
							synchronized (records) {
								try {
									records.wait();
								} catch (InterruptedException e) {
								}
							}
						}
						
						while(records.size() > 0){
							int size = records.size();
							List<ActionRecord> tempRecords = new ArrayList<ActionRecord>();
							for(int i=0; i<size; i++){
								tempRecords.add(records.remove(0));
							}
							
							if(listener != null){
								ac.put("records", tempRecords);
								
								try{
									listener.doAction("actionExecuted", ac);
								}catch(Throwable e){
									logger.error("do action record error", e);
								}
							}
						}
						
						//考虑到动作记录可能出现递归的情况，最快每300毫秒执行一次
						Thread.sleep(300);
					}catch(Throwable t){
						logger.error("handler action record error", t);
					}	
				}					
			}
		}.start();*/
	}
	
	public void recordAction(Action action, Object caller, ActionContext context, Map<String, Object> parameters, long namoTime, boolean successed){
		if(records.size() > maxSize){
			//如果存在性能问题，放弃记录
			return;
		}
		
		if("xworker.ide.debug.context.DebugContextInstance".equals(action.getThing().getMetadata().getPath())){
			return;
		}
		
		String threadName = Thread.currentThread().getName();
		if(threadContext.get(threadName) == null){
			//获取堆栈信息
			List<Bindings> bindingsStack = context.getScopes();
			for (int i = 0; i < bindingsStack.size() - 1; i++) {
				Bindings bindings = bindingsStack.get(i);
				if(bindings.getCaller() != null){
					Object caller_ = bindings.getCaller();
					if(caller_ instanceof Thing){
						ActionRecord record = new ActionRecord((Thing) caller_, bindings.getCallerMethod(), context, parameters, namoTime, successed);
						records.add(record);
					}else if(caller_ instanceof Action){
						ActionRecord record = new ActionRecord((Action) caller_, caller_, context, bindings, namoTime, true, i);
						records.add(record);
					}else{
					}
				}else{
				}
			}
			
			threadContext.put(threadName, threadName);
		}
		
		ActionRecord record = new ActionRecord(action, caller, context, parameters, namoTime, successed);
		records.add(record);
		
		synchronized(records){
			records.notify();
		}
	}
	
	public void recordAction(Thing thing, String method, ActionContext context, Map<String, Object> parameters, long namoTime, boolean successed){
		if(records.size() > maxSize){
			//如果存在性能问题，放弃记录
			return;
		}
		if("xworker.ide.debug.context.DebugContextInstance".equals(thing.getMetadata().getPath())){
			return;
		}
		
		String threadName = Thread.currentThread().getName();
		if(threadContext.get(threadName) == null){
			//获取堆栈信息
			List<Bindings> bindingsStack = context.getScopes();
			for (int i = 0; i < bindingsStack.size() - 1; i++) {
				Bindings bindings = bindingsStack.get(i);
				if(bindings.getCaller() != null){
					Object caller_ = bindings.getCaller();
					if(caller_ instanceof Thing){
						ActionRecord record = new ActionRecord((Thing) caller_, bindings.getCallerMethod(), context, parameters, namoTime, successed);
						records.add(record);
					}else if(caller_ instanceof Action){
						ActionRecord record = new ActionRecord((Action) caller_, caller_, context, bindings, namoTime, true, i);
						records.add(record);
					}else{
					}
				}else{
				}
			}
			
			threadContext.put(threadName, threadName);
		}
		
		ActionRecord record = new ActionRecord(thing, method, context, parameters, namoTime, successed);
		records.add(record);
		
		synchronized(records){
			records.notify();
		}
	}
	
	/**
	 * 设置具有actionExecuted方法的监听事物。
	 * 
	 * @param listener
	 */
	public void setListener(Thing listener){
		this.listener = listener;
	}
	
	public Thing getListener(){
		return listener;
	}
	
	public void setListenerActionContext(ActionContext actionContext){
		this.ac = actionContext;
	}
	
	public ActionContext getListenerActionContext(){
		return ac;
	}
	
	public void stop(){
		World.getInstance().setActionListener(null);
		stop = true;
	}
	
	public boolean isStop(){
		return stop;
	}
	
	public void start(){
		records.clear();
		threadContext.clear();
		
		World.getInstance().setActionListener(debuger);
		stop = false;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public List<ActionRecord> getRecords() {
		return records;
	}

}