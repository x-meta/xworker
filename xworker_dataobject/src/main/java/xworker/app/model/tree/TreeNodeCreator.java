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
package xworker.app.model.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class TreeNodeCreator {
    public static Object getTreeModel(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
    	String treeModelRef = self.getString("treeModelRef");
        if(treeModelRef != null && !"".equals(treeModelRef)){
            Thing treeModel = (Thing) actionContext.get(treeModelRef);
            if(treeModel != null){
                return treeModel;
            }else{
                Thing modelThing = World.getInstance().getThing(treeModelRef);
                if(modelThing != null){
                    Thing model = new Thing();
                    model.put("descriptors", "xworker.app.model.tree.TreeModelInsAction");
                    model.put("extends", "xworker.app.model.tree.TreeModelInsAction");
                    model.put("modelThing", modelThing);
                    model.put("listeners", new ArrayList<Thing>());
                    return model;
                }
            }
        }
        
        Thing treeModels = self.getThing("TreeModels@0");
        if(treeModels != null){
            if(treeModels.getChilds().size() > 0){
                Thing modelThing = treeModels.getChilds().get(0);
                if(modelThing != null){
                    Thing model = new Thing();
                    model.put("descriptors", "xworker.app.model.tree.TreeModelInsAction");
                    model.put("extends", "xworker.app.model.tree.TreeModelInsAction");
                    model.put("modelThing", modelThing);
                    model.put("listeners", new ArrayList<Thing>());
                    model.put("treeNodes", new HashMap<String, Map<String, Object>>());
                    return model;
                }
            }
        }
        
        return null;
    }

}