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
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.GlobalConfig;

public class NoneOpenWindowActions {
	/**
	 * 没有弹出窗口的属性编辑器说明初始化。
	 * 
	 * @param actionContext
	 */
	public static void init(ActionContext actionContext){
		String inputType = (String) actionContext.get("inputType");
		Thing inputDesc = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.openWins.NoneConfigEditorInfo");
		Thing attrDesc = null;
		if(inputType != null){
			for(Thing child : inputDesc.getChilds()){
				if(inputType.equals(child.getMetadata().getName())){
					attrDesc = child;
					break;
				}
			}
			
			if(attrDesc == null){
				for(Thing child : inputDesc.getChilds()){
					if("___no__editor__".equals(child.getMetadata().getName())){
						attrDesc = child;
						break;
					}
				}
			}			
		}
		
		Browser browser = (Browser) actionContext.get("browser");
		//Browser descBrowser = (Browser) actionContext.get("descBrowser");
		browser.setUrl(GlobalConfig.getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&thing=" + attrDesc.getMetadata().getPath());
		//descBrowser.setUrl(GlobalConfig.getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&thing=" + inputDesc.getMetadata().getPath());
	}
}