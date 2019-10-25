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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.xworker.AttributeEditor;

public class TextButtonEditor {
	public static Object create(ActionContext actionContext) {
		World world = World.getInstance();
		//Thing self = actionContext.getObject("self");
		
		// 为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();
		context.put("parent", actionContext.get("parent"));
		context.put("parentContext", actionContext);
		context.put("explorerActions", actionContext.get("explorerActions"));
		context.put("explorerContext", actionContext.get("explorerContext"));
		context.put("utilBrowser", actionContext.get("utilBrowser"));

		xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		
		Thing attribute = gridData.source;
		String inputAttrs = attribute.getString("inputattrs");
		Map<String, String> params = UtilString.getParams(inputAttrs, "&");
		
		// 输入编辑器
		Thing inputThing = null;
		if("true".equals(params.get("textReadOnly"))){
			inputThing = world.getThing("xworker.swt.xworker.attributeEditor.TextButtonEditor/@composite1");
		}else{
			inputThing = world.getThing("xworker.swt.xworker.attributeEditor.TextButtonEditor/@composite");
		}
		Composite composite = (Composite) inputThing.doAction("create", context);
		
		context.put("attribute", attribute);
		Text text = (Text) context.get("text");
		Button editButton = (Button) context.get("editButton");
		
		String buttonText= params.get("buttonText");
		if(buttonText != null && !"".equals(buttonText)){
			editButton.setText(buttonText);
		}
		String actionName = params.get("buttonActionName");
		if(actionName == null || "".equals(actionName)){
			actionName = "run";
		}
		editButton.setData("action", actionName);
		
		// 在事物编辑器里被调用
		composite.setLayoutData((GridData) actionContext.get("layoutData"));

		if (actionContext.get("modifyListener") != null) {
			text.addModifyListener((ModifyListener) actionContext
					.get("modifyListener"));
		}
		// Text输入框为输入属性编辑器的Model事物
		actionContext.getScope(0).put(attribute.getString("name") + "Input",
				text);

		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;   
	}

	public static void editButtonAction(ActionContext actionContext) {
		Text text = (Text) actionContext.get("text");
		Button editButton = (Button) actionContext.get("editButton");
		Thing attribute = (Thing) actionContext.get("attribute");
		
		ActionContext parentContext = (ActionContext) actionContext
				.get("parentContext");
		ActionContext ac = new ActionContext();
		ac.put("value", text.getText());
		ac.put("text", text);
		ac.put("parentContext", actionContext.get("parentContext"));
		ac.put("utilBrowser", actionContext.get("utilBrowser"));
		ac.put("explorerActions", actionContext.get("explorerActions"));
		ac.put("explorerContext", actionContext.get("explorerContext"));
		ac.put("textContext", actionContext.get("actionContext"));
		ac.put("parentModel", parentContext.get("__editModel__model"));
		if (ac.get("parentModel") == null) {
			ac.put("parentModel", parentContext.get("model"));
		}
		ac.put("parentForm", parentContext.get("__dataObjectForm"));
		ac.put("attribute", attribute);
		ac.put("button", editButton);
		
		String action = (String) editButton.getData("action");
		attribute.doAction(action, actionContext);
	}

	public static void showError(Shell shell, String message) {
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		box.setText("OpenWindowEditor");
		box.setMessage(message);
		box.open();
	}

}