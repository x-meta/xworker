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
package xworker.html.base.validate;

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ValidateTemplateBlankCreator {
    @SuppressWarnings("unchecked")
	public static Object process(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        Thing wordsNode = self.getThing("InputThing@0");
        String validateCodes = wordsNode.getMetadata().getName();
        Map<String, Object> data = (Map<String, Object>) actionContext.get("dat");
        for(Thing thing : (List<Thing>) wordsNode.get("attribute@")){
            if(!"emsg".equals(thing.getMetadata().getName())){
                if(validateCodes != ""){
                    Object value = data.get(thing.getMetadata().getName());
                    if(value != null){
                        validateCodes += "|" + value;
                    }
                }else{
                	Object value = data.get(thing.getMetadata().getName());
                    if(value != null){
                        validateCodes += value;
                    }
                }
            }
        }
        
        Thing thing = new Thing();
        thing.set("alt", validateCodes);
        thing.set("emsg", data.get("emsg"));
        
        return thing;
    }

}