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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class EventActionsEventExecutorCreator {
	private static Logger log = LoggerFactory.getLogger(EventActionsEventExecutorCreator.class);
	
    @SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        try{
            Object result = null;
            
            for(Thing actions : (List<Thing>) self.get("actions@0")){
                for(Thing action : actions.getChilds()){
                    result = action.getAction().run(actionContext, null, true);
        
                    if(ActionContext.RETURN == actionContext.getStatus() || 
                        ActionContext.CANCEL == actionContext.getStatus() || 
                        ActionContext.BREAK == actionContext.getStatus() || 
                        ActionContext.EXCEPTION == actionContext.getStatus()){
                        break;
                    }
                }
            }
            
            String successMessage = self.getString("successMessage");
            if(successMessage != null && !"".equals(successMessage)){
                String swtIcon = self.getString("exceptionIcon");
                String message = self.getString("successMessage");
                String title = self.getString("successTitle");
                
                try{
                    actionContext.push(null).put("result", result);
                    showMessage(swtIcon, title, message, actionContext);
                }finally{
                    actionContext.pop();
                }
            }
        }catch(Exception e){
            log.error("EventActions exception", e);
            String exceptionMessage = self.getString("exceptionMessage");
            if(exceptionMessage != null && !"".equals(exceptionMessage)){
                String swtIcon = self.getString("successIcon");
                String message = self.getString("exceptionMessage");
                String title = self.getString("exceptionTitle");
                
                try{
                    actionContext.push(null).put("e", e);
                    showMessage(swtIcon, title, message, actionContext);
                }finally{
                    actionContext.pop();
                }
            }
        }
    }
    
    public static void showMessage(String icon, String title, String message, ActionContext actionContext){
        Shell shell = Display.getCurrent().getActiveShell();
        //title = UtilTemplate.processString(actionContext, title);
        //message = UtilTemplate.processString(actionContext, message); 
        
        int style = SWT.OK;
        if("INFO".equals(icon)){
            style = style | SWT.ICON_INFORMATION;
        }else if("WARNING".equals(icon)){
            style = style | SWT.ICON_WARNING;
        }else{
            style = style | SWT.ICON_ERROR;
        }
    
        MessageBox  box = new MessageBox(shell, style);
        box.setText(title);
        box.setMessage(message);
        box.open();
    }

}