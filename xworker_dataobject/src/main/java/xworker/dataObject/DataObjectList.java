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
package xworker.dataObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.xmeta.*;
import xworker.dataObject.query.Condition;
import xworker.lang.executor.Executor;
import xworker.task.TaskManager;

/**
 * 数据对象列表。
 * 
 * @author zhangyuxiang
 *
 */
public class DataObjectList extends CopyOnWriteArrayList<DataObject>{
	private static final String TAG = DataObjectList.class.getName();
	private static final long serialVersionUID = 1L;
	
	/** 描述者 */
	Thing descriptor = null;
	/** 父数据对象 */
	DataObject parent = null;
	/** 是否已初始化 */
	boolean inited = false;
	/** 初始化时的动作上下文 */
	ActionContext actionContext;
	/** 监听器列表 */
	List<DataObjectListListener> listeners = null;
	boolean fireEvent = true;
	Condition condition;
	String refAttributeName;
	String localAttributeName;

	public DataObjectList() {		
	}
	
	/**
	 * 构造函数。
	 * 
	 * @param descriptor
	 */
	public DataObjectList(String descriptor){
		this.descriptor = World.getInstance().getThing(descriptor);
		this.parent = null;
	}
	
	/**
	 * 构造函数。
	 * 
	 * @param descriptor
	 */
	public DataObjectList(Thing descriptor){
		this(descriptor, null);
	}
	
	/**
	 * 构造函数。
	 * 
	 * @param descriptor 描述者
	 * @param parent 父
	 */
	public DataObjectList(Thing descriptor, DataObject parent){
		this.descriptor = descriptor;
		this.parent = parent;
		if(parent != null) {
			World world = World.getInstance();
			this.descriptor = world.getThing(descriptor.getString("dataObjectPath"));
			refAttributeName = descriptor.getString("refAttributeName");
			localAttributeName = descriptor.getString("localAttributeName");
		}
	}
	
	/**
	 * 开始批量修改，执行后对List的修改不会触发事件。
	 */
	public void begin() {
		this.fireEvent = false;
	}
	
