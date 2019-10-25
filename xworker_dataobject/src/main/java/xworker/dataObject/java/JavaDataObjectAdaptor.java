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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectCreator;
import xworker.dataObject.PageInfo;
import xworker.dataObject.ValidateResult;

public class JavaDataObjectAdaptor implements JavaDataObject{

	@Override
	public int create(DataObject theData, ActionContext actionContext) {
		return 0;
	}

	@Override
	public ValidateResult createValidate(DataObject theData,
			ActionContext actionContext) {
		return null;
	}

	@Override
	public int delete(DataObject theData, ActionContext actionContext) {
		return 0;
	}

	@Override
	public int deleteBatch(DataObject theData, Object conditionConfig,
			Map<String, Object> datas, ActionContext actionContext) {
		return 0;
	}

	@Override
	public String getAttributeDescriptor(Thing dataObject,	ActionContext actionContext) {
		return (String) DataObjectCreator.getAttributeDescriptor(actionContext);
	}

	@Override
	public String getMappingAttributeName(Thing dataObject,
			ActionContext actionContext) {
		return (String) DataObjectCreator.getMappingAttributeName(actionContext);
	}

	@Override
	public List<Object> getMappingFields(Thing dataObject,
			ActionContext actionContext) {
		return Collections.emptyList();
	}

	@Override
	public boolean isMappingAble(Thing dataObject, ActionContext actionContext) {
		return false;
	}

	@Override
	public List<DataObject> query(Thing dataObject, Object conditionConfig,
			Map<String, Object> datas, PageInfo pageInfo,
			ActionContext actionContext) {
		return Collections.emptyList();
	}

	@Override
	public int update(DataObject theData, ActionContext actionContext) {
		return 0;
	}

	@Override
	public int updateBatch(DataObject theData, Object conditionConfig,
			Map<String, Object> datas, ActionContext actionContext) {
		return 0;
	}

	@Override
	public ValidateResult updateValidate(DataObject theData,
			ActionContext actionContext) {
		return null;
	}

}