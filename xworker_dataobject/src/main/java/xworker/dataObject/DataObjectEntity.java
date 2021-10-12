package xworker.dataObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.cache.DataObjectCache;
import xworker.dataObject.query.Condition;
import xworker.dataObject.query.QueryConfig;
import xworker.task.UserTask;

import javax.management.Query;

public abstract class DataObjectEntity<T extends DataObjectEntity<?>> {
	public abstract T createInstance() ;
	
	DataObject data;
	
	public DataObjectEntity() {
		
	}
	
	public DataObjectEntity(DataObject data) {
		this.data = data;
	}
	
	public void setDataObject(DataObject data) {
		this.data = data;
	}
	
	public DataObject getDataObject() {
		return data;
	}
	
	public void addListener(DataObjectListener listener) {
		if(data != null) {
			data.addListener(listener);
		}
	}
	
	public void removeListener(DataObjectListener listener) {
		if(data != null) {
			data.removeListener(listener);
		}
	}
	
	public List<DataObjectListener> getListeners(){
		if(data != null) {
			return data.getListeners();
		} else {
			return Collections.emptyList();
		}
	}
	
	public void setAttributes(String attributes, ActionContext actionContext) {
		if(data != null) {
			data.setAttributes(attributes, actionContext);
		}		
	}
	
	public Object put(String key, Object value) {
		if(data != null) {
			return data.put(key, value);
		}
		
		return null;
	}
	
	public Object set(String key, Object value) {
		return put(key, value);
	}

	public String getString(String key){
		if(data != null) {
			return data.getString(key);
		}
		
		return null;
	}
	
	public byte getByte(String key){
		if(data != null) {
			return data.getByte(key);
		}
		
		return 0;
	}
	
	public short getShort(String key){
		if(data != null) {
			return data.getShort(key);
		}
		
		return 0;
	}
	
	public int getInt(String key){
		if(data != null) {
			return data.getInt(key);
		}
		
		return 0;
	}
	
	public long getLong(String key){
		if(data != null) {
			return data.getLong(key);
		}
		
		return 0;
	}
	
	public boolean getBoolean(String key){
		if(data != null) {
			return data.getBoolean(key);
		}
		
		return false;
	}
	
	public byte[] getBytes(String key){
		if(data != null) {
			return data.getBytes(key);
		}
		
		return null;
	}
	
	public Date getDate(String key){
		if(data != null) {
			return data.getDate(key);
		}
		
		return null;
	}
	
	public double getDouble(String key){
		if(data != null) {
			return data.getDouble(key);
		}
		
		return 0;
	}
	
	public float getFloat(String key){
		if(data != null) {
			return data.getFloat(key);
		}
		
		return 0;
	}
	
	
	public BigDecimal getBigDecimal(String key){
		if(data != null) {
			return data.getBigDecimal(key);
		}
		
		return null;
	}
	
	public BigInteger getBigInteger(String key){
		if(data != null) {
			return data.getBigInteger(key);
		}
		
		return null;
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
		if(data != null) {
			return data.put(key, value, fireEvent);
		} else {
			return null;
		}		
	}
	
	/**
	 * 开始批量修改。
	 */
	public void begin() {
		if(data != null) {
			data.begin();
		}
	}
	
	/**
	 * 结束批量修改，并触发changeEvent。
	 */
	public void finish() {
		if(data != null) {
			data.finish();
		}
	}
	
	public void putAll(Map<? extends String, ? extends Object> m) {
		if(data != null) {
			data.putAll(m);
		}
	}

	/**
	 * 设置是否已初始化，如果未初始化，那么字段变动时不会记录到脏数据中。
	 */
	public void setInited(boolean inited) {
		if(data != null) {
			data.setInited(inited);
		}
	}

	/**
	 * 返回是否已初始化。
	 * 
	 * @return 是否已初始化
	 */
	public boolean isInited() {
		if(data != null) {
			return data.isInited();
		}
		
		return false;
	}

	/**
	 * 返回是否是脏数据。
	 * 
	 * @return 如果是脏数据返回true，否则返回false
	 */
	public boolean isDirty() {
		if(data != null) {
			return data.isDirty();
		}
		
		return false;
	}
	
