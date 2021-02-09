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
package xworker.swt.widgets;

import org.eclipse.swt.widgets.Control;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class ControlCreator {
    public static void init(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		//应用模板，如果存在
		Thing StyleManager = (Thing) actionContext.get("StyleManager");
		if(StyleManager != null){
		    StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", actionContext.get("control"), "widgetThing", self}));
		}
		
		//使用模板的方法应用自身定义的样式
		World world = World.getInstance();
		Thing styleThing = world.getThing("xworker.swt.style.StyleSet/@Style");
		Object createResource = styleThing.doAction("createResource", actionContext);
		
		Action styleAction = world.getAction("xworker.swt.style.StyleSet/@Style/@actions/@applyControl");
		styleAction.run(actionContext, UtilMap.toParams(new Object[]{"widget", actionContext.get("control"), "createResource", createResource}));
		
		//设置css相关属性
		Control control = actionContext.getObject("control");
		if(control != null){
			control.setData("style", self.getString("cssStyle"));
			control.setData("class", self.getString("cls"));
		}
		
		//数据绑定相关
		control.setData("dataBinds", self.getStringBlankAsNull("dataBinds"));		
		control.setData("reactorRules", self.getStringBlankAsNull("reactorRules"));
	}

}