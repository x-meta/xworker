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
package xworker.swt.xworker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class DataTableTableColumnCreator {
    public static void create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        TableColumn column = new TableColumn((Table) actionContext.get("parent"), SWT.NONE);
        
        String columnText = UtilString.getString(self.getString("text"), actionContext);
        if(columnText != null)
            column .setText(columnText);
        String toolTipText = UtilString.getString(self.getString("toolTipText"), actionContext);
        if(toolTipText != null)
            column.setToolTipText(toolTipText);
        else{
            column.setToolTipText(columnText);
        }
        String imagestr = self.getString("image");
        if(imagestr != null && !"".equals(imagestr)){
        	Image image = (Image) actionContext.get(imagestr);
        	if(image != null){
        		column.setImage(image);
        	}
        }
        String width = self.getString("width");
        if(width != null && !"".equals(width) && !"-1".equals(width))
            column.setWidth(self.getInt("width"));
        
        actionContext.getScope(0).put(self.getString("name"), column);    
        actionContext.peek().put("parent", column);
        for(Thing child : self.getAllChilds()){
            child.doAction("create", actionContext);
        }
        actionContext.peek().remove("parent");
    }

    public static void toJavaCode(ActionContext actionContext){
    	/*
        String code = "TableColumn ${self.name} = new TableColumn(${self.parent.name}, SWT.NONE);";
        
        if(self.text != null)
            code = code + "\n${self.name}.setText(${self.text});";
        if(self.toolTipText != null)
            code = code + "\n${self.name}.setToolTipText(${self.toolTipText});";
        if(self.image != null)
            code = code + "\n${self.name}.setImage(${self.image});";
        if(self.width != "-1")
            code = code + "\n${self.name}.setWidth(${self.width});";
            
        for(child in self.childs){
            if(child.metadata.objectName != "scripts")
                code = code + "\n\n" + child.exec("toJavaCode", binding);
        }
        return code; */
    }

}