	/**
	 * 返回数据对象的标记是否是新数据对象，即还未保存到存储的数据对象。
	 */
	public boolean isNew() {
		if(data != null) {
			return data.isNew();
		}
		
		return false;
	}

	/**
	 * 返回数据对象是否标记为删除。
	 */
	public boolean isDeleted() {
		if(data != null) {
			return data.isDeleted();
		}
		
		return false;
	}
	
	/**
	 * 返回元数据。
	 * 
	 * @return 元数据
	 */
	public DataObjectMetadata getMetadata() {
		if(data != null) {
			return data.getMetadata();
		}
		
		return null;
	}

	/**
	 * 返回主键和主键的值。
	 * 
	 * @return 主键和对应的值
	 */
	public Object[][] getKeyAndDatas() {
		if(data != null) {
			return data.getKeyAndDatas();
		}
		
		return null;
	}

	/**
	 * 初始化默认值，并设置状态为已初始化。
	 * 
	 * @param actionContext 变量上下文
	 */
	public void init(ActionContext actionContext) {
		if(data != null) {
			data.init(actionContext);
		}
	}

	/**
	 * 查询数据，是DataObjectUtil的一种快捷方式。
	 * 
	 * @param conditions 条件集合
	 * @param actionContext 变量上下文
	 * @return 查询结果
	 */
	public List<T> query(Map<String, Object> conditions, ActionContext actionContext){
		if(data != null) {
			return toTList(data.query(conditions, actionContext));
		}
		
		return Collections.emptyList();
	}
	
	public Object get(Object key) {
		if(data != null) {
			return data.get(key);
		}
		
		return null;
	}

