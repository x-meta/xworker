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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

import ognl.OgnlException;
import xworker.lang.actions.ActionUtils;
import xworker.swt.design.Designer;
import xworker.swt.util.XWorkerTreeUtil;

public class CommonActions {
	public static Object createWidget(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing realSelf = ActionUtils.getSelf(actionContext);
		if(realSelf == null){
			realSelf= self;
		}
		Object parent = actionContext.get("parent");
		String parentVarName = self.getStringBlankAsNull("parentVarName");
		if(parentVarName != null){
			parent = OgnlUtil.getValue(self, "parentVarName", parentVarName, actionContext);
		}
		
		ActionContext ac = actionContext;
		if(self.getBoolean("newActionContext")){
			ac = new ActionContext();
			ac.put("parent", parent);
			ac.put("parentContext", actionContext);
			actionContext.getScope(0).put(self.getMetadata().getName(), ac);
		}
		
		Object result = null;
		try{
			Designer.pushCreator(realSelf);
			Bindings bindings = ac.push();
			bindings.put("parent", parent);
						
			String widgetPath = self.getStringBlankAsNull("widgetPath");
			if(widgetPath != null){
				Thing widget = World.getInstance().getThing(widgetPath);
				if(widget != null){
					result = widget.doAction("create", ac);
				}
			}
			
			if(result != null){
				//保存data
				if(result instanceof Widget && self.getStringBlankAsNull("newActionCotnextDataName") != null){
					String name = UtilString.getString(self, "newActionCotnextDataName", actionContext);
					((Widget) result).setData(name, result);
				}
			}
			
			if(self.getBoolean("isSwtCreateMethod")){
				List<Thing> things = actionContext.getThings();
	            Thing s = null;
	            if(things.size() > 1){
	                s = things.get(things.size() - 2);
	            }
	            
	            //创建子节点，使用本来的变量上下文
				actionContext.peek().put("parent", result);
				for(Thing child : s.getChilds()){
					child.doAction("create", actionContext);
				}
				
				//保存全局变量上下文
				String globalVariableName = s.getMetadata().getName();
				if("newActionContext".equals(self.getString("saveGlobalVariableMethod")) && ac != actionContext){
					actionContext.getScope(0).put(globalVariableName, ac);
				}else{
					actionContext.getScope(0).put(globalVariableName, result);
				}
			}else{			
				//保存全局变量上下文
				String globalVariableName = UtilString.getString(self, "globalVariableName"	, actionContext);
				if(globalVariableName != null && !"".equals(globalVariableName)){
					if("newActionContext".equals(self.getString("saveGlobalVariableMethod")) && ac != actionContext){
						actionContext.getScope(0).put(globalVariableName, ac);
					}else{
						actionContext.getScope(0).put(globalVariableName, result);
					}
				}
			}									
		}finally{
			ac.pop();
			Designer.popCreator();
		}
		
		if(result instanceof Control){
			Designer.attachCreator((Control) result, realSelf.getMetadata().getPath(), actionContext);
		}
		
		return result;
	}
	
	public static Object getThingIcon(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Control control = self.doAction("getParent", actionContext);
		Thing thing = self.doAction("getThing", actionContext);
		
		if(control != null && thing != null) {
			return XWorkerTreeUtil.getIcon(thing, control, actionContext);
		}else {
			return null;
		}
	}
	
	public static Object getThingColor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Control control = self.doAction("getParent", actionContext);
		Thing thing = self.doAction("getThing", actionContext);
		
		if(control != null && thing != null) {
			return XWorkerTreeUtil.getColor(thing, control, actionContext);
		}else {
			return null;
		}
	}
	
	public static Object getThingFont(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Control control = self.doAction("getParent", actionContext);
		Thing thing = self.doAction("getThing", actionContext);
		
		if(control != null && thing != null) {
			return XWorkerTreeUtil.getFont(thing, control, actionContext);
		}else {
			return null;
		}
	}
}