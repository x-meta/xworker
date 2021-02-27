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

import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;

public class ShellActionsShellDialogCreator {
	private static final String TAG = ShellActionsShellDialogCreator.class.getName();
	
    public static Object run(final ActionContext actionContext) throws OgnlException{
        final Thing self = (Thing) actionContext.get("self");
        
        Shell shell = self.doAction("getShell", actionContext);
        if(shell == null) {
        	shell = self.doAction("createShell", actionContext);        			
        }
        if(shell == null) {
        	Executor.info(TAG, "Shell is null, can not open. action=" + self.getMetadata().getPath());
        	return null;
        }
        
        final ActionContext ac = self.doAction("getShellActionContext", actionContext, "shell", shell);
        final Boolean async = self.doAction("isAsync", actionContext);
        SwtDialog dialog = new SwtDialog(shell, ac);
        final Display display = shell.getDisplay();
        return dialog.open(new SwtDialogCallback() {
			@Override
			public void disposed(final Object result) {
				self.doAction("onDispose", actionContext, "result", result);
				
				if(result != null) {
					self.doAction("ok", actionContext, "result", result);
				}else {
					self.doAction("cancel", actionContext, "result", result);
				}
			}
        	
        }, !async);
    }

    public static ActionContext getShellActionContext(ActionContext actionContext) {
    	Shell shell = actionContext.getObject("shell");
    	ActionContext ac = (ActionContext) shell.getData("actionContext");
    	if(ac == null) {
    		ac = actionContext;
    	}
    	
    	return ac;
    }
    
    public static Shell createShell(ActionContext actionContext) {
        Thing self = (Thing) actionContext.get("self");
                
        Shell parentShell = self.doAction("getParentShell", actionContext);
        if(parentShell == null) {
	        if(Display.getCurrent() != null){
	        	parentShell = Display.getCurrent().getActiveShell();
	        }	        
        }
        
        Thing shellThing = self.doAction("getShellThing", actionContext);
        if(shellThing == null){
            Executor.info(TAG, "ShellThing is null, ation=" + self.getMetadata().getPath());
            return null;
        }
        
        ActionContext ac = new ActionContext();
        ac.put("parent", parentShell);
        ac.put("parentContext", actionContext);
        
        //带入的变量
        Map<String, Object> variables = self.doAction("getVariables", actionContext); 
        if(variables != null) {
        	ac.putAll(variables);
        }
        
        //初始变量设置
        for(Thing varSettings : self.getChilds("VarSettingOnInit")){
            for(Thing varSetting : varSettings.getChilds()){
                Object value = OgnlUtil.getValue(varSetting.getString("sourcePath"), actionContext);
                //log.info("setValue=" + value);
                OgnlUtil.setValue(varSetting.getString("targetPath"), ac, value);
            }
        }
        
        Shell shell = (Shell) shellThing.doAction("create", ac);
        shell.setData("actionContext", ac);
        
        //创建后变量
        for(Thing varSettings : self.getChilds("VarSettingOnCreated")){
            for(Thing varSetting : varSettings.getChilds()){
                Object value = OgnlUtil.getValue(varSetting.getString("sourcePath"), actionContext);
                OgnlUtil.setValue(varSetting.getString("targetPath"), ac, value);
            }
        }
        
        return shell;
    }
}