	/**
	 * 装载数据，并设置状态为已初始化。
	 * 
	 * 如果数据存在返回本事物，否则返回null或者抛出异常。
	 */
	@SuppressWarnings("unchecked")
	public T load(ActionContext actionContext) {
		if(data != null) {
			DataObject dataObject = data.load(actionContext);
			if(dataObject != null) {
				this.data = dataObject;
				return (T) this;
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public T load(ActionContext actionContext, QueryConfig queryConfig){
		if(data != null) {
			DataObject dataObject = data.load(actionContext, queryConfig);
			if(dataObject != null) {
				this.data = dataObject;
				return (T) this;
			}
		}

		return null;
	}
	
	/**
	 * <p>根据条件查询，如果返回的数据对象的列表不为空，那么把第一个数据对象作为加载的数据对象。</p>
	 * 
	 * <p>如果加载的数据对象不为null，那么把数据对象的所有属性放到当前数据对象中，并返回当前数据对象。</p>
	 */
	@SuppressWarnings("unchecked")
	public T load(ActionContext actionContext, Condition condition) {
		if(data != null) {
			DataObject dataObject = data.load(actionContext, condition);
			if(dataObject != null) {
				this.data = dataObject;
				return ((T) this);
			}
		}
		
		return null;
	}
	
	/**
	 * 根据条件批量更新数据，其中要更新的内容是当前数据对象的脏字段。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 查询条件
	 *
	 * @return int值，更新的记录数
	 */
	public int update(ActionContext actionContext, Condition condition){
		if(data != null) {
			return data.update(actionContext, condition);
		}
		
		return 0;
	}

	public int update(ActionContext actionContext, QueryConfig queryConfig){
		if(data != null){
			return data.update(actionContext, queryConfig);
		}

		return 0;
	}

	/**
	 * 根据条件批量更新数据，其中要更新的内容是当前数据对象的脏字段。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 条件，一般是条件模型
	 * @param queryDatas 查询条件，一般是Map&lt;String, Object&gt;
	 * @return 更新的数量
	 */
	public int update(ActionContext actionContext, Object condition,
			Object queryDatas) {
		if(data != null) {
			return data.update(actionContext, condition, queryDatas);
		}
		
		return 0;
	}

	public int delete(ActionContext actionContext, QueryConfig queryConfig){
		if(data != null){
			return data.delete(actionContext, queryConfig);
		}

		return 0;
	}
	
	/**
	 * 根据条件批量删除数据。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 查询条件
	 * @return 删除的数量
	 */
	public int delete(ActionContext actionContext, Condition condition){
		if(data != null) {
			return data.delete(actionContext, condition);
		}
		
		return 0;
	}
	
	/**
	 * 根据条件批量删除数据。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 条件，一般是条件模型
	 * @param queryDatas 查询条件，一般是Map&lt;String, Object&gt;
	 * @return 删除的数量
	 * 
	 */
	public int delete(ActionContext actionContext, Object condition,
			Object queryDatas) {
		if(data != null) {
			return data.delete(actionContext, condition, queryDatas);
		}
		
		return 0;
	}

	public List<T> query(ActionContext actionContext, QueryConfig queryConfig){
		if(data != null){
			return toTList(data.query(actionContext, queryConfig));
		}

		return Collections.emptyList();
	}

	/**
	 * 通过查询条件查询。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 查询条件
	 * @return 查询结果列表
	 */
	public List<T> query(ActionContext actionContext, Condition condition){
		if(data != null) {
			return toTList(data.query(actionContext, condition));
		}
		
		return Collections.emptyList(); 
	}
	
	public List<T> toTList(List<DataObject> datas) {
		if(datas == null || datas.size() == 0) {
			return Collections.emptyList();
		}
		
		List<T> ds = new ArrayList<T>();
		for(DataObject data : datas) {
			T t = createInstance();
			t.setDataObject(data);
			ds.add(t);
		}
		
		return ds;
	}
	
	/**
	 * 查询数据。
	 * 
	 * @param actionContext 变量上下文
	 * @param condition 条件，一般是条件模型
	 * @param queryDatas 查询条件，一般是Map&lt;String, Object&gt;
	 * @return 查询结果列表
	 */
	public List<T> query(ActionContext actionContext, Object condition,
			Object queryDatas) {
		if(data != null) {
			return toTList(data.query(actionContext, condition, queryDatas));
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public boolean equals(Object o) {
		if(data != null) {
			return data.equals(o);
		}
		
		return false;
	}

	/**
	 * 比较是否与另一个数据对象是相同的。比较描述者和主键。
	 */
	public boolean equals(DataObject dataObject) {
		if(data != null) {
			return data.equals(dataObject);
		}
		
		return false;
	}
	
	/**
	 * 通过主键比对是否和本数据对象相同。
	 * 
	 * keys是name1, value1, name2, value2...成对出现的。name是主键的名字，value是主键的值。
	 */
	public boolean equals(Object ... keys) {
		if(data != null) {
			return data.equals(keys);
		}
		
		return false;
	}

	/**
	 * 提交变更，并肩标志改为默认值。
	 * 
	 * @param actionContext 变量上下文
	 */
	public void commit(ActionContext actionContext) {
		if(data != null) {
			data.commit(actionContext);
		}
	}

	/**
	 * 创建如果成功返回创建后的数据对象，否则返回null。
	 * 
	 * @return 结果
	 */
	public T create(ActionContext actionContext) {
		if(data != null) {
			return toT(data.create(actionContext));
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public T toT(DataObject dataObject) {
		if(dataObject == null) {
			return null;
		}
		
		this.data = dataObject;
		return (T) this;
	}
	
	/**
	 * 创建或更新。
	 */
	public void createOrUpdate(ActionContext actionContext) {
		if(data != null) {
			data.createOrUpdate(actionContext);
		}		
	}

	/*
	public T createOrUpdate(List<String> keys, ActionContext actionContext) {
		if(data != null) {
			return toT(data.createOrUpdate(keys, actionContext));
		}
		
		return null;
	}*/
	
	public T createOrUpdate( ActionContext actionContext, String ... keys) {
		if(data != null) {
			return toT(data.createIfNotExists(actionContext, keys));
		}
		
		return null;
	}
	
	/**
	 * 通过keys执行的字段名作为查询条件判断记录是否存在，如果存在则不创建，否则执行创建。
	 * 
	 * @param keys 关键字列表
	 * @param actionContext 变量上下文
	 * 
	 * @return 如果新建了数据对象，或返回第一个已经有了数据对象
	 */
	public T createIfNotExists(List<String> keys, ActionContext actionContext){
		if(data != null) {
			return toT(data.createIfNotExists(keys, actionContext));
		}
		
		return null;
	}

	public T createIfNotExists(ActionContext actionContext, String ... keys){
		if(data != null) {
			return toT(data.createIfNotExists(actionContext, keys));
		}
		
		return null;
	}
	
	/**
	 * 更新。
	 * 
	 * @return 已更新的数量
	 */
	public int update(ActionContext actionContext) {
		if(data != null) {
			return data.update(actionContext);
		}
		
		return 0;
	}

	/**
	 * 删除。
	 * 
	 * @return 已删除的数量
	 */
	public int delete(ActionContext actionContext) {
		if(data != null) {
			return data.delete(actionContext);
		}
		
		return 0;
	}

	/**
	 * 执行动作。
	 * 
	 * @param name 动作名
	 * @return 结果
	 */
	public Object doAction(String name) {
		if(data != null) {
			return data.doAction(name);
		}
		
		return null;
	}

	/**
	 * 执行动作。
	 * 
	 * @param name 动作名
	 * @param actionContext 变量上下文
	 * @return 结果
	 */
	public Object doAction(String name, ActionContext actionContext) {
		if(data != null) {
			return data.doAction(name, actionContext);
		}
		
		return null;
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
		if(data != null) {
			return data.doAction(name, actionContext, params);
		}
		
		return null;
	}

	public Object doAction(String name, ActionContext actionContext, Object...params) {
		if(data != null) {
			return data.doAction(name, actionContext, params);
		}
		
		return null;
	}
	
	/**
	 * 获取数据的标志，是新的还是旧的还是已删除的。
	 * 
	 * @return 标志
	 */
	public int getFlag() {
		if(data != null) {
			return data.getFlag();
		}
		
		return 0;
	}

	/**
	 * 设置数据的标志，是新的还是旧的还是已删除的。
	 */
	public void setFlag(int flag) {
		if(data != null) {
			data.setFlag(flag);
		}
	}

	/**
	 * 设置第三方数据，运行时调用者需要的数据。
	 * 
	 * @param key 键
	 * @param value 值
	 */
	public void setData(String key, Object value) {
		if(data != null) {
			data.setData(key, value);
		}
	}

	/**
	 * 获取第三方数据，运行时调用者需要的数据。
	 * 
	 * @param key  键
	 * @return 值
	 */
	public Object getData(String key) {
		if(data != null) {
			return data.getData(key);
		}
		
		return null;
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
		if(data != null) {
			data.fireOnLoaded(actionContext);
		}
	}
	
	/**
	 * 触发自动初始化。
	 * 
	 * @param actionContext 变量上下文
	 */
	public void fireBeforeCreate(ActionContext actionContext){
		if(data != null) {
			data.fireBeforeCreate(actionContext);
		}
		
	}
	
	public void fireEvent(String eventName, ActionContext actionContext){
		if(data != null) {
			data.fireEvent(eventName, actionContext);
		}
	}
	
	/**
	 * 返回用户任务，通过用户任务在界面上可以查看执行的进度，或者取消执行等。
	 * 
	 * @param dataObject 数据对象
	 * @return 用户任务
	 */
	public static UserTask getUserTask(Thing dataObject, ActionContext actionContext){
		return DataObject.getUserTask(dataObject, actionContext);
	}
	
	/**
	 * 调用用户任务的finished()方法，如果用户任务存在的话。
	 */
	public static void userTaskFinished(){
		DataObject.userTaskFinished();
	}

	/**
	 * 返回包装过的Java对象。
	 *
	 * @return 包装的Java对象，不存在返回null
	 */
	public Object getWrappedObject() {
		if(data != null) {
			return data.getWrappedObject();
		}
		
		return null;
	}

	/**
	 * 获取源Java对象。
	 */
	public Object getSource() {
		if(data != null) {
			return data.getSource();
		}
		
		return null;
	}

	/**
	 * 设置源Java对象。
	 * 
	 */
	public void setSource(Object source) {
		if(data != null) {
			data.setSource(source);
		}
	}

	public void setWrappedObject(Object wrappedObject) {
		if(data != null) {
			data.setWrappedObject(wrappedObject);
		}
	}

	@Override
	public String toString() {
		if(data != null) {
			return data.toString();
		}else {
			return super.toString();
		}
	}
	
	
}
