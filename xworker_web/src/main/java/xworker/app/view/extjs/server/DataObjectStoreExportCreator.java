

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
package xworker.app.view.extjs.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.JsonFormator;
import xworker.lang.executor.Executor;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;

public class DataObjectStoreExportCreator {
	private static final String TAG = DataObjectStoreExportCreator.class.getName();
	
	public static int getInt(String value, int defaultValue){
        try{
            return Integer.parseInt(value);
        }catch(Exception e){
            return defaultValue;
        }
    }
	
    @SuppressWarnings("unchecked")
	public static void doAction(ActionContext actionContext) throws OgnlException, IOException{
        World world = World.getInstance();
        
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        
        String dataObjectPath = request.getParameter("dataObjectPath");
        String conditionPath = request.getParameter("conditionPath");
        
        if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAOBJECT, "export", dataObjectPath, actionContext)){
        	return;
        }
        
        //数据对象
        Thing dataObject = world.getThing(dataObjectPath);
        
        //查询定义
        Thing condition = world.getThing(conditionPath);
        String currName=request.getParameter("fileName")==null? "统计结果":request.getParameter("fileName");
        //是否固定前10000
        String istop=request.getParameter("istop")==null? "false":request.getParameter("istop");
        //分页信息
        Map<String, Object> pageInfo = new HashMap<String, Object>();
        pageInfo.put("start", istop.equals("true")? 0: getInt(request.getParameter("start"), 0));
        pageInfo.put("limit", istop.equals("true")? 10000:getInt(request.getParameter("limit"), 10000));
        if((Integer) pageInfo.get("limit") == 0){
            pageInfo.put("limit", getInt(request.getParameter("pageSize"), 0));
        }
        pageInfo.put("datas", new ArrayList<Object>());
        pageInfo.put("success", true);
        pageInfo.put("msg",  "");
        pageInfo.put("totalCount", 0);
        pageInfo.put("sort",  request.getParameter("sort"));
        pageInfo.put("dir", request.getParameter("dir"));
        
        //查询参数
        Object conditionData = null;
        if(condition != null){
            conditionData = condition.doAction("parseHttpRequest", actionContext);
        }
        
        actionContext.put("aggregateColumns", request.getParameter("aggregateColumns"));
        actionContext.put("groupColumns", request.getParameter("groupColumns"));
        
        //查询
        try{
	        Object datas = dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"conditionData",conditionData, "conditionConfig",condition, "pageInfo",pageInfo}));
	        //输出json格式的数据
	        Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
	        String code = "[]";
	        if(datas == pageInfo){
	            code = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data", pageInfo.get("datas")}));
	        }else{
	            code = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data", datas}));
	            if(datas instanceof DataObject){
	                code = code + "[" + code + "]";
	                pageInfo.put("totalCount", 1);	                
	            }else{
	                if((Integer) pageInfo.get("totalCount") == 0){
	                	if(datas instanceof List){
	                        pageInfo.put("totalCount", ((List) datas).size());
	                    }else{
	                        pageInfo.put("totalCount",  OgnlUtil.getValue("length",datas));
	                    }
	                }
	            }    
	            pageInfo.put("limit", pageInfo.get("totalCount"));
	        }
	        
	        //Executor.info(TAG, code);
	        //输出到httpResponse
	        if(code.startsWith("{")){
	            code = "[" + code + "]";
	        }
	        
	        //是否动态生成store和gridColumn定义，如果pageInfo.dynamicDataObject存在
	        //println "store";
	        String storeCode = "";
	        String columnCode = "";
	        if(pageInfo.get("dynamicDataObject") != null){
	            Thing grid = new Thing("xworker.app.view.extjs.widgets.AppItems/@DataObjectGridPanel");
	            Thing dynamicDataObject = (Thing) pageInfo.get("dynamicDataObject");
	            grid.put("dataObject", dynamicDataObject.getMetadata().getPath());
	            
	            Thing store = (Thing) grid.doAction("createExtStore", actionContext, UtilMap.toMap(new Object[]{"dataObject", dynamicDataObject, "cmpType","grid"}));
	            //store作为生产reader的metadata用，不需要的子节点删除
	            Thing stores = store.getChilds().get(0);
	            for(int i=0; i<stores.getChilds().size(); i++){
	                Thing child = stores.getChilds().get(i);
	                if(!"fields".equals(child.getThingName())){
	                    stores.removeChild(child);
	                    i--;
	                }
	            }
	            storeCode = (String) store.doAction("toJavaScriptCode", actionContext);
	            storeCode = "\n    metaData: " + storeCode.substring(storeCode.indexOf("(") + 1, storeCode.lastIndexOf(")")) + ",";
	            
	            Thing columnModel = (Thing) grid.doAction("createExtGridColumns", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dynamicDataObject}));
	            columnCode = "\n   columnModel: " + columnModel.doAction("toJavaScriptCode", actionContext) + ",";
	            //println columnCode;
	        }
	        
	        
	        String exportName=currName+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	        String filePath=System.getProperty("user.dir")+"\\"+exportName+".xls";
	        System.out.println("filePath---------------"+filePath);
	        createExcel(new ObjectMapper().readValue(code, List.class), filePath,  getColum(columnCode), "统计结果");
	       /* response.setContentType("text/plain; charset=utf-8");
	        String result = "{\n" +
	        		"success:true,\n" +
	        		"totalCount:0,\n" +
	        		"msg:'导出成功,文件位置是"+exportName+"',\n" +
	        		"pageSize:0\n" +
	        		"}";
	        response.getWriter().println(result);*/
	        transferFile(filePath, exportName, response);
        }catch(Exception e){
        	Executor.error(TAG, "DataObjectStore export error", e);
        	
        	response.setContentType("text/plain; charset=utf-8");
	        String result = "{\n" +
	        		"success:false,\n" +
	        		"totalCount:0,\n" +
	        		"msg:'" + JsonFormator.formatString(ExceptionUtil.getRootMessage(e)) + "',\n" +
	        		"pageSize:0\n" +
	        		"}";
	        response.getWriter().println(result);
        }
    }
    
    //得到栏目
    public static List<String> getColum(String jsonStr){
    	String[] str = jsonStr.split("header");
    	List<String> columLis=new ArrayList<String>();
    	for (int i = 1; i < str.length; i++) {
			String curStr=str[i].substring(str[i].indexOf("'")+1, str[i].indexOf(",")-1);
			columLis.add(curStr);
		}
    	return columLis;
    }
    /**
     * 导出Excel
     * @param list：结果集合
     * @param filePath：指定的路径名
     * @param fields：导出字段 key:对应实体类字段    value：对应导出表中显示的中文名
     * @param sheetName：工作表名称
     */
    public static void createExcel(List list,String filePath,List<String> fields,String sheetName){
        sheetName = sheetName!=null && !sheetName.equals("")?sheetName:"sheet1";
        WritableWorkbook wook = null;//可写的工作薄对象
        try {
            wook = Workbook.createWorkbook(new File(filePath));//指定导出的目录和文件名 如：D:\\test.xls
                                                                            
            //设置头部字体格式
            WritableFont font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, 
                    false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
            //应用字体
            WritableCellFormat wcfh = new WritableCellFormat(font);
            //设置其他样式
            wcfh.setAlignment(Alignment.CENTRE);//水平对齐
            wcfh.setVerticalAlignment(VerticalAlignment.CENTRE);//垂直对齐
            wcfh.setBorder(Border.ALL, BorderLineStyle.THIN);//边框
            wcfh.setBackground(Colour.YELLOW);//背景色
            wcfh.setWrap(false);//不自动换行
                                                                            
            //设置内容日期格式
            DateFormat df = new DateFormat("yyyy-MM-dd HH:mm:ss");
            //应用日期格式
            WritableCellFormat wcfc = new WritableCellFormat(df);
                                                                            
            wcfc.setAlignment(Alignment.CENTRE);
            wcfc.setVerticalAlignment(VerticalAlignment.CENTRE);//垂直对齐
            wcfc.setBorder(Border.ALL, BorderLineStyle.THIN);//边框
            wcfc.setWrap(false);//不自动换行
                                                                            
            //创建工作表
            WritableSheet sheet = wook.createSheet(sheetName, 0);
            SheetSettings setting = sheet.getSettings();
            setting.setVerticalFreeze(1);//冻结窗口头部
                                                                            
            int columnIndex = 0;  //列索引
            List<String> methodNameList = new ArrayList<String>();
            if(fields!=null){
                //开始导出表格头部
                for (int i =0;i<fields.size();i++) {
                    // 应用wcfh样式创建单元格
                    sheet.addCell(new Label(columnIndex, 0, fields.get(i), wcfh));
                    //记录字段的顺序，以便于导出的内容与字段不出现偏移
                    methodNameList.add(fields.get(i));
                    columnIndex++;
                }
                if(list!=null && list.size()>0){
                    //导出表格内容
                    for (int i = 0,len = list.size(); i < len; i++) {
                    	
                    	Map<String, Object> map = (Map<String, Object>) list.get(i);
                        Set<String> set = map.keySet();
                        for (Iterator<String> it = set.iterator();it.hasNext();) {
                            
                            for (int j = 0; j < methodNameList.size(); j++) {
                            	String key = it.next();
                        		sheet.addCell(new Label(j, i+1, map.get(key).toString(), wcfc));
                        	 }
                        }
                    	
                    }
                }
                wook.write();
                System.out.println("导出Excel成功！");
            }else{
                throw new Exception("传入参数不合法");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(wook!=null){
                    wook.close();
                }
               
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    /**
     * 传输文件 
     * @param fileName
     * @param prefix
     * @param response
     */
    public static void transferFile(String fileName,String prefix,HttpServletResponse response ) {
        ByteArrayOutputStream os = getOutStreamByte(fileName,1024);
        ByteArrayInputStream inStream = null;
        try {
            inStream = new ByteArrayInputStream(os.toByteArray());
            if (os != null && os.size() > 0) {
                long filelength = os.size();
                // 设置输出的格式
                response.reset();
                response.setContentType("application/x-msdownload");
                response.setContentLength((int) filelength);
                response.setContentType("text/html;charset=UTF-8");
                response.addHeader(
                        "Content-Disposition",
                        "attachment; filename=\""
                                + new String(prefix.getBytes("UTF-8"),
                                        "ISO8859_1") + ".xls\"");
                // 循环取出流中的数据
                byte[] b = new byte[4];
                int len;
//              os.flush();
                ServletOutputStream out =  response.getOutputStream();
                while ((len = inStream.read(b)) != -1){
                    out.write(b, 0, len);
                }
                out.flush();
                out.close();
                response.flushBuffer();
                File file = new File(fileName);
                if(file.isFile() && file.exists()){
                    file.delete();
                }
            }
 
        } catch (FileNotFoundException e) {
        	Executor.error(TAG, "要发送的文件不存在");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	Executor.error(TAG, "编码格式不支持");
            e.printStackTrace();
        } catch (IOException e) {
        	Executor.debug(TAG, "文件流异常");
            e.printStackTrace();
        }finally{
            if(null != inStream){
                try {
                    inStream.close();
                    Executor.debug(TAG, "inStream close ok");
                } catch (IOException e) {
                	Executor.error(TAG, "文件关闭异常");
                    e.printStackTrace();
                }
            }
         
        }
    }
    
    public static ByteArrayOutputStream getOutStreamByte(String filename , int buffSize) {
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filename));
            out = new ByteArrayOutputStream(buffSize);
            byte[] temp = new byte[buffSize];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in != null){
                try {
                    in.close();
                    Executor.debug(TAG, "close when read finished");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }if(out != null){
                try {
                	Executor.debug(TAG, "close when write to out finished");
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
             
        }
        return out;
    }
    

}