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
package xworker.lang.actions.thing;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import xworker.util.UtilData;

public class CheckOrCreateThingActions {
    public static Object run(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        String sthingManager = self.doAction("getThingManager", actionContext);
        ThingManager thingManager = world.getThingManager(sthingManager);
        if(thingManager == null) {
        	if(sthingManager == null || "".equals(sthingManager)) {
        		thingManager = world.getThingManagers().get(0);
        	}else {
        		if(thingManager == null){
                    world.createThingManager(sthingManager, "");
                    thingManager = world.getThingManager(sthingManager);
                }
        	}
        }
        
        String thingPath = self.doAction("getThingPath", actionContext);
        Thing thing = null;
        if(UtilData.isTrue(self.doAction("isGloableThing", actionContext))) {
        	thing = world.getThing(thingPath);
        }else {
        	thing = thingManager.getThing(thingPath);
        }
       
        if(thing == null){
        	Thing thingForCreate = self.doAction("getThing", actionContext);
        	thingForCreate.saveAs(thingManager.getName(), thingPath);            
        }
        
        return thing;
    }

	public static Thing getThing(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing replaceThing = self.doAction("getReplaceThing", actionContext);
		if (replaceThing != null) {
			return replaceThing.detach();			
		} else {
			return new Thing((String) self.doAction("getThingDescriptor", actionContext));
		}
	}
    
    public static Thing createThing(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
    	String descriptor = self.doAction("getDescriptor", actionContext);
    	return new Thing(descriptor);
    }
}