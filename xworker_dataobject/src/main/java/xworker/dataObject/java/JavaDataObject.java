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

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.ValidateResult;

public interface JavaDataObject {
	/**
	 * 删除指定的数据，返回删除的行数。
	 * 
	 * @param theData
	 * @param actionContext
	 * @return
	 */
	public int delete(DataObject theData, ActionContext actionContext);
	
	/**
	 * <p>创建数据保存到存储中，自动初始化和通过Action初始化关键字的操作已被执行，其他方式的初始化关键字需自行实现。</p>
	 * 
	 * 如果创建失败，通常是抛出异常。
	 * 
	 * @param theData
	 * @param actionContext
	 * @return
	 */
	public int create(DataObject theData, ActionContext actionContext);
	
	/**
	 * 更新数据，返回更新的行数。
	 * 
	 * @param theData
	 * @param actionContext
	 * @return
	 */
	public int update(DataObject theData, ActionContext actionContext);
	
	/**
	 * 批量更新数据，返回更新的行数。
	 * 
	 * @param theData 数据对象
	 * @param conditionConfig 条件配置
	 * @param datas 条件数据
	 * @param actionContext 动作上下文
	 * 
	 * @return
	 */
	public int updateBatch(DataObject theData, Object conditionConfig, Map<String, Object> datas, ActionContext actionContext);
	
	/**
	 * 批量删除数据，返回更新的行数。
	 * 
	 * @param theData 数据对象
	 * @param conditionConfig 条件配置
	 * @param datas 条件数据
	 * @param actionContext 动作上下文
	 * 
	 * @return
	 */
	public int deleteBatch(DataObject theData, Object conditionConfig, Map<String, Object> datas, ActionContext actionContext);
	
	/**
	 * <p>查询，返回查询的结果。</p>
	 * 
	 * 如果pageInfo不为null，表示只取部分内容，此时需要设置pageInfo的总记录数和当前返回的数据。
	 * 
	 * @param dataObject 数据对象
	 * @param conditionConfig 查询条件配置
	 * @param datas 数据
	 * @param pageInfo 分页信息
	 * @param actionContext 变量上下文
	 * @return 查询结果
	 */
	public List<DataObject> query(Thing dataObject, Object conditionConfig, Map<String, Object> datas, PageInfo pageInfo, ActionContext actionContext);
	
	/**
	 * 创建之前的校验。
	 * 
	 * @param theData 数据对象
	 * @param actionContext 变量上下文
	 * 
	 * @return 校验结果
	 */
	public ValidateResult createValidate(DataObject theData, ActionContext actionContext);
	
	/**
	 * 更新时的校验。
	 * 
	 * @param theData 数据对象
	 * @param actionContext 变量上下文
	 * 
	 * @return 校验结果
	 */
	public ValidateResult updateValidate(DataObject theData, ActionContext actionContext);
	
	/**
	 * 是否可以映射，也就是通过通过工具编辑属性，比如编辑时映射数据库字段到数据对象的属性。
	 * 
	 * @param dataObject 数据对象
	 * @param actionContext 变量上下文
	 * @return 是否是可映射的
	 */
	public boolean isMappingAble(Thing dataObject, ActionContext actionContext);
	
	/**
	 * 获取可映射的属性列表。
	 * 
	 */
	public List<Object> getMappingFields(Thing dataObject, ActionContext actionContext);
	
	/**
	 * 获取映射到事物的属性名，比如数据库字段名映射到数据库数据对象的column属性。
	 * 
	 * @param dataObject 数据对象
	 * @param actionContext 变量上下文
	 * @return 映射属性名
	 */
	public String getMappingAttributeName(Thing dataObject, ActionContext actionContext);
	
	/**
	 * 返回数据对象的属性定义路径。
	 * 
	 * @param dataObject 数据对象
	 * @param actionContext 变量上下文
	 * @return 描述者
	 */
	public String getAttributeDescriptor(Thing dataObject, ActionContext actionContext);
	
}