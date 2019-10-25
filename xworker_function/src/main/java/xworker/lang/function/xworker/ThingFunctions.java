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
package xworker.lang.function.xworker;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingFunctions {

	public static Object getDescriptors(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getDescriptors();
	}
	
	public static Object getDescriptor(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getDescriptor();
	}
	
	public static Object getExtends(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getExtends();
	}
	
	public static Object getLabel(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getMetadata().getLabel();
	}
	
	public static Object getName(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getMetadata().getName();
	}
	
	public static Object getPath(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getMetadata().getPath();
	}
	
	public static Object getThing(ActionContext actionContext){
		String thingPath = (String) actionContext.get("thingPath");
		return World.getInstance().getThing(thingPath);
	}
	
	public static Object getThingName(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getThingName();
	}
	
	public static Object getThingNames(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getThingNames();
	}
	
	public static Object getDescription(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		return thing.getMetadata().getDescription();
	}
	
	public static Object addChild(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		Thing child = getThing("child", actionContext);
		if(thing != null && child != null){
			Boolean soft = (Boolean) actionContext.get("soft");
			if(soft){
				thing.addChild(child, false);
			}else{
				thing.addChild(child, true);
			}
		}
		
		return true;
	}
	
	public static Object getChildThing(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		String childThingPath = (String) actionContext.get("childThingPath");
		return thing.getThing(childThingPath);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getChildThings(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		String childThingPath = (String) actionContext.get("childThingPath");
		return (List<Thing>) thing.get(childThingPath);
	}
	
	public static Object setAttribute(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		String attributeName = (String) actionContext.get("attributeName");
		Object attributeValue = actionContext.get("attributeValue");
		thing.set(attributeName, attributeValue);		
		return thing;
	}
	
	public static Thing setThingRegist(ActionContext actionContext){
		Thing thing = getThing("thing", actionContext);
		Thing registTo = getThing("registTo", actionContext);		
		String registType = (String) actionContext.get("registType");
		
		String value = registType + "|" + registTo.getMetadata().getPath();
		String th_registThing = thing.getStringBlankAsNull("th_registThing");
		if(th_registThing == null){
			th_registThing = value;
		}else{
			th_registThing = th_registThing + "," + value;
		}
		thing.set("th_registThing", th_registThing);
		return thing;
	}
	
	/**
	 * 一般的操作时事物是String的路径或事物本身时都应该可以，所以建立此方法统一返回事物。
	 * 
	 * @param name
	 * @param actionContext
	 * @return
	 */
	public static Thing getThing(String name, ActionContext actionContext){
		Object thing = actionContext.get(name);
		if(thing instanceof Thing){
			return (Thing) thing;
		}else{
			if(thing != null){
				if(thing instanceof String){
					return World.getInstance().getThing((String) thing);
				}else{
					World.getInstance().getThing(String.valueOf(thing));
				}
			}
			
			return null;
		}
	}
}