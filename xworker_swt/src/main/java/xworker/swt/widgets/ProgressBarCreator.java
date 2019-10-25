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
import org.eclipse.swt.widgets.ProgressBar;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ProgressBarCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("HORIZONTAL".equals(selfStyle)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfStyle)){
			style |= SWT.VERTICAL;
		}		
		
		if(self.getBoolean("SMOOTH"))
		    style |= SWT.SMOOTH;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("INDETERMINATE"))
		    style |= SWT.INDETERMINATE;
		    
		Composite parent = (Composite) actionContext.get("parent");
		ProgressBar bar = new ProgressBar(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", bar);
		try{
		    self.doAction("super.init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		bar.setMaximum(self.getInt("maximum", 100));
		bar.setMinimum(self.getInt("minimum", 0));
		bar.setSelection(self.getInt("selection", 0));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), bar);
		actionContext.peek().put("parent", bar);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(bar, self.getMetadata().getPath(), actionContext);
		return bar;        
	}

}