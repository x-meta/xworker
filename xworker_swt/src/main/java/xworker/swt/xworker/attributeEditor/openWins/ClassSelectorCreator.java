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

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class ClassSelectorCreator {
	public static void selection(ActionContext actionContext) throws OgnlException{
		World world = World.getInstance();
		Event event = (Event) actionContext.get("event");
		
		Thing globalCfg = world.getThing("_local.xworker.config.GlobalConfig");
		String name = (String) OgnlUtil.getValue("name",event.item.getData());
		String url = globalCfg.getString("webUrl") + "do?sc=xworker.ide.worldExplorer.swt.http.ClassDoc/@clsDoc&className=" + name;
		((Browser) actionContext.get("browser")).setUrl(url);

		actionContext.getScope(0).put("selectedClassName", name);
	}
}