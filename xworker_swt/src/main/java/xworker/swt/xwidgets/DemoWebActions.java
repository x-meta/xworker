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
package xworker.swt.xwidgets;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.util.XWorkerUtils;

public class DemoWebActions {
	private static final String TAG = DemoWebActions.class.getName();
	
    public static void changed(ActionContext actionContext) throws OgnlException{
    	//Thing thing = actionContext.getObject("thing");
        Text urlText = actionContext.getObject("urlText");
        Object event = actionContext.getObject("event");
        
        //Thing self = actionContext.getObject("self");
        urlText.setText((String) OgnlUtil.getValue("location", event));
    }

    public static void updateButtonSelection(ActionContext actionContext){
        Thing thing = actionContext.getObject("thing");
        ActionContext thingEditor = actionContext.getObject("thingEditor");
        ActionContainer editorActions = thingEditor.getObject("editorActions");
        Browser browser = actionContext.getObject("browser");
        TabItem demoTabItem = actionContext.getObject("demoTabItem");
        TabFolder demoTabFolder = actionContext.getObject("demoTabFolder");
        
        //先保存事物
        editorActions.doAction("save");
        
        //显示web
        String url = XWorkerUtils.getWebControlUrl(thing);
        browser.setUrl(url);
        
        demoTabItem.setText(thing.getMetadata().getLabel());
        demoTabFolder.setSelection(0);
    }

    public static void setThing(ActionContext actionContext){
    	Object thing = actionContext.getObject("thing");
        ActionContext thingEditor = actionContext.getObject("thingEditor");
        ActionContainer editorActions = thingEditor.getObject("editorActions");
        Browser browser = actionContext.getObject("browser");
        
        if(thing instanceof String){
            thing = World.getInstance().getThing((String) thing);
        }
        
        if(thing == null){
            Executor.warn(TAG, "DemoWeb: thing is null");
            return;
        }
        
        //显示web
        String url = XWorkerUtils.getWebControlUrl((Thing) thing);
        browser.setUrl(url);
        
        //在编辑器中编辑事物
        editorActions.doAction("setThing", actionContext, "thing", thing);
        
        actionContext.getScope(0).put("thing", thing);
    }

}