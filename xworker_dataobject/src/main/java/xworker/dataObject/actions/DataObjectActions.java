package xworker.dataObject.actions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilJava;
import org.xmeta.util.UtilMap;

import com.csvreader.CsvWriter;

import jxl.Workbook;
import jxl.write.Blank;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.db.DbDataObject;
import xworker.lang.executor.Executor;

public class DataObjectActions {
	private static final String TAG = DataObjectActions.class.getName();
	
	public static Thing getDataObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String dataObjectPath = self.getString("dataObjectPath", null, actionContext); 		
		Thing dataObject = null;
		if(dataObjectPath != null){
			dataObject = World.getInstance().getThing(dataObjectPath);
		}

		if(dataObject == null){
			Thing dataObjects = self.getThing("DataObjects@0");
			if(dataObjects != null && dataObjects.getChilds().size() > 0){
				dataObject = dataObjects.getChilds().get(0);
			}
		}
		
		return dataObject;
	}
	
	public static Object getPageInfo(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		boolean debug = UtilAction.getDebugLog(self, actionContext);
		
		if(self.getStringBlankAsNull("pageInfo") == null){
			if(self.getBoolean("createPageInfo")){				
				int start = 0;
				int limit = 50;
				int page = 0;
				int pageSize = -1;
				if(self.getBoolean("isServlet")){					
					HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
					start = UtilData.getInt(request.getParameter(self.getString("start")), 0);					
					limit = UtilData.getInt(request.getParameter(self.getString("limit")), -1);
					if(limit == -1){
						limit = self.getInt("limit", 50);
					}
					page = UtilData.getInt(request.getParameter(self.getString("page")), -1);
					pageSize =  UtilData.getInt(request.getParameter(self.getString("pageSize")), -1);
					if(pageSize == -1){
						pageSize = self.getInt("pageSize", -1);
					}
				}else{
					Object s = UtilData.getData(self, "start", actionContext);
					start = UtilData.getInt(s, -1);					
					
					s = UtilData.getData(self, "limit", actionContext);
					limit = UtilData.getInt(s, -1);
					
					s = UtilData.getData(self, "page", actionContext);
					page = UtilData.getInt(s, -1);
					
					s = UtilData.getData(self, "pageSize", actionContext);
					pageSize = UtilData.getInt(s, -1);
				}
				
				if(start == -1){
					start = 0;
				}
				if(limit == -1){
					limit = 50;
				}
				if(page == -1){
					page = 1;
				}
				if(pageSize == -1){
					pageSize = 50;
				}
				PageInfo pageInfo = new PageInfo(start, limit);
				if(pageSize > 0){
					pageInfo.setLimit(pageSize);
				}
				if(page >0){
					//pageInfo.setPageSize(pageSize);
					pageInfo.setPage(page);
				}
				String pageInfoVarName = self.getStringBlankAsNull("pageInfoVarName");
				if(pageInfoVarName != null){
					Bindings bindings = UtilAction.getVarScope(self.getString("pageInfoVarScope"), actionContext);
					bindings.put(pageInfoVarName, pageInfo);
				}
				
				if(debug){
					if(self.getBoolean("isServlet")){	
						Executor.info(TAG, "create page info from servlet, start=" + start + ",limit=" + limit 
								+ ",page=" + page + ",pageSize=" + pageSize);
					}else{
						Executor.info(TAG, "create page info from actionContext, start=" + start + ",limit=" + limit 
								+ ",page=" + page + ",pageSize=" + pageSize);
					}
				}
				return pageInfo;
			}else{
				if(debug){
					Executor.info(TAG, "no pageInfo defined and no create new PageInfo");
				}
				return null;
			}
		}else{
			if(debug){
				Executor.info(TAG, "get PageInfo from actionContext, pageInfo=" + self.getString("pageInfo"));
			}
			return UtilData.getData(self, "pageInfo", actionContext);
		}
	}
	
	public static Object getConditionData(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		if(self.getStringBlankAsNull("conditionData") == null){
			if(self.getBoolean("isServlet")){
				HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
				
				Map<String, Object> data = new HashMap<String, Object>();
				for(Object key : request.getParameterMap().keySet()){
					String p = key.toString();
					data.put(p, request.getParameter(p));
				}
				
				return data;
			}else{
				return null;
			}
		}else{
			return UtilData.getObject(self, "conditionData", actionContext);
		}
	}
	
	public static Object getConditionConfig(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Object config = self.getObject("conditionConfig", actionContext);//OgnlUtil.getValue(self, "conditionConfig", actionContext);
		if(config == null && self.getStringBlankAsNull("conditionConfig") != null){
			config = World.getInstance().getThing(self.getString("conditionConfig"));
		}
		if(config == null){
			config = self.getThing("Condition@0");
		}
		
		return config;
	}
	
	public static Object query(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new ActionException("query: DataObject is null, path=" + self.getMetadata().getPath());
		}
		Object pageInfo = self.doAction("getPageInfo", actionContext);
		if(pageInfo instanceof PageInfo){
			pageInfo = ((PageInfo) pageInfo).getPageInfoData();
		}
		Object conditionData = self.doAction("getConditionData", actionContext);
		Object conditionConfig = self.doAction("getConditionConfig", actionContext);
		if(conditionConfig == null){
			conditionConfig = dataObject.getThing("Condition@0");
		}
		
		DataObject.beginThreadCache();
	    try{
	        return dataObject.doAction("query", actionContext, 
	        		UtilMap.toMap(new Object[]{"conditionData", conditionData, 
	        				"conditionConfig", conditionConfig, "pageInfo", pageInfo}));
	    }finally{
	        DataObject.finishThreadCache();
	    }
	}
	
	public static void iterator(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new ActionException("query: DataObject is null, path=" + self.getMetadata().getPath());
		}
		Object pageInfo = self.doAction("getPageInfo", actionContext);
		if(pageInfo instanceof PageInfo){
			pageInfo = ((PageInfo) pageInfo).getPageInfoData();
		}
		Object conditionData = self.doAction("getConditionData", actionContext);
		Object conditionConfig = self.doAction("getConditionConfig", actionContext);
		if(conditionConfig == null){
			conditionConfig = dataObject.getThing("Condition@0");
		}
		
		DataObject.beginThreadCache();
	    try{
	    	Thing actionThing = self.getActionThing("doIterator");
	    	Action action = null;
	    	if(actionThing != null){
	    		action = actionThing.getAction();
	    	}
	        dataObject.doAction("iterator", actionContext, 
	        		UtilMap.toMap(new Object[]{"conditionData", conditionData, 
	        				"conditionConfig", conditionConfig, "pageInfo", pageInfo, "action", action}));
	    }finally{
	        DataObject.finishThreadCache();
	    }
	}
	
	public static void exportList(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		boolean debug = UtilAction.getDebugLog(self, actionContext);
		
		Iterable<Object> iterable = UtilJava.getIterable(self.doAction("getDataObjectList", actionContext));		
		Iterator<Object> itertor = iterable.iterator();
		Object dataObject = null;
		if(itertor.hasNext()){
			dataObject = itertor.next();
		}else{
			return;
		}
	    try{
	    	//压入一个变量栈，让导出方法有可以保存的变量的地方
	    	Bindings bindings = actionContext.push();
	    	bindings.put("exportContext", bindings);
	    	bindings.put("dataObject", dataObject);
	    	bindings.put("exporter", self);
	    	
	    	String type = self.getStringBlankAsNull("type");
	    	if(type == null){
	    		Executor.warn(TAG, "export type is null, do not export, thing=" + self.getMetadata().getPath());
	    		return;
	    	}
	    	
	    	//iter是变量输出的地方
	    	Action iter = getSelfAction(self, type + "ExportIter");	    	
	    	if(iter == null){
	    		Executor.warn(TAG, "must implement " + type + "ExportIter method, thing=" + self.getMetadata().getPath());
	    		return;
	    	}
	    	
	    	try{
	    		if(debug){
	    			Executor.info(TAG, type + "ExportInit");
	    		}
		    	self.doAction(type + "ExportInit", actionContext);
		    	if(debug){
		    		Executor.info(TAG, type + "iterator");
		    	}
		    	itertor = iterable.iterator();
		    	while(itertor.hasNext()){
		    		bindings.put("data", itertor.next());
		    		iter.run(actionContext);
		    	}		        
	    	}finally{
	    		self.doAction(type + "ExportEnd", actionContext);
	    		if(debug){
	    			Executor.info(TAG, type + "ExportEnd");
	    		}
	    	}
	    }finally{
	    	actionContext.pop();
	        DataObject.finishThreadCache();
	    }
	}
	
	public static void export(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		boolean debug = UtilAction.getDebugLog(self, actionContext);
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		if(dataObject == null){
			throw new ActionException("query: DataObject is null, path=" + self.getMetadata().getPath());
		}
		Object pageInfo = self.doAction("getPageInfo", actionContext);
		if(pageInfo instanceof PageInfo){
			pageInfo = ((PageInfo) pageInfo).getPageInfoData();
		}
		Object conditionData = self.doAction("getConditionData", actionContext);
		Object conditionConfig = self.doAction("getConditionConfig", actionContext);
		if(conditionConfig == null){
			conditionConfig = dataObject.getThing("Condition@0");
		}
		
		DataObject.beginThreadCache();
	    try{
	    	//压入一个变量栈，让导出方法有可以保存的变量的地方
	    	Bindings bindings = actionContext.push();
	    	bindings.put("exportContext", bindings);
	    	bindings.put("dataObject", dataObject);
	    	Object output = self.doAction("getOutput", actionContext);
	    	if(self.getBoolean("isServlet")) {
	    		if(output == null || output instanceof String) {	    			
	    			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
	    			response.setContentType("application/octet-stream");
	    			response.addHeader("Content-Disposition","attachment; filename=\"" + output + "\"");
	    			output = response.getOutputStream();
	    		}
	    	}
	    	if(output == null) {
	    		Executor.warn(TAG, "output is null, can not export, thing=" + self.getMetadata().getPath());
	    		return;
	    	}
	    	bindings.put("output", output);
	    	
	    	String type = self.getString("type", "csv", actionContext);
	    	if(type == null){
	    		Executor.warn(TAG, "export type is null, do not export, thing=" + self.getMetadata().getPath());
	    		return;
	    	}
	    	
	    	//iter是变量输出的地方
	    	Action iter = getSelfAction(self, type + "ExportIter");	    	
	    	if(iter == null){
	    		Executor.warn(TAG, "must implement " + type + "ExportIter method, thing=" + self.getMetadata().getPath());
	    		return;
	    	}
	    	
	    	try{
	    		if(debug){
	    			Executor.info(TAG, type + "ExportInit");
	    		}
		    	self.doAction(type + "ExportInit", actionContext);
		    	if(debug){
		    		Executor.info(TAG, type + "iterator");
		    	}
		        dataObject.doAction("iterator", actionContext, 
		        		UtilMap.toMap(new Object[]{"conditionData", conditionData, 
		        				"conditionConfig", conditionConfig, "pageInfo", pageInfo, "action", iter, "exporter", self}));
	    	}finally{
	    		self.doAction(type + "ExportEnd", actionContext);
	    		if(debug){
	    			Executor.info(TAG, type + "ExportEnd");
	    		}
	    	}
	    }finally{
	    	actionContext.pop();
	        DataObject.finishThreadCache();
	    }
	}
	
	public static Action getSelfAction(Thing self, String actionName){
		Thing actionThing = self.getActionThing(actionName);
    	Action action = null;
    	if(actionThing != null){
    		action = actionThing.getAction();
    		return action;
    	}else{
    		return null;
    	}
	}
	
	public static void excelExportInit(ActionContext actionContext) throws OgnlException, IOException, RowsExceededException, WriteException{
		Thing self = (Thing) actionContext.get("self");
		Thing dataObject = (Thing) actionContext.get("dataObject");
		Bindings exportContext = (Bindings) actionContext.get("exportContext");
		Object out = exportContext.get("output");	
		if(out == null){
			throw new ActionException("Export excel: output is null, thing=" + self.getMetadata().getPath());
		}
		
		//创建Excel
		WritableWorkbook workbook = null;
		if(out instanceof OutputStream){
			workbook = Workbook.createWorkbook((OutputStream) out);
		}else if(out instanceof File){
			workbook = Workbook.createWorkbook((File) out);
		}else if(out instanceof String){
			workbook = Workbook.createWorkbook(new File((String) out));
		}else{
			throw new ActionException("Exeport excel: output only suppor OutputSream, File or String, currentIs " + out.getClass() + 
					", thing=" + self.getMetadata().getPath());
		}
		WritableSheet sheet = workbook.createSheet(self.getMetadata().getLabel(), 0);
		exportContext.put("workbook", workbook);
		exportContext.put("sheet", sheet);
		exportContext.put("row", 1);
		
		//写入标题
		int col = 0;
		boolean nameHeader = UtilData.isTrue(self.doAction("isUseNameHeader", actionContext));
		for(Thing attr : dataObject.getChilds("attribute")){
			String text = nameHeader ? attr.getMetadata().getName() : attr.getMetadata().getLabel();
			Label label = new Label(col, 0, text);
			sheet.addCell(label);
			col++;
		}
	}
	
	public static void excelExportIter(ActionContext actionContext) throws RowsExceededException, WriteException{
		Thing self = (Thing) actionContext.get("exporter");
		if(UtilData.isTrue(self.doAction("doFilter", actionContext))) {
			return;
		}
		
		Thing dataObject = (Thing) actionContext.get("dataObject");
		Bindings exportContext = (Bindings) actionContext.get("exportContext");
		DataObject data = (DataObject) actionContext.get("data");
		WritableSheet sheet = (WritableSheet) exportContext.get("sheet");
		Integer row = (Integer) exportContext.get("row");
		if(sheet != null){
			int col = 0;
			jxl.write.DateFormat dateFormat = new jxl.write.DateFormat("yyyy-mm-dd"); 
			jxl.write.WritableCellFormat dateCellFormat = new jxl.write.WritableCellFormat(dateFormat); 
			jxl.write.DateFormat timeFormat = new jxl.write.DateFormat("yyyy-mm-dd hh:mm:ss"); 
			jxl.write.WritableCellFormat timeCellFormat = new jxl.write.WritableCellFormat(timeFormat); 
			for(Thing attr : dataObject.getChilds("attribute")){
				String type = attr.getString("type");			
				if("date".equals(type) || "datetime".equals(type)){
					Date value = data.getDate(attr.getMetadata().getName());
					if(value != null){
						jxl.write.WritableCellFormat sf = "date".equals(type) ? dateCellFormat : timeCellFormat;
						DateTime dt = new DateTime(col, row, value, sf);
						sheet.addCell(dt);
					}else{
						sheet.addCell(new Blank(col, row));
					}
				}else{
					String value = data.getString(attr.getMetadata().getName());
					if(value != null){
						Label dt = new Label(col, row, value);
						sheet.addCell(dt);
					}else{
						sheet.addCell(new Blank(col, row));
					}
				}
				col++;
			}
			//创建新的一行
			exportContext.put("row", row + 1);			
		}
	}

	public static void excelExportEnd(ActionContext actionContext) throws WriteException, IOException{
		Bindings exportContext = (Bindings) actionContext.get("exportContext");
		WritableWorkbook workbook = (WritableWorkbook) exportContext.get("workbook");		
		if(workbook != null){
			workbook.write();
			workbook.close();
		}		
	}
	
	public static void csvExportInit(ActionContext actionContext) throws OgnlException, IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing dataObject = (Thing) actionContext.get("dataObject");
		Bindings exportContext = (Bindings) actionContext.get("exportContext");
		
		Object out = exportContext.get("output");		
		if(out == null){
			throw new ActionException("Export excel: output is null, thing=" + self.getMetadata().getPath());
		}
		
		//创建Excel
		OutputStream output = null;
		if(out instanceof OutputStream){
			output = (OutputStream) out;
		}else if(out instanceof File){
			output = new FileOutputStream((File) out);
			exportContext.put("closeOutput", true);
		}else if(out instanceof String){
			output = new FileOutputStream((String) out);
			exportContext.put("closeOutput", true);
		}else{
			throw new ActionException("Exeport Csv: output only suppor OutputSream, File or String, currentIs " + out.getClass() + 
					", thing=" + self.getMetadata().getPath());
		}
		
		CsvWriter writer = new CsvWriter(output, ',', Charset.forName("utf-8"));
		exportContext.put("csvOutput", output);
		exportContext.put("writer", writer);
		exportContext.put("row", 1);
		
		//写入标题
		boolean nameHeader = UtilData.isTrue(self.doAction("isUseNameHeader", actionContext));
		for(Thing attr : dataObject.getChilds("attribute")){
			String label = nameHeader ? attr.getMetadata().getName() : attr.getMetadata().getLabel();
			writer.write(label);
		}
		
		if(UtilAction.getDebugLog(self, actionContext)){
			Executor.info(TAG, "start write to csv");
		}
	}
	
	public static void csvExportIter(ActionContext actionContext) throws IOException{		
		Thing self = (Thing) actionContext.get("exporter");
		Thing dataObject = (Thing) actionContext.get("dataObject");
		Bindings exportContext = (Bindings) actionContext.get("exportContext");
		DataObject data = (DataObject) actionContext.get("data");
		if(UtilData.isTrue(self.doAction("doFilter", actionContext))) {
			return;
		}
		
		CsvWriter writer = (CsvWriter) exportContext.get("writer");		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer row = (Integer) exportContext.get("row");
		if(writer != null){
			writer.endRecord();
			for(Thing attr : dataObject.getChilds("attribute")){
				String type = attr.getString("type");
				String svalue = "";
				if("date".equals(type) || "datetime".equals(type)){
					Date value = data.getDate(attr.getMetadata().getName());
					if(value != null){
						svalue = sf.format(value);
					}
				}else{
					svalue = data.getString(attr.getMetadata().getName());					
				}
				if(svalue == null){
					svalue = "";
				}

				writer.write(svalue);
			}
			
			writer.flush();
			exportContext.put("row", row + 1);	
			
			if(self != null && UtilAction.getDebugLog(self, actionContext) && row % 2000 == 0){
				Executor.info(TAG, "write to csv: rows = " + row);
			}
		}		
		
	}
	
	public static void csvExportEnd(ActionContext actionContext) throws IOException{
		Bindings exportContext = (Bindings) actionContext.get("exportContext");
		CsvWriter writer = (CsvWriter) exportContext.get("writer");
		if(writer != null){
			writer.flush();
			writer.close();
		}
		
		Object closeOutput = exportContext.get("closeOutput");
		if(closeOutput instanceof Boolean && (Boolean) closeOutput == true){
			OutputStream csvOutput = (OutputStream) exportContext.get("csvOutput");
			if(csvOutput != null) {
				csvOutput.close();
			}			
		}
		
		Thing self = (Thing) actionContext.get("self");
		if(UtilAction.getDebugLog(self, actionContext)){
			Executor.info(TAG, "write to csv finshed, total rows=" + exportContext.get("row"));
		}
	}
	
	public static void sqlExportInit(ActionContext actionContext){
		Executor.warn(TAG, "Sql export is not implemented");
	}
	
	public static void sqlExportIter(ActionContext actionContext){
		Executor.warn(TAG, "Sql export is not implemented");
	}
	
	public static void sqlExportEnd(ActionContext actionContext){
		Executor.warn(TAG, "Sql export is not implemented");
	}
	
	public static void ddl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
		Thing dataSource = (Thing) self.doAction("getDataSource", actionContext);
		
		if(dataObject == null){
			Executor.warn(TAG, "DDL: dataObject is null, action=" + self.getMetadata().getPath());
			return;
		}
		if(dataSource == null){
			Executor.warn(TAG, "DDL: dataSource is null, action=" + self.getMetadata().getPath());
			return;
		}
			
		DbDataObject.mapping2ddl(dataObject, dataSource, actionContext);
	}
		
	public static void main(String args[]){
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			CsvWriter writer = new CsvWriter(bout, ',', Charset.forName("utf-8"));
			writer.setUseTextQualifier(true);
			writer.setForceQualifier(true);
			writer.write("abcd");
			writer.write("1231231223232");
			writer.endRecord();
			writer.flush();
			
			System.out.println(bout.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
