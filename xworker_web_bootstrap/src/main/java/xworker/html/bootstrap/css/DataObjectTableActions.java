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
import org.xmeta.World;

public class DataObjectTableActions {
    public static Object toHtml(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String html = "";
        if(self.getBoolean("responsive")){
            html = "<div class=\"table-responsive\">\n";
        }
        html = html + "<table class=\"table";
        html = html + getClass(self, "striped");
        html = html + getClass(self, "bordered");
        html = html + getClass(self, "hover");
        html = html + getClass(self, "condensed");
        //html = html + getClass(self, "responsive");
        html = html + "\">\n";
        html = html + "  <thead>\n";
        html = html + "    <tr>\n";
        
        //--------------头---------------
        if(self.getBoolean("line")){
            html= html + "      <th>#</th>\n";    
        }
        Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
        if(dataObject == null){
            html = html + "      <td>dataObject not exists, path=" + self.getMetadata().getPath() + "</td>\n";
            html = html + "    </tr>\n";
            html = html + "  </thead>\n";
            html = html + "</table>";
            if(self.getBoolean("responsive")){
                html = html + "\n</div>";
            }
            return html;
        }else{
            for(Thing attr : dataObject.getChilds("attribute")){
                if(attr.getBoolean("showInTable")){
                    html = html + "      <th";
                    if(attr.getStringBlankAsNull("gridWidth") != null){
                        html = html + " width=\"" + attr.getString("gridWidth") + "\"";
                    }
                    html = html + ">" + attr.getMetadata().getLabel() + "</th>\n";
                }
            }
            html = html + "    </tr>\n";
            html = html + "  </thead>\n";
            
            //--------------内容体-------------------
            html = html + "  <tbody>\n";
            html = html + "<#list " + self.getString("datas" ) + " as data>\n";
            html = html + "    <tr>\n";
            if(self.getBoolean("line")){
                html= html + "      <th>${data_index + 1}</th>\n";    
            }
            for(Thing attr : dataObject.getChilds("attribute")){
                if(attr.getBoolean("showInTable")){
                    html = html + "      <td>${(data." + attr.getString("name") + ")?if_exists}</td>\n";
                }
            }
            html = html + "    </tr>\n";
            html = html +"</#list>\n";
            html = html + "  </tbody>\n";
            html = html + "</table>";
            if(self.getBoolean("responsive")){
                html = html + "\n</div>";
            }
            return html;
        }
        
        
    }
    
    public static String getClass(Thing self, String attr){    
        if(self.getBoolean(attr)){
            return " table-" + attr;
        }else{
            return "";
        }
    }

    public static Object getDataObject(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        String path = self.getStringBlankAsNull("dataObject");
        if(path != null){
            return world.getThing(path);
        }else{
            Thing objs = self.getThing("DataObjects@0");
            if(objs != null && objs.getChilds().size() > 0){
                return objs.getChilds().get(0);
            }else{
                return null;
            }
        }
    }

}