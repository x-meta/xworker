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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.ActionContainer;
import xworker.swt.util.PoolableControlFactory;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;
import xworker.swt.xworker.AttributeEditor;

public class ColorEditorCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
        
		//为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();
		context.put("parent", actionContext.get("parent"));
		context.put("parentActionContext", actionContext);
		
		//输入编辑器
		Thing inputThing = world.getThing("xworker.swt.xworker.attributeEditor.ColorEditor/@composite");
		Composite composite = (Composite) inputThing.doAction("create", context);
		
		xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Object attribute = gridData.source;
		Combo text = (Combo) context.get("colorText");
		for(String key : SwtUtils.getSWTKeys()) {
			if(key.startsWith("COLOR")) {
				text.add(key);
			}
		}
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
		    actionContext.getScope(0).put(((Thing) attribute).getString("name") + "Input", text);		    
		}
		
		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;        
	}
    
    public static void editButtonAction(ActionContext actionContext){
    	Combo colorText = (Combo) actionContext.get("colorText");   	

    	Shell shell =  (Shell) PoolableControlFactory.borrowControl(colorText.getShell(), 
    						"xworker.swt.widgets.ColorDialog", actionContext);
    				
    	//shell.setLocation(text.getLocation().x, text.getLocation().y);
    	ActionContainer ac = (ActionContainer) shell.getData("actions");			
    	ColorDialog dialog = (ColorDialog) ac.doAction("getColorDialog");
    	//dialog.setl
    	int[] rgb = UtilSwt.parseRGB(colorText.getText());
    	if(rgb != null){
    		dialog.setRGB(new RGB(rgb[0], rgb[1], rgb[2]));
    	}
    	//dialog.
    	RGB colorRgb = dialog.open();
    	if(colorRgb != null){
    		String rgbStr = UtilSwt.RGBToString(colorRgb);
    		colorText.setText("\"" + rgbStr + "\"");
    		//UtilSwt.setBackground(text, text.getText(), null);
    	}
    				
    	colorText.setFocus();
    }

}