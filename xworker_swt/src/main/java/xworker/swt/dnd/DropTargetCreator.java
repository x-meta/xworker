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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEffect;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class DropTargetCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = 0;
		if(self.getBoolean("DROP_NONE")){
			style = style | DND.DROP_NONE;
		}
		if(self.getBoolean("DROP_COPY")){
			style = style | DND.DROP_COPY;
		}
		if(self.getBoolean("DROP_MOVE")){
			style = style | DND.DROP_MOVE;
		}
		if(self.getBoolean("DROP_LINK")){
			style = style | DND.DROP_LINK;
		}

		Control parent = (Control) actionContext.get("parent");
		DropTarget dragTarget = new DropTarget(parent, style);
		
		Control oldParent = parent;
		Bindings bindings = actionContext.push(null);
		try{
		    bindings.put("parent", dragTarget);
		
		    List<Transfer> transfers = new ArrayList<Transfer>();
		    for(Thing transferThing : (List<Thing>) self.get("Transfer@")){
		    	Transfer transfer = (Transfer) transferThing.doAction("create", actionContext);
		        if(transfer != null){
		            transfers.add(transfer);
		        }
		    }
		    dragTarget.setTransfer(transfers.toArray(new Transfer[transfers.size()]));
		    
		    for(Thing child : self.getAllChilds()){
		        if("DropTargetEffect".equals(child.getThingName())){
		            bindings.put("parent", oldParent);
		            DropTargetEffect effect = (DropTargetEffect) child.doAction("create", actionContext);
		            if(effect != null){
		                //log.info("effct=" + effect);
		                dragTarget.setDropTargetEffect(effect);
		                dragTarget.addDropListener(effect);
		            }
		        }else{
		            bindings.put("parent", dragTarget);
		            child.doAction("create", actionContext);
		        }
		    }
		}finally{
		    actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getString("name"), dragTarget);
		return dragTarget;        
	}

}