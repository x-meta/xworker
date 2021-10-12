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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

import xworker.dataObject.cache.DataObjectCache;

public class DataObjectMetadata {
	/** 是否是脏数据 */
	private boolean dirty = false;
	/** 脏字段 */
	private String[] dirtyFields = null;	
	/** 元数据所绑定的数据对象 */
	private DataObject dataObject = null;
	/** 旧的键值数据 */
	private Object[][] oldKeyData = null;
	/** 是否已初始化数据 ，默认true*/
	private boolean inited = true;
	private DataObjectMetadataCache metadataCache;
	
	/**
	 * 构造一个数据对象的元数据。
	 */
	public DataObjectMetadata(Thing descriptor, DataObject dataObject){
		this.dataObject = dataObject;
		this.metadataCache = DataObjectMetadataCache.getInstance(descriptor);
	}
	
	/**
	 * 返回是否是脏数据。
	 */
	public boolean isDirty(){		
		return dirty;
	}
	
	/**
	 * 清除脏数据的状态。
	 */
	public void cleanDirty(){
		dirtyFields = null;
		dirty = false;
	}
	
	/**
	 * 当数据对象更新后重置主键，预防主键已修改的情况。
	 */
	public void updateKeyAndDatas(){
		Thing[] keys = getKeys();
		if(keys == null){
			return;
		}
		
		Object[][] oldKeyDatas = getOldKeyDatas();
		for (Thing key : keys) {
			String keyName = key.getString("name");
			if (oldKeyDatas != null) {
				for (int n = 0; n < oldKeyDatas.length; n++) {
					if (keyName.equals(oldKeyDatas[n][0])) {
						oldKeyDatas[n][1] = dataObject.get(keyName);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 返回脏字段列表。
	 */
	public String[] getDirtyFields(){
		if(dirtyFields == null){
			return null;
		}
		
		int offset = 0;
		for (String dirtyField : dirtyFields) {
			if (dirtyField != null) {
				offset++;
			} else {
				break;
			}
		}
		String[] dfs = new String[offset];
		System.arraycopy(dirtyFields, 0, dfs, 0, offset);
		return dfs;
	}
	
	/**
	 * 返回旧的键值数据列表。
	 */
	public Object[][] getOldKeyDatas(){
		if(oldKeyData == null){
			return null;
		}
		
		int offset = 0;
		for (Object[] oldKeyDatum : oldKeyData) {
			if (oldKeyDatum != null) {
				offset++;
			} else {
				break;
			}
		}
		Object[][] dfs = new Object[offset][2];
		System.arraycopy(oldKeyData, 0, dfs, 0, offset);
		return dfs;
	}
	
	/** 
	 * 返回关键字及值。
	 * 
	 * 数组第一位为定义事物，第二位为值
	 */
	public Object[][] getKeyAndDatas(){
		Thing[] keys = getKeys();
		if(keys == null){
			return null;
		}
		
		Object[][] oldKeyDatas = getOldKeyDatas();
		Object[][] datas = new Object[keys.length][2];
		for(int i=0; i<keys.length; i++){
			String keyName = keys[i].getString("name");
			Object keyValue = null;
			if(oldKeyDatas != null){
				for (Object[] keyData : oldKeyDatas) {
					if (keyName.equals(keyData[0])) {
						keyValue = keyData[1];
						break;
					}
				}
			}
			if(keyValue == null){
				keyValue = dataObject.get(keyName);			
			}
			datas[i][0] = keys[i];
			datas[i][1] = keyValue;
		}
		
		return datas;
	}
	
	/**
	 * 重置状态。
	 */
	protected void resetStatus(){
		dirty = false;
		oldKeyData = null;
		dirtyFields = null;
	}
	
	/**
	 * 返回是否已初始化数据。
	 */
	protected boolean isInited(){
		return inited;
	}
	
	/**
	 * 设置是否已初始化的状态。
	 */
	protected void setInited(boolean inited){
		this.inited = inited;
	}

	protected List<Thing> getRelationThings(String name){
		return metadataCache.attributeRelationThings.get(name);
	}
	/**
	 * 当DataObject属性发生变化前时会调用此方法，主要是生产脏数据信息。
	 */
	protected synchronized void beforeFieldChanged(String name, Object value){				
		Thing definition = this.getDefinition(name);
		if(definition == null){
			//没有定义返回
			return;
		}
		
		if(definition.getBoolean("many")){
			//如果是列表，不处理
			return;
		}
				
		String thingName = definition.getThingName();
		//查看关联事物的变化
		/* 已改成直接清空关联事物，见DataObject.put()方法
		if("attribute".equals(thingName)){
			//查看是否有关联事物			
			List<Thing> ar = metadataCache.attributeRelationThings.get(name);
			if(ar != null && ar.size() > 0){
				for(Thing t : ar){
					if(t == null){
						continue;
					}
					if(t.getBoolean("many")){
						DataObjectList dlist = new DataObjectList(t, dataObject);
						dlist.setInited(false);
						//dlist.load(null);
						dataObject.put(t.getString("name"), dlist, false);
					}else{
						Thing relationThing = World.getInstance().getThing(t.getString("dataObjectPath"));
						if(relationThing != null){
							DataObject d = null;
							if(relationThing.getBoolean("cacheRelation")){
							    d = DataObjectCache.getDataObject(relationThing, t.getString("refAttributeName"), value);
							}else{
								d = DataObjectCache.getDataObjectThreadCache(relationThing, t.getString("refAttributeName"), value);
							}
							dataObject.put(t.getString("name"), d, false);
						}
					}
				}
			}
		}else{
			//如果是多对一或一对一的事物发生了变化
			if(!definition.getBoolean("many") && value instanceof DataObject){
				DataObject v = (DataObject) value;
				Object av = v.get(definition.getString("refAttributeName"));
				name = definition.getString("localAttributeName");
				dataObject.put(name, av, false);
			}
		}*/
		
		//脏数据的处理
		if(!inited){
			//未初始化返回，不记录脏数据
			return;
		}
		//数据对象已修改
		dirty = true;
		if("attribute".equals(thingName)){
			if(definition.getBoolean("dataField")){
				if(definition.getBoolean("key")){
					//关键字的值发生了变化
					if(oldKeyData == null){
						oldKeyData = new Object[3][2];
					}
					
					boolean added = false;
					int offset = 0;
					for(int i=0; i<oldKeyData.length; i++){
						offset ++;
						if(oldKeyData[i][0] != null){
							if(name.equals(oldKeyData[i][0])){
								added = true;
								break;
							}
						}else{
							oldKeyData[i][0] = name;
							oldKeyData[i][1] = dataObject.get(name);
							added = true;
							break;
						}
					}
					
					if(!added){
						if(offset < oldKeyData.length){
							oldKeyData[offset-1][0] = name;
							oldKeyData[offset-1][1] = dataObject.get(name);
						}else{
							Object[][] noldKeyData = new Object[offset + 3][2];
							System.arraycopy(oldKeyData, 0, noldKeyData, 0, oldKeyData.length);
							oldKeyData = noldKeyData;
							oldKeyData[offset][0] = name;
							oldKeyData[offset][1] = dataObject.get(name);
						}
					}
				}
			}
		}
				
		//把字段名加入到已修改字段的数组中
		if(definition.getBoolean("dataField")){
			if(dirtyFields == null){
				dirtyFields = new String[10];
			}
			boolean added = false;
			int offset = 0;
			for(int i=0; i<dirtyFields.length; i++){
				offset++;
				if(dirtyFields[i] != null){
					if(name.equals(dirtyFields[i])){
						added = true;
						break;
					}
				}else{
					dirtyFields[i] = name;
					added = true;
					break;
				}
			}
			if(!added){
				if(offset >= dirtyFields.length){
					String[] ndirtyFields = new String[offset + 10];
					System.arraycopy(dirtyFields, 0, ndirtyFields, 0, dirtyFields.length);
					dirtyFields = ndirtyFields;
					dirtyFields[offset] = name;
				}
			}
		}
	}
	
	/**
	 * 返回属性定义。
	 */
	public List<Thing> getAttributes(){
		return metadataCache.attributeList;
	}
	
	/**
	 * 返回子模型的定义。
	 */
	public List<Thing> getThings(){
		return metadataCache.thingList;
	}
	
	/**
	 * 返回关键字列表。
	 */
	public Thing[] getKeys(){
		return metadataCache.keys;
	}
	
	/**
	 * 返回描述者模型。
	 */
	public Thing getDescriptor(){
		return metadataCache.getDescriptor();
	}
	
	/**
	 * 获取属性或者子模型的定义。
	 */
	public Thing getDefinition(String name){
		if(name == null){
			return null;
		}
		
		Thing attr = metadataCache.attributes.get(name);
		if(attr != null){
			return attr;
		}else{
			return metadataCache.relationThings.get(name);
		}
	}

	public static DataObject getRelationCache(String dataObjectPath, Object id){
		return null;
	}
}