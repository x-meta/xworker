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
package xworker.app.view.swt.widgets;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class DataObjectEditWindowActions {
    public static void okButtonAction(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        Thing form = actionContext.getObject("form");
        Shell shell = actionContext.getObject("shell");
        
        Thing dataObject = world.getThing("xworker.app.test.dataObject.thing.Sex");
        form.doAction("setDataObject", actionContext, "dataObject", dataObject);
        shell.pack();
    }

    public static void cancelButtonSelection(ActionContext actionContext){
    	Shell shell = actionContext.getObject("shell");
        shell.dispose();
    }

}