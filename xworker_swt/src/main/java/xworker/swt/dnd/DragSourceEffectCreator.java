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

import org.eclipse.swt.dnd.DragSourceEffect;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TableDragSourceEffect;
import org.eclipse.swt.dnd.TreeDragSourceEffect;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;

public class DragSourceEffectCreator {
    public static Object create(final ActionContext actionContext){
    	final Thing self = (Thing) actionContext.get("self");
    	String selfType = self.getString("type");
    	//RWT的版本目前没有Table和Tree的Effect
    	if("TableDragSourceEffect".equals(selfType) && SwtUtils.isRWT() == false){
    		Table parent = (Table) actionContext.get("parent");
    		return new TableDragSourceEffect(parent);
    	}else if("TreeDragSourceEffect".equals(selfType) && SwtUtils.isRWT() == false){
    		return new TreeDragSourceEffect((Tree) actionContext.get("parent"));
    	}else{
    		Control parent = (Control) actionContext.get("parent");
    		return new DragSourceEffect(parent){
    			ActionContext ac = actionContext;
    			Thing thing = self;
    			
				@Override
				public void dragFinished(DragSourceEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragFinished", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void dragSetData(DragSourceEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragSetData", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void dragStart(DragSourceEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragStart", ac);
	                }finally{
	                    ac.pop();
	                }
				}
    			
    		};
    	}    
	}
}