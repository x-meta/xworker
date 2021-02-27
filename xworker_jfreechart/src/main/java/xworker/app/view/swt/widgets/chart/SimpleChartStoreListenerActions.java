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

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class SimpleChartStoreListenerActions {
	private static final String TAG = SimpleChartStoreListenerActions.class.getName();
	
    public static Object onReconfig(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        return null;
        //self.doAction("onLoaded", actionContext);
    }

    public static void onLoaded(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        Executor.info(TAG, "init datas.");
        Thing chartThing = (Thing) self.get("chartThing");
        ChartComposite chartComposite = (ChartComposite) self.get("chart");
        Thing store = (Thing) self.get("store");
        Thing dataObject = (Thing) store.get("dataObject");
        
        //图标类型
        String type = chartThing.getStringBlankAsNull("type");
        if(type == null || type == ""){
            type = "line";
        }
        //图表的数据的变量
        String valueField = chartThing.getString("valueField");
        String categoryField = chartThing.getString("categoryField");
        String domainField = chartThing.getString("domainField");
        String valueTitle = "";
        String domainTitle = "";
        Executor.info(TAG, "valueField=" + valueField + ", categoryField=" + categoryField + ",domainField=" + domainField);
        boolean ok = false;
        for(Thing attr : dataObject.getChilds("attribute")){
            if(attr.getMetadata().getName().equals(valueField)){
                valueTitle = attr.getMetadata().getLabel();
                ok = true;
                break;
            }
        }
        if(!ok){
            for(Thing attr : dataObject.getChilds("attribute")){
                if(isValueType(attr.getString("type"))){
                    valueTitle = attr.getMetadata().getLabel();
                    valueField = attr.getString("name");
                }
            }
        }
        ok = false;
        for(Thing attr : dataObject.getChilds("attribute")){
            if(attr.getString("name").equals( categoryField) || categoryField.equals(attr.getString("displayField") == categoryField)){   
                if(attr.getStringBlankAsNull("displayField") != null){
                    categoryField = attr.getString("displayField");
                }     
                ok = true;
                break;
            }
        }
        Executor.info(TAG, "0valueField=" + valueField + ", categoryField=" + categoryField + ",domainField=" + domainField);
        if(!ok){
            for(Thing attr : dataObject.getChilds("attribute")){
                if(attr.getString("name") != valueField){
                    if(attr.getStringBlankAsNull("displayField") != null){
                        categoryField = attr.getString("displayField");
                    }else{
                        categoryField = attr.getString("name");
                    }
                }
            }
        }
        ok = false;
        for(Thing attr : dataObject.getChilds("attribute")){
            if(attr.getString("name") == domainField || attr.getString("displayField") == domainField){
                if(attr.getStringBlankAsNull("displayField") != null){
                    domainField = attr.getString("displayField");
                }  
                domainTitle = attr.getMetadata().getLabel();
                ok = true;
                break;
            }
        }
        if(!ok){
            for(Thing attr : dataObject.getChilds("attribute")){
                if(attr.getString("name") != valueField){
                    if(attr.getStringBlankAsNull("displayField") != null){
                        domainField = attr.getString("displayField");
                    }else{
                        domainField = attr.getString("name");
                    }  
                    domainTitle = attr.getMetadata().getLabel();
                }
            }
        }
        
        //图表数据
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(DataObject record : (List<DataObject>) store.get("records")){
            Object value = null;
            String category = "";
            String domain = "";
            if(valueField != null && !"".equals(valueField)){
                value = OgnlUtil.getValue(valueField, record);            
            }
            if(categoryField != null && !"".equals(categoryField)){
                category = (String) OgnlUtil.getValue(categoryField, record);
            }
            if(categoryField.equals(domainField)){
                domain = category;
                category = "";            
            }else if(domainField != null && !"".equals(domainField)){
                domain = (String) OgnlUtil.getValue(domainField, record);
            }
            if(domain == null && category != null){
                domain = category;
                category = "";
            }
            if(value != null){
                //Executor.info(TAG, "add datas, value=" + value + ", category=" + category + ", domain=" + domain);
                dataset.addValue((Number) value, category, domain);
            }
        }
        
        Executor.info(TAG, "domainTitle=" + domainTitle + ",valueTitle=" + valueTitle);
        //创建图表
        if("line".equals(type)){
                JFreeChart chart = ChartFactory.createLineChart(chartThing.getString("title"), 
        					"domainTitle", 
        					"valueTitle", 
        					dataset, 
        					PlotOrientation.VERTICAL,
        					chartThing.getBoolean("legend"), 
        					chartThing.getBoolean("tooltips"), 
        					chartThing.getBoolean("urls"));
        	    chartComposite.setChart(chart);	   
                //chart.getTitle().setFont(new Font("黑体", Font.BOLD, 16));
        		//chart.getLegend().setItemFont(new Font("黑体", Font.PLAIN, 12));
        		//CategoryPlot plot = chart.getCategoryPlot();
        		
        		Executor.info(TAG, "set chart property");
        		//	   customise the range axis...    
        		//用于处理图表的两个轴：纵轴和横轴
        		//NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        		//rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        		//rangeAxis.setAutoRangeIncludesZero(true);
        		//rangeAxis.setUpperMargin(0.20);
        		//rangeAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        		//rangeAxis.setLabelAngle(Math.PI / 2.0);				
        		
        		//CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
        		//domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));			
        		//domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        		//domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));
        		
        		//负责如何显示一个图表对象
        		//LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();		 
        		//renderer.setBaseShape(new Rectangle2D.Double(-1.5, -1.5, 3, 3)); //设置点    
        		//renderer.setBaseShapesVisible(true);//是否显示图形
        		//renderer.setSeriesPaint(3, Color.red);//设置图片颜色
        		//renderer.setSeriesLinesVisible(3, false);
        		//renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());//显示折点数据    
        		//renderer.setBaseItemLabelsVisible(true);
        		//renderer.setBaseItemLabelFont(new Font("宋体", Font.PLAIN, 12)); 
        	    chart.setNotify(true);
        }else if("bar".equals(type)){
        	    JFreeChart chart = ChartFactory.createBarChart(chartThing.getString("title"), 
        					domainTitle, 
        					valueTitle, 
        					dataset, 
        					PlotOrientation.VERTICAL,
        					chartThing.getBoolean("legend"), 
        					chartThing.getBoolean("tooltips"), 
        					chartThing.getBoolean("urls"));		
        	    chartComposite.setChart(chart);
        	    chart.setNotify(true);
        }else if("pie".equals(type)){
        	/*
        	    JFreeChart chart = ChartFactory.(chartThing.getString("title"), dataset,
                            chartThing.getBoolean("legend"), 
        					chartThing.getBoolean("tooltips"), 
        					chartThing.getBoolean("urls"));
        	    chartComposite.setChart(chart);
        	    chart.setNotify(true);
        	    break;*/
        }    
        
        chartComposite.redraw();
        return;
        
        
    }
    
    public static boolean  isValueType(String type){
        type = type.toLowerCase();
        if("byte".equals(type) || "short".equals(type) || "int".equals(type) || "float".equals(type) || 
                "long".equals(type) || "double".equals(type) || "float".equals(type)){
            return true;
        }
        
        return false;
    }

    public static void onDataChanged(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        self.doAction("onLoaded", actionContext);
    }

    public static Object beforeLoad(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        return null;
    }

    public static void onInsert(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        self.doAction("onLoaded", actionContext);
    }

    public static void onUpdate(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        self.doAction("onLoaded", actionContext);
    }

    public static void onRemove(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        self.doAction("onLoaded", actionContext);
    }

}