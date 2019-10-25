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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class SliderCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Action styleAction = world.getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		
		Composite parent = (Composite) actionContext.get("parent");
		Slider slider = new Slider(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", slider);
		bindings.put("self", self);
		try{
		    Action initAction = world.getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		slider.setThumb(self.getInt("thumb", 0));
		slider.setIncrement(self.getInt("increment", 1));
		String maximum = self.getString("maximum");
		if(maximum != null && !"".equals(maximum)) 
		    slider.setMaximum(self.getInt("maximum", 1000000));
		String minimun = self.getString("minimun");
		if(minimun != null && !"".equals(minimun))
		    slider.setMinimum(self.getInt("maximum", -100000));
		String pageIncrement = self.getString("pageIncrement");
		if(pageIncrement != null && !"".equals("pageIncrement"))
		    slider.setPageIncrement(self.getInt("pageIncrement", 10));
		String selection = self.getString("selection");
		if(selection != null && !"".equals(selection))
		    slider.setSelection(self.getInt("selection", 1));
		       
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), slider);
		actionContext.peek().put("parent", slider);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(slider, self.getMetadata().getPath(), actionContext);
		return slider;        
	}

}