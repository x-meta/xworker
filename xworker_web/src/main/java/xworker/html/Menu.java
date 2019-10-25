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
package xworker.html;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;

public class Menu {
    String action;
    String name;
    String url;
    String target;
    String description;
    String image;
    String label;
    List<Menu> subMenus;

    String split = "false";

    public static Menu createSplit(){
        Menu menu = new Menu();
        menu.setSplit(true);
        return menu;
    }

    public Menu(){

    }
    
    public Menu(String action, String name, String url, String target, String description) {
        this.action = action;
        this.name = name;
        this.url = url;
        this.target = target;
        this.description = description;
    }
    
    public Menu(Thing view){
    	action = gs(view.getString("action"));
        target = gs(view.getString("target"));
        description = gs(view.getString("description"));
        url = gs(view.getString("url"));    
        label = gs(view.getString("label"));
        name = gs(view.getString("name"));
        image = gs(view.getString("image"));
        
        if("true".equals(view.getString("isSplit"))){
            //view
            setSplit(true);
        }
        
        for(Thing child : view.getAllChilds()){
        	Menu subMenu = new Menu(child);
        	this.addSubMenu(subMenu);
        }
    }

    public String getIsSplit(){
    	return split;
    }
    
    public void setSplit(boolean split) {
        this.split = "" + split;
    }

    @SuppressWarnings("unchecked")
	public List getChilds() {
        return subMenus;
    }

    public void setSubMenus(List<Menu> subMenus) {
        this.subMenus = subMenus;
    }

    public void addSubMenu(Menu menu){
        if(subMenus == null){
            subMenus = new ArrayList<Menu>();
        }

        subMenus.add(menu);
    }

    public String getAction() {
        if(action == null){
            return "null";
        }else{
            return action;
        }
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        if(name == null){
            return "null";
        }else{
            return "'" + name + "'";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        if(url == null){
            return "null";
        }else{
            return "'" + url + "'";
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        if(target == null){
            return "null";
        }else{
            return "'" + target + "'";
        }
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDescription() {
        if(description == null){
            return "null";
        }else{
            return "'" + description + "'";
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }    

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	private static String gs(String str){
        if(str == null || "".equals(str)){
            return null;
        }else{
            return str;
        }
    }

	public String getLabel() {
		if(name == null){
            return "null";
        }else{
            return "'" + label + "'";
        }
	}

	public void setLabel(String label) {
		this.label = label;
	}
}