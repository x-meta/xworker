package xworker.statistics.flow.inputers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.statistics.flow.DataInputer;

public class FileLineDataInputer {
	DataInputer dataInputer;
	File file;
	String encode;
	byte status = 0; 
	
	public FileLineDataInputer(DataInputer dataInputer, File file, String encode){
		this.dataInputer = dataInputer;
		this.file = file;
		this.encode = encode;
	}
	
	public synchronized void start() throws IOException{
		if(status == 0){
			FileInputStream fin = new FileInputStream(file);
			BufferedReader br = null;
			try{
				status = 1;
				
				if(encode == null || "".equals(encode)){
					br = new BufferedReader(new InputStreamReader(fin));
				}else{
					br = new BufferedReader(new InputStreamReader(fin, encode));
				}
				
				String line = null;
				while((line = br.readLine()) != null){
					dataInputer.input(line);
					
					if(status == 2){
						break;
					}
				}
			
				br.close();
			}finally{
				status = 2;
				fin.close();				
				if(br != null){
					br.close();
				}
				
				dataInputer.inputFinished();
			}						
		}else if(status == 2){
			throw new ActionException("FileLineDataInputer is already stoped, thing=" + dataInputer.thing.getMetadata().getPath());
		}
	}
	
	public void stop(){
		status = 2;
	}
	
	public synchronized static FileLineDataInputer getDataInputer(ActionContext actionContext){
		DataInputer dataInputer = actionContext.getObject("dataInputer");
		if(dataInputer.object == null){
			Thing self = actionContext.getObject("self");
			File file = self.doAction("getFile", actionContext);
			String encode = self.doAction("getEncode", actionContext);
					
			dataInputer.object = new FileLineDataInputer(dataInputer, file, encode);
		}
		
		return (FileLineDataInputer) dataInputer.object;
	}
	
	public static void start(ActionContext actionContext) throws IOException{
		FileLineDataInputer finputer = getDataInputer(actionContext);
		finputer.start();
	}
	
	public static void stop(ActionContext actionContext) throws IOException{
		FileLineDataInputer finputer = getDataInputer(actionContext);
		finputer.stop();
	}
}
