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
package xworker.lang.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.util.UtilTemplate;

public class ThingTemplate {
	private static Logger log = LoggerFactory.getLogger(ThingTemplate.class);
	
	/**
	 * 执行事物模板的入口方法。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object process(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		try{
		    //parentThing是否存在
		    Bindings bindings = actionContext.push();
		    Thing parentThing = (Thing) actionContext.get("parentThing");
		    if(parentThing == null){
		        parentThing = new Thing();
		        bindings.put("parentThing", parentThing);
		    }
		    
		    //执行节点
		    Action action = world.getAction("xworker.lang.util.ThingTemplate/@actions/@processNode");
		    for(Thing child : self.getAllChilds()){
		        action.run(actionContext, UtilMap.toMap(new Object[]{"node", child}), true);
		    }
		    
		    return parentThing;
		}finally{
		    actionContext.pop();
		}
	}
	
	/**
	 * 处理节点。
	 * 
	 * @param actionContext
	 * @return
	 * @throws Throwable 
	 */
	public static Object processNode(ActionContext actionContext) throws Throwable{
		World world = World.getInstance();

		Thing parentThing = (Thing) actionContext.get("parentThing");
		
		int sint = actionContext.getStatus();
		if(sint != ActionContext.RUNNING){
		    return null;
		}

		Action action = world.getAction("xworker.lang.util.ThingTemplate/@actions/@processNode");
		Thing node = (Thing) actionContext.get("node");
		
		//如果是模板的语句节点，执行模板语句
		if(node.isThing("xworker.lang.util.ThingTemplateFlag") && node.getInt("ingoreCount") == 0){
		    Object result = node.doAction("process", actionContext, null, true);    
		    return result;
		}else{ 
		    Thing thing = new Thing();
		    //替换节点事物的属性的模板值，使用Freemarker模板
		    for(String key : node.getAttributes().keySet()){
		    	Object ovalue = node.get(key);
		    	if(ovalue != null && ovalue instanceof String){
			        String value = (String) ovalue;
			        if(value != null && (value.indexOf("${") != -1 || value.indexOf("<#") != -1 || value.indexOf("<@") != -1)){
			            //如果值含有Freemarker模板的标识，那么调用模板
			            try{
			            	ovalue = UtilTemplate.processString(actionContext, value);
			            }catch(Throwable t){
			                //log.error("process tempalte : " + node.getMetadata().getPath() + " : " + key);
			                throw new XMetaException("process tempalte : " + node.getMetadata().getPath() + " : " + key, t);
			            }
			        }
		    	}
		    
		        if(ovalue != null){
		        	thing.getAttributes().put(key, ovalue);
		        }
		    }
		    
		    if(actionContext.get("parentThing") != null){
		        parentThing.addChild(thing);
		    }
		    
		    for(Thing child : node.getChilds()){
		        action.run(actionContext, UtilMap.toMap(new Object[]{"node", child, "parentThing", thing}), true);
		    }
		    
		    return thing;
		}
	}
	
	/**
	 * 实现动作的run方法。
	 * 
	 * @param actionContext
	 */
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		actionContext.put("parentThing", self.doAction("getParentThing", actionContext));
		Thing thing = (Thing) process(actionContext);
		
