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

import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.ActionContainer;

public class ParamsEditorCreator {
	@SuppressWarnings("unchecked")
	public static void okButtonSelection(ActionContext actionContext){
		Thing dataForm = (Thing) actionContext.get("dataForm");
		
		Map<String, Object> values = (Map<String, Object>) dataForm.doAction("getValues", actionContext);
		Thing dataObject = (Thing) actionContext.get("dataObject");
		String value = "";
		for(Thing attr : (List<Thing>) dataObject.get("attribute@")){
		    String v = (String) values.get(attr.getString("name"));
		    if(v != null && !"".equals(v)){
		        if(value != ""){
		            value = value + "&";
		        }
		        value = value + attr.getString("name") + "=" + v.replaceAll("&", "&amp;");
		    }
		}
		((Text) actionContext.get("text")).setText(value);
		((Shell) actionContext.get("shell")).dispose();
	}
	
	public static void init(ActionContext actionContext){
		String param = (String) actionContext.get("param");
		World world = World.getInstance();
		
		//参数
		Map<String, String> params = UtilString.getParams(param, ",");

		//设置表单
		Thing dataObject = world.getThing(params.get("thingPath"));
		//log.info("dataObject=" + dataObject);
		actionContext.getScope(0).put("dataObject", dataObject);
		Thing dataForm = (Thing) actionContext.get("dataForm");
		dataForm.doAction("setDataObject", actionContext, UtilMap.toMap(new Object[]{"dataObject", dataObject}));
		
		if("true".equals(params.get("nodesc"))){
			((Browser) actionContext.get("helpBrowser")).dispose();
		}
		((Shell) actionContext.get("shell")).pack();

		//设置表单的值
		Map<String, String> values = UtilString.getParams(((Text) actionContext.get("text")).getText());
		dataForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", values}));

		//事物的文档
		((ActionContainer) actionContext.get("helpBrowserAction")).doAction("setThing", UtilMap.toMap(new Object[]{"thing", dataObject}));
	}
}