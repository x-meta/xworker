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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.xworker.AttributeEditor;

public class OpenWindowEditorCreator {
    public static Object create(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		//为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();
		context.put("parent", actionContext.get("parent"));
		context.put("parentContext", actionContext);
		context.put("explorerActions", Designer.getExplorerActions());
		context.put("explorerContext", Designer.getExplorerActionContext());
		context.put("utilBrowser", actionContext.get("utilBrowser"));
		context.put("thing", actionContext.get("thing"));
		
		//输入编辑器
		Thing inputThing = world.getThing("xworker.swt.xworker.attributeEditor.OpenWindowEditor/@composite");
		Composite composite = (Composite) inputThing.doAction("create", context);
		
		xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Thing attribute = gridData.source;
		context.put("attribute", attribute);
		Text text = (Text) context.get("text");
		
		//在事物编辑器里被调用
		composite.setLayoutData((GridData) actionContext.get("layoutData"));
		
		if(actionContext.get("modifyListener") != null){
		    text.addModifyListener((ModifyListener) actionContext.get("modifyListener"));
		}
		//Text输入框为输入属性编辑器的Model事物
		actionContext.getScope(0).put(attribute.getString("name") + "Input", text);
		
		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;        
	}
    
    public static void editButtonAction(ActionContext actionContext){
    	final Text text = (Text) actionContext.get("text");
    	//窗口只打开一个
    	Shell oldShell = (Shell) text.getData("shell");
    	if(oldShell != null && !oldShell.isDisposed()) {
    		oldShell.setVisible(true);
    		oldShell.setActive();
    		return;
    	}
    	
    	//窗口事物
    	Shell shell = text.getShell();
    	Thing attribute = (Thing) actionContext.get("attribute");
    	
    	String inputAttrs = attribute.getString("inputattrs");
    	if(inputAttrs == null || "".equals(inputAttrs)){
    	    inputAttrs = "xworker.swt.xworker.attributeEditor.OpenWindows";
    	    //showError(shell, "属性的输入扩展属性没有设置弹出窗口的路径！");
    	    //return;
    	}

    	String[] ws = inputAttrs.split("[|]");
    	Thing winThing = World.getInstance().getThing(ws[0]);
    	if(winThing == null){
    	    showError(shell, "Window not exists，path=" + ws[0]);
    	    return;
    	}

    	//参数
    	String param = "";
    	if(ws.length >= 2){
    	    param = ws[1];
    	}
    	
    	ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
    	ActionContext ac = new ActionContext();
    	ac.put("parent", shell);
    	ac.put("value", text.getText());
    	ac.put("text", text);
    	ac.put("param", param);
    	ac.put("params", UtilString.getParams(param, ","));
    	ac.put("parentContext", actionContext.get("parentContext"));
    	ac.put("utilBrowser", actionContext.get("utilBrowser"));
    	ac.put("explorerActions", actionContext.get("explorerActions"));
    	ac.put("explorerContext", actionContext.get("explorerContext"));
    	ac.put("textContext", actionContext.get("actionContext"));
    	ac.put("parentModel", parentContext.get("__editModel__model"));
    	if(ac.get("parentModel") == null){
    	    ac.put("parentModel", parentContext.get("model"));
    	}
    	ac.put("parentForm", parentContext.get("__dataObjectForm"));
    	ac.put("thing", parentContext.get("thing"));

    	Shell winShell = (Shell) winThing.doAction("create", ac);
    	text.setData("shell", winShell);
    	if(winShell != null && !winShell.isDisposed()){
    	    SwtDialog dialog = new SwtDialog(shell, winShell, ac);
    	    dialog.open(new SwtDialogCallback() {
				@Override
				public void disposed(Object result) {
					if(result != null && !text.isDisposed()) {
						text.setText(String.valueOf(result));
					}
				}
    	    	
    	    });
    	    
    	}
    }
    
    public static void showError(Shell shell, String message){
    	MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
	    box.setText("OpenWindowEditor");
	    box.setMessage(message);
	    box.open();
	}

}