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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class OpenWindowsActionActions {
    public static Object run(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        Thing shellThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.openWins.DataObjectAttrOpenWindow/@thingShell");
        Object newShell = shellThing.doAction("create", actionContext,
            "descriptorPath", self.getString("thingPath"), "split", self.getString("split"));
        
        return newShell;
    }

}