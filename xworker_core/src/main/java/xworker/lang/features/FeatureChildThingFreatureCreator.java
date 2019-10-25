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

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class FeatureChildThingFreatureCreator {
    @SuppressWarnings("unchecked")
	public static void apply(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        Thing thing = (Thing) actionContext.get("thing");
        
        List<Thing> matchedChilds = (List<Thing>) self.doAction("match", actionContext);
        if(matchedChilds.size() == 0){
            Thing child = new Thing();
            thing.addChild(child);
        }
        
        //对符合条件的子事物使用下级特征
        for(Thing child : matchedChilds){
            for(Thing schild : self.getAllChilds()){
                schild.doAction("apply", actionContext, UtilMap.toMap(new Object[]{"thing", child}));
            }
        }
    }

    public static Object match(ActionContext actionContext){
        List<String[]> keys = new ArrayList<String[]>();
        Thing self = (Thing) actionContext.get("self");
        Thing thing = (Thing) actionContext.get("thing");
        
        for(String keyValue : self.getString("featureValues").split("[,]")){
            String[] kvs = keyValue.split("[=]");
            keys.add(new String[]{kvs[0], kvs[1]});
        }
        
        List<Thing> matchedChilds = new ArrayList<Thing>();
        for(Thing child : thing.getChilds()){
            boolean match = true;
            for(String[] ks : keys){
                if(!child.getString(ks[0]).equals(ks[1])){
                    match = false;
                    break;
                }
            }
            if(match){
                matchedChilds.add(child);
            }
        }
        
        return matchedChilds;
    }

}