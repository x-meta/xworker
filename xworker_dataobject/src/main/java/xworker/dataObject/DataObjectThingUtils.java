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
package xworker.dataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;

public class DataObjectThingUtils {
	/**
	 * 获取关键字段的定义列表。
	 * 
	 * @param dataObject
	 * @return 关键字属性事物列表
	 */
    public static List<Thing> getKeyAttributes(Thing dataObject){
        List<Thing> keys = new ArrayList<Thing>();
        for(Thing attribute : dataObject.getChilds("attribute")){
            if(attribute.getBoolean("key")){
                keys.add(attribute);
            }
        }
        
        //def firstAttribute = self.getThing("attribute@0");
        //if(keys.size() == 0 && firstAttribute){
        //    keys.add(firstAttribute);
        //}
        return keys;
    }
    
    /**
     * 获取编辑属性。
     * 
     * @param self 事物
     * @param editorType 编辑类型
     * @return 要编辑的属性列表
     */
    public static List<Map<String, Object>> getEditorAttributes(Thing self, String editorType){
        if(editorType == null || "".equals(editorType)){
            editorType = DataObjectConstants.EDIT_TYPE_EDIT;
        }
        
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        for(Thing attribute : self.getAllChilds()){
            if(!"attribute".equals(attribute.getThingName())){
                continue;
            }
            if(attribute.getBoolean("viewField") == false){
                //不是界面字段
                continue;
            }
            
            if(attribute.getBoolean(editorType + "Editor") == false){
                //不是当前界面类型的字段
                continue;
            }
            
            Map<String, Object> atr = new HashMap<String, Object>();
            atr.put("attribute", attribute);
            if(DataObjectConstants.EDIT_TYPE_CREATE.equals(editorType)){
            	atr.put("viewConfig", attribute.get("CreateCofnig@0"));
            }else if(DataObjectConstants.EDIT_TYPE_EDIT.equals(editorType)){
            	atr.put("viewConfig", attribute.get("EditConfig@0"));
            }else if(DataObjectConstants.EDIT_TYPE_VIEW.equals(editorType)){
            	atr.put("viewConfig", attribute.get("ViewConifg@0"));
            }else if(DataObjectConstants.EDIT_TYPE_GRID.equals(editorType)){
            	atr.put("viewConfig", attribute.get("GridConifg@0"));
            }else if(DataObjectConstants.EDIT_TYPE_QUERY.equals(editorType)){
            	atr.put("viewConfig", attribute.get("QueryConfig@0"));
            }else{
            	atr.put("viewConfig", attribute.get("EditConfig@0"));
            }
            if(atr.get("viewConfig") == null){
                atr.put("viewConfig", attribute);
            }
            
            attributes.add(atr);
        }
        
        return attributes;
    }
    
    /**
     * 返回可能要输出的字段列表。
     * 
     * @param self
     * @return
     */
    public static List<Thing> getReaderAttributes(Thing self){
        List<Thing> attributes = new ArrayList<Thing>();
        for(Thing attribute : self.getChilds()){
            if(!"attribute".equals(attribute.getThingName())){
                continue;
            }
        
            if(attribute.getBoolean("readField")){
                attributes.add(attribute);
            }
        }
        
        return attributes;
    }
}