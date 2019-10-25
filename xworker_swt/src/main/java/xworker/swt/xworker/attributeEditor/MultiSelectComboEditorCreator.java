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
package xworker.swt.xworker.attributeEditor;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.xworker.AttributeEditor;

public class MultiSelectComboEditorCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
    	xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Thing attribute = gridData.source;
		Thing self = actionContext.getObject("self");
		
		//为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();		
		context.put("parentContext", actionContext);		
		context.put("attribute", attribute);
		
		//输入编辑器
		Thing inputThing = new Thing("xworker.swt.xworker.MultiSelectCombo");
		inputThing.put("BORDER", "true");
		inputThing.put("READONLY", "false");
		String inputAttrs = attribute.getString("inputattrs");        
        if(inputAttrs != null && !"".equals(inputAttrs)){
        	String params[] = inputAttrs.split("[,]");
        	if(params.length > 1){
        		inputThing.put("popWinMaxRows", params[1]);
        	}
        	if(params.length > 2){
        		inputThing.put("filter", params[2]);
        	}
        }else{        
        	inputThing.put("filter", "false");
        }
		
		//数据仓库
	    Thing dataStoreThing = ThingDescriptorForm.getDataStoreThing(attribute);
	    if(dataStoreThing != null){
	    	
	        Thing dataStore = (Thing) dataStoreThing.doAction("create", context);
	        context.put("dataStore", dataStore);
	        inputThing.put("dataSource", "dataStore");
	        inputThing.put("dataName", "dataStore");
	        inputThing.put("idName", getIdField(dataStore));
	        inputThing.put("labelName", getLabelField(dataStore));
	    }else{
		    //自定义数据
		    inputThing.put("dataSource", "selfValues");
		    for(Thing value : (java.util.List<Thing>) attribute.get("value@")){
		        inputThing.addChild(value, false);
		    }
		}
		
	    context.put("parent", actionContext.get("parent"));
		Composite composite = (Composite) inputThing.doAction("create", context);
		Text text =  (Text) composite.getData(AttributeEditor.INPUT_CONTROL);
		//Composite composite = (Composite) text.getData("composite");
		
		//创建子节点
		if(actionContext.get("isThingEditor") == null){
		    //不支持在一般的swt界面中被调用    
		}else{
		    //在事物编辑器里被调用
		    //log.info("layoutData=" + layoutData);
		    composite.setLayoutData((GridData) actionContext.get("layoutData"));
		    
		    if(actionContext.get("modifyListener") != null){
		        text.addModifyListener((ModifyListener) actionContext.get("modifyListener"));
		    }
		    //Text输入框为输入属性编辑器的Model事物
		    actionContext.getScope(0).put(attribute.getString("name") + "Input", composite.getData(AttributeEditor.ACTIONCONTAINER));
		}
		
		//composite.setData(AttributeEditor.ACTIONCONTAINER, comboAction);
		return composite;        
	}

	/**
	 * 通过数据仓库取数据的标识字段，只返回第一个数据对象关键字字段，如果数据仓库没有定义数据对象或数据对象没有关键字，
	 * 返回null。
	 * 
	 * @param dataStore 数据仓库
	 * @return 标签
	 */
	public static String getIdField(Thing dataStore){
		Thing dataObject = (Thing) dataStore.get("dataObject");
		if(dataObject != null){
			for(Thing attr : dataObject.getChilds("attribute")){
				if(attr.getBoolean("key")){
					return attr.getString("name");
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 通过数据仓库区标签字段，先从数据仓库的定义取，其次通过数据对象的displayName属性取。
	 * 
	 * @param dataStore 数据仓库
	 * @return 标签
	 */
	public static String getLabelField(Thing dataStore){
		Thing config = (Thing) dataStore.get("config");
		Thing dataObject = (Thing) dataStore.get("dataObject");
		 
		//首先取DataStore定义的标签字段
		String labelField = config.getString("labelField");
		 
		//其次取数据对象上定义的标签字段
        if((labelField == null || "".equals(labelField)) && dataObject != null){
            labelField = dataObject.getString("displayName");
        }
        
        return labelField;
	}
}