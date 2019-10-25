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
package xworker.app.model.tree.swtimpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.app.model.tree.TreeModelUtil;
import xworker.util.UtilFileIcon;

public class FileTreeModelCreator {
    public static Object getRoot(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	String filePath = (String) self.doAction("getFilePath", actionContext);
        File file = new File(filePath);
        
        Object node= self.doAction("createFileNode", actionContext, UtilMap.toMap(new Object[]{"file", file.getAbsoluteFile()}));
        return node;
    }
    
    public static String getFilePath(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return self.getString("filePath");
    }

    public static Object getChilds(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	String id = (String) actionContext.get("id");
        if(id == null || "".equals(id) || "__TreeNodeRootId__".equals(id)){
            return self.doAction("getRoots", actionContext);
        }
        
        File file = new File(id);
        if(file.isDirectory()){
            List<Object> childs = new ArrayList<Object>();
            //先显示目录
            for(File child : file.listFiles()){
                if(child.isFile()){
                     continue;
                }
                
                childs.add(self.doAction("createFileNode", actionContext, UtilMap.toMap(new Object[]{"file", child})));
            }
            
            //然后是文件
            if(self.getBoolean("showFile") == true){
            	List<File> files = new ArrayList<File>();
                for(File child : file.listFiles()){
                    if(!child.isFile()){
                         continue;
                    }
                    
                    files.add(child);
                }
                
                Collections.sort(files);
                for(File child : files) {
                	childs.add(self.doAction("createFileNode", actionContext, UtilMap.toMap(new Object[]{"file", child})));
                }
            }
            return childs;
        }else{
            return null;
        }
    }

    public static Object createFileNode(ActionContext actionContext) throws IOException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	File file = (File) actionContext.get("file");
        String icon = UtilFileIcon.getFileIcon(file, false);
        String fileName = file.getName();
        String id = file.getAbsolutePath();
        if(fileName == null || "".equals(fileName)){
            fileName = id;
        }
                
        /*
        if(file.isDirectory()){
            icon = "xworker:core/images/swteditor/package.gif";
        }else{
            icon = null;
            int index = fileName.lastIndexOf(".");
            if(index != -1){
                String extension = fileName.substring(index + 1, fileName.length());
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
                }
            }
            
            if(icon == null){
                icon = "xworker:core/images/swteditor/normalFile.gif";
            }
        }
        */
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

}