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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class TreeTreeItemCreator {
    public static void toJavaCode(ActionContext actionContext){
    	/*
        String code = "TreeItem ${self.name} = new TreeItem(${self.parent.name}, SWT.NONE);";
        
        if(self.text != null){
            code = code + "\n${self.name}.setText(${self.text});";
        }
        if(self.checked != null)
            code = code + "\n${self.name}.setChecked(true);";
        
        for(child in self.childs)
            code = code + "\n" + child.exec("toJavaCode", binding);
        return code;*/
    }

    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        
        TreeItem item = new TreeItem((Tree) actionContext.get("parent"), SWT.NONE);
        
        item.setText(self.getString("text"));
        
        if(self.getBoolean("checked"))
            item.setChecked(true);
        
        actionContext.getScope(0).put(self.getString("name"), item);
        //创建子节点
        Bindings bindings = actionContext.push(null);
        try{
            bindings.put("parent", item);
            
            for(Thing child : self.getAllChilds()){
                child.doAction("create", actionContext);
            }
        }finally{
            actionContext.pop();
        }
        
        //应用模板，如果存在
        Thing StyleManager = (Thing) actionContext.get("StyleManager");
        if(StyleManager != null){
            StyleManager.doAction("apply", actionContext, UtilMap.toParams(new Object[]{"widget", item, "widgetThing", self}));
        }
        return item;
    }

}