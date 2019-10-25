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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

public class ThingCollectionActions {
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object run(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String dataNames = self.getStringBlankAsNull("things");
        String type = self.getStringBlankAsNull("type");
        if(dataNames == null){
            //如果没有指定要读取的事物，那么直接返回
            if("single".equals(type)){
                return null;
            }else{
                return Collections.EMPTY_LIST;
            }
        }
        
        //读取事物
        Object data = null;
        if("multi".equals(type)){
            data = new ArrayList<Object>();
        }
        String[] dns = dataNames.split("[,]");
        for(String name : dns){
            name = name.trim();
        
            if(true){//name.split("[:]").length >= 2){
                //读取事物
                Object obj = getData(name);
                if(obj == null) continue;
                
                if("single".equals(type)){
                    data = obj;
                    break;
                }else{
                    if(obj instanceof List)
                        ((List<Object>) data).addAll((List) obj);
                    else 
                        ((List<Object>) data).add(obj);
                }
            }/*else{
                //从动作上下文中读物事物
                Object obj = actionContext.get(name);
                if(obj == null) continue;
                
                if("single".equals(type)){
                    data = obj;
                    break;
                }else{
                	if(obj instanceof List)
                        ((List<Object>) data).addAll((List) obj);
                    else 
                        ((List<Object>) data).add(obj);
                }
            }*/
        }
        
        String dataName = self.getStringBlankAsNull("dataName");
        if(dataName != null){
            if(self.getBoolean("append") == false){
                actionContext.put(dataName, data);
            }else{
                if("single".equals(type)){
                    if(data != null) actionContext.put(dataName, data);
                }else{
                    Object ds = actionContext.get(dataName);
                    if(ds != null && ds instanceof List){
                        ((List) ds).addAll((List) data);
                    }else{
                        actionContext.put(dataName, data);
                    }
                }
            }
        }
        
        return data;
        
        
    }

    public static Object getData(String name){
    	World world = World.getInstance();
        String[] ns = name.split("[ ]");
        Object obj = world.get(ns[0]);
        //println obj;
        if(obj instanceof Thing){
            return obj;
        }else if(obj instanceof Category || obj instanceof ThingManager){
            //是包或者数据工厂（根包）
            String includeChild = "true";
            String structureName = null;
            String nameRegex = null;
            if(ns.length > 1){
                for(String n : ns){
                    String[] nss = n.split("[=]");
                    if(nss[0] == "d" && nss.length > 1){
                        includeChild = nss[1].trim();
                    }
                    if(nss[0] == "n" && nss.length > 1){
                        nameRegex = nss[1].trim();
                    }
                    if(nss[0] == "s" && nss.length > 1){
                        structureName = nss[1].trim();
                    }
                }
            }
            ThingManager factory = null;
            String packageName = "";
            if(obj instanceof ThingManager){
                factory = (ThingManager) obj;
                packageName = "";
            }else{
                factory = ((Category) obj).getThingManager();
                packageName = ((Category) obj).getName();
            }
            
            boolean inc = false;
            if(includeChild == "true") inc = true;
            
            Iterator<Thing> datas = factory.iterator(packageName, (String) structureName, inc);
            //List<Thing> datas = factory.getThings(packageName, (String) structureName, inc);
            if(nameRegex != null && nameRegex != ""){
                //println nameRegex;
                List<Thing> d = new ArrayList<Thing>();
                while(datas.hasNext()){
                	Thing data = datas.next();
                    String dname = data.getMetadata().getName();
    
                    if(dname.matches(nameRegex)){
                        d.add(data);
                    }
                }
                
                return d;
            }else{
                return datas;
            }
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public static void printObjects(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        List<Thing> datas = (List<Thing>) self.doAction("run");
        for(Thing d : datas){
            System.out.println("thingCollection: " + d.getMetadata().getPath());
        }
    }

    @SuppressWarnings("unchecked")
	public static void addThing(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        Thing thingForAdd = actionContext.getObject("thing");
        if(thingForAdd != null){
            //取已有的事物
            List<Thing> things = (List<Thing>) self.doAction("run");
            //logger.info(things);
            if(things instanceof Thing){        
                return;
            }else if(things instanceof List){        
                for(Thing t : (List<Thing>) things){
                    if(t.getMetadata().getPath().equals(thingForAdd.getMetadata().getPath())){
                        //已经存在，返回
                        return;
                    }
                }
                
                //事物不存，添加
                addThing(self, thingForAdd);
                self.save();
            }else if(things == null){
                //事物不存，添加
                addThing(self, thingForAdd);
                self.save();
            }
        }
    }

    public static void addThing(Thing t1, Thing t2){
        String thingsStr = t1.getStringBlankAsNull("things");
        if(thingsStr == null){
            thingsStr = t2.getMetadata().getPath();
        }else{
            thingsStr += ",\n" + t2.getMetadata().getPath();
        }
        t1.set("things", thingsStr);
    }
}