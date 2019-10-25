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
package xworker.swt.xworker.attributeEditor;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class ThingRegistEditorShellCreator {
    @SuppressWarnings("unchecked")
	public static void addButtonSelection(ActionContext actionContext){
        Thing thingRegist = (Thing) actionContext.get("thingRegist");
        
        if((Boolean) thingRegist.doAction("validate", actionContext)){
            boolean have = false;
            CCombo registTypeCombo = (CCombo) actionContext.get("registTypeCombo");
            String stype = registTypeCombo.getText();
            if(registTypeCombo.getSelectionIndex() != -1){
            	List<Thing> types = (List<Thing>) actionContext.get("types");
            	Thing type = types.get(registTypeCombo.getSelectionIndex());
            	stype = type.getString("value");
            }
                        
            Text thingPathText = (Text) actionContext.get("thingPathText");            
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) actionContext.get("dataList");
            for(Map<String, Object> data : dataList){
                if(data.get("type").equals(stype) && data.get("thing").equals(thingPathText.getText())){
                    have = true;
                    break;
                }
            }    
            
            if(!have){
                Map<String, Object> data = UtilMap.toParams(new Object[]{"type",stype, "thing",thingPathText.getText()});
                Table registsTable= (Table) actionContext.get("registsTable");
                TableItem titem = new TableItem(registsTable, SWT.NONE);
                String[] texts = new String[2];
                texts[0] = String.valueOf(data.get("type"));
                texts[1] = String.valueOf(data.get("thing"));
                titem.setText(texts);
                titem.setData(data);
                dataList.add(data);
            }
            
            thingPathText.setText("");
        }
    }

    @SuppressWarnings("unchecked")
	public static void removeButtonSelection(ActionContext actionContext){
    	Table registsTable= (Table) actionContext.get("registsTable");
        TableItem[] items = registsTable.getSelection();
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) actionContext.get("dataList");
        Button removeRegistButton = (Button) actionContext.get("removeRegistButton");
        
        for(TableItem item : items){
        	Map<String, Object> data = (Map<String, Object>) item.getData();
           dataList.remove(data);
           item.dispose();
        }
        
        if(registsTable.getItems().length == 0){
            removeRegistButton.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
	public static void okButtonSelection(ActionContext actionContext){
        String str = "";
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) actionContext.get("dataList");
        for(Map<String, Object> data : dataList){
            if(str != ""){
                str = str + ",";
            }
            str = str + data.get("type") + "|" + data.get("thing");
        }
        
        Text text = (Text) actionContext.get("text");
        Shell shell = (Shell) actionContext.get("shell");
        text.setText(str);
        shell.dispose();
    }

    public static void cancelButtonSelection(ActionContext actionContext){
    	Shell shell = (Shell) actionContext.get("shell");
        shell.dispose();
    }

    public static void registsTableSelection(ActionContext actionContext){
    	Table registsTable= (Table) actionContext.get("registsTable");
        TableItem[] items = registsTable.getSelection();
        Button removeRegistButton = (Button) actionContext.get("removeRegistButton");
        if(items != null && items.length != 0){
            removeRegistButton.setEnabled(true);
        }
    }

}