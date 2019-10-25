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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.ItemIndex;

public class ToolBarToolItemCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
    	Integer index = ItemIndex.getRemove();
    	return create(actionContext, self, index == null ? -1 : index);
    }

    public static Object create(ActionContext actionContext, Thing self, int index){
        int style = SWT.NONE;
        String selfType = self.getString("type");
        if("PUSH".equals(selfType)){
        	style |= SWT.PUSH;
        }else if("DROP_DOWN".equals(selfType)){
        	style |= SWT.DROP_DOWN;
        }else if("RADIO".equals(selfType)){
        	style |= SWT.RADIO;
        }else if("CHECK".equals(selfType)){
        	style |= SWT.CHECK;
        }else if("SEPARATOR".equals(selfType)){
        	style |= SWT.SEPARATOR;
        }
       
        ToolItem item = null;
        if(index == -1){
        	item = new ToolItem ((ToolBar) actionContext.get("parent"), style);
        }else{
        	ToolBar toolBar = actionContext.getObject("parent");
        	if(toolBar.getItemCount() < index){
        		index = toolBar.getItemCount();        		
        	}
        	item = new ToolItem ((ToolBar) actionContext.get("parent"), style, index);
        }
        String imagestr = self.getString("image");
        if(imagestr != null && !"".equals(imagestr)){
        	Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                    "xworker.swt.graphics.Image", "imageFile", actionContext); 
        	//(Image) actionContext.get(imagestr);
        	if(image != null && !image.isDisposed()){
        		item.setImage(image);
        	}
        }
        String toolTiptext = UtilString.getString(self.getString("toolTiptext"), actionContext);
        if(toolTiptext != null)
            item.setToolTipText(toolTiptext);
        String text = UtilString.getString(self.getString("text"), actionContext);
        if(text != null)
            item.setText(text);
        String width = self.getString("width");
        if(width != null && !"".equals(width)){
            item.setWidth(self.getInt(self.getString("width"), 0));
        }    
        if(self.getBoolean("enabled") == false){
            item.setEnabled(false);
        }
        
        //保存变量和创建子事物
        actionContext.getScope(0).put(self.getString("name"), item);
        Object oldParent = actionContext.get("parent");
        actionContext.peek().put("parent", item);
        for(Thing child : self.getAllChilds()){
            if("listeners".equals(child.getThingName())){  
            }else{
                continue;
            }
            
            child.doAction("create", actionContext);
        } 
        
        actionContext.peek().put("parent", oldParent);
        for(Thing child : self.getAllChilds()){
            if("listeners".equals(child.getThingName())){  
                continue;
            }else{
            }
            Object chld = child.doAction("create", actionContext);
            if(chld instanceof Control){
                ((Control) chld).pack();
                item.setWidth(((Control) chld).getSize ().x);
                item.setControl(((Control) chld));
                break;
            }
        } 
        
        actionContext.peek().remove("parent");
        
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", item, "widgetThing", self}));
        }
        
        Designer.attach(item, self.getMetadata().getPath(), actionContext);
        return item;
    }
}