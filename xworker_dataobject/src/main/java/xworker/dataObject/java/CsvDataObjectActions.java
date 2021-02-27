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
package xworker.dataObject.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.lang.actions.ActionUtils;
import xworker.lang.executor.Executor;

public class CsvDataObjectActions {
	private static final String TAG = CsvDataObjectActions.class.getName();
	
	public static char getDelimiter(String delimiter){
		return ActionUtils.getDelimiter(delimiter);
		/*
		if(delimiter == null || "".equals(delimiter)){
			return ',';
		}
		
		if("\\t".equals(delimiter)){
			return '\t';
		}else if("\\s".equals(delimiter)){
			return ' ';
		}else if("\\n".equals(delimiter)){
			return '\n';
		}else{
			return delimiter.charAt(0);
		}*/
	}
	
	public static char getDelimiter(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return getDelimiter(self.getStringBlankAsNull("delimiter"));
	}
	
    public static void loadCsvDatas(ActionContext actionContext) throws IOException{
        Thing self = actionContext.getObject("self");
        
        //常量类
        Charset charset = Charset.forName(self.getString("charset"));
        
        //Csv数据源
        CsvReader csvReader = null;
        try{  
        	char delimiter = (Character) self.doAction("getDelimiter", actionContext);
	        Object csvSource = self.doAction("getCsvSource", actionContext);
	        if(csvSource instanceof String && new File((String) csvSource).exists()){
	        	csvSource = new File((String) csvSource);
	        }	        
	        if(csvSource instanceof File){
	            csvReader = new CsvReader(((File) csvSource).getAbsolutePath(), delimiter, charset);
	        }else if(csvSource instanceof String){
	            csvReader = new CsvReader(new ByteArrayInputStream(((String) csvSource).getBytes(charset)), 
	            		delimiter, charset);
	        }
        }catch(Exception e){
            Executor.error(TAG, "CsvDataObject: create CsvReader from file error", e);
        }
        
        
        //读取Csv的数据
        if(csvReader == null){
            self.setData(Constants.HEADERS, new HashMap<String, Object>());
            self.setData(Constants.DATAS, new ArrayList<DataObject>());
            self.setData(Constants.COLUMNCOUNT, 0);
        }else{
            //列最大数
            int columnCount = 0;
            //头-标题
            Map<String, String> headMap = new HashMap<String, String>();    
            if(self.getBoolean("haveHeaders")){
                if(csvReader.readHeaders()){
                    String[] headers = csvReader.getHeaders();
                    columnCount = headers.length;
                    for(int i=0; i<headers.length; i++){
                        headMap.put("h" + i, headers[i]);
                    }
                }
            }
            self.setData(Constants.HEADERS, headMap);
            
            //所有数据
            List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
            int index = 0;
            while(csvReader.readRecord()){
                Map<String, Object> data = new HashMap<String, Object>();        
                for(int i=0; i<csvReader.getColumnCount(); i++){
                    data.put("c" + i, csvReader.get(i));
                    String name = headMap.get("h" + i);
                    if(name != null){
                    	data.put(name, data.get("c" + i));
                    }
                }
                data.put("index", index);
                index++;
                datas.add(data);
                
                if(columnCount < csvReader.getColumnCount()){
                    columnCount = csvReader.getColumnCount();
                }
            }
            self.setData(Constants.DATAS, datas);
            self.setData(Constants.COLUMNCOUNT, columnCount);
        }
    }

    public static void clearCsvDatas(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //常量类
        
        self.setData(Constants.HEADERS, null);
        self.setData(Constants.DATAS, null);
        self.setData(Constants.COLUMNCOUNT, null);
    }
    
