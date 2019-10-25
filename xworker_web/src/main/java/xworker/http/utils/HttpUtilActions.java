package xworker.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class HttpUtilActions {
	public static String getIpAddress(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String[] headers = self.doAction("getHeaders", actionContext);
		HttpServletRequest request = actionContext.getObject("request");
		return IpUtils.getIpAddress(request, headers);
	}
	
	public static void writeFile(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		HttpServletResponse response = actionContext.getObject("response");
		
		String contentType = null;
		Thing typeThing = World.getInstance().getThing("xworker.things.p2018.p12.p28.ContentTypes");
		if(typeThing != null) {
			String type = file.getName();
			int index = type.lastIndexOf(".");
			if(index > 0) {
				type = type.substring(index - 1, type.length());
			}
			
			List<DataObject> datas = DataObjectUtil.query(typeThing.getMetadata().getPath(), UtilMap.toMap("fileExt", type), actionContext);
			if(datas.size() > 0) {
				contentType = datas.get(0).getString("contentType");
			}
		}
		
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		response.setContentType(contentType);
		if(file.exists()) {
			response.setContentLengthLong(file.length());
		}
		
		self.doAction("writeFile", actionContext, "file", file);
	}
	
	public static void doWriteFile(ActionContext actionContext) throws IOException {
		HttpServletResponse response = actionContext.getObject("response");
		File file = actionContext.getObject("file");
		FileInputStream fin = new FileInputStream(file);
		try {
			IOUtils.copy(fin, response.getOutputStream());
		}finally {
			fin.close();
		}
	}
	
	public static String readRequestToString(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		String encode = self.doAction("getEncode", actionContext);
		
		HttpServletRequest request = actionContext.getObject("request");
		if(encode == null || "".equals(encode)) {
			return IOUtils.toString(request.getInputStream());
		}else {
			return IOUtils.toString(request.getInputStream(), encode);
		}
	}
	
	public static byte[] readRequestToBytes(ActionContext actionContext) throws IOException {
		HttpServletRequest request = actionContext.getObject("request");
		return IOUtils.toByteArray(request.getInputStream());
	}
}
