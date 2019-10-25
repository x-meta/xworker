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
package xworker.lang.features;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FeatureCreator {
    public static void apply(ActionContext actionContext){
        if(actionContext.get("thing") == null){
            return;
        }

        Thing self = (Thing) actionContext.get("self");        
        for(Thing child : self.getAllChilds()){
            child.doAction("apply", actionContext);
        }
    }

    public static Object createFeatures(ActionContext actionContext){
        Thing thing = (Thing) actionContext.get("thing");
        //创建特征事物
        Thing feature = new Thing("xworker.lang.features.Feature");
        createFeatures(thing, feature);
        return feature;
    }
    
    public static void createFeatures(Thing thing, Thing feature){
        //创建属性特征
        for(Thing attributeDesc : thing.getAllAttributesDescriptors()){
            String attrValue = thing.getString(attributeDesc.getString("name"));
            if(attrValue != null && attrValue != "" && attrValue != attributeDesc.getString("default")){
                //创建属性特征
                Thing attrFeature = new Thing("xworker.lang.features.Feature/@AttributeFreature");
                attrFeature.put("name", attributeDesc.get("name"));
                attrFeature.put("label", attributeDesc.get("nlabel"));
                attrFeature.put("en_label", attributeDesc.get("nen_label"));
                attrFeature.put("zh_label", attributeDesc.get("nzh_label"));
                attrFeature.put("value", attrValue);
                feature.addChild(attrFeature);
            }
        }
        
        //创建子事物特征
        for(Thing child : thing.getChilds()){
            Thing childFeature = new Thing("xworker.lang.features.Feature/@ChildThingFreature");
            Thing childDesc = child.getDescriptor();
            childFeature.put("name", childDesc.get("name"));
            childFeature.put("label", childDesc.get("label"));
            childFeature.put("en_label", childDesc.get("en_label"));
            childFeature.put("zh_label", childDesc.get("zh_label"));
            childFeature.put("featureValues",  "name=" + child.get("name"));
            feature.addChild(childFeature);
            
            createFeatures(child, childFeature);
        }
    }

}