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

public class IteratorGroovyCreator {
	/**
	 * 不再使用，使用Iterator。
	 * 
	 * @param actionContext
	 */
    public static void run(ActionContext actionContext){
    	/*
        import org.xmeta.Thing;
        import org.xmeta.Action;
        import org.xmeta.ActionContext;
        import org.xmeta.Bindings;
        import org.xmeta.World;
        import org.xmeta.cache.ThingEntry;
        
        import xworker.util.UtilJavaScript;
        import xworker.util.UtilAction;
        
        import java.util.Collection;
        import java.util.Iterator;
        
        import ognl.Ognl;
        import  ognl.OgnlException;
        String dataName = self.getString("varName");
        String varName = self.getString("collectionName");
        
        def c = OgnlUtil.getValue(varName, actionContext);//UtilAction.runAsGroovy(self, "collectionName", actionContext, self.getMetadata().getPath()); 
        
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
                    //System.out.println(child.getMetadata().getPath());
                    result = child.getAction().run(actionContext, null, true);
                   
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
        }*/
    }

}