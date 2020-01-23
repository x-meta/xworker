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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilAction;

import xworker.lang.actions.ActionUtils;
import xworker.swt.util.DialogCallback;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtUtils;

public class MessageBoxActionsMessageBoxCreator {
	private static Logger logger = LoggerFactory.getLogger(MessageBoxActionsMessageBoxCreator.class);
    public static Object run(final ActionContext actionContext){
        final Thing self = (Thing) actionContext.get("self");
        
        //当前需要有活动的shell
        Shell shell = (Shell) self.doAction("getShell", actionContext); 
        if(shell == null){
        	shell = Display.getCurrent().getActiveShell();
        }
        if(shell == null){
            return null;
        }
        
        String title = self.doAction("getTitle", actionContext); 
        		//UtilString.getString(self.getString("title"), actionContext);
        //if(title != null){
        //    title = UtilTemplate.processString(actionContext, title);
        //}
        String message = self.doAction("getMessage", actionContext); 
        if(message == null) {
        	return null;
        }
        		//UtilString.getString(self.getString("message"), actionContext);
        //if(message != null){
        //   message = UtilTemplate.processString(actionContext, message);
        //}
        
        int style = SWT.NONE;
        String icon = self.getString("icon");
        if("ICON_ERROR".equals(icon)){
        	style = style | SWT.ICON_ERROR;
        }else if("ICON_INFORMATION".equals(icon)){
        	style = style | SWT.ICON_INFORMATION;
        }else if("ICON_QUESTION".equals(icon)){
        	style = style | SWT.ICON_QUESTION;
        }else if("ICON_WARNING".equals(icon)){
        	style = style | SWT.ICON_WARNING;
        }else if("ICON_WORKING".equals(icon)){
        	style = style | SWT.ICON_WORKING;
        }
       
        String buttons = self.getString("buttons");
        if("OK".equals(buttons)){
        	style = style | SWT.OK;
        }else if("OK | CANCEL".equals(buttons)){
        	style = style | SWT.OK | SWT.CANCEL;
        }else if("YES | NO".equals(buttons)){
        	style = style | SWT.YES | SWT.NO;
        }else if("YES | NO | CANCEL".equals(buttons)){
        	style = style | SWT.YES | SWT.NO | SWT.CANCEL;
        }else if("RETRY | CANCEL".equals(buttons)){
        	style = style | SWT.RETRY | SWT.CANCEL;
        }else if("ABORT | RETRY | IGNORE".equals(buttons)){
        	style = style | SWT.ABORT | SWT.RETRY | SWT.IGNORE;
        }
        
        MessageBox box = new MessageBox(shell, style);
        box.setText(title);
        box.setMessage(message);
        
        final Map<String, Object> vars = self.doAction("getVariables", actionContext);
        if(SwtUtils.isRWT()) {        	
        	Thing swt = World.getInstance().getThing("xworker.swt.SWT");
        	swt.doAction("openMessageBoxRWT", actionContext, "messageBox", box, "callback", new DialogCallback() {
				@Override
				public void dialogClosed(int returnCode) {
					try {
						switch(returnCode){
			            case SWT.OK:
			            	ActionUtils.executeActionAndChild(self, "ok", actionContext, vars);
			            	break;
			            case SWT.CANCEL:
			            	ActionUtils.executeActionAndChild(self, "cancel", actionContext, vars);
			            	break;
			            case SWT.YES:
			            	ActionUtils.executeActionAndChild(self, "yes", actionContext, vars);
			            	break;
			            case SWT.NO:
			            	ActionUtils.executeActionAndChild(self, "no", actionContext, vars);
			            	break;
			            case SWT.RETRY:
			            	ActionUtils.executeActionAndChild(self, "retry", actionContext, vars);
			            	break;
			            case SWT.ABORT:
			            	ActionUtils.executeActionAndChild(self, "abort", actionContext, vars);
			            	break;
			            case SWT.IGNORE:
			            	ActionUtils.executeActionAndChild(self, "ignore", actionContext, vars);
			            	break;		     
						}
					}catch(Exception e) {
						logger.error("openMessageBoxRWT error, thing=" + self.getMetadata().getPath(), e);
					}
				}
        	});
        	return null;
        }else {
	        int result = box.open();
	        switch(result){
	            case SWT.OK:
	            	return ActionUtils.executeActionAndChild(self, "ok", actionContext, vars);
	            case SWT.CANCEL:
	            	return ActionUtils.executeActionAndChild(self, "cancel", actionContext, vars);
	            case SWT.YES:
	            	return ActionUtils.executeActionAndChild(self, "yes", actionContext, vars);
	            case SWT.NO:
	            	return ActionUtils.executeActionAndChild(self, "no", actionContext, vars);
	            case SWT.RETRY:
	            	return ActionUtils.executeActionAndChild(self, "retry", actionContext, vars);
	            case SWT.ABORT:
	            	return ActionUtils.executeActionAndChild(self, "abort", actionContext, vars);
	            case SWT.IGNORE:
	            	return ActionUtils.executeActionAndChild(self, "ignore", actionContext, vars);	            			
	            default:
	                return "OK";
	        }
        }
    }
 
    public static Object prompt(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        //当前需要有活动的shell
        Shell shell = Display.getCurrent().getActiveShell();
        if(shell == null){
            return null;
        }
        
        String title = self.doAction("getTitle", actionContext); 
        String message = self.doAction("getMessage", actionContext);
        String content = self.doAction("getText", actionContext);
        
        Thing shellThing = World.getInstance().getThing("xworker.swt.actions.PromptShell");
        ActionContext ac = new ActionContext();
        ac.put("parent", shell);
        ac.put("thing", self);
        ac.put("context", actionContext);
        ac.put("title", title);
        ac.put("message", message);
        
        Shell dialogShell = (Shell) shellThing.doAction("create", ac);
        if(content != null) {
        	((Text) ac.getObject("text")).setText(content);
        }

        if(SwtUtils.isRWT()) {
        	SwtDialog dialog = new SwtDialog(shell, dialogShell, actionContext);
        	dialog.open(null);
        	return null;
        }else {
        	return SwtDialog.open(dialogShell, ac);
        }
    }
    
}