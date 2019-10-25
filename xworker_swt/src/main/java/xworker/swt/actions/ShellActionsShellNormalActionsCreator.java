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

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class ShellActionsShellNormalActionsCreator {
	private static Logger log = LoggerFactory.getLogger(ShellActionsShellNormalActionsCreator.class);
	
    public static void run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        String shellName = self.getString("shellName");
        if(shellName == null || "".equals(shellName)){
            shellName = self.getString("name");
        }
        
        Shell shell = (Shell) OgnlUtil.getValue(shellName, actionContext);
        if(shell == null){
            log.warn("ShellNormalActions: shell is not exists, name=" + self.getString("name"));
            return;
        }
        
        String method = self.getString("method");
        if("active".equals(method)){
        	shell.setActive();
        }else if("dispose".equals(method)){
        	//应该使用Close
        	shell.close();
        	//shell.dispose();
        }else if("enable".equals(method)){
        	shell.setEnabled(true);
        }else if("disable".equals(method)){
        	shell.setEnabled(false);
        }else if("fullScreen".equals(method)){
        	shell.setFullScreen(true);
        }else if("unFullScreen".equals(method)){
        	shell.setFullScreen(false);
        }else if("visible".equals(method)){
        	shell.setVisible(true);
        }else if("unVisible".equals(method)){
        	shell.setVisible(false);
        }
    }
}