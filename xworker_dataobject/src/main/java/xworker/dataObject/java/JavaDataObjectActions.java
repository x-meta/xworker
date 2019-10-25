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
package xworker.dataObject.java;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;

public class JavaDataObjectActions {
	//private static Logger log = LoggerFactory.getLogger(JavaDataObjectActions.class);
	private static final String key = "__JavaDataObjectActions-java_action_class";
	
	/**
	 * 获取类库。
	 * 
	 * @param dataObjectThing
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static JavaDataObject getJavaDataObject(Thing dataObjectThing) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		JavaDataObject obj = (JavaDataObject) dataObjectThing.getData(key);
		if(obj == null){
			String classPath = dataObjectThing.getString("actionClass");
			if(classPath != null){
				Class<?> cls = Class.forName(classPath);
				obj = (JavaDataObject) cls.newInstance();
			}
		}
		
		return obj;
	}

	/**
	 * 删除数据。
	 * 
	 * @param actionContext
	 * @return
	 * @throws SQLException
	 */
	public static int delete(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		
		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.delete(theData, actionContext);
		}else{
			return 0;
		}
	}

	/**
	 * 插入数据。
	 * 
	 * @param actionContext
	 * @throws Exception
	 */
	public static void create(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			obj.create(theData, actionContext);
		}
	}

	/**
	 * 更新数据。
	 * 
	 * @param actionContext
	 * @throws SQLException
	 */
	public static int update(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.update(theData, actionContext);
		}else{
			return 0;
		}
	}

	/**
	 * 批量更新。
	 * 
	 * @param actionContext
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	public static int updateBatch(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		if (theData == null) {
			theData = new DataObject(self);
		}

		Map<String, Object> datas = (Map<String, Object>) actionContext	.get("conditionData");
		if (datas == null) {
			datas = Collections.emptyMap();
		}
		// log.info("" + datas);
		Object conditionConfig = (Object) actionContext.get("conditionConfig");
		if (conditionConfig == null) {
			conditionConfig = Collections.emptyMap();
		}

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.updateBatch(theData, conditionConfig, datas, actionContext);
		}else{
			return 0;
		}
	}

	/**
	 * 批量删除数据。
	 * 
	 * @param actionContext
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static int deleteBatch(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		if (theData == null) {
			theData = new DataObject(self);
		}

		Map<String, Object> datas = (Map<String, Object>) actionContext	.get("conditionData");
		if (datas == null) {
			datas = Collections.emptyMap();
		}
		// log.info("" + datas);
		Object conditionConfig = (Object) actionContext.get("conditionConfig");
		if (conditionConfig == null) {
			conditionConfig = Collections.emptyMap();
		}

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.deleteBatch(theData, conditionConfig, datas, actionContext);
		}else{
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object query(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");

		// ---------------查询参数----------------
		Map<String, Object> datas = (Map<String, Object>) actionContext
				.get("conditionData"); // 查询条件数据
		if (datas == null) {
			datas = Collections.emptyMap();
		}

		Object conditionConfig = actionContext.get("conditionConfig"); // 查询配置
		if (conditionConfig == null) {
			conditionConfig = new Thing();
		}

		// 分页信息
		Map<String, Object> pageInfo = (Map<String, Object>) actionContext.get("pageInfo");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			if(pageInfo == null){
				return obj.query(self, conditionConfig, datas, null, actionContext);
			}else{
				return obj.query(self, conditionConfig, datas, new PageInfo(pageInfo), actionContext);
			}
		}else{
			return Collections.emptyList();
		}
	}
	
	public static Object createValidate(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.createValidate(theData, actionContext);
		}else{
			return null;
		}
	}
	
	public static Object updateValidate(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.createValidate(theData, actionContext);
		}else{
			return null;
		}
	}
	
	public static boolean isMappingAble(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.isMappingAble(self, actionContext);
		}else{
			return false;
		}
	}
	
	public static Object getMappingFields(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.getMappingFields(self, actionContext);
		}else{
			return Collections.emptyList();
		}
	}
	
	public static Object getMappingAttributeName(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.getMappingAttributeName(self, actionContext);
		}else{
			return null;
		}
	}
	
	public static Object getAttributeDescriptor(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");

		JavaDataObject obj = getJavaDataObject(self);
		if(obj != null){
			return obj.getAttributeDescriptor(self, actionContext);
		}else{
			return null;
		}
	}
}