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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class DataTreeTreeColumnCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        TreeColumn column = new TreeColumn((Tree) actionContext.get("parent"), SWT.NONE);
        
        String text = UtilString.getString(self, "text", actionContext);
        if(text != null)
            column.setText(text);
        
        String toolTipText = UtilString.getString(self, "toolTipText", actionContext);
        if(toolTipText != null)
            column.setToolTipText(toolTipText);
        
        Image image = (Image) self.getObject("image", actionContext);
        if(image != null)
            column.setImage(image);
        
        int width = self.getInt("width", -1);
        if(width != -1)
            column.setWidth(width);
        
        actionContext.getScope(0).put(self.getString("name"), column);
        actionContext.peek().put("parent", column);
        for(Thing child : self.getAllChilds()){
            child.doAction("create", actionContext);
        }
        actionContext.peek().remove("parent");
        
        return column;
    }

    public static void toJavaCode(ActionContext actionContext){
    	/*
        String code = "TreeColumn ${self.name} = new TreeColumn(${self.parent.name}, SWT.NONE);";
        
        if(self.text != null)
            code = code + "\n${self.name}.setText(${self.text});";
        if(self.toolTipText != null)
            code = code + "\n${self.name}.setToolTipText(${self.toolTipText});";
        if(self.image != null)
            code = code + "\n${self.name}.setImage(${self.image});";
        if(self.width != "-1")
            code = code + "\n${self.name}.setWidth(${self.width});";
            
        for(child in self.childs){
            code = code + "\n\n" + child.exec("toJavaCode", binding);
        }
        return code;*/
    }

}