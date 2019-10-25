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

import org.xmeta.ActionContext;

public class DataTableTableEditorCreator {
    public static void toJavaCode(ActionContext actionContext){
    	/*
        String code = "${self.name} = new TableEditor(${self.parent.parent.name});";
        
        return code; */
    }

    public static void toEditorCode(ActionContext actionContext){
    	/*
        String code = "";
        
        if(self.horizontalAlignment != null)
            code = code + "\n${self.name}.horizontalAlignment = SWT.${self.horizontalAlignment};";
        if(self.grabHorizontal == "true")
            code = code + "\n${self.name}.grabHorizontal = true;";
        if(self.minimumWidth != null && self.minimumWidth != "0")
            code = code + "\n${self.name}.minimumWidth = ${self.minimumWidth};";
        if(self.verticalAlignment != null)
            code = code + "\n${self.name}.verticalAlignment = SWT.${self.verticalAlignment};";
        if(self.grabVertical == "true")
            code = code + "\n${self.name}.grabVertical = true;";
        if(self.minimumHeight != null && self.minimumHeight != "0")
            code = code + "\n${self.name}.minimumHeight = SWT.${self.minimumHeight};";
        
        for(child in self.childs){
            che = child.detach();
            che.setParent(self.parent);
            code = code + "\n\n" + che.exec("toJavaCode", binding);
            code = code + "\n${self.name}.setEditor (${che.name}, newItem, ${self.detachedSource.parent.getItemIndex("TableColumn")});";
            
            //设置编辑的值
            scriptObj = self.getDataObject("scripts@0/script@setData");
            if(scriptObj != null){
                //如果设置的设置值的脚本则运行脚本
                 code = code + "\ndataCenter.runScript(\"${scriptObj.metadata.path}\", binding);";
            }else{
                code = code + "\n${che.name}.setText(newItem.getText(${self.detachedSource.parent.getItemIndex("TableColumn")}))";
            }
            //只处理一个子节点
            break;
        }    
        return code; */
    }

    public static void toEditorDisposeCode(ActionContext actionContext){
    	/*
        String code = "";
        
        for(child in self.childs){
            code = code + "\n${child.name}.dispose();";
        }    
        return code;*/
    }

    public static void toEditorResetCode(ActionContext actionContext){
    	/*
        String code = "def rowIndex = ${self.parent.parent.name}.getSelectionIndex();";
        
        for(child in self.childs){
            che = child.detach();
            
            //设置编辑的值
             scriptObj = self.getDataObject("scripts@0/script@getData");
            if(scriptObj != null){
                //如果设置的设置值的脚本则运行脚本
                 code = code + "\n    dataCenter.runScript(\"${scriptObj.metadata.path}\", binding);";
            }else{
                code = code + "\n    ${self.parent.parent.name}.updatedValueAt(rowIndex, ${self.detachedSource.parent.getItemIndex("TableColumn")}, oldItem, ${che.name}.getText());";
            }
            //只处理一个子节点
            break;
        }    
        return code;*/
    }

}