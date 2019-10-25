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
package xworker.ui.function.xworker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;

import xworker.swt.util.ResourceManager;
import xworker.ui.UIHandler;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class ObjectFieldAndMethodSelector {
	@SuppressWarnings("unchecked")
	public static void doEvent(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		if(uiRequest != null){
			FunctionRequest functionRequest = (FunctionRequest) uiRequest.getRequestMessage();
			if(event.widget == actionContext.get("cancelButton")){
				if(functionRequest.getCallback() == null){
					FunctionManager.sendRequest(functionRequest, actionContext);
					return;
				}else{
					functionRequest.getCallback().cancel(actionContext);
					return;
				}
			}

			Tree tree = (Tree) actionContext.get("tree");
			if(tree.getSelection().length > 0){
				TreeItem item = tree.getSelection()[0];
				Map<String, Object> data = (Map<String, Object>) item.getData();
				Object value = null;
				if(event.widget == actionContext.get("selectFieldButton")){
					value = data.get("proto");
				}else if(event.widget == actionContext.get("selectMethodButton")){
					value = data.get("proto");
				}
				
				if(functionRequest.getCallback() == null){
					FunctionManager.finishRequest(functionRequest, value);
					return;
				}else{
					functionRequest.getCallback().ok(value, actionContext);
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void init(ActionContext actionContext){
		//函数请求和对象
		Object object = null;
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		if(uiRequest != null){
			FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
			FunctionParameter param = fnRequest.getParameter("object");
			object = param.getValue();
		}else{
			object = actionContext.get("tree");
		}
		
		List<Map<String, Object>> fieldAndMethods = new ArrayList<Map<String, Object>>(); 
		//获取对象的字段和方法
		Class cls = object.getClass();
		for(Field field : cls.getDeclaredFields()){
			if(field.isSynthetic()){
		        continue;
		    }
		    if(!Modifier.isPublic(field.getModifiers())){
		        continue;
		    }
		    
		    Map<String, Object> f = new HashMap<String, Object>();
		    f.put("name", field.getName());
		    f.put("label", field.getName() + " " + field.getType());
		    f.put("type", "field");
		    f.put("proto", field);
		    fieldAndMethods.add(f);
		}
		
		for(Method method : cls.getDeclaredMethods()){
			if(!Modifier.isPublic(method.getModifiers())){
		        continue;
		    }
			
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("name", method.getName());
			m.put("type", "method");
			m.put("proto", method);
			String label = method.getName() + "(";
			
			boolean first = true;
			for(Type paramType : method.getGenericParameterTypes()){
				if(!first){
					label = label + ", ";
				}
				
				label = label + ((Class) paramType).getSimpleName();
			}
			label = label + ") " + method.getReturnType().getSimpleName();
			m.put("label", label);
			fieldAndMethods.add(m);
		}
			
		//在树上显示字段和方法
		try{
			Tree tree = (Tree) actionContext.get("tree");
			Bindings bindings = actionContext.push();
			bindings.put("parent", tree);
			
			Image filedImage = (Image) ResourceManager.createIamge("/icons/bullet_green.png", actionContext);
			Image methodImage = (Image) ResourceManager.createIamge("/icons/cog_go.png", actionContext);
			
			for(Map<String, Object> data : fieldAndMethods){
				TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setText((String) data.get("label"));
				item.setData(data);
				if("field".equals(data.get("type"))){
					item.setImage(filedImage);
				}else{
					item.setImage(methodImage);
				}
			}
		}finally{
			actionContext.pop();
		}
		
		//显示文档
		UIHandler browserHandler = (UIHandler) actionContext.get("browserHandler");		
		if(uiRequest != null){
			FunctionRequest functionRequest = (FunctionRequest) uiRequest.getRequestMessage();
			if(functionRequest != null){
				FunctionCallback callback = functionRequest.getCallback();
				if(callback != null){
					browserHandler.requestUI(new UIRequest(callback.getParam().getDescriptor(), actionContext),  actionContext);
				}else{
					browserHandler.requestUI(new UIRequest(functionRequest.getDescriptor(), actionContext),  actionContext);
				}
			}
		}
	}
}