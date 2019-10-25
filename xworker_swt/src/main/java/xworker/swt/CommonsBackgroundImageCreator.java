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
package xworker.swt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.OgnlException;

public class CommonsBackgroundImageCreator {
    public static void create(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        Image image = (Image) self.getObject("image", actionContext);
        if(image != null){
            ((Control) actionContext.get("parent")).setBackgroundImage(image);
        }else{
            Thing imageObj = self.getThing("Image@0");
            if(imageObj != null){
                ActionContext newContext = new ActionContext();
                newContext.put("parent", null);
                image = (Image) imageObj.doAction("create", newContext);
                ((Control) actionContext.get("parent")).setBackgroundImage(image);
            }
        }
    }

}