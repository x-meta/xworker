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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.dnd.DropTargetEffect;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilJava;

import xworker.util.JavaUtils;

public class DropTargetEffectCreator {
    public static Object create(final ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	final Thing self = (Thing) actionContext.get("self");
    	String selfType = self.getString("type");
    	if("TableDropTargetEffect".equals(selfType)){
    		return new TableDropTargetEffect((Table) actionContext.get("parnet"));
    	}else if("TreeDropTargetEffect".equals(selfType)){    		
    		try {
    			return JavaUtils.newInstance("org.eclipse.swt.dnd.TreeDropTargetEffect", new Class<?>[] {Tree.class}, 
    					new Object[] {actionContext.get("parent")});
    		}catch(Exception e) {
    			return null;
    		}
//    		return new TreeDropTargetEffect((Tree) actionContext.get("parent"));
    	}else if("StyledTextDropTargetEffect".equals(selfType)){
    		return UtilJava.newInstance("org.eclipse.swt.custom.StyledTextDropTargetEffect", actionContext.get("parent"));
    	}else{
    		return new DropTargetEffect((Control) actionContext.get("parent")){
    			Thing thing = self;
    			ActionContext ac = actionContext;
    			
				@Override
				public void dragEnter(DropTargetEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragEnter", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void dragLeave(DropTargetEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragLeave", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void dragOperationChanged(DropTargetEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragOperationChanged", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void dragOver(DropTargetEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dragOver", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void drop(DropTargetEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("drop", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				public void dropAccept(DropTargetEvent event) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("event", event);
	                    thing.doAction("dropAccept", ac);
	                }finally{
	                    ac.pop();
	                }
				}
    			
    		};
    	}		  
	}
}