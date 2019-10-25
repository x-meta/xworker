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
package xworker.java.swing.colorchooser;

import java.awt.Color;

import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.awt.AwtCreator;

public class ColorSelectionModelCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
	
		ColorSelectionModel model = null;
		Color selectedColor = AwtCreator.createColor(self, "selectedColor", actionContext);
		if(selectedColor != null){
			model = new DefaultColorSelectionModel(selectedColor);
		}else{
			model = new DefaultColorSelectionModel();
		}
		
		try{
			actionContext.push().put("parent", model);
			
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), model);
		
		return model;
	}
}