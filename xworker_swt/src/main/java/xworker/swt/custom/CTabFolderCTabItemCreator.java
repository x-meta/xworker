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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;

public class CTabFolderCTabItemCreator {
	public static String TOPRIGHT = "__CTabFolderCTabItem_TOPRIGHT__"; 
    public static Object create(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
        return create(actionContext, self, -1);
    }

    public static Object create(ActionContext actionContext, Thing self, int index){
        int style = SWT.NONE;
        if(self.getBoolean("CLOSE"))
            style = SWT.CLOSE | style;
            
        CTabFolder parent = (CTabFolder) actionContext.get("parent");
        CTabItem item = null;
        if(index == -1){
        	item = new CTabItem(parent, style);
        }else{
        	if(index > parent.getChildren().length){
        		index = parent.getChildren().length;
        	}
        	item = new CTabItem(parent, style, index);
        }
        if(self.getString("text") != null)
            item.setText(UtilString.getString(self.getString("text"), actionContext));
        if(self.getString("toolTipText") != null)
            item.setToolTipText(UtilString.getString(self.getString("toolTipText"), actionContext));
            
        //保存变量和创建子事物
        actionContext.getScope(0).put(self.getString("name"), item);
        
        if(!self.getBoolean("delayReload")) {
	        actionContext.peek().put("parent", parent);
	        for(Thing child : self.getAllChilds()){
	            Control control = (Control) child.doAction("create", actionContext);
	            item.setControl(control);
	            //应该只有一个控件节点
	            break;
	        }
	        actionContext.peek().remove("parent");
        }
        
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", item, "widgetThing", self}));
        }
                
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
        	item.setImage(image);
        }
        
        item.setData(Designer.DATA_THING, self.getMetadata().getPath());
        item.setData(Designer.DATA_ACTIONCONTEXT, actionContext);
        return item;
    }
    
    public static Control getTopRightControl(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    			
    	Event event = actionContext.getObject("event");
    	Control control = (Control) event.item.getData(TOPRIGHT);
    	if(control != null && !control.isDisposed()) {
    		return control;
    	}
    	
    	Thing controlThing = self.doAction("getTopRightControlThing", actionContext);
    	if(controlThing != null) {
    		//parent有CTabFilder那边的Selection中的代码预先设定了
    		control = controlThing.doAction("create", actionContext);
    		if(control != null) {
    			event.item.setData(TOPRIGHT, control);
    		}
    		
    		return control;
    	}
    	
    	return null;
    }
}