    public static void doSaveCsvDatas(ActionContext actionContext) throws IOException{
    	Thing self = actionContext.getObject("self");
    	String saveMethod = self.getStringBlankAsNull("saveMethod");
    	
    	Charset charset = Charset.forName(self.getString("charset"));
    	char delimiter = (Character) self.doAction("getDelimiter", actionContext);
    	CsvWriter csvWriter = null;
    	OutputStream out = null;
    	if("file".equals(saveMethod)){
    		File file = self.doAction("getCsvFile", actionContext);
    		if(file == null){
    			throw new ActionException("Save csv data to file but file is null, thing=" + self.getMetadata().getPath());
    		}
    		
    		csvWriter = new CsvWriter(file.getAbsolutePath(), delimiter, charset);
    		writeToCsvWriter(self, csvWriter);
    	}else if("text".equals(saveMethod)){
    		out = new ByteArrayOutputStream();
    		csvWriter = new CsvWriter(out, delimiter, charset);
    		writeToCsvWriter(self, csvWriter);
    		
    		String text = out.toString();
    		self.doAction("doSave", actionContext, "text", text);
    	}else if("outputStream".equals(saveMethod)){
    		out = self.doAction("getOutputStream", actionContext);
    		if(out == null){
    			throw new ActionException("Save csv data to outputStream but outputStream is null, thing=" + self.getMetadata().getPath());
    		}
    		
    		try{
    			csvWriter = new CsvWriter(out, delimiter, charset);
    		}finally{
    			self.doAction("onSaveFinished", actionContext, "out", out);
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void writeToCsvWriter(Thing self, CsvWriter csvWriter) throws IOException{
    	try{
            //数据
            Map<String, String> headers = (Map<String, String>) self.getData(Constants.HEADERS);
            List<Map<String, Object>> datas = (List<Map<String, Object>>) self.getData(Constants.DATAS);
            int columnCount = (Integer) self.getData(Constants.COLUMNCOUNT);
            //检查最大行数，如果属性映射的列超过了最大列数，那么自动扩充
            for(Thing attribute : self.getChilds("attribute")){
                try{
                	String propertyPath= attribute.getString("propertyPath");
                    int column = Integer.parseInt(propertyPath.substring(1, propertyPath.length()));
                    if(columnCount < column){
                        columnCount = column;
                    }
                }catch(Exception e){
                }
            }
            
            //保存头-标题
            if(self.getBoolean("haveHeaders")){
                for(int i=0; i<columnCount; i++){
                    String header = headers.get("h" + i);
                    if(header == null){
                        header = "";
                    }
                    csvWriter.write(header, true);
                }
                csvWriter.endRecord();
            }
            
            //保存所有数据
            for(Map<String, Object>data : datas){
                for(int i=0; i<columnCount; i++){
                    Object value = data.get("c" + i);
                    String v = null;
                    if(value == null){
                        v = "";
                    }else{
                        v = value.toString();
                    }
                    csvWriter.write(v, true);
                }
                csvWriter.endRecord();
            }
            csvWriter.flush();
        }finally{
            csvWriter.close();
        }
    }

	public static void saveCsvDatas(ActionContext actionContext) throws OgnlException, IOException{
        Thing self = actionContext.getObject("self");
        //保存策略
        String saveStrategy = self.getStringBlankAsNull("saveStrategy");
        if("auto".equals(saveStrategy)){
        	//自动保存
        	doSaveCsvDatas(actionContext);
        }else if("delay".equals(saveStrategy)){
        	long delay = self.getLong("saveDelay");
        	self.doAction("doSaveDelay", actionContext, "delay", delay, "vars", "data", "data", self);
        }
        //其它默认不保存
    }
    
    @SuppressWarnings("unchecked")
	public static Object doAction(ActionContext actionContext, String actionPath, boolean needSave){ 
    	Thing self = actionContext.getObject("self");
        
        //临时数据
        List<Map<String, Object>> datas = (List<Map<String, Object>>) self.getData(Constants.DATAS);
        if(datas == null){
            self.doAction("loadDatas", actionContext);
            datas = (List<Map<String, Object>>) self.getData(Constants.DATAS);
        }
        String listDataName = "__datas__";
        self.set("listData", listDataName);
        self.set("dataClassName", "java.util.HashMap");
        
        try{
            Bindings bindings = actionContext.push(null);
            bindings.put(listDataName, datas);
        
            //使用ListDataObject的load方法    
            Action action = World.getInstance().getAction(actionPath);
            
            Object obj = action.run(actionContext);
            if(needSave){
	            if(obj != null && self.getBoolean("autoSave")){
	                self.doAction("saveDatas", actionContext);
	            }
            }
            return obj;
        }finally{
            actionContext.pop();
        }
    }

	public static Object doLoad(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@load", false);
    }

	public static Object doCreate(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@create", true);
    }

	public static Object doUpdate(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@update", true);       
    }

    public static Object updateBatch(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@updateBatch", true);  
    }

    public static Object doDelete(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@delete", true);         
    }

    public static Object deleteBatch(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@deleteBatch", true);         
    }

    public static Object doQuery(ActionContext actionContext){
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@query", false);          
    }
    
    public static Object iterator(ActionContext actionContext) {
    	return doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@iterator", false);
    }

    @SuppressWarnings("unchecked")
	public static Object isMappingAble(ActionContext actionContext){
        Thing thing = actionContext.getObject("self");
        
        //临时数据
        List<Map<String, Object>> datas = (List<Map<String, Object>>) thing.getData(Constants.DATAS);
        if(datas == null || datas.size() == 0){
        	thing.doAction("loadDatas", actionContext);
            datas = (List<Map<String, Object>>) thing.getData(Constants.DATAS);
        }
        
        Object headers = thing.getData(Constants.HEADERS);
        //def attributes = [];
        //添加CSV的列
        if(headers == null && (Integer) thing.getData(Constants.COLUMNCOUNT) <= 0){
            throw new ActionException("找不到可以映射的CSV数据列，请查看CSV文件或数据是否存在。");
        }
        
        return true;
    }

    @SuppressWarnings("unchecked")
	public static Object getMappingFields(ActionContext actionContext){
    	Thing thing = actionContext.getObject("self");
        
        //临时数据
    	thing.setData(Constants.DATAS, null);
    	 List<Map<String, Object>> datas = (List<Map<String, Object>>) thing.getData(Constants.DATAS);
        if(datas == null || datas.size() == 0){
            thing.doAction("loadDatas", actionContext);
            datas = (List<Map<String, Object>>) thing.getData(Constants.DATAS);
        }
        
        Map<String, String> headers = (Map<String, String>) thing.getData(Constants.HEADERS);
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        Map<String, Object> index = UtilMap.toMap("colName", "index", "colTitle", "index");
        attributes.add(index);
        
        //添加CSV的列
        if(headers != null && thing.getBoolean("haveHeaders")){
            for(int i=0; i<headers.size(); i++){
                Map<String, Object> attr = new HashMap<String, Object>();
                String colName = "c" + i;
                String colTitle = headers.get("h" + i);
                attr.put("colName", colName);
                attr.put("colTitle", colTitle);
                attributes.add(attr);
            }
        }else if((Integer) thing.getData(Constants.COLUMNCOUNT) > 0){
        	Integer count = (Integer) thing.getData(Constants.COLUMNCOUNT);
            for(int i=0; i<count; i++){
                Map<String, Object> attr = new HashMap<String, Object>();
                String colName = "c" + i;
                String colTitle = "h" + i;
                attr.put("colName", colName);
                attr.put("colTitle", colTitle);
                attributes.add(attr);
            }
        }
        return attributes;
    }

    public static Object getMappingAttributeName(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        return "propertyPath";
    }
    
    public static class Constants{
        public static final String HEADERS = "_headerNames";
        public static final String DATAS = "_datas";
        public static final String COLUMNCOUNT = "_columnCount";
        
        public static Object run(ActionContext actionContext){
            return null;
        }
    }

}