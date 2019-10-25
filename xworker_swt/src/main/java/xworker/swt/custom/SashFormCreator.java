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
package xworker.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class SashFormCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("HORIZONTAL".equals(selfStyle)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfStyle)){
			style |= SWT.VERTICAL;
		}
		
		if(UtilString.eq(self, "SMOOTH", "true")){
			style |= SWT.SMOOTH;
		}
		
		if(UtilString.eq(self, "BORDER", "true")){
			style |= SWT.BORDER;
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		SashForm sash = new SashForm(parent, style);
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", sash);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		sash.setData("name", self.get("name"));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), sash);
		actionContext.peek().put("parent", sash);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		String weights = self.getString("weights");
		if(weights != null && !"".equals(weights)){
		    try{
		    	int childCount = sash.getChildren().length;
		    	if(childCount > 0){
		    		int[] ws = new int[childCount];
		    		int[] sws = UtilString.toIntArray(weights);
		    		for(int i=0; i<ws.length; i++){
		    			if(i < sws.length){
		    				ws[i] = sws[i];
		    			}
		    		}
		    		sash.setWeights(ws);
		    	}
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		}
		
		Designer.attach(sash, self.getMetadata().getPath(), actionContext);
		return sash;      
	}

}