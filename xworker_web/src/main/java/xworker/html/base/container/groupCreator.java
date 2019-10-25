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

public class groupCreator {
    public static Object toHtml(ActionContext actionContext) throws Throwable{
        Thing self = (Thing) actionContext.get("self");
        
        String layout = self.getString("layout");
        List<List<GridData>> rows = new ArrayList<List<GridData>>();
        String childHtmls = "";
        if(layout == "formLayout"){
            int cols = self.getInt("cols");
            rows = GridLayout.layout(self.getAllChilds(), cols);    
            
            for(List<GridData> row : rows){
                for(GridData col : row){
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
         
        return UtilTemplate.process(UtilMap.toMap(new Object[]{"group",self, "childHtmls",childHtmls, "rows",rows}), "/xworker/html/base/templates/group.ftl", "freemarker", "utf-8");
    }

}