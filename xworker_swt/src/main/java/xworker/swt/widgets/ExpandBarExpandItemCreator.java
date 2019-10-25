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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class ExpandBarExpandItemCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        return create(self, actionContext, -1);
    }

    public static ExpandItem create(Thing self, ActionContext actionContext, int index){
    	ExpandBar parent = actionContext.getObject("parent");
    	ExpandItem item = null;
    	if(index < 0 || index > parent.getItemCount()){
    		item = new ExpandItem((ExpandBar) actionContext.get("parent"), SWT.NONE);
    	}else{
    		item = new ExpandItem((ExpandBar) actionContext.get("parent"), SWT.NONE, index);
    	}
    	
        item.setText(UtilString.getString(self.getString("text"), actionContext));
        String simage = self.getString("image");
        if(simage != null && !"".equals(simage)){
        	Image image = (Image) actionContext.get(simage);
        	if(image != null){
        		item.setImage(image);
        	}
        }
        
        String sheight = self.getString("height");        		
        if(sheight != null && !"".equals(sheight))
            item.setHeight(self.getInt("height"));
            
        int height = 0;
        actionContext.getScope(0).put(self.getString("name"), item);
        actionContext.peek().put("parent", actionContext.get("parent"));
        for(Thing child : self.getAllChilds()){
            Control control = (Control) child.doAction("create", actionContext);
            item.setControl(control);
            height += control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;    
        }
        actionContext.peek().remove("parent");
        
        item.setData(Designer.DATA_THING, self.getMetadata().getPath());
        item.setHeight(height);
        
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", item, "widgetThing", self}));
        }
        
        Designer.attach(item, self.getMetadata().getPath(), actionContext);
        return item;
    }
}