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
import org.eclipse.swt.widgets.Scale;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ScaleCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		
		String selfType = self.getString("type");
		if("HORIZONTAL".equals(selfType)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfType)){
			style |= SWT.VERTICAL;
		}		
		
		Composite parent = (Composite) actionContext.get("parent");
		Scale scale = new Scale(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", scale);
		try{
		    self.doAction("super.init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		String increment = self.getString("increment");
		if(increment != null && !"".equals(increment)){
		    scale.setIncrement(self.getInt("increment", 1));
		}
		String maximum = self.getString("maximum");
		if(maximum != null && !"".equals(maximum)){
		    scale.setMaximum(self.getInt("maximum", 1));
		}
		String minimum = self.getString("minimum");
		if(minimum != null && !"".equals(minimum)){
		    scale.setMinimum(self.getInt("minimum", 1));
		}
		String pageIncrement = self.getString("pageIncrement");
		if(pageIncrement != null && !"".equals(pageIncrement)){
		    scale.setPageIncrement(self.getInt("pageIncrement", 1));
		}
		
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), scale);
		actionContext.peek().put("parent", scale);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(scale, self.getMetadata().getPath(), actionContext);
		return scale;       
	}

}