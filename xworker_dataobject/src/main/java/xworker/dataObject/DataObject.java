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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import xworker.dataObject.cache.DataObjectCache;
import xworker.dataObject.query.Condition;
import xworker.dataObject.query.UtilCondition;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.executor.Executor;
import xworker.task.TaskManager;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;

/**
 * <p>数据存储是一种关系模型，一个数据对象是一个二维表，数据对象可以关联不同种类的数据，如数据库数据对象关联CSV数据对象。</p>
 *
 * <p>数据对象可以映射数据，实现CURD操作，数据对象也包含界面相关的数据，可以用于快速生成界面。</p>
 *
 * <p>数据对象是一个Map，用于存放key-value键值对，MetaData保存了关键字、属性定义和脏字段信息等。</p>
 * 
 * <p>数据对象的定义是模型，数据对象一般在模型里调用。</p>
 * 
 * <p>数据对象如果已初始化setInited(true)，那么会记录脏字段信息，否则不会记录。</p>
 * 
 * 数据对象的标志(flag)表示数据的存储情况，FLAG_STORED=1表示已存储，FLAG_NEW新数据，FLAG_DELETED已删除。
 * 数据对象的标志设置为FLAG_STORED和FLAG_DELETED并不表示数据在真实的存储中的状态，但会影响当前数据对象的操作，
 * 当数据对象commit()时，如果是新的或已删除，那么会调用create或delete方法。
 * 
 * @author zhangyuxiang
 * 
 */
public class DataObject extends HashMap<String, Object> {
	private static final String TAG = DataObject.class.getName();
	private static final long serialVersionUID = 1L;

	/** 标志 - 已保存 */
	public static final int FLAG_STORED = 1;
	/** 标志 - 新数据 */
	public static final int FLAG_NEW = 2;
	/** 标志 - 已删除 */
	public static final int FLAG_DELETED = 3;

	/** 数据对象特殊的属性，如果值为true，那么在某些控件中会有选中的状态 */
	public static final String CHECKED_ATTRIBUTE_NAME = "__dataobject__checked__";
	
	/** 用户可跟踪的任务状态，由具体数据对象调用和实现 */
	private static final ThreadLocal<UserTask> userTasks =new ThreadLocal<UserTask>(); 
			
	/**
	 * 元数据。
	 */
	private DataObjectMetadata metadata = null;

	private int flag = FLAG_STORED;

	/** 和SWT控件setData和getData一样，用于保存第三方的数据 */
	private Map<String, Object> datas = new HashMap<String, Object>();
	
	/**
	 * 数据对象监听器，用来监听数据对象的变更等。比如UI联动。
	 */
	private List<DataObjectListener> listeners = null;
	
	/**
	 * WRAP的Java对象。
	 */
	private Object wrappedObject = null;
	
	/**
	 * 使用事物定义的数据对象时包装的源Java对象。
	 */
	private Object source = null;
	
	/**
	 * 修改栈。
	 */
	private ThreadLocal<Stack<Object>> modifiyStack = new ThreadLocal<Stack<Object>>();
	
	/**
	 * 无描述者的构造函数，这样构造出来的数据对象只能当作临时数据(Map)用。
	 */
	public DataObject() {
	}

	/**
	 * 包装Java对象的模式，取值和赋值都通过Ognl的表达式来完成。
	 */
	public DataObject(Object wrappedObject) {
		this.wrappedObject = wrappedObject;
	}

	
	/**
	 * 使用描述者的路径构造一个数据对象。
	 * 
	 * @param descriptorPath
	 */
	public DataObject(String descriptorPath) {
		Thing descriptor = World.getInstance().getThing(descriptorPath);
		if(descriptor == null){
			throw new ActionException("Descriptor is null, path=" + descriptorPath);
		}
				
		metadata = new DataObjectMetadata(descriptor, this);
	}

	/**
	 * 构造函数。
	 * 
	 * @param descriptor 描述者
	 */
	public DataObject(Thing descriptor) {
		metadata = new DataObjectMetadata(descriptor, this);
	}

	/**
	 * 返回第一个主键的值。
	 * @return
	 */
	public Object getKeyValue(){
		Object[][] keyDatas = getKeyAndDatas();
		if(keyDatas != null && keyDatas.length > 0){
			return keyDatas[0][1];
		}

		return null;
	}

