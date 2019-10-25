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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ExtendDataObjectCreator {
    public static Object initExtends(ActionContext actionContext){
        return null;
    }

    /*
    @SuppressWarnings("unchecked")
	public static void copyExtendDataObject(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing thing = (Thing) actionContext.get("thing");
    	
        Thing ext = world.getThing(thing.getString("extendedDataObject"));
        //println ext;
        if(ext != null){
            ext = ext.detach();
            ext.set("extends", null);
            List<Thing> actions = (List<Thing>) ext.get("actions@");
            for(Thing ac : actions){
                ext.removeChild(ac);
            }
        
            thing.paste(ext);
            ((SwtListener) actionContext.get("refreshMenuSelection")).handleEvent(null);
        }
    }
*/
    public static Object load(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("load", actionContext);
        }else{
            return null;
        }
    }

    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("create", actionContext);
        }else{
            return null;
        }
    }

    public static Object update(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("update", actionContext);
        }else{
            return null;
        }
    }

    public static Object delete(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("delete", actionContext);
        }else{
            return null;
        }
    }

    public static Object query(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("query", actionContext);
        }else{
            return null;
        }
    }

    public static Object updateBatch(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("updateBatch", actionContext);
        }else{
            return 0;
        }
    }

    public static Object deleteBatch(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("deleteBatch", actionContext);
        }else{
            return 0;
        }
    }

    public static Object isMappingAble(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("isMappingAble", actionContext);
        }else{
            return false;
        }
    }

    public static Object getMappingFields(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("getMappingFields", actionContext);
        }else{
            return null;
        }
    }

    public static Object getMappingAttributeName(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("getMappingAttributeName", actionContext);
        }else{
            return null;
        }
    }

    public static Object getAttributeDescriptor(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObj = world.getThing(self.getString("extendedDataObject"));
        if(dataObj != null){
            return dataObj.doAction("getAttributeDescriptor", actionContext);
        }else{
            return null;
        }
    }

}