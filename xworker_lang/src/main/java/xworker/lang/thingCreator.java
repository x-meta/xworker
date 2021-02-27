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
package xworker.lang;

import java.io.ByteArrayOutputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.World;
import org.xmeta.codes.XmlCoder;

public class thingCreator {
    public static Object toXml(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return XmlCoder.encodeToString(self);
    }
    
    public static Object toJson(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	ThingCoder coder = World.getInstance().getThingCoder("xer.js");
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	coder.encode(self, out);
    	return new String(out.toByteArray());
    }

    public static Object getAttribute(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	String name = (String) actionContext.get("name");
    	
        Object value = self.get(name);
        if(value == null){
            for(Thing extend_ : self.getAllExtends()){
                value = extend_.get(name);
                if(value != null){
                    return value;
                }
            }
            
            return value;
        }else{
            return value;
        }
    }

    public static Object getAllDEPaths(ActionContext actionContext){
        Thing thing = (Thing) actionContext.get("thing");
        if(thing == null){
            thing = (Thing) actionContext.get("self");
        }
        
        String paths = "";
        for(Thing th : thing.getDescriptors()){
            if(paths.indexOf(th.getMetadata().getPath()) == -1){
                if(paths != ""){
                    paths = paths + ",";
                }
                paths = paths + th.getMetadata().getPath();
            }
        }
        
        for(Thing th : thing.getAllExtends()){
            if(paths.indexOf(th.getMetadata().getPath()) == -1){
                if(paths != ""){
                    paths = paths + ",";
                }
                paths = paths + th.getMetadata().getPath();
            }
        }
        
        return paths;
    }

    public static Object getIDEAdditionalThings(ActionContext actionContext){
    	return null;
    }
}