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
package xworker.swt.xworker.attributeEditor;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class TreeComboEditorCreator {
    public static Object create(ActionContext actionContext){
    	xworker.swt.form.GridData gridData = (xworker.swt.form.GridData) actionContext.get("gridData");
		Thing attribute = gridData.source;
		
		//创建treeCombo
		Thing treeCombo = new Thing("xworker.swt.xworker.TreeCombo");
		treeCombo.getAttributes().putAll(attribute.getAttributes());
		for(Thing child : attribute.getChilds()){
		    treeCombo.addChild(child, false);
		}
		
		String inputattrs = attribute.getString("inputattrs");
		if(inputattrs == null || "".equals(inputattrs)){
		    //没有设置输入扩展属性的，认为是有value子事物定义的树
		    treeCombo.set("dataSource", "selfValues");
		}else{
		    Map<String, String> params = UtilString.getParams(inputattrs, "&");
		    treeCombo.getAttributes().putAll(params);
		    treeCombo.set("name", attribute.get("name"));
		}
		
		return treeCombo.doAction("create", actionContext);        
	}
}