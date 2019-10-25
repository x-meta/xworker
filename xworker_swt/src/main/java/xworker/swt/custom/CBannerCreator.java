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
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.UtilSwt;

public class CBannerCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		Composite parent = (Composite) actionContext.get("parent");
		CBanner banner = new CBanner(parent, SWT.NONE);
		
		Point point = UtilSwt.createPoint(self.getString("rightMinimumSize"));
		if(point != null) banner.setRightMinimumSize(point);
		
		int rightWidth = self.getInt("rightWidth", -1);
		if(rightWidth != -1) banner.setRightWidth(rightWidth);
		
		banner.setSimple(self.getBoolean("simple"));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), banner);
		actionContext.peek().put("parent", banner);
		try{
			for(Thing child : self.getAllChilds()){
			    child.doAction("create", actionContext);
			}
		}finally{
			actionContext.peek().remove("parent");
		}
		
		Designer.attach(banner, self.getMetadata().getPath(), actionContext);
		return banner;       
	}

}