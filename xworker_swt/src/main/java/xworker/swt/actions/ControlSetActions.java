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
package xworker.swt.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class ControlSetActions {
	public static Map<String, List<Control>> run(ActionContext actionContext){
		Map<String, List<Control>> controls = new HashMap<String, List<Control>>();
		Map<String, String> pathNameMap = new HashMap<String,String>();
		
		Thing self = (Thing) actionContext.get("self");
		
		if(self.getString("ref") != null){
			Thing ref = World.getInstance().getThing(self.getString("ref"));
			if(ref != null){
				self = ref;
			}
		}
		
		//父控件列表
		List<String> parents = new ArrayList<String>();
		if(self.getString("parentConrols") != null){
			for(String p : self.getString("parentConrols").split("[,]")){
				p = p.trim();
				if("".equals(p)){
					continue;
				}else{
					parents.add(p);
				}
			}
		}
		
		//查找范围和要查找的控件
		String scope = self.getString("scope");
		List<String> controlPathList = new ArrayList<String>();
		for(Thing child : self.getChilds()){
			controlPathList.add(child.getString("thingPath"));
		}
		
		//初步获取指定控件的所有列表，可能包含于多个shell
		List<Map<String, List<Control>>> controlList = null;
		if("currentEditor".equals(scope)){
			Map<String, List<Control>> controlMap = Designer.getControlsInCurrentEditor(controlPathList);
			if(controlMap != null){
				controlList = new ArrayList<Map<String, List<Control>>>();
				controlList.add(controlMap);
			}
		}else{
			controlList = Designer.getControlsInSameShell(controlPathList);
		}
		
		//进一步细分和查找控件
		if(controlList != null){
			for(Map<String, List<Control>> controlMap : controlList){
				for(String key : controlMap.keySet()){
					List<Control> ctls = controlMap.get(key);
					for(Control c : ctls){
						if(belongParents(parents, c)){
							String name = pathNameMap.get(key);
							if(name == null){
								for(Thing child : self.getChilds()){
									if(key.equals(child.getString("thingPath"))){
										name = child.getString("name");
										break;
									}
								}
							}
							if(name != null){
								List<Control> cls = controls.get(name);
								if(cls == null){
									cls = new ArrayList<Control>();
									controls.put(name, cls);
								}
								cls.add(c);
							}
						}						
					}
				}
			}
		}
		
		return controls;
	}
	
	public static boolean belongParents(List<String> parents, Control control){
		if(parents == null || parents.size() == 0){
			return true;
		}
		
		int parentIndex = parents.size() - 1;
		Control parent = control;
		while(parent != null){
			String parenControlPath = Designer.getControlThingPath(parent);
			if(parenControlPath == null){
				continue;
			}
						
			String parentPath = parents.get(parentIndex);
			if(parentPath.equals(parenControlPath)){
				parentIndex--;
			}			
			
			if(parentIndex == -1){
				break;
			}
		}
		
		return parentIndex == -1; //所有的parent都已找到
	}
}