		String varName = self.getString("varName");
	    if(varName != null && !"".equals(varName)){
	        Bindings bds = (Bindings) self.doAction("getVarScope", actionContext);
	        if(bds != null){
	            bds.put(varName, thing);
	        }
	    }
	    return thing;		
	}
	
	@SuppressWarnings("unchecked")
	public static Object listProcess(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        
        Thing parentThing = (Thing) actionContext.get("parentThing");
        Action action = world.getAction("xworker.lang.util.ThingTemplate/@actions/@processNode");
        
        String dataName = self.getString("varName");
        String varName = self.getString("collectionName");
        
        //Collection c = (Collection) UtilAction.runAsGroovy(self, "collectionName", actionContext, self.getMetadata().getPath());//UtilJavaScript.runScript(varName, actionContext);
        Iterable<Object> c = (Iterable<Object>) OgnlUtil.getValue(varName, actionContext);
        if(c == null){
        	throw new ActionException("List collection is null, path=" + self.getMetadata().getPath());
        }
        Bindings bindings = actionContext.push(null);
        try{
            Object result = null;
            Iterator<Object> iter = c.iterator();
            int i = 0;
            bindings.put(dataName + "_index", i);
            
            while(iter.hasNext()){           
                Object b = iter.next(); 
                bindings.put(dataName + "_index", i);
                bindings.put(dataName + "_hasNext", iter.hasNext());
                bindings.put(dataName, b);
                for(Thing child : self.getAllChilds()){
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("node", child);
                    result = action.run(actionContext, parameters, true); 
                      if(result != null && result instanceof Thing){
                        if(parentThing == null){
                            return result;
                        }
                    }      
                 
        		    int sint = actionContext.getStatus();
    	    	    if(sint != ActionContext.RUNNING){
    				    break;
    			    }
                }
                
                i++;
                if(actionContext.getStatus() == ActionContext.BREAK){
                    actionContext.setStatus(ActionContext.RUNNING);
                    break;
                }else if(actionContext.getStatus() == ActionContext.CONTINUE){
                    actionContext.setStatus(ActionContext.RUNNING);
                    continue;
                }else if(actionContext.getStatus() != ActionContext.RUNNING){
              	    break;
                }
            }
            
            return result;
        }finally{
            actionContext.pop();
        }        
	}
	
	@SuppressWarnings("unchecked")
	public static Object thingProcess(ActionContext actionContext) throws OgnlException, IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        Thing parentThing = (Thing) actionContext.get("parentThing");  
         
        String thingPath = UtilTemplate.processThingAttributeToString(self, "thingPath", actionContext);
        Object thing = null;
        if(thingPath == null || "".equals(thingPath)){
        }else{               
	        thing = world.get(thingPath);
	        if(thing == null && !"".equals(thingPath)){
	        	try{
	        		thing = OgnlUtil.getValue(self, "thingPath", thingPath, actionContext);
	        	}catch(Exception e){
	        		
	        	}
	        
	        }
	        
	        addThing(parentThing, thing, self.getBoolean("addSoft"), self.getBoolean("pasteToParent"));	        
        }
                
        String thingVar = UtilTemplate.processThingAttributeToString(self, "thingVar", actionContext);
        if(thingVar != null && !"".equals(thingVar)){
        	thing = OgnlUtil.getValue(self, "thingVar", thingVar, actionContext);
        	if(thing != null){
        		addThing(parentThing, thing, self.getBoolean("addSoft"), self.getBoolean("pasteToParent"));
        	}
        }
        
        return thing;
	}
	
	@SuppressWarnings("unchecked")
	private static void addThing(Thing parentThing, Object thing, boolean addSoft, boolean paste){
		if(thing != null){
			if(paste){
				if(thing instanceof Thing){
        			parentThing.paste((Thing) thing);
        		}else if(thing instanceof List){
        			List<Thing> things = (List<Thing>) thing;
        			for(Thing t : things){
        				parentThing.paste(t);
        			}
        		}
			}else{
	        	if(addSoft){
	        		if(thing instanceof Thing){
	        			parentThing.addChild((Thing) thing, false);
	        		}else if(thing instanceof List){
	        			List<Thing> things = (List<Thing>) thing;
	        			for(Thing t : things){
	        				parentThing.addChild(t, false);
	        			}
	        		}
	        	}else{
	        		if(thing instanceof Thing){
	        			parentThing.addChild(((Thing) thing).detach());
	        		}else if(thing instanceof List){
	        			List<Thing> things = (List<Thing>) thing;
	        			for(Thing t : things){
	        				parentThing.addChild(t.detach());
	        			}
	        		}
	        	}
			}
        }
	}
	
	public static Object setattributeProcess(ActionContext actionContext) throws Exception{
		 Thing self = (Thing) actionContext.get("self");
	        
	     Thing parentThing = (Thing) actionContext.get("parentThing");
	     if(parentThing != null){
	    	 for(Thing attr : self.getChilds("AttributeTemplate")){
	    		 String name = attr.getString("name");
	    		 String value = attr.getString("value");
	    		 value = UtilTemplate.processString(actionContext, value);
	    		 parentThing.set(name, value);
	    	 }
	     }
	     return null;
	}
	
	public static Object ifProcess(ActionContext actionContext) throws Exception{
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        
        Thing parentThing = (Thing) actionContext.get("parentThing");  
        Action action = world.getAction("xworker.lang.util.ThingTemplate/@actions/@processNode");      

        List<Thing> elseIfList = self.getChilds("#ElseIf");
        Thing elseObj = self.getThing("#Else@0");
        
        Object result = "success";
    	List<Thing> statements = null;        	
    	Object condition =  self.doAction("condition", actionContext);
    	if(condition != null && (Boolean) condition){
    		//If
    		statements = self.getAllChilds();
    		List<Thing> forRemoved = new ArrayList<Thing>();
    		boolean isFirstActions = false;
    		for(Thing s : statements){
    			//过滤ElseIf和Else
    			if(s.isThingByName("#ElseIf") || s.isThingByName("#Else")){
    				forRemoved.add(s);
    			}else if(!isFirstActions && s.isThingByName("actions")){
    				isFirstActions = false;
    				forRemoved.add(s);
    			}
    		}
    		
    		for(Thing r : forRemoved){
    			statements.remove(r);
    		}
    	}else{
    		//执行ElseIf
    		boolean isElseIf = false;
    		for(Thing elseIf : elseIfList){        		    
    			if((Boolean) elseIf.doAction("condition", actionContext)){
    				isElseIf = true;
    				statements = elseIf.getAllChilds();
    				
    				//移除第一个actions节点
    				for(Thing child : statements){
    					if(child.isThingByName("actions")){
    						statements.remove(child);
    						break;
    					}
    				}
    				
    				break;
    			}
    		}
    		
    		if(!isElseIf && elseObj != null){
    			statements = elseObj.getAllChilds();
    		}
    		
    		
    	}
    	
    	if(statements != null){
    		for(Thing child : statements){
    		    Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("node", child);
                result = action.run(actionContext, parameters, true); 
                if(result != null && result instanceof Thing){
                    if(parentThing == null){
                        return result;
                    }
                }     
                //System.out.println(actionContext.getStatus());
				if(actionContext.getStatus() != ActionContext.RUNNING){
					break;
				}
            }
    	}            
        
        return result;
    }
	
	public static void breakProcess(ActionContext context){	    
        context.setStatus(ActionContext.BREAK);
    }	
	
	public static void continueProcess(ActionContext context){	    
        context.setStatus(ActionContext.CONTINUE);
    }	 
	
	public static void returnProcess(ActionContext actionContext) throws Exception{	
        actionContext.setStatus(ActionContext.RETURN);
    }
	
	public static void actionsProcess(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getAllChilds()){
			child.getAction().run(actionContext, null, true);
			
			if(actionContext.getStatus() == ActionContext.BREAK){
                actionContext.setStatus(ActionContext.RUNNING);
                break;
            }else if(actionContext.getStatus() == ActionContext.CONTINUE){
                actionContext.setStatus(ActionContext.RUNNING);
                continue;
            }else if(actionContext.getStatus() != ActionContext.RUNNING){
          	    break;
            }
		}
	}
}
