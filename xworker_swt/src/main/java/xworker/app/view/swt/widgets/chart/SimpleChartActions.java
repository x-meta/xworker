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
package xworker.app.view.swt.widgets.chart;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.design.Designer;

public class SimpleChartActions {
    public static Object create(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        
        //先创建ChartComposite
        Action chartCompositeAction = world.getAction("xworker.jfreechart.swt.ChartComposite/@actions/@create");
        Composite chartComposite = (Composite) chartCompositeAction.run(actionContext);
        
        Designer.attach(chartComposite, self.getMetadata().getPath(), actionContext);
        
        //创建图表事物
        Thing thing = new Thing(self.getMetadata().getPath());
        thing.getAttributes().putAll(self.getAttributes());
        thing.put("control", chartComposite);
        thing.put("chart", chartComposite);
        
        //绑定到数据仓库
        Thing store = (Thing) OgnlUtil.getValue(self.getString("store"), actionContext);
        if(store != null){
        	Thing listener = new Thing("xworker.app.view.swt.widgets.chart.SimpleChartStoreListener");
            listener.put("chartThing", thing);
            listener.put("chart", chartComposite);
            listener.put("store", store);
        
            //先调用监听初始化
            listener.doAction("onReconfig", actionContext, "store", store);
            store.doAction("addListener", actionContext, "listener", listener);
        }
        
        //保存变量
        actionContext.getScope(0).put(self.getString("name"), thing);
        
        return chartComposite;
    }

    public static void createChart(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //获取数据仓库
        Thing store = (Thing) OgnlUtil.getValue(self.getString("store"), actionContext);
    }

    public static void setDataStore(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing store = actionContext.getObject("store");
        
        if(actionContext.get("store") != null){
        	Thing listener = new Thing("xworker.app.view.swt.widgets.chart.SimpleDSChartStoreListener");
            listener.put("chartThing", self);
            listener.put("store", store);
        
            //先调用监听初始化
            listener.doAction("onReconfig", actionContext, "store", store);
            store.doAction("addListener", actionContext, "listener", listener);
        }
    }

}