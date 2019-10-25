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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class FileOutputStreamCreator {
    public static void init(ActionContext actionContext) throws FileNotFoundException{
    	Thing self = (Thing) actionContext.get("self");
        ActionContext acContext = (ActionContext) actionContext.get("acContext");
        
        File file = new File(UtilString.getString(self.getString("filePath"), acContext));
        if(!file.exists()){
        	file.getParentFile().mkdirs();
        }
        FileOutputStream fout = new FileOutputStream(file);
        actionContext.getScope(0).put("fout", fout);
        acContext.put(self.getString("dataName"), fout);
        //log.info(self.dataName);
    }

    public static void success(ActionContext actionContext) throws IOException{
        if(actionContext.get("fout") != null){
            ((FileOutputStream) actionContext.get("fout")).close();
        }
    }

    public static void exception(ActionContext actionContext) throws IOException{
        if(actionContext.get("fout") != null){
        	((FileOutputStream) actionContext.get("fout")).close();
        }
    }

    public static Object inherit(ActionContext actionContext){
        return null;
    }

}