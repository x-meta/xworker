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
package xworker.app.view.extjs.widgets.app;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.GeneratedObjectEntry;

import xworker.http.ResultView;
import xworker.util.XWorkerUtils;

public class TitleTreeAppActions {
    @SuppressWarnings("unchecked")
	public static void httpDo(ActionContext actionContext) throws Throwable{
        Thing self = actionContext.getObject("self");
        HttpServletResponse response = actionContext.getObject("response");
        
        //生成事物的缓存
        String key = "GeneratedThingEntry";
        GeneratedObjectEntry gThingEntry = (GeneratedObjectEntry) self.getData(key);
        if(gThingEntry == null){
            gThingEntry = new GeneratedObjectEntry(self, "toHttpView");
            self.setData(key, gThingEntry);
        }
        
        //生成界面事物
        Thing viewThing = ((List<Thing>) gThingEntry.getObject(actionContext)).get(0);
        viewThing.getMetadata().setPath(self.getMetadata().getPath() + "HttpView");
        
        //输出界面
        response.setContentType("text/html; charset=utf-8");
        //客户端不要有缓存
        response.setDateHeader("expires",-1);        
        response.setHeader("Cache-Control", "no-cache");        
        response.setHeader("Pragma", "no-cache"); 
        
        ResultView.processViewThing(viewThing, actionContext);
    }

    @SuppressWarnings("unchecked")
	public static void menu_viewViewModel(ActionContext actionContext){
		Thing self = actionContext.getObject("currentThing");
		String key = "GeneratedThingEntry";
		GeneratedObjectEntry gThingEntry = (GeneratedObjectEntry) self
				.getData(key);
		if (gThingEntry == null) {
			gThingEntry = new GeneratedObjectEntry(self, "toHttpView");
			self.setData(key, gThingEntry);
		}

		// 生成界面事物
		Thing viewThing = ((List<Thing>) gThingEntry.getObject(actionContext))
				.get(0);
		XWorkerUtils.ideOpenThing(viewThing);
    }
}