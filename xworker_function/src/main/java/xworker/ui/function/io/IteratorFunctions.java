package xworker.ui.function.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class IteratorFunctions {
	/**
	 * 遍历每一行。
	 * 
	 * @param actionContext 变量上下文
	 * @throws IOException IO一场
	 */
	public static Object iteraLine(ActionContext actionContext) throws IOException{
		Object input = actionContext.get("input");
		
		FunctionRequest request = actionContext.getObject("request");
		if(input instanceof String){
			return handleString((String) input, request, actionContext);
		}else if(input instanceof InputStream){
			return handle(new BufferedReader(new InputStreamReader((InputStream) input)), request, actionContext);
		}else if(input instanceof File){
			return handleFile((File) input, request, actionContext);
		}else if(input instanceof Reader){
			if(input instanceof BufferedReader){
				return handle((BufferedReader) input, request, actionContext);
			}else {
				return handle(new BufferedReader((Reader) input), request, actionContext);
			}
		}else{
			throw new ActionException("Can not handle input, input can be (String, InputStraem, File or Reader)");
		}
		
	}
	
	public static Object handle(BufferedReader br, FunctionRequest request, ActionContext actionContext) throws IOException{
		String line = null;
		FunctionParameter param = null;
		Thing handle = null;
		if(request != null){
			//在函数中去handle参数
			param = request.getParameter("handle");
		}else{
			//在静默执行时取handle子事物
			Thing self = actionContext.getObject("self");
			for(Thing child : self.getChilds()){
				if(child.getMetadata().getName().equals("handle")){
					handle = child;
					break;
				}
			}
		}
		
		int count = 0;
		while((line = br.readLine()) != null){
			line = line.trim();
			if("".equals(line)){
				continue;
			}
			
			actionContext.peek().put("line", line);
			if(request != null){				
				if(param.getRequest() != null){					
					param.getRequest().run(actionContext);
					if(UtilData.isTrue(param.getValue())){
						count++;
					}
				}else{
					count++;
				}
			}else{
				if(handle != null){
					Object obj = handle.doAction("doFunction", actionContext);
					if(UtilData.isTrue(obj)){
						count++;
					}
				}else{
					count++;
				}
			}
		}
		
		return count;
	}	
	
	public static Object handleFile(File file, FunctionRequest request, ActionContext actionContext) throws IOException{
		FileInputStream fin = new FileInputStream(file);
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			
			return handle(br, request, actionContext);
		}finally{
			fin.close();
		}
	}
	
	public static Object handleString(String input, FunctionRequest request, ActionContext actionContext) throws IOException{		
		ByteArrayInputStream bin = new ByteArrayInputStream(input.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(bin));
		
		return handle(br, request, actionContext);
	}
}
