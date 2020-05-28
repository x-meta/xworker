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
package xworker.swt.xworker.attributeEditor.openWins;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtDialog;
import xworker.util.XWorkerUtils;

public class SelectThingOpenWindowActions {
    public static void thingManagerButtonSelection(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Shell shell = actionContext.getObject("shell");
        World world = World.getInstance();
        
        ActionContext ac = new ActionContext();
        ac.put("parent", shell.getParent());
        
        Thing selector = world.getThing("xworker.ide.worldexplorer.swt.tools.ThingSelector/@shell");
        Shell sshell = (Shell) selector.doAction("create", ac);
        Object result = SwtDialog.open(sshell, ac);
        if(result != null){
            actionContext.getScope(0).put("result", result);
            shell.dispose();
        }
    }
    
    public static Object selectThing(ActionContext actionContext){
    	Shell shell = (Shell) XWorkerUtils.getIDEShell();
        World world = World.getInstance();
        
        ActionContext ac = new ActionContext();
        ac.put("parent", shell);
        
        Thing selector = world.getThing("xworker.ide.worldexplorer.swt.tools.ThingSelector/@shell");
        Shell sshell = (Shell) selector.doAction("create", ac);
        return SwtDialog.open(sshell, ac);
    }

}