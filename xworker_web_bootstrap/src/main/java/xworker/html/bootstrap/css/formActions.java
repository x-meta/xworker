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
package xworker.html.bootstrap.css;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.html.HtmlUtil;

public class formActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<form class=\"";
        String type = self.getString("type");
        if("inline".equals(type)){
            html = html + " form-inline";
        }else if("horizontal".equals(type)){
            html = html + " form-horizontal";
        }
        
        if(self.getBoolean("navbar-form")){
            html = html + " navbar-form";
        }
        String align = self.getStringBlankAsNull("navbar-align");
        if(align != null){
            html = html + " " + align;
        }
        html = html + "\"";
        
        html = html + HtmlUtil.getAttr(self, "action", "action");
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "autocomplete", "autocomplete");
        html = html + HtmlUtil.getAttr(self, "enctype", "enctype");
        html = html + HtmlUtil.getAttr(self, "novalidate", "novalidate");
        html = html + HtmlUtil.getAttr(self, "target", "target");
        html = html + ">\n";
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</form>";
        return html;
    }

    public static Object toHtml1(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"form-group";
        if(self.getStringBlankAsNull("validate") != null){
            html = html + " " + self.getString("validate");
        }
        if(self.getBoolean("has-feedback")){
            html = html + " has-feedback";
        }
        
        html = html + "\">\n";
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</div>";
        return html;
    }
    
    public static String showLabel(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
    	if(self.getBoolean("showLabel")){
    		String html = "<label";
	        if(self.getStringBlankAsNull("id") != null){
	            html = html + " for=\"" + self.getString("id") + "\"";
	        }
	        if(self.getBoolean("showLabel") == false){
	            html = html + " class=\"sr-only\"";
	        }
	        html = html + ">" + self.getMetadata().getLabel() + "</label>\n";
	        return html;
    	}else{
    		return "";
    	}
    }

    public static Object toHtml2(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        //--------------标签----------------
        String html = "<label";
        if(self.getStringBlankAsNull("id") != null){
            html = html + " for=\"" + self.getString("id") + "\"";
        }
        if(self.getBoolean("showLabel") == false){
            html = html + " class=\"sr-only\"";
        }
        html = html + ">" + self.getMetadata().getLabel() + "</label>\n";
        
        //--------------input----------------
        html = html + "<input type=\"" + self.getString("type") + "\" class=\"form-control";
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        html = html + "\"";
        
        html = html + HtmlUtil.getAttr(self, "id", "id");
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "placeholder", "placeholder");
        html = html + HtmlUtil.getAttr(self, "aria-label", "aria-label");
        html = html + HtmlUtil.getAttr(self, "aria-labelledby", "aria-labelledby");
        html = html + HtmlUtil.getAttr(self, "title", "title");
        html = html + HtmlUtil.getAttr(self, "value", "value");
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        if(self.getBoolean("readonly")){
            html = html + " readonly";
        }
        html = html + "/>";
        
        //---------------help-block---------------
        if(self.getStringBlankAsNull("help-block") != null){
            html = html + "\n<p class=\"help-block\">" + self.getString("help-block") + "</p>";
        }
        
        return html;
    }

    public static Object toHtml3(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "<label";
        if(self.getStringBlankAsNull("for") != null){
            html = html + " for=\"" + self.getString("for") + "\"";
        }
        
        html = html + " class=\"control-label";
        if(self.getBoolean("sr-only")){
            html = html + " sr-only";
        }
        
        html = html + getClass(self, "md");
        html = html + getClass(self, "xs");
        html = html + getClass(self, "sm");
        html = html + getClass(self, "sm-offset");
        html = html + getClass(self, "md-offset");
        html = html + getClass(self, "lg-offset");
        html = html + getClass(self, "md-push");
        html = html + getClass(self, "md-pull");
        html = html + "\">" + self.getMetadata().getLabel() + "</label>";
        
        return html;         
    }
    
    public static String getClass(Thing self, String attr){
        String v = self.getStringBlankAsNull(attr);
        if(v != null){
            return " col-" + attr + "-" + v;
        }else{
            return "";
        }
    }

    public static Object toHtml4(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"input-group\">\n";
        
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</div>";
        return html;
    }

    public static Object toHtml5(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        if(self.getStringBlankAsNull("text") ==null){
            String html = "";
            for(Thing child : self.getChilds()){
                Object code = child.doAction("toHtml", actionContext);
                if(code != null){
                    html = html + code;
                }
            }
            
            return "<div class=\"input-group-addon\">" + html + "</div>";
        }else{
            return "<div class=\"input-group-addon\">" + self.getString("text") + "</div>";
        }
    }

    public static Object toHtml6(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = "<div class=\"input-group-btn\">\n";
        for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
        html = html + "</div>";
        return html;
    }

    public static Object toHtml7(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        if(self.getStringBlankAsNull("icon") == null){
            return null;
        }else{
            return "<span class=\"glyphicon glyphicon-" + self.getString("icon") + " form-control-feedback\" aria-hidden=\"true\"></span>";
        }
    }

    public static Object toHtml8(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = showLabel(actionContext) + "<div class=\"checkbox";
        if(self.getBoolean("inline")){
            html = html + "-inline";
        }
        
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        
        if(self.getStringBlankAsNull("validate") != null){
            html = html + " " + self.getString("validate");
        }
        
        html = html + "\">\n";
        
        html = html + "  <label>\n";
        html = html + "    <input type=\"checkbox\"";
        
        html = html + HtmlUtil.getAttr(self, "value", "value");
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "id", "id");
        
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        if(self.getBoolean("checked")){
            html = html + " checked";
        }
        
        html = html + ">";
        if(self.getStringBlankAsNull("text") != null){
            html = html + self.getString("text");
        }
        
        html = html + "  </label>\n";
        html = html + "</div>";
        
        return html;
    }

    public static Object toHtml9(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = showLabel(actionContext) + "<div class=\"radio";
        if(self.getBoolean("inline")){
            html = html + "-inline";
        }
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        
        if(self.getStringBlankAsNull("validate") != null){
            html = html + " " + self.getString("validate");
        }
        html = html + "\">\n";
        
        html = html + "  <label>\n";
        html = html + "    <input type=\"radio\"";
        
        html = html + HtmlUtil.getAttr(self, "value", "value");
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "id", "id");
        
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        if(self.getBoolean("checked")){
            html = html + " checked";
        }
        
        html = html + ">";
        if(self.getStringBlankAsNull("text") != null){
            html = html + self.getString("text");
        }
        
        html = html + "  </label>\n";
        html = html + "</div>";
        
        return html;
    }

    public static Object toHtml10(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = showLabel(actionContext) + "<select class=\"form-control\"";
        
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "id", "id");
        
        if(self.getBoolean("multiple")){
           html = html + " multiple";
        }
        html = html + ">\n";
        if(self.getStringBlankAsNull("options") == null){
            for(Thing child : self.getChilds()){
                String code = (String) child.doAction("toHtml", actionContext);
                if(code != null){
                    html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
                }
            }
        }else{
            html = html + "  " + HtmlUtil.getIdentString(self.getString("options"), "  ") + "\n";
        }
        html = html + "</select>";
        return html;
    }

    public static Object toHtml11(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "<option";
        if(self.getStringBlankAsNull("value") != null){
            html = html + " value=\"" + self.getString("value") + "\"";
        }
        html = html + ">";
        
        if(self.getStringBlankAsNull("text") != null){
            html = html + self.getString("text");
        }
        html = html + "</option>";
        
        return html;
    }

    public static Object toHtml12(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = showLabel(actionContext) + "<textarea class=\"form-control\"";
        
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "rows", "rows");
        
        html = html + ">";
        
        if(self.getStringBlankAsNull("value") != null){
            html = html + self.getString("value");
        }
        
        html = html + "</textarea>";
        
        return html;
    }

    public static Object toHtml13(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        String html = showLabel(actionContext) +  "<input type=\"" + self.getString("type") + "\"";
        
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        
        html = html + HtmlUtil.getAttr(self, "_class", "class");
        html = html + HtmlUtil.getAttr(self, "id", "id");
        html = html + HtmlUtil.getAttr(self, "name", "name");
        html = html + HtmlUtil.getAttr(self, "placeholder", "placeholder");
        html = html + HtmlUtil.getAttr(self, "aria-label", "aria-label");
        html = html + HtmlUtil.getAttr(self, "aria-labelledby", "aria-labelledby");
        html = html + HtmlUtil.getAttr(self, "title", "title");
        html = html + HtmlUtil.getAttr(self, "value", "value");
        if(self.getBoolean("disabled")){
            html = html + " disabled";
        }
        if(self.getBoolean("readonly")){
            html = html + " readonly";
        }
        html = html + "/>";
        
        return html;
    }

    public static Object toHtml14(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = showLabel(actionContext) + "<p class=\"form-control-static\">";
        if(self.getStringBlankAsNull("value") != null){
            html = html + self.getString("value");
        }
        html = html + "</p>";
        
        return html;
    }

    public static Object fieldset(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
    	String html = "<fieldset";
    	if(self.getBoolean("disabled")){
    		html = html + " disabled";
    	}
    	html = html + ">\n";
    	
    	for(Thing child : self.getChilds()){
            String code = (String) child.doAction("toHtml", actionContext);
            if(code != null){
                html = html + "  " + HtmlUtil.getIdentString(code, "  ") + "\n";
            }
        }
    	
    	html = html + "</fieldset>";
    	return html;
    }
}