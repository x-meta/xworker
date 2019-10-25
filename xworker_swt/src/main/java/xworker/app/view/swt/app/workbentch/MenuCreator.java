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
package xworker.app.view.swt.app.workbentch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MenuCreator {
	private static Logger log = LoggerFactory.getLogger(MenuCreator.class);
    public static Object selectActions(ActionContext actionContext){
    	Thing thing = (Thing) actionContext.get("thing");
        Thing root = thing;
        
        while(!"menus".equals(root.getString("thingName"))){
            root = root.getParent();
            if(root == null){
                break;
            }
        }
        
        if(root != null){
            root = root.getParent();
        }
        if(root == null){
            log.warn("Can not find workbentch or editor");
            return Collections.emptyList();
        }else{
        
            log.info("root=" + root);
            Thing actions = root.getThing("actions@0");
            if(actions == null){
                log.warn("actions is null");
                return Collections.emptyList();
            }
            List<Map<String, Object>> things = new ArrayList<Map<String, Object>>();
            for(Thing action : actions.getChilds()){
                Map<String, Object> th = new HashMap<String, Object>();
                th.put("name", action.getMetadata().getName());
                th.put("label", action.getMetadata().getName());
                th.put("path", action.getMetadata().getName());
                things.add(th);
            }
            return things;
        }
    }

}