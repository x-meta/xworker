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
package xworker.swt.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class ThingFormActions {
	/**
	 * 设置表单的部分值。
	 * 
	 * @param actionContext
	 * @throws OgnlException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setPartialValues(ActionContext actionContext) throws OgnlException {
		Thing self = (Thing) actionContext.get("self");

		//表单变量
		Thing form = (Thing) self.doAction("getForm", actionContext);
		Map values = (Map) self.doAction("getValues", actionContext);
		
		if(values == null){
			values = new HashMap<String, Object>();
		}
		Map formValues = (Map) form.doAction("getValues", actionContext);
		formValues.putAll(values);
		
		form.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", formValues}));
	}
	
	/**
	 * 设置值。
	 * 
	 * @param actionContext
	 * @throws OgnlException
	 */
	public static void setValues(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");

		Thing form = (Thing) self.doAction("getForm", actionContext);
		Object values = self.doAction("getValues", actionContext);
		
		if(values == null){
			values = new HashMap<String, Object>();
		}
		
		form.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", values}));
	}
	
	/**
	 * 获取表单的值。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getFormValues(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//表单变量
		Object values = null;
		Thing form = (Thing) self.doAction("getForm", actionContext);
		if(self.getBoolean("validate")){
			if(UtilData.isTrue(form.doAction("validate", actionContext))){
				values = form.doAction("getValues", actionContext);
			}else{
				//校验不通过返回null
			}
		}else{
			values = form.doAction("getValues", actionContext);
		}
		
		String varName = self.getStringBlankAsNull("varName");
		UtilAction.putVarByActioScope(self, varName, values, actionContext);
		
		return values;
	}
	
	/**
	 * 执行表单的校验。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static boolean validate(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//表单变量
		Thing form = (Thing) self.doAction("getForm", actionContext);
		
		//表单上下文
		ActionContext formActionContext = (ActionContext) form.getData("formContext");
		
		boolean validate = (Boolean) form.doAction("validate", formActionContext);
		String varName = self.getStringBlankAsNull("varName");
		UtilAction.putVarByActioScope(self, varName, validate, actionContext);
		
		return validate;
	}
	
	public static Thing getForm(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Object obj = UtilData.getObject(self, "formVarName", actionContext);
		
		if(obj instanceof Thing){
			return (Thing) obj;
		}else{
			throw new ActionException("should return a Thing, current is " + obj + ", path=" + self.getMetadata().getPath());
		}
	}
	
	public static Object getValues(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		return UtilData.getObject(self, "valueVarName", actionContext);		
	}
	
	/**
	 * 获取属性的控件。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Control getAttributeControl(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//表单变量
		Thing form = (Thing) self.doAction("getForm", actionContext);
		
		//表单上下文
		ActionContext formActionContext = (ActionContext) form.getData("formContext");
		
		//属性控件
		String attributeControlName = self.getString("attributeName") + "Input";
		Control control = (Control) formActionContext.get(attributeControlName);
		
		//保存变量
		String varName = self.getStringBlankAsNull("varName");
		UtilAction.putVarByActioScope(self, varName, control, actionContext);
		
		return control;
	}
}