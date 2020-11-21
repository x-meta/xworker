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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class DialogActionsDirectoryDialogCreator {
    public static Object run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        Shell shell = (Shell) self.doAction("getShell", actionContext);
        if(shell == null){
			//如果没有使用IDE
			shell = (Shell) XWorkerUtils.getIDEShell();
		}
        
        String text = self.doAction("getText", actionContext);
        String message = self.doAction("getMessage", actionContext);
        if(SwtUtils.isRWT()) {
        	ActionContext ac = new ActionContext();
        	ac.put("parent", shell);
        	ac.put("thing", self);
        	ac.put("parentContext", actionContext);
        	String filePath = self.doAction("getFilterPath", actionContext);
        	if(filePath == null) {
        		filePath = ".";
        	}
        	ac.put("filePath", filePath);
        	
        	Thing dialogThing = World.getInstance().getThing("xworker.swt.actions.prototypes.RWTDirectoryDialog");
        	Shell dialogShell = dialogThing.doAction("create", ac);
        	if(text != null && !"".equals(text)) {
        		dialogShell.setText(text);
        	}
        	dialogShell.setVisible(true);
        	return null;
        }else {
	        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NONE);
	        String filterPath = (String) self.doAction("getFilterPath", actionContext);
	        if(filterPath != null && !"".equals(filterPath)){
	            dialog.setFilterPath(UtilString.getString(filterPath, actionContext));
	        }
	        
	        if(message != null && !"".equals(message)){
	            dialog.setMessage(UtilString.getString(message, actionContext));
	        }
	        
	        if(text != null && !"".equals(text)) {
	        	dialog.setText(text);
	        }
	        
	        String dir = dialog.open();
	        self.doAction("open", actionContext, UtilMap.toMap("fileName", dir));
	        
	        return dir;
        }
    }

}