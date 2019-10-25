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
package xworker.lang.actions.data;

import ognl.OgnlException;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilAction;

public class AttributeVarGetter {
	public static Object run(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		//事物
		Thing thing = null;
		String thingVarName = self.getString("thingVarName");
		if(thingVarName != null && !"".equals(thingVarName)){
			thing = (Thing) OgnlUtil.getValue(self, "thingVarName", thingVarName, actionContext);
		}
		
		if(thing == null){
			thing = World.getInstance().getThing(self.getString("thingPath"));			
		}
		
		if(thing == null){
			throw new ActionException("thing is null, attributeVarGetter=" + self.getMetadata().getPath());
		}
		
		//是否是变量
		boolean isVar = thing.getBoolean(self.getString("ifVarAttribute"));
		String attrVarName = thing.getString(self.getString("varAttribute"));
		Object value = null;
		if(isVar){
			value = OgnlUtil.getValue(thing, attrVarName, self.getString("varAttribute"), actionContext);
		}else{
			value = attrVarName;
		}
		
		//是否保存变量
		UtilAction.putVarByActioScope(self, self.getString("varName"), value, actionContext);
		
		//返回值
		return value;
	}
}