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
package xworker.app.model.tree.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.app.model.tree.TreeModelUtil;
import xworker.dataObject.DataObject;
import xworker.util.UtilFileIcon;

public class CLassViewModelCreator {
    @SuppressWarnings("unchecked")
	public static Object getRoot(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(self.getBoolean("flat")){
            return self.doAction("getFlatPackages", actionContext);
        }else{
            return ((Map<String, Object>) self.doAction("getVerticalPackages", actionContext)).get("0");
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        if(self.getBoolean("flat")){
            return ((Map<String, Object>) self.doAction("getFlatPackages", actionContext)).get("childs");
        }else{
            return ((Map<String, Object>) self.doAction("getVerticalPackages", actionContext)).get((String) actionContext.get("id"));
        }
    }

    public static Object createFileNode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	File file = (File) actionContext.get("file");
        String icon = "";
        String fileName = file.getName();
        String id = file.getAbsolutePath();
        if(file.isDirectory()){
            icon = "xworker:core/images/swteditor/package.gif";
        }else{
            icon = null;
            int index = fileName.lastIndexOf(".");
            if(index != -1){
                String extension = fileName.substring(index + 1, fileName.length());
                icon = UtilFileIcon.getFileIcon(extension, false);
                /*
                icon = "_FileIcon_" + extension;
                if(ResourceManager.getResource(icon) == null){
                    Program program = Program.findProgram(extension);
                    if(program != null) {
                        Image image = new Image(null, program.getImageData());
                        icon = "_FileIcon_" + extension;
                        ResourceManager.putResource(icon, image, actionContext);
                    }else{
                        icon = null;
                    }
                }*/
            }
            
            if(icon == null){
                icon = "xworker:core/images/swteditor/normalFile.gif";
            }
        }
        
        Map<String, Object> node = new HashMap<String, Object>();
        TreeModelUtil.setAttributes(self, id, node);
        node.put("text", fileName);
        node.put("icon", icon);
        if(file.isDirectory()){
            node.put("leaf", "false");
        }else{
            node.put("leaf", "true");
        }
        
        return node;
    }

    @SuppressWarnings("unchecked")
	public static Object getVerticalPackages(ActionContext actionContext){    	
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
        Map<String, Object> packages = (Map<String, Object>) world.getData("xworker.app.model.tree.impl.JavaPackageTreeModel-Vertical");
        //packages = null;
        if(packages != null){
            return packages;
        }else{
            Thing packageDataObject = world.getThing("xworker.dataObject.java.PackageDataObject");
            List<DataObject> pkgs = (List<DataObject>) packageDataObject.doAction("getPackages", actionContext);
            packages = new HashMap<String, Object>();
            for(DataObject pkg : pkgs){
                createPackageNode(self, pkg, packages);
            }
            
            for(String key : packages.keySet()){
                Map<String, Object> pk = (Map<String, Object>) packages.get(key);
                if(((List<Object>) pk.get("childs")).size() == 0){
                    pk.put("leaf", "true");
                }
            }
            world.setData("xworker.app.model.tree.impl.JavaPackageTreeModel-Vertical", packages);
        }
        
        return packages;
        
       
    }
    
    //创建包
    @SuppressWarnings("unchecked")
	public static void createPackageNode(Thing self, DataObject packageName, Map<String, Object> context){
        String[] pkds = ((String) packageName.get("name")).split("[.]");
        //println packageName;
        String currentPackage = "";
        for(int i=0; i<pkds.length; i++){
            Map<String, Object> parentNode = null;
            if("".equals(currentPackage)){
                parentNode = (Map<String, Object>) context.get("0");
            }else{
                parentNode = (Map<String, Object>) context.get(currentPackage);
            }
            if(parentNode == null){
                //根节点
                if("".equals(currentPackage)){
                	parentNode = new HashMap<String, Object>();
                	TreeModelUtil.setAttributes(self, "0", parentNode);
                	parentNode.put("text", "");
                	parentNode.put("leaf", "false");
                	parentNode.put("childs", new ArrayList<Object>());
                    
                    context.put("0", parentNode);
                }else{
                    //这种情况应该不会出现
                }
            }
            
            String name = currentPackage == "" ? pkds[i] : currentPackage + "." + pkds[i];
            if(context.get(name) == null){
            	Map<String, Object> node = new HashMap<String, Object>();
            	TreeModelUtil.setAttributes(self, name, node);
            	node.put("text", pkds[i]);
            	node.put("leaf", "false");
            	node.put("childs", new ArrayList<Object>());
                        
                ((List<Object>) parentNode.get("childs")).add(node);
                context.put(name, node);
            }
            currentPackage = name;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getFlatPackages(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
        Map<String, Object> packages = (Map<String, Object>) world.getData("xworker.app.model.tree.impl.JavaPackageTreeModel-Flat");
        //packages = null;
        if(packages != null){
            return packages;
        }else{
            Thing packageDataObject = world.getThing("xworker.dataObject.java.PackageDataObject");
            List<Map<String, Object>> pkgs = (List<Map<String, Object>>) packageDataObject.doAction("getPackages", actionContext);
            List<Object> nodes = new ArrayList<Object>();
            for(Map<String, Object> pkg : pkgs){
                Map<String, Object> node = new HashMap<String, Object>();
                TreeModelUtil.setAttributes(self, pkg.get("name"), node);
                node.put("text", pkg.get("name"));
                node.put("leaf", "true");
                nodes.add(node);

            }
            
            packages = new HashMap<String, Object>();
            TreeModelUtil.setAttributes(self, "0", packages);
            packages.put("text", "");
            packages.put("leaf", "false");
            packages.put("childs", new ArrayList<Object>());
            
            world.setData("xworker.app.model.tree.impl.JavaPackageTreeModel-Flat", packages);
        }
        
        return packages;
    }

}