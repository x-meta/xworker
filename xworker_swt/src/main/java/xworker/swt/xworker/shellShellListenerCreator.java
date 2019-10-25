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
package xworker.swt.xworker;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

import xworker.swt.util.PoolableControlFactory;

public class shellShellListenerCreator {
    public static void shellClosed(ActionContext actionContext){
        Shell shell = (Shell) actionContext.get("shell");
        shell.setVisible(false);
        Object event = actionContext.get("event");        
        if(event instanceof TypedEvent){
        	//((TypedEvent) event).doit = false;
        }else if(event instanceof Event){
        	((Event) event).doit = false;
        }
        
        ((StyledText) actionContext.get("styledText")).setData("frDialog", null);
        PoolableControlFactory.returnControl((Control) actionContext.get("parent"), "xworker.swt.xworker.CodeEditorSearchDialog", shell);
    }

}