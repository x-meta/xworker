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
package xworker.app.view.extjs.widgets.panel;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilThing;

public class QueryFormTableTabPanelActions {
    public static Object toJavaScript(ActionContext actionContext){
        Thing data = actionContext.getObject("data");
        List<Thing> thePanels = actionContext.getObject("thePanel");
        
        Thing configPanel = data.getThing("ConfigPanel@0");
        Thing thePanel = thePanels.get(0);
        if(configPanel != null){
            thePanel.getAttributes().putAll(configPanel.getAttributes());
            for(Thing child : configPanel.getChilds()){
                thePanel.addChild(child, false);
            }
        }
        
        return thePanel.doAction("toJavaScriptCode", actionContext);
    }

    public static Object isDescriptorForOpenWindow(ActionContext actionContext){
        String descriptor = actionContext.getObject("descriptor");
        
        if(UtilThing.isDescriptorEquals1("xworker.app.view.extjs.widgets.form.DataObjectFormPanel", descriptor)){
            return true;
        }else{
            return UtilThing.isDescriptorEquals1("xworker.app.view.extjs.widgets.grid.DataObjectGridPanel", descriptor);
        }
    }

    public static Object getThingForOpenWindow(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String descriptor = actionContext.getObject("descriptor");
        List<Thing> datas = new ArrayList<Thing>();
        
        if(UtilThing.isDescriptorEquals1("xworker.app.view.extjs.widgets.form.DataObjectFormPanel", descriptor)){
            Thing thing = new Thing("xworker.app.view.extjs.widgets.form.DataObjectFormPanel");
            thing.put("id", "'" + self.getString("panelId") + "Form'");
            datas.add(thing);
        }else if(UtilThing.isDescriptorEquals1("xworker.app.view.extjs.widgets.grid.DataObjectGridPanel", descriptor)){
            Thing thing = new Thing("xworker.app.view.extjs.widgets.grid.DataObjectGridPanel");
            thing.put("id", "'" + self.getString("panelId") + "Grid'");
            datas.add(thing);
        }
        
        return datas;
    }

}