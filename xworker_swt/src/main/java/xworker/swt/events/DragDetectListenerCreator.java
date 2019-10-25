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
package xworker.swt.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;

public class DragDetectListenerCreator {
    public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IDragDetectListener listener = new IDragDetectListener(actionContext, self);
    	if(SwtUtils.isRWT()) {
    		listener.addToParent(SWT.Dispose, listener, actionContext);
    	}else {
    		listener.addToParent(actionContext, "addDragDetectListener", DragDetectListener.class);
    	}
		
    	actionContext.getScope(0).put(self.getString("name"), listener);      
	}

}