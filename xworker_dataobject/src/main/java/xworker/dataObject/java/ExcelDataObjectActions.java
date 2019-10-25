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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import xworker.dataObject.DataObject;
import xworker.dataObject.java.CsvDataObjectActions.Constants;

public class ExcelDataObjectActions {
	private static Logger logger = LoggerFactory.getLogger(ExcelDataObjectActions.class);
	
    public static void loadExcelDatas(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        //Excel数据源
        String filePath = self.getStringBlankAsNull("filePath");
        try{
            if(filePath != null){
                filePath = (String) OgnlUtil.getValue(filePath, actionContext);
            }
        }catch(Exception e){
        }
        
        Workbook workbook = null;
        if(filePath != null){
            try{
                workbook = Workbook.getWorkbook(new File(filePath));
            }catch(Exception e){
                logger.error("ExcelDataObject: create workbook from file error, thing=" + self.getMetadata().getPath(), e);        
            }
        }
        
        //读取Excel的数据
        if(workbook == null){
            self.setData(Constants.HEADERS, new HashMap<String, Object>());
            self.setData(Constants.DATAS, new ArrayList<DataObject>());
            self.setData(Constants.COLUMNCOUNT, 0);
        }else{
            //列最大数
            int columnCount = 0;
            
            //logger.info("sheet lengths=" + workbook.getSheets().length);
            Sheet sheet = null;
            String sheetName = self.getStringBlankAsNull("sheetName");
            if(sheetName != null){
                sheet = workbook.getSheet(sheetName);
            }else{
                sheet = workbook.getSheet(0);
            }
            if(sheet == null){
                logger.warn("ExcelDataObject: sheet is null, sheetName=" + sheetName);
                self.setData(Constants.HEADERS, new HashMap<String, Object>());
                self.setData(Constants.DATAS, new ArrayList<DataObject>());
                self.setData(Constants.COLUMNCOUNT, 0);
                return;
            }
            
            //头-标题
            Map<String, String> headMap = new HashMap<String, String>();   
            int rowCount = sheet.getRows();
            int currentRow = 0;
            if(rowCount > 0 && self.getBoolean("haveHeaders")){
                Cell[] cell = sheet.getRow(currentRow);
                currentRow++;
                columnCount = cell.length;
                for(int i=0; i<cell.length; i++){
                    headMap.put("c" + i, cell[i].getContents());
                }
            }
            self.setData(Constants.HEADERS, headMap);
            
            //所有数据
            List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
            int index = 0;
            while(currentRow < rowCount - 1){
                Cell[] cell = sheet.getRow(currentRow);
                currentRow++;
                
                Map<String, Object> data = new HashMap<String, Object>();        
                for(int i=0; i<cell.length; i++){
                    data.put("c" + i, cell[i].getContents());
                    String name = headMap.get("c" + i);
                    if(name != null){
                    	data.put(name, cell[i].getContents());                    	
                    }
                }
                data.put("index", index);
                index++;
                datas.add(data);
                
                if(columnCount < cell.length){
                    columnCount = cell.length;
                }
            }
            self.setData(Constants.DATAS, datas);
            self.setData(Constants.COLUMNCOUNT, columnCount);
        }
    }

    public static void clearExcelDatas(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        self.setData(Constants.HEADERS, null);
        self.setData(Constants.DATAS, null);
        self.setData(Constants.COLUMNCOUNT, null);
    }

    @SuppressWarnings("unchecked")
	public static void saveExcelDatas(ActionContext actionContext) throws RowsExceededException, WriteException, IOException{
        Thing self = actionContext.getObject("self");
        
        //Csv数据源
        String filePath = self.getStringBlankAsNull("filePath");
        try{
            if(filePath != null){
                filePath = (String) OgnlUtil.getValue(filePath, actionContext);
            }
        }catch(Exception e){
        }
        
        WritableWorkbook workbook = null;
        if(filePath != null){
            try{
                File file = new File(filePath);
                if(!file.exists() && self.getBoolean("autoCreateExcel")){
                    workbook = Workbook.createWorkbook(file);
                }else{
                    workbook = Workbook.createWorkbook(file);
                }
            }catch(Exception e){
                logger.error("ExcelDataObject: create workbook from file error", e);
            }
        }
        
        //保存数据
        if(workbook == null){
            return;
        }else{
            try{
                //数据
            	Map<String, String> headers = (Map<String, String>) self.getData(Constants.HEADERS);
                List<Map<String, Object>> datas = (List<Map<String, Object>>) self.getData(Constants.DATAS);
                int columnCount = (Integer) self.getData(Constants.COLUMNCOUNT);
                //检查最大行数，如果属性映射的列超过了最大列数，那么自动扩充
                for(Thing attribute : self.getChilds("attribute")){
                    try{
                    	String propertyPath= self.getString("propertyPath");
                        int column = Integer.parseInt(propertyPath.substring(1, propertyPath.length()));
                        if(columnCount < column){
                            columnCount = column;
                        }
                    }catch(Exception e){
                    }
                }
                
                WritableSheet sheet;
                String sheetName = self.getStringBlankAsNull("sheetName");
                if(sheetName != null){
                    sheet = workbook.getSheet(sheetName);
                }else{
                    sheet = workbook.getSheet(0);
                }
                //移除原有的行，保存新行
                while(sheet.getRows() > 0){
                    sheet.removeRow(0);
                }
                int row = 0;
                //保存头-标题
                if(self.getBoolean("haveHeaders")){
                    for(int i=0; i<columnCount; i++){                
                        String header = headers.get("h" + i);
                        if(header == null){
                            header = "";
                        }
                        Label cell = new Label(i, row, header);
                        sheet.addCell(cell);
                    }
                    row++;
                }
                
                //博阿村所有数据
                for(Map<String, Object> data : datas){
                    for(int i=0; i<columnCount; i++){
                        Object value = data.get("c" + i);
                        String v = null;
                        if(value == null){
                            v = "";
                        }else{
                            v = value.toString();
                        }
                        Label cell = new Label(i, row, v);
                        sheet.addCell(cell);
                    }
                    row++;
                }
            }finally{
                workbook.close();
            }
        }
    }

    public static Object doLoad(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@load", false);
    }

    public static Object doCreate(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@create", true);
    }

    public static Object doUpdate(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@update", true);   
    }

    public static Object updateBatch(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@updateBatch", true);  
    }

    public static Object doDelete(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@delete", true);    
    }

    public static Object deleteBatch(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@deleteBatch", true);    
    }

    public static Object doQuery(ActionContext actionContext){
    	return CsvDataObjectActions.doAction(actionContext, "xworker.dataObject.java.ListDataObject/@actions1/@query", false);      
    }

    @SuppressWarnings("unchecked")
	public static Object getMappingFields(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        Map<String, String> headers = (Map<String, String>) self.getData(Constants.HEADERS);
        if(headers == null){
            //再重新读取一次，有可能之前没有读取过
            self.doAction("loadDatas", actionContext);
            headers = (Map<String, String>)  self.getData(Constants.HEADERS);
            if(headers == null){
                return Collections.EMPTY_LIST;
            }
        }
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for(String key : headers.keySet()){
            datas.add(UtilMap.toMap("colName", key, "colTitle", headers.get(key)));
        }
        return datas;
    }

}