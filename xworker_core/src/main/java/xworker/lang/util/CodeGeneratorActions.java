/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.lang.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import freemarker.template.TemplateException;
import xworker.util.UtilData;
import xworker.util.UtilTemplate;

public class CodeGeneratorActions {
	private static Logger logger = LoggerFactory.getLogger(CodeGeneratorActions.class);
	
    public static void run(ActionContext actionContext) throws IOException, TemplateException{
        Thing self = actionContext.getObject("self");
        
        //获取事物列表
        Thing thingCollection = self.getThing("Things@0");
        String path = self.getMetadata().getPath();
        if(thingCollection == null){
            logger.warn(path + "\n        没有定义事物集合！");
            return;
        }
        
        Object obj = thingCollection.doAction("run", actionContext);
        if(obj == null){
            logger.warn(path + "\n        事物集合返回空事物！");
            return;
        }
        
        List<Thing> things = new ArrayList<Thing>();
        if(obj instanceof Thing){
            things.add((Thing) obj);
        }else{
        	things = UtilData.convert(obj);
        }
        
        //获取模板列表
        List<Thing> templates = self.getChilds("TextTemplate");
        try{
            Bindings bindings = actionContext.push();
            bindings.put("things", things);
            
            //生成单个事物的模板
            for(Thing thing : things){        
                bindings.put("thing", thing);
                
                for(Thing template : templates){
                    if(template.getBoolean("forAllThings")) continue;
                    
                    boolean pass = false;
                    String filter = template.getStringBlankAsNull("filterDescriptors");            
                    if(filter != null){
                        //过滤事物
                        String[] fs = filter.split("[,]");
                        for(String f : fs){
                            if(thing.isThing(f.trim())){
                                pass = true;
                                break;
                            }
                        }
                        
                        if(!pass){
                            continue;
                        }
                    }
                                
                    bindings.put(template.getString("thingsName"), things);
                    bindings.put(template.getString("thingName"), thing);
                    bindings.put("projectName", "/" + thing.getMetadata().getThingManager().getName());
                    bindings.put("thingManagerName", "/" + thing.getMetadata().getThingManager().getName());
                    bindings.put("categoryName", "/" + thing.getMetadata().getCategory().getName().replace('.', '/'));
                    bindings.put("thingName", thing.getMetadata().getName());
                                            
                    Thing t = new Thing("xworker.lang.util.TextTemplate");
                    t.set("name", template.get("name"));
                    t.set("templatePath", template.get("templatePath"));
                    t.set("type", template.get("templateType"));
                    t.set("templateEncoding", template.get("templageEncoding"));
                    t.set("isTemplateContext", "true");
                    String output = UtilTemplate.processString(actionContext, template.getString("outputFile"));
                    String outputDir = template.getStringBlankAsNull("outputDir");
                    String selfOutputDir = self.getStringBlankAsNull("outputDir");
                    if(outputDir != null){
                        output = ((Thing) self.get("template")).getString("outputDir") + "/" + output;
                    }else if(selfOutputDir != null){
                        output = selfOutputDir + "/" + output;
                    }else{
                        logger.warn(template.getMetadata().getPath() + "\n        没有设置输出目录！");
                        continue;
                    }
                    
                    t.set("output", "\"" + output + "\"");
                    t.set("outputEncoding", template.getString("outputEncoding"));
                    t.doAction("run", actionContext);
                }
            }    
            
            //生成多个事物的模板
            if(true){
                for(Thing template : templates){
                    if(template.getBoolean("forAllThings") == false) continue;
                    
                    List<Thing> ts = new ArrayList<Thing>();
                    String filter = template.getStringBlankAsNull("filterDescriptors");            
                    for(Thing thing : things){                
                        if(filter != null && filter != ""){
                            //过滤事物
                            String[] fs = filter.split("[,]");
                            for(String f : fs){
                                if(thing.isThing(f.trim())){
                                    ts.add(thing);                            
                                    break;
                                }
                            }
                        }else{
                            ts.add(thing);
                        }
                    }
                    
                    bindings.put("things", ts);
                    bindings.put(template.getString("thingsName"), ts);
                      
                    Thing t = new Thing("xworker.lang.util.TextTemplate");
                    t.set("name", template.get("name"));
                    t.set("templatePath", template.get("templatePath"));
                    t.set("type", template.get("templateType"));
                    t.set("templateEncoding", template.get("templageEncoding"));
                    t.set("isTemplateContext", "true");
                    String output = UtilTemplate.processString(actionContext, template.getString("outputFile"));
                    String outputDir = template.getStringBlankAsNull("outputDir");
                    String soutputDir = self.getStringBlankAsNull("outputDir");  
                    if(outputDir != null){
                        output = ((Thing) self.get("template")).get("outputDir") + "/" + output;
                    }else if(soutputDir != null){
                        output = soutputDir + "/" + output;
                    }else{
                        logger.warn(template.getMetadata().getPath() + "\n        没有设置输出目录！");
                        continue;
                    }
                    
                    t.set("output", "\"" + output + "\"");
                    t.set("outputEncoding", template.get("outputEncoding"));
                    t.doAction("run", actionContext);
                }
            }
        }finally{
            actionContext.pop();
        }
    }

    public static void swtMenuRun(ActionContext actionContext){
        Thing currentThing = actionContext.getObject("self");
        
        currentThing.doAction("run");
    }

}