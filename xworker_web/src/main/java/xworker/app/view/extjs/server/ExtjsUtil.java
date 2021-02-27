package xworker.app.view.extjs.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.xmeta.ActionContext;

import xworker.util.JacksonFormator;

public class ExtjsUtil {
	/**
	 * 向HttpServletResponse写入符合form回复。
	 * 
	 * 
	 * @param success
	 * @param msg
	 * @param record
	 * @param actionContext
	 * @throws Exception 
	 */
	public static void writeFormResponse(boolean success, String msg, Object record, ActionContext actionContext) throws Exception{
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String recordJson = null;
		if(record != null){
			//Thing jsonFactory = World.getInstance().getThing("xworker.text.JsonDataFormat");
			recordJson = JacksonFormator.formatObject(record);//(String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data",record}));
		}
		
		if(recordJson == null){
			recordJson = "{}";
		}
		if(ServletFileUpload.isMultipartContent(request)){
        	//extjs提交文件是使用iframe，contentType必须是text/html
        	response.setContentType("text/html; charset=utf-8");
        }else{
        	response.setContentType("text/plain; charset=utf-8");
        }
		String code = "{\n" + 
	        	"\"success\":" + success + ",\n" + 
	        	"\"msg\":\"" +msg + "\",\n" + 
	        	"\"data\":" + recordJson + "\n" + 
	        	"}";
		response.getWriter().println(code);
	}
}
