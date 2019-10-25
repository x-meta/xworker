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
package xworker.java.swing;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeListener;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class BoundedRangeModelCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String name = self.getMetadata().getName();
		int value = self.getInt("value");
		int extent = self.getInt("extent");
		int min = self.getInt("min");
		int max = self.getInt("max");
		
		DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(value, extent, min, max);
		
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", model);
			
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		actionContext.getScope(0).put(name, model);
		return model;		
	}
	
	public static void createChangeListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		BoundedRangeModel parent = (BoundedRangeModel) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ChangeListener listener = (ChangeListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addChangeListener(listener);
			}
		}
	}
}