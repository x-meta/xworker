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
package xworker.app.view.swt.app.tpls;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;

public class ApplicationTmplMainCompositeCreator {
    public static Object tabItemSelected(ActionContext actionContext){
        return null;
    }

    public static void tabFolderMouseDoubleClicked(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
        
        //如果是鼠标中键什么也不做
        if(event.button == 2) return;
        
        CTabFolder tabFolder = (CTabFolder) event.widget;
        if(tabFolder.getData("maxStatus") == "max"){
            restoreSashFormWeights(actionContext);
            tabFolder.setData("maxStatus", "nomax");
        }else{
            tabFolder.setData("maxStatus", "max");
            if(tabFolder == actionContext.get("leftTabFolder")){
                setSashFormWeights(new int[]{100,0}, new int[]{100,0,0}, actionContext);
            }else if(tabFolder == actionContext.get("contentTabFolder")){
                setSashFormWeights(new int[]{100,0}, new int[]{0,100,0}, actionContext);
            }else if(tabFolder == actionContext.get("rightTabFolder")){
                setSashFormWeights(new int[]{100,0}, new int[]{0,0,100}, actionContext);
            }else if(tabFolder == actionContext.get("bottomTabFolder")){
                setSashFormWeights(new int[]{0,100}, new int[]{0,0,100}, actionContext);                    
            }
        }
    }
    
    //还原sashform的权值
    public static void restoreSashFormWeights(ActionContext actionContext){
    	SashForm mainSashForm = (SashForm) actionContext.get("mainSashForm");
        if(mainSashForm != null){
            mainSashForm.setWeights((int[]) mainSashForm.getData("oldWeights"));
        }
        
        SashForm topSashForm = (SashForm) actionContext.get("topSashForm");
        if(topSashForm != null){
            topSashForm.setWeights((int[]) topSashForm.getData("oldWeights"));
        }
    }

    //设置sashForm的权值
    public static void setSashFormWeights(int[] mainSashWeights, int[] topSashWeights, ActionContext actionContext){
        SashForm mainSashForm = (SashForm) actionContext.get("mainSashForm");
        if(mainSashForm != null){
            mainSashForm.setData("oldWeights", mainSashForm.getWeights());
            mainSashForm.setWeights(mainSashWeights);
        }
        
        SashForm topSashForm = (SashForm) actionContext.get("topSashForm");
        if(topSashForm != null){
            topSashForm.setData("oldWeights", topSashForm.getWeights());
            topSashForm.setWeights(topSashWeights);
        }
    }

}