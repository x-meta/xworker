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

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.util.GlobalConfig;

public class DataObjectAttrOpenWindowCreator {
	@SuppressWarnings("unchecked")
	public static void okSeleciton(ActionContext actionContext){
		String result = "";
		Thing dataObjectForm = (Thing) actionContext.get("dataObjectForm");
		Map<String, Object> values = (Map<String, Object>) dataObjectForm.doAction("getValues", actionContext);
		for(String key : values.keySet()){
		    String value = String.valueOf(values.get(key));
		    if(value != null && !"".equals(value)){
		        if(result != ""){
		            result = result + actionContext.get("split");
		        }
		        result = result + key + "=" + value;
		    }
		}

		actionContext.getScope(0).put("result", result);
		((Shell) actionContext.get("shell")).dispose();
	}
	
	@SuppressWarnings("unchecked")
	public static void thingshellokSeleciton(ActionContext actionContext){
		String result = "";
		Thing thingForm = (Thing) actionContext.get("thingForm");
		Map<String, Object> values = (Map<String, Object>) thingForm.doAction("getValues", actionContext);
		for(String key : values.keySet()){
		    String value = String.valueOf(values.get(key));
		    if(value != null && !"".equals(value)){
		        if(result != ""){
		            result = result + actionContext.get("split");
		        }
		        result = result + key + "=" + value;
		    }
		}

		actionContext.getScope(0).put("result", result);
		((Shell) actionContext.get("thingShell")).dispose();
	}
	
	public static void shellinit(ActionContext actionContext){
		World world = World.getInstance();

		//设置数据对象初始化表单
		Thing dataObject = world.getThing((String) actionContext.get("dataObjectPath"));
		Thing dataObjectForm = (Thing) actionContext.get("dataObjectForm");
		dataObjectForm.doAction("setDataObject", actionContext, UtilMap.toMap(new Object[]{"dataObject", dataObject}));

		//设置数据
		if(actionContext.get("value") != null){
		    Map<String, String> params = UtilString.getParams((String) actionContext.get("value"), (String) actionContext.get("split"));
		    dataObjectForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
		}

		//设置说明文档
		Browser browser = (Browser) actionContext.get("browser");
		browser.setUrl(GlobalConfig.getWebUrl() + "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" + dataObject.getMetadata().getPath());
		actionContext.getScope(0).put("split", actionContext.get("split"));
		Shell shell = (Shell) actionContext.get("shell"); 
		shell.pack();
		Point size = shell.getSize();
		if(size.x < 400){
			size.x = 400;
		}
		if(size.y < 300){
			size.y = 300;
		}
		shell.setSize(size);
	}
	
	public static void thingshellInit(ActionContext actionContext){
		World world = World.getInstance();

		//设置数据对象初始化表单
		Thing descriptorThing = world.getThing((String) actionContext.get("descriptorPath"));
		Thing thingForm = (Thing) actionContext.get("thingForm");
		thingForm.doAction("setDescriptor", actionContext, UtilMap.toMap(new Object[]{"descriptor", descriptorThing}));

		//设置数据
		if(actionContext.get("value") != null){
    		Map<String, String> params = UtilString.getParams((String) actionContext.get("value"), (String) actionContext.get("split"));
		    thingForm.doAction("setValues", actionContext, UtilMap.toMap(new Object[]{"values", params}));
		}

		actionContext.getScope(0).put("split", actionContext.get("split"));
		((Shell) actionContext.get("shell")).pack();
	}
}