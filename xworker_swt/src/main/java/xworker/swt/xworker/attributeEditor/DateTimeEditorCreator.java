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
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.xworker.AttributeEditor;

public class DateTimeEditorCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		//为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();
		context.put("parent", actionContext.get("parent"));
		context.put("parentActionContext", actionContext);
		
		xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Thing attribute = gridData.source;
		
		//输入编辑器
		Thing inputThing = new Thing("xworker.swt.xworker.DatePickerCombo");
		inputThing.put("pattern", attribute.getString("pattern"));
		inputThing.put("BORDER", "true");
		inputThing.put("showTime", "true");
				
		Composite composite =inputThing.doAction("create", context);
			
		Text text = (Text) composite.getData(AttributeEditor.INPUT_CONTROL);
		//创建子节点
		if(actionContext.get("isThingEditor") == null){
		    //在一般的swt界面中被调用
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", context.get("composite"));
		    try{
		        for(Thing child : self.getAllChilds()){
		            child.doAction("create", actionContext);
		        }
		    }finally{
		        actionContext.pop();
		    }
		    actionContext.getScope(0).put(self.getString("name"), text);
		}else{
		    //在事物编辑器里被调用
		    composite.setLayoutData((GridData) actionContext.get("layoutData"));
		    
		    if(actionContext.get("modifyListener") != null){
		        text.addModifyListener((ModifyListener) actionContext.get("modifyListener"));
		    }
		    //Text输入框为输入属性编辑器的Model事物
		    actionContext.getScope(0).put(attribute.getString("name") + "Input", text);
		}
		
		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		
		return composite;       
	}

}