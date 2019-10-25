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
package xworker.app.view.extjs.tools.fileManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.util.UtilFileIcon;

public class FileDataObjectActions {
    public static Object query(ActionContext actionContext) throws IOException{
        Thing self = actionContext.getObject("self");
        HttpServletRequest request = actionContext.getObject("request");
        String rootFilePath = request.getServletContext().getRealPath("/");
        //servlet.getServletContext().getRealPath("/")
        File dir = new File(request.getParameter("id"));
        List<DataObject> files = new ArrayList<DataObject>();
        //先添加目录
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<File> fs = new ArrayList<File>();
        for(File child : dir.listFiles()){
            if(child.isDirectory()){
                fs.add(child);
            }
        }
        Collections.sort(fs);
        for(File child : fs){
            if(child.isDirectory()){
            	DataObject obj = new DataObject("xworker.app.view.extjs.tools.fileManager.FileDataObject");
            	String filePath = child.getAbsolutePath().replace('\\', '/'); 
                obj.put("icon", "<img src=\"" + UtilFileIcon.getFileIcon(child, false) + "\"/>");
                obj.put("id", filePath);
                obj.put("webPath", filePath.substring( rootFilePath.length(), filePath.length()));
                obj.put("name", child.getName());
                obj.put("date", sf.format(new Date(child.lastModified())));
                obj.put("type", "文件夹");
                obj.put("size", "");
                
                files.add(obj);
            }
        }
        
        //然后添加文件
        fs.clear();
        for(File child : dir.listFiles()){
            if(child.isFile()){
                fs.add(child);
            }
        }
        Collections.sort(fs);
        for(File child : fs){
            if(child.isFile()){
            	DataObject obj = new DataObject("xworker.app.view.extjs.tools.fileManager.FileDataObject");
                obj.put("icon", "<img src=\"" + UtilFileIcon.getFileIcon(child, false) + "\"/>");
                String filePath = child.getAbsolutePath().replace('\\', '/'); 
                obj.put("id", filePath);
                obj.put("webPath", filePath.substring(rootFilePath.length(), filePath.length()));
                obj.put("name", child.getName());
                obj.put("date", sf.format(new Date(child.lastModified())));
                obj.put("type", child.getName());
                obj.put("size", "" + child.length());
                files.add(obj);
            }
        }
        
        return files;
    }

}