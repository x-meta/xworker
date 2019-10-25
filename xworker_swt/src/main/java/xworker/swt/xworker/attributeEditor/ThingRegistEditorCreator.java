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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.xworker.AttributeEditor;

public class ThingRegistEditorCreator {
    public static Object create(ActionContext actionContext){
	    World world = World.getInstance();
	    Thing self = (Thing) actionContext.get("self");
        
        //为不污染原始动作上下文，新建动作上下文
        ActionContext context = new ActionContext();
        context.put("parent", actionContext.get("parent"));
        context.put("parentActionContext", actionContext);
        
        //输入编辑器
        Thing inputThing = world.getThing("xworker.swt.xworker.attributeEditor.ThingRegistEditor/@composite");
        Composite composite = inputThing.doAction("create", context);
        
        Text text = (Text) context.get("text");
        text.setData("actionContext", context);
        
        //创建子节点
        if(actionContext.get("gridData") == null){
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
        	xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
            Thing attribute = gridData.source;
            
            //设置布局
            GridData textGridData = (GridData) context.get("textGridData");
            int _widthHint = (Integer) actionContext.get("_widthHint");
            textGridData.widthHint = _widthHint * attribute.getInt("size", 17);
            
            GridData cgridData = new GridData();
            cgridData.horizontalSpan = ThingDescriptorForm.getColspan(gridData.colspan);
            cgridData.verticalSpan = gridData.rowspan;
            ((Composite) context.get("composite")).setLayoutData(cgridData);
            
            if(actionContext.get("modifyListener") != null){
                text.addModifyListener((ModifyListener) actionContext.get("modifyListener"));
            }
            //Text输入框为输入属性编辑器的Model事物
            actionContext.getScope(0).put(gridData.source.getString("name") + "Input", text);
        }
        
        composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;   
    }
    
    public static void editButtonAction(final ActionContext actionContext){
    	World world = World.getInstance();
    	
    	Thing dialogObject = world.getThing("xworker.swt.xworker.attributeEditor.ThingRegistEditor/@shell");
    	Shell newShell = (Shell) dialogObject.doAction("create", actionContext);

    	SwtDialog dialog = new SwtDialog((Shell) actionContext.get("shell"), newShell, actionContext);
    	dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object r) {
				String result = (String) r;
		    	if(result != null)
		    	    ((Text) actionContext.get("text")).setText(result);
			}
    		
    	});
    	
    }

}