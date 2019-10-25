/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.app.view.extjs.xworker.thingEditor.dataobjects;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingIndex;
import org.xmeta.ThingManager;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.http.HttpRequestBean;

public class DescriptorsDataObjectActions {
    public static Object query(ActionContext actionContext){
        World world = World.getInstance();
        HttpRequestBean requestBean = actionContext.getObject("requestBean");
        
        //获取事物
        String thingPath = (String) requestBean.get("thingPath");
        thingPath = thingPath.substring(6, thingPath.length());
        Thing thing = world.getThing(thingPath);
        
        List<DataObject> datas = new ArrayList<DataObject>();
        for(Thing child : thing.getChilds()){
            if("Category".equals(child.getThingName())){
                continue;
            }
            
            DataObject data = new DataObject("xworker.app.view.extjs.xworker.thingEditor.dataobjects.DescriptorsDataObject");
            data.put("name", child.getMetadata().getLabel());
            data.put("path", child.getMetadata().getPath());
            data.put("descriptor", child.getString("descriptor"));
            datas.add(data);
        }
        
        return datas;
    }
    
    public static Object queryThing(ActionContext actionContext){
    	World world = World.getInstance();
        HttpRequestBean requestBean = actionContext.getObject("requestBean");
        
        //获取事物
        String thingManagerName = (String) requestBean.get("thingManager");
        String categoryName = (String) requestBean.get("category");
        ThingManager thingManager = world.getThingManager(thingManagerName);
        List<ThingIndex> indexs = thingManager.getThingIndexs(categoryName);
                
        List<DataObject> datas = new ArrayList<DataObject>();
        if(indexs != null){
	        for(ThingIndex child : indexs ){
	            DataObject data = new DataObject("xworker.app.view.extjs.xworker.thingEditor.dataobjects.ThingDataObject");
	            data.put("name", child.getName());
	            data.put("path", child.path);
	            data.put("descriptor", child.descriptors);
	            datas.add(data);
	        }
        }
        
        return datas;
    }

}