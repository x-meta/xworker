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
package xworker.html.base.component;

import java.util.regex.Matcher;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class htmlCodeCreator {
    public static Object toHtml(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");

        String code =  self.getStringBlankAsNull("htmlCode");
        if(code != null){
        	for(Thing child : self.getChilds()){
        		String html = (String) child.doAction("toHtml", actionContext);
        		if(html == null){
        			html = "";
        		}
        		
        		code = code.replaceAll("%%" + child.getMetadata().getName() + "%%", Matcher.quoteReplacement(html));
        	}
        }
        
        return code;
    }

}