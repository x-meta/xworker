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
package xworker.lang.actions;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;

import xworker.util.XWorkerUtils;

public class ThingTemplateAction {
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
			
		String selfVarName = self.getStringBlankAsNull("selfVarName");
		List<Thing> selfthings = actionContext.getThings();
		if(selfVarName != null && selfthings.size() >= 2) {
			actionContext.peek().put(selfVarName, selfthings.get(selfthings.size() - 2));
		}
		
		Thing template = self.doAction("getThingTemplate", actionContext);//self.getThing("ThingTemplate@0");
		if(template != null){
			
			Thing thing = (Thing) template.doAction("process", actionContext);
			String returnType = self.doAction("getReturnType", actionContext);
			Object returnObj = null;
			if("firstChild".equals(returnType)) {
				if(thing.getChilds().size() > 0) {
					Thing forReturn = thing.getChilds().get(0);
					forReturn.setParent(null);
					returnObj = forReturn;
				}
			}else if("childsList".equals(returnType) || !self.getBoolean("useRootThing")) {
				List<Thing> things = thing.getChilds();
				//默认设置父节点为空
				for(Thing th : things){
					th.setParent(null);
				}
				
				returnObj = things;
			}else {
				returnObj = thing;
			}
			
			//在IDE中打开事物
			if(UtilData.isTrue(self.doAction("isOpenGeneratedThing", actionContext))){
				if(returnObj instanceof Thing) {
					XWorkerUtils.ideOpenThing((Thing) returnObj);
				}else {
					XWorkerUtils.ideOpenThing(thing);
				}
			}
			
			//是否执行动作
			String actionName = self.getString("actionName");
			if(actionName != null && !"".equals(actionName) && returnObj instanceof Thing){
				return ((Thing) returnObj).doAction(actionName, actionContext);				
			}
			
			String varName = self.getString("varName");
			if(varName != null && !"".equals(varName)){
				Bindings bindings = UtilAction.getVarScope(self, actionContext);// (Bindings) self.doAction("getVarScope", actionContext);
				bindings.put(varName, returnObj);				
			}
			
			return returnObj;
		}else{
			return null;
		}
	}
}