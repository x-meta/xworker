/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.html.base.container;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.html.GridData;
import xworker.html.GridLayout;
import xworker.util.UtilTemplate;

public class FormLayoutCreator {
    public static Object toHtml(ActionContext actionContext) throws Throwable{
        Thing self = (Thing) actionContext.get("self");
        
        String layout = self.getString("layout");
        List<List<GridData>> rows = new ArrayList<List<GridData>>();
        String childHtmls = "";
        int cols = self.getInt("cols", 2);
        if("formLayout".equals(layout)){    
            rows = GridLayout.layout(self.getAllChilds(), cols);    
            
            for(List<GridData> row : rows){
                for(GridData col : row){
                    col.set("class", ((Thing) col.userData).getString("class"));
                    if(col.get("class") == null || col.get("class") == ""){
                        col.set("class", self.getString("cellClass"));
                    }
                    col.set("style", ((Thing) col.userData).getString("style"));
                    
                    col.set("labelClass", ((Thing) col.userData).getString("labelClass"));
                    if(col.get("labelClass") == null || col.get("labelClass") == ""){
                        col.set("labelClass", self.getString("cellLabelClass"));
                    }
                    col.set("labelStyle", ((Thing) col.userData).getString("labelStyle"));
                    
                    col.set("labelHAlign", ((Thing) col.userData).getString("labelHAlign"));
                    if(col.get("labelHAlign") == null || col.get("labelHAlign") == ""){
                        col.set("labelHAlign", self.getString("cellLabelHAlign"));
                    }
                    col.set("labelVAlign", ((Thing) col.userData).getString("labelVAlign"));
                    if(col.get("labelVAlign") == null || col.get("labelVAlign") == ""){
                        col.set("labelVAlign", self.getString("cellLabelVAlign"));
                    }
                    //log.info(col.get("labelVAlign"));
                    col.set("hAlign", ((Thing) col.userData).getString("hAlign"));
                    if(col.get("hAlign") == null || col.get("hAlign") == ""){
                        col.set("hAlign", self.getString("cellHAlign"));
                    }
                    col.set("vAlign", ((Thing) col.userData).getString("vAlign"));
                    if(col.get("vAlign") == null || col.get("vAlign") == ""){
                        col.set("vAlign", self.getString("cellVAlign"));
                    }
                    col.set("style", ((Thing) col.userData).getString("style"));
                    col.set("labelStyle", ((Thing) col.userData).getString("labelStyle"));
                    
                    String colHtml = (String) ((Thing) col.userData).doAction("toHtml", actionContext);
                    col.set("html", colHtml);
                }
            }
        }else{
            for(Thing child : self.getChilds()){
                String html = (String) child.doAction("toHtml", actionContext);
                if(html != null){
                    childHtmls = childHtmls + html + "\n";
                }
            }
        }
         
        return UtilTemplate.process(UtilMap.toMap(new Object[]{"group",self, "childHtmls",childHtmls, "rows",rows, "cols",cols}), "/xworker/html/base/templates/FormLayout.ftl", "freemarker", "utf-8");
    }

}