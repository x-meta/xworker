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
package xworker.html.bootstrap.xworker;

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilThing;

import xworker.dataObject.DataObjectThingUtils;
import xworker.lang.executor.Executor;

public class XWorkerFormActions {
	private static final String TAG = XWorkerFormActions.class.getName();
	
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        //获取数据对象
        Thing dataObject =  UtilThing.getThingFromAttributeOrChilds(self, "dataObject", "dataObjects@0");
        if(dataObject == null){
            Executor.warn(TAG, "XWroekrForm: dataObject is null, path=" + self.getMetadata().getPath());
            return null;
        }
        
        //获取数据表格
        String html = "";
        List<Map<String, Object>> attributes = DataObjectThingUtils.getEditorAttributes(dataObject, self.getString("type"));
        for(Map<String, Object> attr : attributes){
            //界面的定义
            Thing cfg = (Thing) attr.get("viewConfig");
            //原始属性的定义
            Thing atr = (Thing) attr.get("attribute");
            String name = atr.getMetadata().getName();
            String value = "${" + name + "?if_exists}";
            
            String inputType = cfg.getString("inputtype");
            if("hidden".equals(inputType)){
                html = html + "<input type=\"hidden\" name=\"" + name + "\" value=\"$" + "{" + name + "?if_exists}\"/>\n";
            }else if("textarea".equals(inputType) || "html".equals(inputType)){
                html = html + self.doAction("createTextarea", actionContext, "attribute", atr, "value", value);
            }else if("label".equals(inputType)){
                html = html + self.doAction("createLabel", actionContext, "attribute", atr, "value", value);
            }else if("select".equals(inputType) || "inputSelect".equals(inputType) || "popCombo".equals(inputType)){
                html = html + self.doAction("createSelect", actionContext, "attribute", atr, "value", value);
            }else if("multSelect".equals(inputType)){
                html = html + self.doAction("createMultSelect", actionContext, "attribute", atr, "value", value);
            }else{        
            	Thing formGroup = new Thing("xworker.html.bootstrap.css.form/@form-group");
            	Thing input = new Thing("xworker.html.bootstrap.css.form/@form-group/@input");
                input.set("name", atr.getMetadata().getName());   
                input.set("label", atr.getMetadata().getLabel());
                input.set("showLabel", cfg.getString("showLabel"));
                input.set("type", getRealInputType(inputType));
                input.set("value", value);
                
                html = html + input.doAction("toHtml", actionContext);
            }
        }
        
        return html;
        
       
    }
    
    public static String getRealInputType(String type){
        if("text".equals(type)){
                 return "text";
        }else if("truefalse".equals(type) || "truefalseselect".equals(type)){
                 return "checkbox";
        }else if("checkBox".equals(type)){
                return "checkbox";	         
        }else if("radio".equals(type)){
                return "radio";
        }else if("html".equals(type) || "htmlDesc".equals(type)){
        	return "text";        	
        }else if("hidden".equals(type)){
        	return "hidden";
        }else if("password".equals(type)){
                 return "password";
        }else if("datePick".equals(type)){
                 return "date";
        }else if("dateTime".equals(type)){
                return "datetime";
        }else if("time".equals(type)){
                return "time";
        }else if("openWindow".equals(type)){
                 return "text";         
        }else if("textButton".equals(type)){
                 return "text";         
        }else if("dataSelector".equals(type)){
                 return "text";         
        }else if("pathSelector".equals(type)){
                 return "text";         
        }else if("codeEditor".equals(type)){
                 return "text";         
        }else if("fontSelect".equals(type)){
                 return "text";         
        }else if("colorpicker".equals(type)){
                return "color";
        }else if("imageSelector".equals(type)){
                 return "text";         
        }else if("file".equals(type) || "filePath".equals(type)){
                return "file";
        }else if("selfDefined".equals(type)){
                 return "text";         
        }else if("inputAttrDefined".equals(type)){
                 return "text";         
        }else if("datatable".equals(type)){
                 return "text";
        }else {
                 return "text";   
       }               
   }

    public static Object createTextarea(ActionContext actionContext){
        Thing attribute = actionContext.getObject("attribute");
        
        String name = attribute.getMetadata().getName();
        String label = attribute.getMetadata().getLabel();
        String placeholder = attribute.getString("placeholder");
        String value = actionContext.getObject("value");
        
        String html = "<div class=\"form-group\">\r\n";
        html = html + "<label for=\"" + name + "\">" + label + "</label>\r\n";
        html = html + "<textarea class=\"form-control\" rows=\"3\" id=\"" + name + "\" name=\"" + name + "\" placeholder=\"" + placeholder + "\">" + value + "</textarea>\r\n";
        html = html + "</div>";
        return html;
    }

    public static Object createLabel(ActionContext actionContext){
    	Thing attribute = actionContext.getObject("attribute");
        String name = attribute.getMetadata().getName();
        String label = attribute.getMetadata().getLabel();
        String placeholder = attribute.getString("placeholder");
        String value = actionContext.getObject("value");
        
        String html = "<div class=\"form-group\">\r\n";
        html = html +"  <label for=\"" + name + "\">" + label + "</label>\r\n";
        html = html + "<p class=\"form-control-static\">" + value + "</p>\r\n";
        html = html + "</div>";
        return html;
    }

    public static Object createSelect(ActionContext actionContext){
    	Thing attribute = actionContext.getObject("attribute");
    	String name = attribute.getMetadata().getName();
    	String label = attribute.getMetadata().getLabel();
    	String placeholder = attribute.getString("placeholder");
    	String value = actionContext.getObject("value");
        
        String html = "<div class=\"form-group\">\r\n";
        html = html + "  <label for=\"" + name + "\">" + label + "</label>\r\n";
        html = html + "  <select class=\"form-control\" id=\"" + name + "\" name=\"" + name + "\" placeholder=\"" + placeholder + "\">\r\n";
        
        for(Thing v : attribute.getChilds("value")){  
            html = html + "\n  <option>" + v.getString("value") + "</option>";
        }  
        
        html = html + "</select>\r\n";
        html = html + "</div>";
        
        return html;
    }

    public static Object createMultSelect(ActionContext actionContext){
    	Thing attribute = actionContext.getObject("attribute");
    	String name = attribute.getMetadata().getName();
    	String label = attribute.getMetadata().getLabel();
    	String placeholder = attribute.getString("placeholder");
    	//String value = actionContext.getObject("value");
        
        String html = "<div class=\"form-group\">\r\n";
        html = html + "  <label for=\"" + name + "\">" + label + "</label>\r\n";
        html = html + "  <select multiple class=\"form-control\" id=\"" + name + "\" name=\"" + name + "\" placeholder=\"" + placeholder + "\">\r\n";
        
        for(Thing v : attribute.getChilds("value")){  
            html = html + "\n  <option>" + v.getString("value") + "</option>";
        }  
        
        html = html + "</select></div>";
        
        return html;
    }

}