package xworker.statistics.flow.datasaver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.csvreader.CsvWriter;

import xworker.dataObject.DataObject;
import xworker.statistics.flow.DataInputer;
import xworker.util.StringUtils;

public class CsvDataSaver {
	File csvFile;
	char delimiter;
	String charset;
	CsvWriter writer;
	
	public CsvDataSaver(File csvFile, char delimiter, String charset){
		this.csvFile = csvFile;
		this.delimiter = delimiter;
		this.charset = charset;
	}
	
	public void init(){
		writer = new CsvWriter(csvFile.getAbsolutePath(), delimiter, Charset.forName(charset));
	}
	
	public void close(){
		writer.close();
	}
	
	public void save(DataObject data) throws IOException{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Thing attr : data.getMetadata().getDescriptor().getChilds("attribute")){
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
		writer.flush();		
	}
	
	public synchronized static CsvDataSaver getDataSaver(ActionContext actionContext){
		DataInputer dataSaver = actionContext.getObject("dataSaver");
		if(dataSaver.object == null){
			Thing self = actionContext.getObject("self");
			File file = self.doAction("getCsvFile", actionContext);
			String charset = self.doAction("getCharset", actionContext);
			char delimiter = StringUtils.getDelimiter((String) self.doAction("getDelimiter", actionContext));
					
			dataSaver.object = new CsvDataSaver(file, delimiter, charset);
		}
		
		return (CsvDataSaver) dataSaver.object;
	}
	
	public static void init(ActionContext actionContext) throws IOException{
		CsvDataSaver dataSaver = getDataSaver(actionContext);
		dataSaver.init();
	}
	
	public static void close(ActionContext actionContext) throws IOException{
		CsvDataSaver dataSaver = getDataSaver(actionContext);
		dataSaver.close();
	}
	
	public static void save(ActionContext actionContext) throws IOException{
		DataObject data = actionContext.getObject("data");
		if(data != null){
			CsvDataSaver dataSaver = getDataSaver(actionContext);
			dataSaver.save(data);
		}
	}
}
