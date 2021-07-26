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
package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThrowCreator {
	public static Object run(ActionContext actionContext) throws Throwable{
        Thing self = (Thing) actionContext.get("self");

        Object value = self.doAction("getValue", actionContext);
        if(value != null){
            actionContext.setThrowedObject(value);
            actionContext.setStatus(ActionContext.EXCEPTION);
            return value;
        }

        Throwable throwable = self.doAction("getThrowable", actionContext);
        if(throwable != null){
            throw throwable;
        }

        throw new Exception(self.getMetadata().getLabel() + ":" + self.getMetadata().getPath());
    }	  
}