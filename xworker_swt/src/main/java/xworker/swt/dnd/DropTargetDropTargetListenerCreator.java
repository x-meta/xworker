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

import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class DropTargetDropTargetListenerCreator {
    public static void create(final ActionContext actionContext){
    	final Thing self = (Thing) actionContext.get("self");
    	
    	DropTargetListener listener = new DropTargetListener(){
    		Thing thing = self;
    		ActionContext ac = actionContext;
    		public void invoke(DropTargetEvent event, String name){
                try{
                    Bindings bindings = ac.push(null);
                    bindings.put("event", event);
                    thing.doAction(name, ac);
                }finally{
                    ac.pop();
                }
    		}
    		
			@Override
			public void dragEnter(DropTargetEvent event) {
				invoke(event, "dragEnter");
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
				invoke(event, "dragLeave");
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				invoke(event, "dragOperationChanged");
			}

			@Override
			public void dragOver(DropTargetEvent event) {
				invoke(event, "dragOver");
			}

			@Override
			public void drop(DropTargetEvent event) {
				invoke(event, "drop");
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
				invoke(event, "dropAccept");
			}
    		
    	};
    
        ((DropTarget) actionContext.get("parent")).addDropListener(listener);
        actionContext.getScope(0).put(self.getString("name"), listener);
    }

}