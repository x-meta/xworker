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
package xworker.html.extjs.tools;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
public class ImportExtjsFromDocCreator {
    public static void run(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing thread = world.getThing("xworker.lang.Thread");
        thread.doAction("start1", actionContext, UtilMap.toMap(new Object[]{"action",self, "method","import"}));
    }

    public static void import1(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        self.doAction("doIterator", actionContext, UtilMap.toMap(new Object[]{"listener", self}));
    }

    public static void listFile(ActionContext actionContext){
    	/*
        Thing self = (Thing) actionContext.get("self");
        
        //不处理目录
        Event event = (Event) actionContext.get("event");
        if(event.file.isDirectory()){
            return;
        }
        
        //文件和事物名
        def file = event.file;
        def fileName = file.name;
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        def thingName = fileName;
        def packageName = "web.html.extjs";
        if(thingName.lastIndexOf(".") != -1){
            packageName = packageName + "." + thingName.substring(0, thingName.lastIndexOf("."));
            thingName = thingName.substring(thingName.lastIndexOf(".") + 1, thingName.length());    
        }
        
        def thing = new Thing("xworker.lang.MetaDescriptor3");
        thing.set("name", thingName);
        thing.metadata.setThingManager(world.get("xworker:ui"));
        thing.metadata.setPath("xworker.html.extjs." + fileName);
        thing.metadata.setCategory(new FileCategory(packageName, world.get("xworker:ui")));
        
        //取文件内容
        def fileReader = new Thing("xworker.io.FileReader");
        fileReader.fileVarName = "file";
        def content = fileReader.doAction("getContentString", actionContext, ["file":event.file]);
        
        //初始化继承
        initExtend(thing, content);
        //读取配置
        read(thing, content);
        thing.save();
        
        event.doit = false;
        */
    }
    
  //解除一个事物的内容
    public static void  read(Thing  thing, String content){
    	/*
        def index = 0;
        while(true){
            index = content.indexOf("config-row", index);
            def endIndex = content.indexOf("</tr>", index);
            if(index == -1){
                break;
            }
            
            index = content.indexOf("sig", index);
            index = content.indexOf("<b><a href=", index);
            index = content.indexOf("\">", index + 10) + 2;
            def index1 = content.indexOf("</a>", index);
            //属性名
            def attributeName = content.substring(index, index1);
            index = content.indexOf(": ", index1) + 2;
            index1 = content.indexOf("<div ", index);
            //类型
            def type = content.substring(index, index1).trim();
            index = content.indexOf("mdesc", index1) + 7;
            index1 = content.indexOf("</div>", index);
            //默认值
            def defaultValue = "";
            def index2 = content.indexOf("(defaults to ", index1);
            if(index2 != -1 && index2 < endIndex){
                def index3 = content.indexOf(").", index2);
                if(index3 != -1){
                    defaultValue = content.substring(index2 + 13, index3).trim();
                }
            }
            println type;
            //描述
            def description = content.substring(index, index1).trim();
            index = content.indexOf("msource");
            if(content.indexOf("msource\"><a", index1) != -1 
                && content.indexOf("msource\"><a", index1) < content.indexOf("</td>", index)){
                index = content.indexOf("msource\"><a", index1) + 11;
                index = content.indexOf("\">", index) + 2;
                index1 = content.indexOf("</a>", index);
            }else{
                index = content.indexOf("msource", index1) + 9;
                index1 = content.indexOf("</td>", index);
            }
            def scope = content.substring(index, index1).trim();
            
            if(scope == thing.name){
                def isAttribute = true;
                def childDescriptor = "";
                if(type.indexOf("Array") != -1){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Array";
                }else if(type.indexOf("Object") != -1){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Object";
                }else if(type == "DataReader"){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Ext.data.DataReader";
                }else if(type.startsWith("Ext")){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs." + type;
                }else if(type.indexOf("Function") != -1){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Function";
                }else if(type.indexOf("Mixed") != -1){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Mixed";
                }else if(type == "TreeLoader"){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Ext.tree.TreeLoader";
                }else if(type.indexOf("Template") != -1){
                    isAttribute = false;
                    childDescriptor = "xwroker.ui.web.html.extjs.Ext.Template";
                }
                        
                if(isAttribute){
                    def attributeThing = new Thing("xworker.lang.MetaDescriptor3/@attribute");
                    attributeThing.set("name", attributeName);
                    attributeThing.set("type", type.toLowerCase());
                    attributeThing.set("description", description);
                    attributeThing.set("group", thing.name);
                    
                    if(attributeThing.type == "boolean"){
                        attributeThing.set("inputtype", "truefalse");
                    }
                    thing.addChild(attributeThing);
                }else{
                    def thildThing = new Thing("xworker.lang.MetaDescriptor3/@thing");
                    thildThing.set("name", attributeName);
                    thildThing.set("description", description);
                    thildThing.set("extends", "childDescriptor");
                    thing.addChild(thildThing);
                }
    
               // println "add attribute " + attributeName;
            }else{
                //println "not attribute " + attributeName + ",thingName=" + thing.name + ",scope=" + scope;
            }
        }
        */
    }
    
    public static void initExtend(Thing thing, String content){
    	/*
        def index = content.indexOf("inheritance res-block");
        def endIndex = content.indexOf("</div>", index);
        if(index == -1){
            thing.set("extends", "xworker.html.extjs.ExtThing");
            return;
        }	
        def extendStr = "";
        while(true){
            index = content.indexOf("ext:cls=", index) + 9;
            if(index > endIndex){
                break;
            }
            
            def index1 = content.indexOf("\">", index);
            extendStr = content.substring(index, index1);        
        }
        if(extendStr != ""){
            thing.set("extends", "xworker.html.extjs.ExtThing,xworker.html.extjs." + extendStr);
        }else{
            thing.set("extends", "xworker.html.extjs.ExtThing");
        }
        */
    }

}