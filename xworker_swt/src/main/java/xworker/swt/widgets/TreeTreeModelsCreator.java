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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.swt.SwtTreeModelUtils;

import java.util.List;

public class TreeTreeModelsCreator {
    public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        for(Thing child : self.getChilds()){
            Object object = child.doAction("create", actionContext);

            if(object instanceof TreeModel){
                //为新的TreeModel方式，旧的逐渐抛弃，旧的比较难懂，新的比较简单异动
                TreeModel treeModel = (TreeModel) object;
                //保存变量
                actionContext.g().put(child.getMetadata().getName(), treeModel);

                //绑定
                if(treeModel.isBindToParent()){
                    Object parent = actionContext.getObject("parent");
                    SwtTreeModelUtils.bind(treeModel, parent, actionContext);
                }

                List<String> controls = treeModel.getParentControls();
                if(controls != null){
                    for(String control : controls){
                        SwtTreeModelUtils.bind(treeModel, actionContext.get(control), actionContext);
                    }
                }
            }
            //只有第一个子节点生效
            break;
        }
    }

}