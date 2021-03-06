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

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ComboBoxModelCreator {
	@SuppressWarnings("rawtypes")
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String arrayItems = self.getStringBlankAsNull("arrayItems");
		String vectoryItems = self.getStringBlankAsNull("vectoryItems");
		
		ComboBoxModel model = null;
		if(arrayItems != null){
			Object[] items =  (Object[]) actionContext.get(arrayItems);
			if(items != null){
				model = new DefaultComboBoxModel(items);
			}
		}else if(vectoryItems != null){
			Vector items = (Vector) actionContext.get(vectoryItems);
			if(items != null){
				model = new  DefaultComboBoxModel(items);
			}
		}
		
		if(model == null){
			model = new  DefaultComboBoxModel();
		}
		
		actionContext.put(self.getMetadata().getName(), model);
		
		return model;
	}
}