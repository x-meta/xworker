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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.ItemIndex;
import xworker.swt.util.SwtUtils;

public class MenuMenuItemCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
        String selfStyle = self.getString("style");
        if("CHECK".equals(selfStyle)){
        	style |= SWT.CHECK;
        }else if("CASCADE".equals(selfStyle)){
        	style |= SWT.CASCADE;
        }else if("PUSH".equals(selfStyle)){
        	style |= SWT.PUSH;
        }else if("RADIO".equals(selfStyle)){
        	style |= SWT.RADIO;
        }else if("SEPARATOR".equals(selfStyle)){
        	style |= SWT.SEPARATOR;
        }
                
        Menu parent = actionContext.getObject("parent");
        Integer index = ItemIndex.getRemove(); //调用者设置的index，如果存在
        MenuItem item = null;
        if(index != null) {
        	item = new MenuItem(parent, style, index);
        }else {
        	item = new MenuItem(parent, style);
        }
        String text = UtilString.getString(self.getString("text"), actionContext);
        String accelerator = self.getString("accelerator");
        if(accelerator != null && !"".equals(accelerator)){
            text = text + "\t" + accelerator;
        }
        item.setText(text);
        Image image = (Image) actionContext.get(self.getString("image"));
        if(image != null){
            item.setImage(image);
        }
        if(accelerator != null){
            item.setAccelerator(SwtUtils.getAccelerator(accelerator));
        }
        String enabled = self.getString("enabled");
        if(enabled != null && !"".equals(enabled) && !self.getBoolean("enabled")){
            item.setEnabled(false);
        }
        String selected = self.getString("selected");
        if(selected != null && !"".equals(selected)){
            item.setSelection(self.getBoolean("selected"));
        }
        
        //保存变量和创建子事物
        actionContext.getScope(0).put(self.getString("name"), item);
        actionContext.peek().put("parent", item);
        for(Thing child : self.getAllChilds()){    
            child.doAction("create", actionContext);
        }
        actionContext.peek().remove("parent");
        
        Designer.attach(item, self.getMetadata().getPath(), actionContext);
        //应用模板，如果存在     
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", item, "widgetThing", self}));
        }
        return item;
    }

}