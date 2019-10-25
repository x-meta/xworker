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
package xworker.swt.dnd;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.SwtListener;

public class DropTargetListenerCreator {
    public static void create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        SwtListener listener = new SwtListener(self, actionContext);
        String type = self.getString("type");
        Widget parent = (Widget) actionContext.get("parent");
        if("DragEnter".equals(type)){
        	parent.addListener(DND.DragEnter, listener);
        }else if("DragLeave".equals(type)){
        	parent.addListener(DND.DragLeave, listener);
        }else if("DragOver".equals(type)){
        	parent.addListener(DND.DragOver, listener);
        }else if("DragOperationChanged".equals(type)){
        	parent.addListener(DND.DragOperationChanged, listener);
        }else if("DropAccept".equals(type)){
        	parent.addListener(DND.DropAccept, listener);
        }else if("Drop".equals(type)){
        	parent.addListener(DND.Drop, listener);
        }
    }

}