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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.data.InputDataManager;
import xworker.swt.data.inputdatamanagers.ThingValueListIDM;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ListCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("SINGLE".equals(selfStyle)){
			style |= SWT.SINGLE;
		}else if("MULTI".equals(selfStyle)){
			style |= SWT.MULTI;
		}
	
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		    
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;
		    
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		    
		Composite parent = (Composite) actionContext.get("parent");
		List list = new List(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", list);
		try{
		    self.doAction("super.init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		java.util.List<Thing> values = self.getChilds("value"); 
		for(Thing v : values){
		    list.add(v.getMetadata().getLabel());
		}
		if(values.size() > 0) {
			InputDataManager.setInputDataManager(list, new ThingValueListIDM(list, values, actionContext));
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), list);
		actionContext.peek().put("parent", list);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(list, self.getMetadata().getPath(), actionContext);
		return list;       
	}
}