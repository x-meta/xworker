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

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

public class ThingAction {
	private static final String TAG = ThingAction.class.getName();
	
	@SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//要执行动作的事物
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		if(thing == null){
			Executor.info(TAG, "ThingAction thing is null, action=" + self.getMetadata().getPath());
			return null;
		}
		
		//要执行的动作名
		String actionName = (String) self.doAction("getActionName", actionContext);
		if(actionName == null){
			actionName = "run";
		}
		
		//执行所在的变量上下文
		ActionContext ac = (ActionContext) self.doAction("getActionContext", actionContext);
		if(ac == null){
			ac = actionContext;
		}
		
		//参数
		Map<String, Object> params = (Map<String, Object>) self.doAction("getParameters", actionContext);
		
		//执行
		Object result = thing.doAction(actionName, ac, params, true);
		
		return result;
	}
	
	public static Object getParameters(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return UtilData.getParams(self.getStringBlankAsNull("parameters"), actionContext);
	}
	
	public static Object getActionContext(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String acstr = self.getStringBlankAsNull("actionContext");
		ActionContext ac = actionContext;
		if(acstr != null){
			ac = (ActionContext) actionContext.get(acstr);
		}
		
		if(ac == null){
			ac = actionContext;
		}
		return ac;
	}
	
	public static Object getThing(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");

		String thingType = self.getString("thingType");
		if(thingType == null || "".equals(thingType)){
			thingType = "path";
		}
		
		Thing thing = null;
		if("path".equals(thingType)){
			thing = World.getInstance().getThing(self.getString("thing"));
		}else{
			Object tree = self.getData("thingPathOgnlTree"); 
			if(tree == null){
				tree = Ognl.parseExpression(self.getString("thing"));
				self.setData("thingPathOgnlTree", tree);
			}
			thing = (Thing) OgnlUtil.getValue(tree, actionContext);
		}
		
		return thing;
	}
}