	/**
	 * 设置第一个主键的值。
	 *
	 * @param value
	 */
	public void setKeyValue(Object value){
		Thing[] keys = getMetadata().getKeys();
		if(keys != null  && keys.length > 0){
			this.put(keys[0].getMetadata().getName(), value);
		}
	}

	public void addListener(DataObjectListener listener) {
		if(listener == null) {
			return;
		}
		
		synchronized(this) {
			if(listeners == null) {
				listeners = new CopyOnWriteArrayList<DataObjectListener>();				
			}
			if(!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	public void removeListener(DataObjectListener listener) {
		if(listeners != null){
			listeners.remove(listener);
		}
	}
	
	public List<DataObjectListener> getListeners(){
		if(listeners == null) {
			return Collections.emptyList();
		}
		
		return listeners;
	}
	
	/**
	 * 设置多个属性。
	 * 
	 * attributs字符串的格式：
	 * name=value的格式，默认一行设置一个属性
	 * #或//是注释
	 * 三个连续的引号（""")是具有换行的文本，结尾也是三个连续的引号
	 * 支持var:、ognl:等表达式
	 * 
	 * @param attributes
	 * @param actionContext
	 */
	public void setAttributes(String attributes, ActionContext actionContext) {
		if(attributes == null || "".equals(attributes)) {
			return;
		}
		
		begin();
		try {
			String[] lines = attributes.split("[\n]");
			for(int i=0; i<lines.length; i++) {
				String line = lines[i];
				if(line.startsWith("#") || line.startsWith("//")) {
					//注释
					continue;
				}
				
				int index = line.indexOf("=");
				if(index == -1) {
					//没有设置属性
					continue;
				}			
				String name = line.substring(0, index).trim();
				String value = line.substring(index + 1, line.length()).trim();
				if(value.startsWith("\"\"\"")){
					//多行文本
					value = value.substring(3, value.length());
					if(value.endsWith("\"\"\"")) {
						//单行文本，就结束了
						value = value.substring(0, value.length() - 3);
					} else {
						//是多行文本
						for(int n = i + 1; n < lines.length; n++) {
							String strLine = lines[n];
							int endIndex = strLine.indexOf("\"\"\"");
							if(endIndex != -1) {
								value = value + "\n" + strLine.substring(0, endIndex);
								i = n;
								break;
							}else {
								value = value + "\n" + strLine; 
							}
						}
					}
				}
				
				Object v = value;
				if(value.indexOf("\n") == -1 && value.indexOf(":") != -1) {
					//有可能是var: ognl: 等表达式
					v = UtilData.getData(value, actionContext);
				}
				this.put(name, v);
			}
		}finally {
			finish();
		}
		
	}
	
	@Override
	public Object put(String key, Object value) {
		return put(key, value, true);
	}
	
	public Object set(String key, Object value) {
		return put(key, value, true);
	}

	public String getString(String key){
		return UtilData.getString(get(key), null);
	}
	
	public byte getByte(String key){
		return UtilData.getByte(get(key), (byte) 0);
	}
	
	public short getShort(String key){
		return UtilData.getShort(get(key), (short) 0);
	}
	
	public int getInt(String key){
		return UtilData.getInt(get(key), 0);
	}
	
	public long getLong(String key){
		return UtilData.getLong(get(key), 0);
	}
	
	public boolean getBoolean(String key){
		return UtilData.getBoolean(get(key), false);
	}
	
	public byte[] getBytes(String key){
		return UtilData.getBytes(get(key), null);
	}
	
	public Date getDate(String key){
		return UtilData.getDate(get(key), null);
	}
	
	public double getDouble(String key){
		return UtilData.getDouble(get(key), 0);
	}
	
	public float getFloat(String key){
		return UtilData.getFloat(get(key), 0);
	}
	
	
	public BigDecimal getBigDecimal(String key){
		return UtilData.getBigDecimal(get(key), null);
	}
	
	public BigInteger getBigInteger(String key){
		return UtilData.getBigInteger(get(key), null);
	}
	
	/**
	 * 一般为可以展示并可以选择数据对象列表的界面使用。
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return getBoolean(DataObject.CHECKED_ATTRIBUTE_NAME);
	}
	
	/**
	 * 一般为可以展示并可以选择数据对象列表的界面使用。
	 */
	public void setChecked(boolean checked) {
		this.set(DataObject.CHECKED_ATTRIBUTE_NAME, checked);
	}
	
	/**
	 * 设置数据对象的键值。
	 * 
	 * @param key 键
	 * @param value 值
	 * @param fireEvent 是否触发事件
	 * @return 结果
	 */
	public Object put(String key, Object value, boolean fireEvent) {
		if (key == null) {
			return null;
		}

		begin();
		try {
			DataObjectCache.begin();	
			try {				
				if (fireEvent && metadata != null) {
					metadata.beforeFieldChanged(key, value);
				}
				
				if(metadata != null){
					Thing definition = metadata.getDefinition(key);
					if (definition != null && "attribute".equals(definition.getThingName())) {
						value = DataObjectUtil.getValue(value, definition);
						value = super.put(key, value);
						setWrappedObjectValue(definition, key, value);
						return value;
						
					} else {
						value = super.put(key, value);
						setWrappedObjectValue(definition, key, value);
						return value;
					}
				}else{
					value = super.put(key, value);
					setWrappedObjectValue(null, key, value);
					return value;
				}				
			}finally {
				DataObjectCache.finish();
			}
		}finally {
			finish();
		}
	}
	
	private void setWrappedObjectValue(Thing attributeDefinition, String key, Object value) {
		if(wrappedObject != null) {
			if(attributeDefinition != null && (attributeDefinition.getBoolean("readOnly") 
					|| attributeDefinition.getBoolean("propertyReadOnly"))) {
				//只读不写入到对象中
				return;
			}
			
			String path = key;
			if(attributeDefinition != null) {
				path = attributeDefinition.getStringBlankAsNull("propertyPath");
				if(path == null) {
					path = key;
				}
			}
			if(key != null && !"".equals(key)) {
				OgnlUtil.setValue(path, wrappedObject, value);
			}
		}
	}
	
	/**
	 * 开始批量修改。
	 */
	public void begin() {
		Stack<Object> stack = modifiyStack.get();
		if(stack == null) {
			stack = new Stack<Object>();
			modifiyStack.set(stack);
		}
		
		stack.push(this);
	}
	
	/**
	 * 结束批量修改，并触发changeEvent。
	 */
	public void finish() {
		Stack<Object> stack = modifiyStack.get();
		stack.pop();
		
		if(stack == null || stack.size() == 0) {
			if(listeners != null) {
				for(DataObjectListener listener : listeners) {
					listener.changed(this);
				}
			}
		}
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		begin();
		try {
			for (String key : m.keySet()) {
				this.put(key, m.get(key));
			}
		}finally {
			finish();
		}
	}

	/**
	 * 设置是否已初始化，如果未初始化，那么字段变动时不会记录到脏数据中。
	 * 
	 * @param inited
	 */
	public void setInited(boolean inited) {
		if(metadata != null){
			metadata.setInited(inited);
		}
	}

	/**
	 * 返回是否已初始化。
	 * 
	 * @return 是否已初始化
	 */
	public boolean isInited() {
		if(metadata == null){
			return false;
		}
		return metadata.isInited();
	}

	/**
	 * 返回是否是脏数据。
	 * 
	 * @return 如果是脏数据返回true，否则返回false
	 */
	public boolean isDirty() {
		return metadata.isDirty() || flag == DataObject.FLAG_DELETED
				|| flag == DataObject.FLAG_NEW;
	}
	
	/**
	 * 返回数据对象的标记是否是新数据对象，即还未保存到存储的数据对象。
	 * 
	 * @return
	 */
	public boolean isNew() {
		return flag == DataObject.FLAG_NEW;
	}

	/**
	 * 返回数据对象是否标记为删除。
	 * 
	 * @return
	 */
	public boolean isDeleted() {
		return flag == DataObject.FLAG_DELETED;
	}
	
	/**
	 * 返回元数据。
	 * 
	 * @return 元数据
	 */
	public DataObjectMetadata getMetadata() {
		return metadata;
	}

	/**
	 * 返回主键和主键的值。
	 * 
	 * @return 主键和对应的值
	 */
	public Object[][] getKeyAndDatas() {
		return metadata.getKeyAndDatas();
	}

	/**
	 * 初始化默认值，并设置状态为已初始化。
	 * 
	 * @param actionContext 变量上下文
	 */
	public void init(ActionContext actionContext) {
		setInited(true);
		doAction("init", actionContext);
	}

	/**
	 * 查询数据，是DataObjectUtil的一种快捷方式。
	 * 
	 * @param conditions 条件集合
	 * @param actionContext 变量上下文
	 * @return 查询结果
	 */
	public List<DataObject> query(Map<String, Object> conditions, ActionContext actionContext){
		return DataObjectUtil.query(getMetadata().getDescriptor().getMetadata().getPath(), conditions, actionContext);
	}
	
	@Override
	public Object get(Object key) {
		if (this.wrappedObject != null) {
			if (metadata != null) {
				Thing definition = metadata.getDefinition(key.toString());
				if (definition != null) {
					String propertyPath = definition.getStringBlankAsNull("propertyPath");
					if (propertyPath == null) {
						propertyPath = key.toString();
					}

					return OgnlUtil.getValue(propertyPath, wrappedObject);
				}
			}

			return OgnlUtil.getValue(key, wrappedObject);
		}

		if (!isInited()) {
			load(null);
		}

		String keyStr = String.valueOf(key);
		if (keyStr.indexOf('.') != -1) {
			String[] keys = keyStr.split("[.]");
			//DataObject data = this;
			Object data = this;
			for (String k : keys) {
				if (data instanceof DataObject) {
					data = ((DataObject) data).get(k);
				} else {
					data = OgnlUtil.getValue(k, data);
				}
				if (data == null) {
					break;
				}
			}
			return data;
		} else {
			Object obj = super.get(key);
			/*
			下面的对象也应该是延迟加载的。
			if (obj instanceof DataObject) {
				DataObject dobj = (DataObject) obj;
				if (!dobj.isInited()) {
					dobj = dobj.load(null);
					super.put((String) key, dobj);
				}
			} else if (obj instanceof DataObjectList) {
				DataObjectList dobjlist = (DataObjectList) obj;
				if (!dobjlist.isInited()) {
					dobjlist.load(null);
				}
			}*/

			return obj;
		}
	}

	/**
	 * 装载数据，并设置状态为已初始化。
	 * 
	 * 如果数据存在返回本事物，否则返回null或者抛出异常。
	 * 
	 * @param actionContext 变量上下文
	 */
	public DataObject load(ActionContext actionContext) {
		setInited(true);
		if(metadata == null) {
			return this;
		}else {
			DataObject dataObj = (DataObject) doAction("load", actionContext);
			if(dataObj == null) {
				return null;
			}
			
			if(dataObj != this) {
				this.putAll(dataObj);
			}
			
			return this;
		}
	}

	/**
	 * 后台加载数据对象。加载后一般会触发监听器的加载和修改事件。
	 *
	 * @param actionContext
	 */
	public void loadBackground(final ActionContext actionContext){
		TaskManager.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (DataObject.this) {
						if (DataObject.this.isInited()) {
							return;
						}else{
							setInited(true);
						}
					}

					DataObject.this.load(actionContext);
				}catch(Exception e){
					Executor.warn(TAG, "Load dataobject error, path=" + getMetadata().getDescriptor().getMetadata().getPath(), e);
				}
			}
		});
	}
	
	/**
	 * <p>根据条件查询，如果返回的数据对象的列表不为空，那么把第一个数据对象作为加载的数据对象。</p>
	 * 
	 * <p>如果加载的数据对象不为null，那么把数据对象的所有属性放到当前数据对象中，并返回当前数据对象。</p>
	 * 
	 * @param actionContext
	 * @param condition
	 * @return
	 */
	public DataObject load(ActionContext actionContext, Condition condition) {
		List<DataObject> datas = query(actionContext, condition);
		if(datas.size() > 0) {
			setInited(true);
			DataObject dataObj = datas.get(0);
			this.putAll(dataObj);

			return this;
		}else {
			return null;
		}
	}
	
	/**
	 * 根据条件批量更新数据，其中要更新的内容是当前数据对象的脏字段。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 查询条件
	 * @return
	 */
	public int update(ActionContext actionContext, Condition condition){
		return update(actionContext, condition.getConditionThing(), condition.getConditionValues()); 
	}
	
	/**
	 * 根据条件批量更新数据，其中要更新的内容是当前数据对象的脏字段。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 条件，一般是条件模型
	 * @param queryDatas 查询条件，一般是Map&lt;String, Object&gt;
	 * 
	 * @return
	 */
	public int update(ActionContext actionContext, Object condition,
			Object queryDatas) {
		if (actionContext == null) {
			actionContext = new ActionContext();
		}

		try {
			Bindings bindings = actionContext.push(null);
			//查询条件的变量
			bindings.put("condition", condition);
			bindings.put("conditionConfig", condition);
			//查询条件对应的值
			bindings.put("datas", queryDatas);
			bindings.put("conditionData", queryDatas);

			return (Integer) doAction("updateBatch", actionContext);
		} finally {
			actionContext.pop();
		}
	}
	
	/**
	 * 根据条件批量删除数据。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 查询条件
	 * @return 删除的数量
	 */
	public int delete(ActionContext actionContext, Condition condition){
		return delete(actionContext, condition.getConditionThing(), condition.getConditionValues()); 
	}
	
	/**
	 * 根据条件批量删除数据。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 条件，一般是条件模型
	 * @param queryDatas 查询条件，一般是Map&lt;String, Object&gt;
	 * @return 删除的数量
	 */
	public int delete(ActionContext actionContext, Object condition,
			Object queryDatas) {
		if (actionContext == null) {
			actionContext = new ActionContext();
		}

		try {
			Bindings bindings = actionContext.push(null);
			//查询条件的变量
			bindings.put("theData", this);
			bindings.put("condition", condition);
			bindings.put("conditionConfig", condition);
			//查询条件对应的值
			bindings.put("datas", queryDatas);
			bindings.put("conditionData", queryDatas);

			return (Integer) doAction("deleteBatch", actionContext);
		} finally {
			actionContext.pop();
		}
	}
	
	/**
	 * 通过查询条件查询。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 查询条件
	 * @return
	 */
	public List<DataObject> query(ActionContext actionContext, Condition condition){
		return query(actionContext, condition.getConditionThing(), condition.getConditionValues()); 
	}
	
	/**
	 * 查询数据。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 条件，一般是条件模型
	 * @param queryDatas 查询条件，一般是Map&lt;String, Object&gt;
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataObject> query(ActionContext actionContext, Object condition, Object queryDatas) {
		if (actionContext == null) {
			actionContext = new ActionContext();
		}

		try {
			Bindings bindings = actionContext.push(null);
			//查询条件的变量
			bindings.put("condition", condition);
			bindings.put("conditionConfig", condition);
			//查询条件对应的值
			bindings.put("datas", queryDatas);
			bindings.put("conditionData", queryDatas);

			return (List<DataObject>) doAction("query", actionContext);
		} finally {
			actionContext.pop();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DataObject) {
			return equals((DataObject) o);
		}else {
			return false;
		}
	}

	/**
	 * 比较是否与另一个数据对象是相同的。比较描述者和主键。
	 * 
	 * @param dataObject
	 * @return
	 */
	public boolean equals(DataObject dataObject) {
		if(dataObject == this) {
			return true;
		}
		
		if(dataObject == null) {
			return false;
		}
		
		if(dataObject.getMetadata().getDescriptor() != this.getMetadata().getDescriptor()) {
			return false;
		}
		
		Object[][] otherKeyDatas = dataObject.getKeyAndDatas();
		Object[][] keyDatas = this.getKeyAndDatas();
		if(otherKeyDatas == null || keyDatas == null || (otherKeyDatas.length != keyDatas.length) || keyDatas.length == 0) {
			//没有设置主键的也返回false
			return false;
		}
		
		for(int i = 0; i < keyDatas.length; i++) {
			Object obj1 = otherKeyDatas[i][1];
			Object obj2 = keyDatas[i][1];
			
			if(obj1 == null && obj2 == null) {
				continue;
			}else if(obj1 == null || obj2 == null) {
				return false;
			}else if(!obj1.equals(obj2)){
				return false;
			}			
		}
		
		return true;
	}

	/**
	 * 根据主键的值比较是否匹配。和equals(Object ... keys)方法不同，本方法不传主键的名字，
	 * 因此匹配时传入的数组的主键顺序应该和数据对象定义的主键顺序一致，否则可能会不能正确匹配。
	 *
	 * @param keys
	 * @return
	 */
	public boolean equalsByKey(Object ... keys){
		if(keys == null) {
			return false;
		}

		Object[][] keyDatas = this.getKeyAndDatas();
		if(keyDatas == null || keyDatas.length == 0) {
			//如果没有主键，不能通过主键比较，返回false
			return false;
		}

		if(keys.length < keyDatas.length){
			//键值个数不一样，缺少键值
			return false;
		}

		for(int i = 0; i < keyDatas.length; i++) {
			Object[] ks = keyDatas[i];
			Object key = DataObjectUtil.getValue(keys[i], (Thing) ks[0]);
			if(!UtilCondition.equals(ks[1], key, null, null, false, null)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 通过主键比对是否和本数据对象相同。
	 * 
	 * keys是name1, value1, name2, value2...成对出现的。name是主键的名字，value是主键的值。
	 * 
	 * @param keys
	 * @return
	 */
	public boolean equals(Object ... keys) {
		if(keys == null) {
			return false;
		}
		
		Object[][] keyDatas = this.getKeyAndDatas();
		if(keyDatas == null || keyDatas.length == 0) {
			//如果没有主键，不能通过主键比较，返回false
			return false;
		}
		
		for(int i = 0; i < keyDatas.length; i++) {
			if(keys.length <= i) {
				return false;
			}
			
			Object[] ks = keyDatas[i];
			boolean have = false;
			for(int n =0; n < keys.length; n++) {
				if(((Thing) ks[0]).getMetadata().getName().equals(keys[n])) {
					have = true;
					if(!UtilCondition.equals(ks[1], keys[n + 1], null, null, false, null)) {
						return false;
					}
				}
				
				n = n + 2;
				if(n >= keys.length) {
					break;
				}
			}
			if(!have) {
				return false;
			}			
		}
		
		return true;
	}

	/**
	 * 提交变更，并肩标志改为默认值。
	 * 
	 * @param actionContext 变量上下文
	 */
	public void commit(ActionContext actionContext) {
		switch (flag) {
		case DataObject.FLAG_DELETED:
			delete(actionContext);
			break;
		case DataObject.FLAG_NEW:
			create(actionContext);
			break;
		case DataObject.FLAG_STORED:
			if (isDirty()) {
				update(actionContext);
			}
			break;
		}
		
		flag = 0;
	}

	/**
	 * 创建如果成功返回创建后的数据对象，否则返回null。
	 * 
	 * @return 结果
	 */
	public DataObject create(ActionContext actionContext) {
		return (DataObject) doAction("create", actionContext);
	}

	/**
	 * 创建或更新。先按主键加载，如果存在则更新，否则创建。
	 *
	 * @param actionContext
	 *
	 * @return　返回DataObject是创建，返回int是更新
	 */
	public Object createOrUpdate(ActionContext actionContext) {
		DataObject loadData = this.load(actionContext); 
		if(loadData != null){
			return this.update(actionContext);
		}else{
			return this.create(actionContext);
		}
	}

	/**
	 * 根据指定的主键列表创建或更新。根据keys查询，条件的值是本数据对象中对应的值。如果查询存在，则把本数据对象的脏字段设置到查询结果的
	 * 数据对象中，并执行更新操作，返回
	 *
	 * @param keys
	 * @param actionContext
	 * @return
	 */
	/* 该方法存在歧义，因此不建议使用。
	public DataObject createOrUpdate(List<String> keys, ActionContext actionContext) {
		if(keys == null || keys.size() == 0){
			throw new ActionException("No key fields");
		}
		
		Map<String, Object> conditions = new HashMap<String, Object>();
		for(String key : keys){
			conditions.put(key, get(key));
		}
		
		DataObject loadData = null;
		List<DataObject> datas = DataObjectUtil.query(getMetadata().getDescriptor().getMetadata().getPath(), conditions, actionContext);
		if(datas != null && datas.size() > 0){
			loadData = datas.get(0);
		}
		
		if(loadData != null){
			for(String name : this.metadata.getDirtyFields()){
				loadData.put(name, get(name));
			}
			loadData.update(actionContext);
			
			return loadData;
		}else{
			return this.create(actionContext);
		}
	}
	
	public DataObject createOrUpdate(ActionContext actionContext, String ... keys) {
		List<String> ks = new ArrayList<String>();
		for(String key : keys){
			ks.add(key);
		}
		
		return createOrUpdate(ks, actionContext);
	}*/
	
	/**
	 * 通过keys执行的字段名作为查询条件判断记录是否存在，如果存在则不创建，否则执行创建。
	 * 
	 * @param keys 关键字列表
	 * @param actionContext 变量上下文
	 * 
	 * @return 如果新建了数据对象，或返回第一个已经有了数据对象
	 */
	public DataObject createIfNotExists(List<String> keys, ActionContext actionContext){
		Map<String, Object> conditions = new HashMap<String, Object>();
		for(String key : keys){
			conditions.put(key, get(key));
		}
		
		List<DataObject> datas = DataObjectUtil.query(getMetadata().getDescriptor().getMetadata().getPath(), conditions, actionContext);
		if(datas == null || datas.size() <= 0){
			return this.create(actionContext);
		}else{
			return datas.get(0);
		}
	}

	public DataObject createIfNotExists(ActionContext actionContext, String ... keys){
		List<String> ks = new ArrayList<String>();
		for(String key : keys){
			ks.add(key);
		}
		
		return createIfNotExists(ks, actionContext);
	}
	
	/**
	 * 更新。
	 * 
	 * @return 已更新的数量
	 */
	public int update(ActionContext actionContext) {
		Object obj = doAction("update", actionContext);
		
		//清空脏数据和重置主键
		metadata.updateKeyAndDatas();
		metadata.cleanDirty();		
		
		return (int) UtilData.getLong(obj, 0);
	}

	/**
	 * 删除。
	 * 
	 * @return 已删除的数量
	 */
	public int delete(ActionContext actionContext) {
		Object obj = doAction("delete", actionContext);
		return (int) UtilData.getLong(obj, 0);
	}

	/**
	 * 执行动作。
	 * 
	 * @param name 动作名
	 * @return 结果
	 */
	public Object doAction(String name) {
		return doAction(name, null, (Map<String, Object>) null);
	}

	/**
	 * 执行动作。
	 * 
	 * @param name 动作名
	 * @param actionContext 变量上下文
	 * @return 结果
	 */
	public Object doAction(String name, ActionContext actionContext) {
		return doAction(name, actionContext, (Map<String, Object>) null);
	}

	/**
	 * 执行动作。
	 * 
	 * @param name 动作名
	 * @param actionContext 变量上下文
	 * @param params 参数
	 * @return 结果
	 */
	public Object doAction(String name, ActionContext actionContext,
			Map<String, Object> params) {
		Thing thing = metadata.getDescriptor();

		if (actionContext == null) {
			actionContext = new ActionContext();
		}

		Bindings bindings = actionContext.push(null);
		try {
			bindings.setCaller(this, name);
			bindings.put("theData", this);
			return thing.doAction(name, actionContext, params);
		} finally {
			actionContext.pop();
		}
	}

	public Object doAction(String name, ActionContext actionContext, Object...params) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i = 0; i<params.length; i++) {
			if(i < params.length - 1) {
				map.put(String.valueOf(params[i]), params[i + 1]);
			}
			i++;
		}
		
		return doAction(name, actionContext, map);
	}
	
	/**
	 * 获取数据的标志，是新的还是旧的还是已删除的。
	 * 
	 * @return 标志
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * 设置数据的标志，是新的还是旧的还是已删除的。
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * 设置第三方数据，运行时调用者需要的数据。
	 * 
	 * @param key 键
	 * @param value 值
	 */
	public void setData(String key, Object value) {
		datas.put(key, value);
	}

	/**
	 * 获取第三方数据，运行时调用者需要的数据。
	 * 
	 * @param key  键
	 * @return 值
	 */
	public Object getData(String key) {
		return datas.get(key);
	}

	/**
	 * 开始线程缓存。 try{ beginThreadCache(); }finally{ finishThreadCache(); }
	 */
	public static void beginThreadCache() {
		DataObjectCache.begin();
	}

	/**
	 * 结束线程缓存。和beginThreadCache()匹配。
	 */
	public static void finishThreadCache() {
		DataObjectCache.finish();
	}

	/**
	 * 数据对象装载后的初始化 ，初始化一对多的关系和装载事件。
	 */
	public void fireOnLoaded(ActionContext actionContext){
		Thing descriptor = getMetadata().getDescriptor();

		//初始化多个属性列表
        List<Thing> things = getMetadata().getThings();
        for(int i=0; i<things.size(); i++){
        	Thing thing = things.get(i);

            if(thing.getBoolean("many")){
            	//列表映射（一对多）
                DataObjectList list = new DataObjectList(thing, this);
                list.setInited(false);
                put(thing.getString("name"), list, false);
            }else{
            	//单对象映射（多对一）
            	Thing refDesc = World.getInstance().getThing(thing.getString("dataObjectPath"));
            	if(refDesc != null) {
					DataObject data = DataObjectCache.getDataObjectThreadCache(refDesc, thing.getString("refAttributeName"),
							get(thing.getString("localAttributeName")));
					put(thing.getMetadata().getName(), data, false);
				}
			}
        }

        //触发onLoaded事件
        if(descriptor.getBoolean("onLoaded")){
        	descriptor.doAction("onLoaded", actionContext);
        }
	}
	
	/**
	 * 触发自动初始化。
	 * 
	 * @param actionContext 变量上下文
	 */
	public void fireBeforeCreate(ActionContext actionContext){
		Thing descriptor = getMetadata().getDescriptor();
		
		//是否需要自动初始化
		if(descriptor.getBoolean("autoInit")){
			doAction(descriptor.getString("autoInitAction"), actionContext);
		}
	}
	
	public void fireEvent(String eventName, ActionContext actionContext){
		Thing descriptor = getMetadata().getDescriptor();
		
		if(descriptor.getBoolean(eventName)){
			doAction(eventName, actionContext);
		}
	}
	
	/**
	 * 返回用户任务，通过用户任务在界面上可以查看执行的进度，或者取消执行等。
	 * 
	 * @param dataObject
	 * @return
	 */
	public static UserTask getUserTask(Thing dataObject, ActionContext actionContext){
		if(dataObject == null || dataObject.getBoolean("showUserTask") == false){
			return null;
		}
		
		UserTask task = userTasks.get();
		if(task == null || task.getTaskThing() != dataObject){
			task = UserTaskManager.createTask(dataObject, false);
			task.setActionContext(actionContext);
			
			userTasks.set(task);
		}
		
		return task;
	}
	
	/**
	 * 调用用户任务的finished()方法，如果用户任务存在的话。
	 */
	public static void userTaskFinished(){
		UserTask task = userTasks.get();
		if(task != null){
			task.finished();
			userTasks.set(null);
		}
	}

	/**
	 * 返回包装过的Java对象。
	 * @return
	 */
	public Object getWrappedObject() {
		return wrappedObject;
	}

	/**
	 * 获取源Java对象。
	 * 
	 * @return
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * 设置源Java对象。
	 * 
	 * @param source
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	@Override
	public Object clone() {	
		DataObject data = (DataObject) super.clone();
		data.listeners = null;
		
		return data;
	}
	
	/**
	 * 把自己的元数据等复制到指定数据对象，内部共用基数数据。
	 * 
	 * @param dataObject
	 */
	public void copyTo(DataObject dataObject) {
		dataObject.datas = this.datas;
		dataObject.flag = this.flag;
		dataObject.metadata = this.metadata;
		dataObject.source = this.source;
		dataObject.wrappedObject = this.wrappedObject;
		dataObject.putAll(this);
	}

	/**
	 * 设置了包装的对象后，数据对象取值和获取值将通过包装的对象获取。从对象上获取和设置值通过Ognl实现，key是数据对象属性的名字，
	 * 如果设置了相应的数据对象属性定义，那么试图通过propertyPath属性来获取属性的ognl表达式。
	 * 
	 * @param wrappedObject
	 */
	public void setWrappedObject(Object wrappedObject) {
		this.wrappedObject = wrappedObject;
	}

	/**
	 * 返回数据对象的标签值。如果数据对象设置了displayName，即要作为标签的字段名，那么返回该字段的值的字符串，否则返回主键的值字符串，
	 * 如果没有主键，使用默认的父类的toString()方法。
	 *
	 * @return
	 */
	public String getLabel() {
		String label = null;
		//优先返回标签字段的内容
		Thing descriptor = this.getMetadata().getDescriptor();
		if(descriptor != null) {
			String labelField = descriptor.getStringBlankAsNull("displayName");
			if(labelField != null) {
				label =  getString(labelField);
				if(label != null && !"".equals(label)) {
					return label;
				}
			}			
		}
		 
		//其次返回第一个主键的值
		Object[][] keys = this.getMetadata().getKeyAndDatas();
		if(keys != null && keys.length > 0) {
			return String.valueOf(keys[0][1]);
		}
		
		//按照HashMap的toString()方法
		return super.toString();
	}
}