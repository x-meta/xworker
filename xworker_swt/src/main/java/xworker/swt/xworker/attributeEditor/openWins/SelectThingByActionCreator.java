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
package xworker.swt.xworker.attributeEditor.openWins;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;

public class SelectThingByActionCreator {
	private static final String TAG = SelectThingByActionCreator.class.getName();
	
	public static void okButtonSelection(ActionContext actionContext){
		Table dataTable = (Table) actionContext.get("dataTable");
		Thing data = (Thing) dataTable.getSelection()[0].getData();
		actionContext.getScope(0).put("result", data.get((String) actionContext.get("returnType")));
		((Shell) actionContext.get("shell")).dispose();
	}
	
	public static void init(ActionContext actionContext){
		World world = World.getInstance();
		String param = (String) actionContext.get("param");

		//参数列表
		Map<String, String> params = UtilString.getParams(param, ",");
		Action action = world.getAction(params.get("actionPath"));
		if(action == null){
		    Executor.warn(TAG, "Action thing is null, actionPath=" + params.get("actionPath"));
		    return;
		}
		actionContext.getScope(0).put("returnType", params.get("returnType"));

		ActionContext parentContext = (ActionContext) actionContext.get("parentContext");
		
		Object things = action.run((ActionContext) parentContext.get("parentContext"));
		actionContext.getScope(0).put("things", things);
		((Thing) actionContext.get("dataStore")).doAction("load", actionContext);
	}
}