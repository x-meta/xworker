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
package xworker.lang.actions.thing;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.util.UtilAction;

public class GetThingFromSelfActions {
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing oldSelf = (Thing) actionContext.get(self.getString("selfName"));
		if(oldSelf == null && self.getBoolean("exceptionOnNoSelf")){
			throw new XMetaException("Source self thing is null, varName=" + self.getString("selfName") + ",path=" + self.getMetadata().getPath());
		}			
		
		World world = World.getInstance();
		//先通过属性获取事物
		Thing thing = world.getThing(oldSelf.getString(self.getString("thingAttributeName")));
		if(thing == null){
			//其次通过子事物获取
			thing = oldSelf.getThing(self.getString("thingChildPath"));
		}
		
		if(thing == null && self.getBoolean("exceptionOnNoThing")){
			throw new XMetaException("Cann't get thing from self thing, GetThingFromSelfAction=" + self.getMetadata().getPath() + ",oldSelfThing=" + oldSelf.getMetadata().getPath());
		}
		
		//保存和返回变量
		UtilAction.putVarByActioScope(self, self.getString("varName"), thing, actionContext);
		
		return thing;
	}
}