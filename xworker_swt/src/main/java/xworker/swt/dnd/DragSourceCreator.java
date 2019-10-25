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
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEffect;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class DragSourceCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = 0;
		if(UtilString.eq(self, "DROP_NONE", "true")){
			style = style | DND.DROP_NONE;
		}
		if(UtilString.eq(self, "DROP_COPY", "true")){
			style = style | DND.DROP_COPY;
		}
		if(UtilString.eq(self, "DROP_MOVE", "true")){
			style = style | DND.DROP_MOVE;
		}
		if(UtilString.eq(self, "DROP_LINK", "true")){
			style = style | DND.DROP_LINK;
		}
		
		Control parent = (Control) actionContext.get("parent");
		DragSource dragSource = new DragSource(parent, style);
		
		Control oldParent = parent;
		Bindings bindings = actionContext.push(null);
		try{
		    bindings.put("parent", dragSource);
		
		    List<Transfer> transfers = new ArrayList<Transfer>();
		    for(Thing transferThing : (List<Thing>) self.get("Transfer@")){
		    	Transfer transfer = (Transfer) transferThing.doAction("create", actionContext);
		        if(transfer != null){
		            transfers.add(transfer);
		        }
		    }
		    dragSource.setTransfer(transfers.toArray(new Transfer[transfers.size()]));
		    
		    Thing effectThing = (Thing) self.get("DragSourceEffect@0");
		    bindings.put("parent", oldParent);
		    if(effectThing != null){
		    	DragSourceEffect effect = (DragSourceEffect) effectThing.doAction("create", actionContext);
		        if(effect != null){
		            dragSource.setDragSourceEffect(effect);
		        }
		    }
		    
		    bindings.put("parent", dragSource);
		    for(Thing child : (List<Thing>) self.get("Listener@")){
		        child.doAction("create", actionContext);
		    }
		}finally{
		    actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getString("name"), dragSource);
		return dragSource;       
	}
}