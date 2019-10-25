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

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class DesignerActions {
	public static void markControls(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		Designer.getExplorerDisplay().asyncExec(new Runnable(){
			public void run(){
				markunmark(self, false, actionContext);
			}
		});
	}
	
	
	public static void markAndTooltipControl(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		Designer.getExplorerDisplay().asyncExec(new Runnable(){
			public void run(){
				markunmark(self, true, actionContext);
			}
		});
	}
	
	public static void markunmark(Thing self, boolean tooltip, ActionContext actionContext){
		//控件集合
			Thing controlSet = null;
			String controlSetPath = self.getString("controlSet");
			if(controlSetPath != null && !"".equals(controlSetPath)){
				controlSet = World.getInstance().getThing(controlSetPath);
			}
			if(controlSet == null){
				controlSet = self.getThing("ControlSet@0");
			}
					
			//标记或取消标记控件
			if(controlSet != null){
				@SuppressWarnings("unchecked")
				Map<String, List<Control>> controls = (Map<String, List<Control>>) controlSet.doAction("run", actionContext);
				if(controls != null){
					String controlsForMark = self.getString("controlsForMark");
					if(controlsForMark != null){
						for(String control : controlsForMark.split("[,]")){
							List<Control> controlList = controls.get(control);
							if(controlList != null){
								for(Control c : controlList){
									if(tooltip){
										Designer.markAndTooltip(c, self, self.getInt("toolTipWidth"), self.getInt("toolTipHeight"));
										return;
									}else{
										Designer.markControl(c);
									}
								}
							}
						}
					}
					
					String controlsForUnMark = self.getString("controlsForUnMark");
					if(controlsForUnMark != null){
						for(String control : controlsForUnMark.split("[,]")){
							List<Control> controlList = controls.get(control);
							if(controlList != null){
								for(Control c : controlList){
									Designer.unmarkControl(c);
								}
							}
						}
					}
				}
			}
	}
}