	/**
	 * 批量修改结束，同时会触发onLoaded()事件。
	 */
	public void finish() {
		this.fireEvent = true;
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onLoaded(this);
			}
		}
	}
	
	public void addListener(DataObjectListListener listener) {
		if(listeners == null) {
			listeners = new CopyOnWriteArrayList<DataObjectListListener>();
		}
		
		if(listeners.contains(listener) == false) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(DataObjectListListener listener) {
		if(listeners != null) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * 装载数据。
	 * 
	 * @param actionContext 变量上下文
	 */
	@SuppressWarnings("unchecked")
	public void load(ActionContext actionContext){
		if(descriptor == null) {
			throw new ActionException("Descriptor is null");
		}
		
		inited = true;

		Thing condition = null;
		Map<String, Object> conditionData = null;
		if(parent != null){
			Condition con = new Condition();
			con.eq(refAttributeName, parent.get(localAttributeName));
			condition = con.getConditionThing();
			conditionData = con.getConditionValues();
		}else {
			conditionData = new HashMap<>();
			Thing con = descriptor.getThing("Condition@0");
			if(con != null){
				condition = con;
			}
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conditionConfig", condition);
		params.put("conditionData", conditionData);
		List<DataObject> datas = (List<DataObject>) doAction("query", actionContext, params);
		setDataObjects(datas);
	}

	/**
	 * 删除全部关联。
	 *
	 * @param actionContext
	 */
	public Integer delete(ActionContext actionContext){
		if(descriptor == null) {
			throw new ActionException("Descriptor is null");
		}

		inited = true;

		Thing condition = null;
		Map<String, Object> conditionData = null;
		if(parent != null){
			Condition con = new Condition();
			con.eq(refAttributeName, parent.get(localAttributeName));
			condition = con.getConditionThing();
			conditionData = con.getConditionValues();
		}else {
			conditionData = new HashMap<>();
			Thing con = descriptor.getThing("Condition@0");
			if(con != null){
				condition = con;
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conditionConfig", condition);
		params.put("conditionData", conditionData);
		return (Integer) doAction("deleteBatch", actionContext, params);
	}

	/**
	 * 后台加载。
	 *
	 * @param actionContext
	 */
	public void loadBackground(final ActionContext actionContext){
		TaskManager.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				try{
					synchronized (DataObjectList.this) {
						if (DataObjectList.this.inited) {
							return;
						}else{
							//这里设置inited不要取消，是为了同步
							DataObjectList.this.inited = true;
						}
					}
					load(actionContext);
				}catch(Exception e){
					Executor.warn(TAG, "Load background error, path=" + descriptor, e);
				}
			}
		});
	}

	/**
	 * 设置新的数据对象列表，并会触发onLoaded()事件。
	 * 
	 * @param datas
	 */
	public void setDataObjects(List<DataObject> datas) {
		clear();
		
		if(datas != null){
			this.addAll(datas);
		}
		
		if(listeners != null && fireEvent) {
			for(DataObjectListListener listener : listeners) {
				listener.onLoaded(this);
			}
		}
	}
	
	/**
	 * 执行动作。
	 * 
	 * @param name
	 * @return
	 */
	public Object doAction(String name){
		return doAction(name, null, null);
	}
	
	/**
	 * 执行动作。
	 * 
	 * @param name
	 * @param actionContext
	 * @return
	 */
	public Object doAction(String name, ActionContext actionContext){
		return doAction(name, actionContext, null);
	}
	
	/**
	 * 执行动作。
	 * 
	 * @param name
	 * @param actionContext
	 * @param params
	 * @return
	 */
	public Object doAction(String name, ActionContext actionContext, Map<String, Object> params){
		//Thing thing = World.getInstance().getThing(descriptor.getString("dataObjectPath"));
		
		if(actionContext == null){
			actionContext = new ActionContext();
		}
		
		Bindings bindings = actionContext.push(null);
		try{
			bindings.setCaller(this, name);
			bindings.put("theData", this);//new DataObject(thing));
			return descriptor.doAction(name, actionContext, params);
		}finally{
			actionContext.pop();
		}
	}
	
	
	public Thing getDescriptor() {
		return descriptor;
	}
	
	public void setDescriptor(Thing descriptor) {
		this.descriptor = descriptor;
		
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onReconfig(this);
			}
		}
	}

	public DataObject getParent() {
		return parent;
	}
	
	/**
	 * 根据主键返回符合条件的数据对象，如果没有返回null。
	 * 
	 * keys是name1, value1, name2, value2...成对出现的。name是主键的名字，value是主键的值。
	 * 
	 * @param keys
	 * @return
	 */
	public DataObject get(Object ... keys) {
		for(int i=0; i<this.size(); i++) {
			DataObject data = this.get(i);
			if(data.equals(keys)) {
				return data;
			}
		}
		
		return null;
	}

	public List<DataObjectListListener> getListeners(){
		if(listeners == null) {
			return Collections.emptyList();
		}
		
		return listeners;
	}
	
	public boolean isInited() {
		return inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	@Override
	public DataObject set(int index, DataObject newDataObject) {
		DataObject oldDataObject = super.set(index, newDataObject);
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onSeted(this, index, newDataObject, oldDataObject);
			}
		}
		return oldDataObject;
	}

	@Override
	public boolean add(DataObject e) {
		boolean result = super.add(e);
		if(listeners != null && result && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onAdded(this, e);
			}
		}
		return result;
	}

	@Override
	public void add(int index, DataObject element) {
		super.add(index, element);
		
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onAdded(this, index, element);
			}
		}
	}

	@Override
	public DataObject remove(int index) {
		DataObject dataObject = super.remove(index);
		
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onRemoved(this, index, dataObject);
			}
		}
		
		return dataObject;
	}

	@Override
	public boolean remove(Object o) {
		int index = -1;
		for(int i =0; i<super.size(); i++) {
			if(o == super.get(i)) {
				index = i;
				break;
			}
		}
		
		boolean r = false;
		if(index != -1) {
			super.remove(index);
			r = true;
		}
		
		if(listeners != null && r && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onRemoved(this, index, (DataObject) o);
			}
		}
		
		return r;
	}
	
	@Override
	public boolean addAll(Collection<? extends DataObject> c) {
		int index = super.size();
		boolean r = super.addAll(c);
	
		if(listeners != null && r && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onAddedAll(this, index, c);
			}
		}
		
		return r;
	}

	@Override
	public boolean addAll(int index, Collection<? extends DataObject> c) {
		boolean r = super.addAll(index, c);
		
		if(listeners != null && r && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onAddedAll(this, index, c);
			}
		}
		
		return r;
	}

	/*
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		int index = fromIndex;
		List<DataObject> list = super.subList(fromIndex, toIndex);
		super.removeRange(fromIndex, toIndex);
		
		if(listeners != null && fireEvent) {
			for(DataObject dataObject : list) {				
				for(int i=0; i<listeners.size(); i++) {
					DataObjectListListener listener = listeners.get(i);
					listener.onRemoved(this, index, dataObject);
				}
				index++;
			}
		}
	}*/
	
	@Override
	public void clear() {
		super.clear();
		
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onLoaded(this);
			}
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result =  super.removeAll(c);
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onLoaded(this);
			}
		}
		
		return result;
	}

	@Override
	public boolean removeIf(Predicate<? super DataObject> filter) {
		boolean result = super.removeIf(filter);
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onLoaded(this);
			}
		}
		return result;
	}

	@Override
	public void replaceAll(UnaryOperator<DataObject> operator) {
		super.replaceAll(operator);
		
		if(listeners != null && fireEvent) {
			for(int i=0; i<listeners.size(); i++) {
				DataObjectListListener listener = listeners.get(i);
				listener.onLoaded(this);
			}
		}
	}
	
	
}