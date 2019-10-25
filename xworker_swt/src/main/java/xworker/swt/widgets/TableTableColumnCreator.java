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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.swt.util.UtilSwt;

public class TableTableColumnCreator {
    public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");        
        int style = SWT.NONE;
        String selfStyle = self.getString("style");
        if("LEFT".equals(selfStyle)){
        	style |= SWT.LEFT;
        }else if("RIGHT".equals(selfStyle)){
        	style |= SWT.RIGHT;
        }else if("CENTER".equals(selfStyle)){
        	style |= SWT.CENTER;
        }
        
        TableColumn column = new TableColumn((Table) actionContext.get("parent"), style);
        
        String columnText = UtilString.getString(self.getString("text"), actionContext);
        if(columnText != null)
            column .setText(columnText);
        String toolTipText = UtilString.getString(self.getString("toolTipText"), actionContext);
        if(toolTipText != null)
            column.setToolTipText(toolTipText);
        else
            column.setToolTipText(columnText);
        String imagestr = self.getString("image");
        if(imagestr != null && !"".equals(imagestr)){
	        Image image = (Image) actionContext.get(imagestr);
	        if(image != null)
	            column .setImage(image);
        }
        String width = self.getString("width");
        if(width != null && !"".equals(width) && !"-1".equals(width))
            column.setWidth(UtilSwt.getInt(self.getInt("width")));
        
        //保存变量和创建子事物
        actionContext.getScope(0).put(self.getString("name"), column);
        actionContext.peek().put("parent", column);
        for(Thing child : self.getAllChilds()){
            child.doAction("create", actionContext);
        }
        actionContext.peek().remove("parent");
        
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", column, "widgetThing", self}));
        }
    }

}