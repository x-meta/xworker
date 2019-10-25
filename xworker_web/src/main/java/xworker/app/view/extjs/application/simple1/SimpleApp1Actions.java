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
package xworker.app.view.extjs.application.simple1;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.World;

public class SimpleApp1Actions {
	public static void doLogined(ActionContext actionContext) throws Throwable{
//        Thing self = actionContext.getObject("self");
        
        Action action = World.getInstance().getAction("xworker.app.view.extjs.widgets.app.TitleTreeApp/@actions/@httpDo");
        action.run(actionContext);
        /*
        self.doAction("httpDo", actionContext);
        
        World world = World.getInstance();
        HttpServletResponse response = actionContext.getObject("response");
        
        //生成extjs界面
        String viewThingPath = self.getMetadata().getPath() + "HttpView";
        Thing viewThing = ((List<Thing>) GeneratedObjectEntry.getObject(self, "toHttpView", actionContext)).get(0);
        /*
        if(viewThing == null || viewThing.getMetadata().getLastModified() < self.getMetadata().getLastModified()){
            //重新生成
        	GeneratedObjectEntry gThingEntry = new GeneratedObjectEntry(self, "toHttpView");
            viewThing = ((List<Thing>) gThingEntry.getObject(actionContext)).get(0);
            
            viewThing.saveAs(self.getMetadata().getCategory().getThingManager().getName(), viewThingPath);
        }*/
        
        //输出界面        
        /*
        response.setContentType("text/html; charset=utf-8");
        ResultView.processViewThing(viewThing, actionContext);
        */
    }

}