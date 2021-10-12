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
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ViewFormCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(UtilString.eq(self, "BORDER", "true")){
			style |= SWT.BORDER;
		}
		if(UtilString.eq(self, "FLAT", "true")){
			style |= SWT.FLAT;
		}
		    
		Composite parent = (Composite) actionContext.get("parent");
		ViewForm viewForm = new ViewForm(parent, style);
		
		viewForm.marginWidth = self.getInt("marginWidth", 0);
		viewForm.marginHeight = self.getInt("marginHeight", 0);
		viewForm.horizontalSpacing = self.getInt("horizontalSpacing", 1);
		viewForm.verticalSpacing = self.getInt("verticalSpacing", 1);
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), viewForm);
		actionContext.peek().put("parent", viewForm);
		Designer.attach(viewForm, self.getMetadata().getPath(), actionContext);
		try{
			for(Thing child : self.getAllChilds()){
			    child.doAction("create", actionContext);
			}
		}finally{
			actionContext.peek().remove("parent");
		}
		
		return viewForm;        
	}

}