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
package xworker.swt.statement;

import java.util.Collection;
import java.util.Iterator;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

public class ITERATORCreator {
    @SuppressWarnings({ "rawtypes" })
	public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        String dataName = self.getString("varName");
        String varName = self.getString("collectionName");
        
        Collection c = (Collection) self.doAction("getCollection", actionContext); 
        
        //System.out.println("Iterator: begin");
        Bindings bindings = actionContext.push(null);
        try{
            Object result = "success";
            Iterator iter = c.iterator();
            int i = 0;
            int size = c.size();
            bindings.put(dataName + "_index", i);
            bindings.put(dataName + "_size", size);
            
            while(iter.hasNext()){           
                Object b = iter.next(); 
                bindings.put(dataName + "_index", i);
                bindings.put(dataName + "_hasNext", iter.hasNext());
                bindings.put(dataName, b);
                //System.out.println("dataName=" + dataName + ",=" + b);
                
                for(Thing child : self.getChilds()){
                    result = child.doAction("create", actionContext, null, true);
                   
        		    int sint = actionContext.getStatus();
    	    	    if(sint != ActionContext.RUNNING){
    				    break;
    			    }
                }
                
                if(actionContext.getStatus() == ActionContext.BREAK){
                    actionContext.setStatus(ActionContext.RUNNING);
                    break;
                }else if(actionContext.getStatus() == ActionContext.CONTINUE){
                    actionContext.setStatus(ActionContext.RUNNING);
                    continue;
                }else if(actionContext.getStatus() != ActionContext.RUNNING){
              	    break;
                }
                
                i++;
            }
            
            return result;
        }finally{
            actionContext.pop();
        }        
    }

    public static Object getCollection(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        return UtilData.getObject(self, "collectionName", actionContext);
    }
}