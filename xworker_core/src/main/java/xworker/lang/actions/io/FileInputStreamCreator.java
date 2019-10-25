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
package xworker.lang.actions.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class FileInputStreamCreator {
    public static void init(ActionContext actionContext) throws FileNotFoundException{       
    	Thing self = (Thing) actionContext.get("self");
    	ActionContext acContext = (ActionContext) actionContext.get("acContext");
    	FileInputStream fin = new FileInputStream(new File(UtilString.getString(self.getString("filePath"), acContext)));
    	actionContext.getScope(0).put("fin", fin);
        acContext.put(self.getString("dataName"), fin);
        //log.info(self.dataName);
    }

    public static void success(ActionContext actionContext) throws IOException{
        if(actionContext.get("fin") != null){
            ((FileInputStream)actionContext.get("fin")).close();
        }
    }

    public static void exception(ActionContext actionContext) throws IOException{
        if(actionContext.get("fin") != null){
            ((FileInputStream)actionContext.get("fin")).close();
        }
    }

    public static Object inherit(ActionContext actionContext){
        return null;
    }

}