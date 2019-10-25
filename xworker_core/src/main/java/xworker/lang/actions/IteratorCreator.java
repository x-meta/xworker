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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilJava;

import ognl.OgnlException;

public class IteratorCreator {
	static World world = World.getInstance();
	
	public static Object getCollection(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String cstr = self.getStringBlankAsNull("collectionName");
		if(cstr == null){
			throw new ActionException("Collection can not be null, action=" + self.getMetadata().getPath());
		}
		
		int index = cstr.indexOf("..");
		if(index != -1){
			String starts = cstr.substring(0, index).trim();
			String ends = cstr.substring(index + 2, cstr.length()).trim();
			
			List<Object> list = new ArrayList<Object>();
			try{
				int start = Integer.parseInt(starts);
				int end = Integer.parseInt(ends);
				for(int i=start; i<= end; i++){
					list.add(i);
				}
			}catch(Exception e){
				if(starts.length() == 1 && ends.length() == 1){
					char start = starts.charAt(0);
					char end = ends.charAt(0);
					for(char i = start; i<=end; i++){
						list.add(i);
					}
				}else{
					throw new ActionException("Unknown range " +  cstr + ", action=" + self.getMetadata().getPath());
				}
			}
			
			return list;
		}
		
		return  OgnlUtil.getValue(self, "collectionName", actionContext);
	}
	
	public static Object run(ActionContext actionContext) throws Exception{
        Thing self = (Thing) actionContext.get("self");
        String dataName = self.getString("varName");
        //String varName = self.getString("collectionName");
        
        Iterable<Object> c = UtilJava.getIterable(self.doAction("getCollection", actionContext));
        //Iterable<Object> c = (Iterable<Object>) OgnlUtil.getValue(varName, actionContext);//UtilAction.runAsGroovy(self, "collectionName", actionContext, self.getMetadata().getPath()); 
        
        //System.out.println("Iterator: begin");
        Bindings bindings = actionContext.push(null);
        bindings.setVarScopeFlag();
        try{
            Object result = null;
            Iterator<Object> iter = c.iterator();
            int i = 0;
            //int size = c.size();
            //bindings.put(dataName + "_index", i);
            //bindings.put(dataName + "_size", size);
            Thing childActions = self.getThing("ChildAction@0"); 
            if(childActions != null){
	            while(iter.hasNext()){           
	                Object b = iter.next(); 
	                bindings.put(dataName + "_index", i);
	                boolean hasNext = iter.hasNext();
	                bindings.put(dataName + "_hasNext", hasNext);
	                bindings.put(dataName + "_has_next", hasNext);
	                bindings.put(dataName, b);
	                //System.out.println("dataName=" + dataName + ",=" + b);
	                for(Thing child : childActions.getChilds()){
	                    //System.out.println(child.getMetadata().getPath());
	                    result = child.getAction().run(actionContext, null, true);
	                   
	        		    int sint = actionContext.getStatus();
	    	    	    if(sint != ActionContext.RUNNING){
	    				    break;
	    			    }
	                }
	                
	                int status = actionContext.getStatus();	            
	                if(status == ActionContext.BREAK){
	                    actionContext.setStatus(ActionContext.RUNNING);
	                    break;
	                }else if(status == ActionContext.CONTINUE){
	                    actionContext.setStatus(ActionContext.RUNNING);
	                    continue;
	                }else if(status == ActionContext.RETURN){
	              	    break;
	                }
	                
	                i++;
	            }
            }
            
            return result;
        }finally{
            actionContext.pop();
        }        
    }

}