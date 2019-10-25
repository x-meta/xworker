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
package xworker.swt.xwidgets.prototypes;

import java.io.IOException;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;
import xworker.util.StringUtils;
import xworker.util.XWorkerUtils;

public class shellActions {
    @SuppressWarnings("unchecked")
	public static void treeSelection(ActionContext actionContext) throws IOException{
        Thing self = actionContext.getObject("self");
        Tree tree = actionContext.getObject("tree");
        Browser browser = actionContext.getObject("browser");
        World world = World.getInstance();
        ActionContainer demoSwt = actionContext.getObject("demoSwt");
        ActionContainer demoWeb = actionContext.getObject("demoWeb");
        ActionContainer demoThing = actionContext.getObject("demoThing");
        ActionContainer demoAutoDemo = actionContext.getObject("demoAutoDemo");
        StackLayout demoCompositeStackLayout = actionContext.getObject("demoCompositeStackLayout");
        Composite demoComposite = actionContext.getObject("demoComposite");
        
        //获取节点的事物
        Thing thing = (Thing) ((Map<String, Object>) tree.getSelection()[0].getData()).get("_source");
        String path = StringUtils.getString(thing, "path", actionContext);
        String thingName = thing.getThingName();
        
        Control control = null;
        if("HTML".equals(thingName)){
            String type = thing.getString("type");
            String url = null;
            if("description".equals(type)){
                url = XWorkerUtils.getThingDescUrl(thing);	
            }else if("webControl".equals(type)){
                url = XWorkerUtils.getWebControlUrl(world.getThing(path));
            }else{
                url = path;
            }
            
            browser.setUrl(url);
            control = browser;
        
        }else if("SWT".equals(thingName)){
            demoSwt.doAction("setComposite", actionContext,"composite", path);
            control = (Control) demoSwt.doAction("getControl", actionContext);    
        }else if("WebControl".equals(thingName)){
            demoWeb.doAction("setThing", actionContext, "thing", path);
            control = (Control) demoWeb.doAction("getControl", actionContext);
        }else if("Thing".equals(thingName)){
            demoThing.doAction("setThing", actionContext, "thing", path);
            control = (Control) demoThing.doAction("getControl", actionContext);
        }else if("AutoDemo".equals(thingName)){
            demoAutoDemo.doAction("setAutoDemo", actionContext, "autoDemo", path);
            control = (Control) demoAutoDemo.doAction("getControl", actionContext);
        }else{
            //默认显示节点的文档
            String url = null;
            url = XWorkerUtils.getThingDescUrl(thing);	
            browser.setUrl(url);
            control = browser;
        }
        
        if(control != null){
            demoCompositeStackLayout.topControl = control;
            demoComposite.layout();
        }
    }

}