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
package xworker.swt.widgets;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.DesignTools;
import xworker.util.XWorkerUtils;

public class ControlActions {
	//private static Logger logger = LoggerFactory.getLogger(ControlActions.class);
			
    public static void menu_viewimageinbrowser(ActionContext actionContext){
        Thing currentThing = actionContext.getObject("currentThing");
        
        String url = XWorkerUtils.getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.SwtImage";
        url = url + "&control=" + currentThing.getMetadata().getPath();
        
        XWorkerUtils.ideOpenUrl(url);        
    }
    
    public static Thing getShellThing(Thing thing){
        if(thing.isThingByName("Shell")){
            return thing;
        }
        
        Thing parent = thing.getParent();
        if(parent != null){
            return getShellThing(parent);
        }
        
        return null;
    }

    public static void menu_openInIde(ActionContext actionContext){
    	Thing currentThing = actionContext.getObject("currentThing");
        
        XWorkerUtils.ideOpenComposite(currentThing);
    }
    
    public static void menu_updateControl(ActionContext actionContext) {
    	Thing currentThing = actionContext.getObject("currentThing");
    	
    	DesignTools.updateAllControls(currentThing);
    }

}