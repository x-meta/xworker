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

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class DoCreator {
	static World world = World.getInstance();

	public static Object run(ActionContext actionContext) throws Exception {
		Thing self = (Thing) actionContext.get("self");

		Object result = "success";
		boolean while_ = true;
		actionContext.peek().setVarScopeFlag();//.isVarScopeFlag = true; //一个新的局部变量范围
		Thing childActions = self.getThing("ChildAction@0"); 
		do {
			for (Thing child : childActions.getChilds()) {
				Action action = world.getAction(child);
				if (action != null) {
					result = action.run(actionContext, null, true);
				}

				int sint = actionContext.getStatus();
				if (sint != ActionContext.RUNNING) {
					break;
				}
			}

			//判断循环的状态
			if (actionContext.getStatus() == ActionContext.BREAK) {
				actionContext.setStatus(ActionContext.RUNNING);
				break;
			} else if (actionContext.getStatus() == ActionContext.CONTINUE) {
				actionContext.setStatus(ActionContext.RUNNING);
			} else if (actionContext.getStatus() != ActionContext.RUNNING) {
				break;
			}else if(ActionContext.EXCEPTION == actionContext.getStatus()){
				break;
			}
			// }while(UtilAction.returnTrueFalse(UtilAction.runAsGroovy(self,
			// "code", actionContext, self.getMetadata().getPath())));
			//执行自己的while方法判断是否while条件成立
			while_ = IfCreator.checkCondition(self, actionContext);			
		} while (while_);

		return result;
	}

}