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
package xworker.swt.widgets;

import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.ActionContainer;

public class ColorDialogCreator {
    public static Object create(ActionContext actionContext){
    	//Thing self = (Thing) actionContext.get("self");
		
		Thing shellThing = World.getInstance().getThing("xworker.swt.widgets.ColorDialog/@color_dialog_shell");
		Shell shell = (Shell) shellThing.doAction("create", actionContext);
		
		ActionContext context = new ActionContext();
		context.put("shell", shell);
		Thing actionThing = World.getInstance().getThing("xworker.swt.widgets.ColorDialog/@shellActions");
		ActionContainer actionContainer = new ActionContainer(actionThing, context);
		shell.setData("actions", actionContainer);
		
		return shell;        
	}
    
    public static Object getColorDialog(ActionContext actionContext){
    	ColorDialog dialog = new ColorDialog((Shell) actionContext.get("shell"));
    	return dialog;
    }

}