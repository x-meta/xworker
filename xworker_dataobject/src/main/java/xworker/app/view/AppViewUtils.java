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
package xworker.app.view;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObjectConstants;

public class AppViewUtils {
	
	/**
	 * 通过属性创建表格的标题列。
	 * 
	 * @param attributes
	 * @return
	 */
	public static List<List<GridColumn>> createGridHeaders(List<ViewAttribute> attributes){
		//header的最大层数
		int maxLevel = 0;
        for(ViewAttribute field : attributes){
            String groupName = field.attribute.getString("columnGroup");
            if(groupName != null && groupName != ""){
                int l = groupName.split("[.]").length;
                if(l > maxLevel){
                    maxLevel = l;
                }
            }
        }
        maxLevel = maxLevel + 1;
        
        List<List<GridColumn>> rows = new ArrayList<List<GridColumn>>();
        for(int i=0; i<maxLevel;i++){
            rows.add(new ArrayList<GridColumn>());
        }
        
        //把输入放入到指定的层中
        for(ViewAttribute field : attributes){
        	Thing attribute = field.attribute;
            String groupName = attribute.getStringBlankAsNull("columnGroup");
            if(groupName == null){
            	//没有分组的都加到第一行
            	GridColumn row = new GridColumn();
            	row.rowspan = maxLevel;
            	row.colspan = 1;
            	row.title = field.attribute.getMetadata().getLabel();
            	row.viewAttribute = field;
            	
                List<GridColumn> rowLevel = getRowLevel(rows, 1);
                rowLevel.add(row);                
            }else{
                String[] names = groupName.split("[.]");
                for(int i=0; i<names.length; i++){
                    List<GridColumn> rowLevel = getRowLevel(rows, i+1);
                    
                    GridColumn row = null;
                    if(rowLevel.size() > 0 && names[i].equals(rowLevel.get(rowLevel.size() - 1).title)){
                        row = rowLevel.get(rowLevel.size() - 1);
                        row.colspan = row.colspan + 1;
                    }else{
                    	row = new GridColumn();
                    	row.title = names[i];
                    	row.colspan = 1;
                    	row.colspan = 1;
   
                        rowLevel.add(row);
                    }
                }
                
                List<GridColumn> rl = getRowLevel(rows, names.length + 1);
                GridColumn column = new GridColumn();
                column.colspan = 1;
                column.rowspan = maxLevel - names.length;
                column.title = field.attribute.getMetadata().getLabel();
            	column.viewAttribute = field;
                rl.add(column);
            }    
        }
        
        return rows;
	}
	
    public static List<GridColumn> getRowLevel(List<List<GridColumn>> rows, int level){
        if(rows.size() < level){
            List<GridColumn> ls = new ArrayList<GridColumn>();
            rows.add(ls);
            return ls;
        }else{
            return rows.get(level-1);
        }
    }
	
	  /**
     * 获取编辑属性。
     * 
     * @param self
     * @param editorType
     * @return
     */
    public static List<ViewAttribute> getEditorAttributes(Thing self, String editorType){
        if(editorType == null || "".equals(editorType)){
            editorType = DataObjectConstants.EDIT_TYPE_EDIT;
        }
        
        List<ViewAttribute> attributes = new ArrayList<ViewAttribute>();
        for(Thing attribute : self.getChilds()){
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
            
            ViewAttribute atr = new ViewAttribute();
            atr.attribute = attribute;
            if(DataObjectConstants.EDIT_TYPE_CREATE.equals(editorType)){
            	atr.viewConfig = attribute.getThing("CreateCofnig@0");
            }else if(DataObjectConstants.EDIT_TYPE_EDIT.equals(editorType)){
            	atr.viewConfig = attribute.getThing("EditConfig@0");
            }else if(DataObjectConstants.EDIT_TYPE_VIEW.equals(editorType)){
            	atr.viewConfig = attribute.getThing("ViewConifg@0");
            }else if(DataObjectConstants.EDIT_TYPE_GRID.equals(editorType)){
            	atr.viewConfig = attribute.getThing("GridConifg@0");
            }else if(DataObjectConstants.EDIT_TYPE_QUERY.equals(editorType)){
            	atr.viewConfig = attribute.getThing("QueryConfig@0");
            }else{
            	atr.viewConfig = attribute.getThing("EditConfig@0");
            }
            if(atr.viewConfig == null){
                atr.viewConfig = attribute;
            }
            
            attributes.add(atr);
        }
        
        return attributes;
    }
    
	/**
	 * 把源事物的属性转移到目标事物上，如果目标属性不为空那么不转移。
	 * 
	 * @param source
	 * @param sourceName
	 * @param target
	 * @param targetName
	 * @param quote 如果不是null，那么目标属性包含在引用中
	 */
	public static void transferStringValue(Thing source, String sourceName, Thing target, String targetName, String quote){
		String targetValue = target.getStringBlankAsNull(targetName);
		if(targetValue == null){
			String sourceValue = source.getStringBlankAsNull(sourceName);
			if(sourceValue != null && quote != null){
				if(!sourceValue.startsWith(quote)){
					sourceValue = quote + sourceValue + quote;
				}
			}
			
			if(sourceValue != null){
				target.put(targetName, sourceValue);
			}
		}
	}
	
	/**
	 * 从指定事物上获取数据对象。
	 * 
	 * XWorker通用的设置数据对象的方法是先从属性dataObjectPath定义，然后从DataObject子节点定义。
	 * @param self
	 * @return
	 */
	public static Thing getDataObject(Thing self, String dataObjectAttributeName, String dataObjectThingName){
		return getThing(self, dataObjectAttributeName, dataObjectThingName, true);
	}
	
	/**
	 * 获取通过self定义的查询条件。
	 * 
	 * @param self
	 * @param conditionAttributeName
	 * @param conditionThingName
	 * @return
	 */
	public static Thing getCondition(Thing self, String conditionAttributeName, String conditionThingName){
		return getThing(self, conditionAttributeName, conditionThingName, false);
	}
	
	/**
	 * 获取self定义的事物，先从属性指定，再从定义的子节点获取。
	 * 
	 * @param self
	 * @param thingAttributeName
	 * @param thingThingName
	 * @return
	 */
	public static Thing getThing(Thing self, String thingAttributeName, String thingThingName, boolean isThingChild){
		World world = World.getInstance();
		
		//先从属性属性或取
		String dataObjectPath = self.getStringBlankAsNull(thingAttributeName);
		if(dataObjectPath != null){
			return world.getThing(dataObjectPath);
		}
		
		//再从子节点中获取
		Thing dataObject = self.getThing(thingThingName + "@0");
		if(isThingChild){
			if(dataObject != null && dataObject.getChilds().size() > 0){
				return dataObject.getChilds().get(0);
			}
		}else{
			return dataObject;
		}
		
		return null;
	}
}