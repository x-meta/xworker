package xworker.lang.flow.uiflow.nodes.dataobject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.csvreader.CsvWriter;

import xworker.dataObject.DataObject;
import xworker.lang.flow.uiflow.IFlow;

public class ListToCsv {
	/**
	 * 把数据对象列表导出到Csv中的方法。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public static void flowRun(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		IFlow uiFlow = actionContext.getObject("uiFlow");
		
		//要保存的数据对象列表
		Object dataObj =  self.doAction("getDataObjectList", actionContext);
		List<DataObject> dataObjectList = null;
		if(dataObj instanceof DataObject){
			dataObjectList = new ArrayList<DataObject>();
			dataObjectList.add((DataObject) dataObj);
		}else{
			dataObjectList = (List<DataObject>) dataObj;
		}
		if(dataObjectList == null){
			uiFlow.log("DataObject list is null, node=" + self.getMetadata().getPath());
			uiFlow.nodeFinished(self, "NODATAOBJECTLIST");
			return;
		}
		
		//csv目标文件
		File csvFile = null;
		Object cf = self.doAction("getCsvFile", actionContext);
		if(cf instanceof File){
			csvFile = (File) cf;
		}else if(cf != null){
			csvFile = new File(String.valueOf(cf));
		}else{
			uiFlow.log("CsvFile is null, node=" + self.getMetadata().getPath());
			uiFlow.nodeFinished(self, "NOCSVFILE");
			return;
		}
				
		//是否是追加模式		
		boolean append = (Boolean) self.doAction("getAppend", actionContext);
		FileWriter fw = new FileWriter(csvFile, append);
		try{
			if(dataObjectList.size() > 0){
				String delimiter = self.getStringBlankAsNull("delimiter");
				char deli = ',';
				if(delimiter != null){
					delimiter = delimiter.trim();
					if(!"".equals(delimiter)){
						if("\\t".equals(delimiter)){
							deli = '\t';
						}else{
							deli = delimiter.charAt(0);							
						}
					}
				}
				CsvWriter writer = new CsvWriter(fw, deli);
				Thing dataObject = dataObjectList.get(0).getMetadata().getDescriptor();
								
				//写入头
				if(append == false && (Boolean) self.doAction("getHead", actionContext)){
					for(Thing attr : dataObject.getChilds("attribute")){
						writer.write(attr.getMetadata().getLabel());						
					}
					writer.endRecord();
				}
				
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//写入数据				
				for(DataObject data : dataObjectList){
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
					writer.endRecord();
				}
				writer.flush();
				writer.close();
			}
		}finally{
			fw.close();
		}
		
		uiFlow.nodeFinished(self, "OK");
	}
}
