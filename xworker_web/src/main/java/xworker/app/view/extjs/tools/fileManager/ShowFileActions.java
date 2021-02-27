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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.app.view.extjs.server.ExtjsUtil;
import xworker.lang.executor.Executor;

public class ShowFileActions {
	private static final String TAG = ShowFileActions.class.getName();
	
    public static Object doAction(ActionContext actionContext) throws Exception{
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        HttpServletRequest request = actionContext.getObject("request");
        String command = request.getParameter("command");
        
        //权限检查
        Thing checker = world.getThing(request.getParameter("checker"));
        Executor.info(TAG, "checker=" + checker);
        if(checker == null || UtilData.isTrue(checker.doAction("check", actionContext)) != true){
            ExtjsUtil.writeFormResponse(false, "没有权限！",null, actionContext);
            return null;
        }else{
            return self.doAction(command, actionContext);
        }
    }

    public static Object listFiles(ActionContext actionContext) throws IOException{
        //Thing self = actionContext.getObject("self");
        HttpServletRequest request = actionContext.getObject("request");
        HttpServletResponse response = actionContext.getObject("response");
        HttpServlet servlet = actionContext.getObject("servlet");
        
        String id = request.getParameter("id");
        File file = new File(id);
        
        if("export" == request.getParameter("ac")){
        	ServletContext context = servlet.getServletContext();
            String contentType = context.getMimeType(file.getPath());
            writeFile(file, contentType, response);
            return null;
        }
        if(file.isFile()){
        	ServletContext context = servlet.getServletContext();
        	String contentType = context.getMimeType(file.getPath());
            Executor.info(TAG, contentType);
            if(contentType.startsWith("text") ){
                writeFile(file, contentType, response);
                return null;
            }else if(contentType.startsWith("image")){
                String url = "do?sc=xworker.app.view.extjs.tools.fileManager.ShowFile&ac=export&id=" + id;
                response.getWriter().println("<img  src=\"" + url + "\"/>");
                return "file";
            }
        }else{
            return "dir";
        }
        
        return null;
    }

    public static Object createFolder(ActionContext actionContext) throws Exception{
        //Thing self = actionContext.getObject("self");
        HttpServletRequest request = actionContext.getObject("request");
        //HttpServletResponse response = actionContext.getObject("response");
        
        File parentFile = new File(request.getParameter("parentFile"));
        String name = request.getParameter("name");
        if(name == null || name == ""){
            ExtjsUtil.writeFormResponse(false, "目录名不能为空！", null, actionContext);
            return null;
        }
        File newFile = new File(parentFile, name);
        if(newFile.exists()){
            ExtjsUtil.writeFormResponse(false, "目录或文件已经存在！", null, actionContext);
            return null;
        }
        
        try{
            newFile.mkdirs();
            ExtjsUtil.writeFormResponse(true, "", UtilMap.toMap("id", newFile.getAbsolutePath(), "name", name), actionContext);
        }catch(Exception e){
            ExtjsUtil.writeFormResponse(false, e.getMessage(), UtilMap.toMap("id", newFile.getAbsolutePath()), actionContext);
        }
        
        return null;
    }

    public static void  writeFile(File file, String contentType, HttpServletResponse response) throws IOException{
        response.setContentType(contentType);
        FileInputStream fin = new FileInputStream(file);
        response.setContentLength(fin.available());
        byte[] bytes = new byte[1024];
        int l = -1;
        OutputStream out = response.getOutputStream();
        while((l = fin.read(bytes)) != -1){    
            out.write(bytes, 0, l);
        }
        fin.close();    
        //println(new String(bytes));
    }
    
    public static String getPath(File imageFile, File imageDirFile, String path) throws UnsupportedEncodingException{
        if(imageFile.equals(imageDirFile)){
            return path;
        }else{
            String name = imageFile.getName();
            name = URLEncoder.encode(name, "UTF-8");
            if(path == null){
                path = name;
            }else{
                path = name + "/" + path;
            }
            return getPath(imageFile.getParentFile(), imageDirFile, path); 
        }
    }
    
    public static void delete(ActionContext actionContext) throws Exception{
        //Thing self = actionContext.getObject("self");
        HttpServletRequest request = actionContext.getObject("request");
        
        File file = new File(request.getParameter("id"));
        try{
            //log.info("id=" + request.getParameter("id"));
            boolean deleted = file.delete();
            ExtjsUtil.writeFormResponse(deleted, "删除文件失败！", UtilMap.toMap("id", request.getParameter("id")), actionContext);
        }catch(Exception e){
            ExtjsUtil.writeFormResponse(false, e.getMessage(), null, actionContext);
        }
    }

    public static void rename(ActionContext actionContext) throws Exception{
    	HttpServletRequest request = actionContext.getObject("request");
        
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        if(name == null || name == ""){
            ExtjsUtil.writeFormResponse(false, "新的文件或目录名不能为空！", null, actionContext);
            return;
        }
        
        File file = new File(id);
        if(file.getName() != name){    
        	File newFile = new File(file.getParentFile(), name);
            if(newFile.exists()){
                ExtjsUtil.writeFormResponse(false, "目标文件或目录已经存，不能重命名！", null, actionContext);
                return;
            }else{
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("isFile", file.isFile()); 
                if(file.renameTo(newFile)){                       
                    data.put("id", newFile.getAbsolutePath());
                    data.put("name", newFile.getName());
                    data.put("oldId", id);
                    ExtjsUtil.writeFormResponse(true, "", data, actionContext);
                    return;
                }else{                
                    ExtjsUtil.writeFormResponse(false, "重命名失败！", null, actionContext);
                }
            }
        }else{
            ExtjsUtil.writeFormResponse(false, "", null, actionContext);
            return;
        }
    }

}