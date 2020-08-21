package xworker.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.util.UtilData;

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
		Thing typeThing = World.getInstance().getThing("xworker.html.utils.ContentTypes");
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
	
	public static void fileRequestHandler(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		//获取浏览器类型
		String browser=request.getHeader("user-agent");
		// 设置响应头，206支持断点续传
		//响应头
		response.setContentType("application/octet-stream;charset=UTF-8");
		
		long totalLength = self.doAction("getTotalLength", actionContext);
		//下载起始位置
		long since=0;
		//下载结束位置
		long until = totalLength - 1;
		
		
		//获取Range，下载范围
		String range = request.getHeader("range");
		if(range != null){
			//剖解range
			range = range.split("=")[1];
			String[] rs=range.split("-");
			try {
				since = Integer.parseInt(rs[0]);
			}catch(Exception e) {
				since = 0;
			}
			if(rs.length > 1){
				try {
					until=Integer.parseInt(rs[1]);
				}catch(Exception e) {						
				}
			}
		}
		if(until > totalLength) {
			until = totalLength - 1;
		}
		
		if((until - since + 1) == totalLength) {
			//下载全部时，不需要设置206
		}else {		
			int http_status=206;
			if(browser.contains("MSIE"))
				http_status=200;//200 响应头，不支持断点续传
			response.setStatus(http_status);
		}

		//设置响应头
		String fileName = self.doAction("getFileName", actionContext);
		String contentType = null;
		Thing typeThing = World.getInstance().getThing("xworker.html.utils.ContentTypes");
		if(typeThing != null) {
			String type = fileName;
			int index = type.lastIndexOf(".");
			if(index > 0) {
				type = type.substring(index - 1, type.length());
			} else {
				type = null;
			}
			
			if(type != null) {
				List<DataObject> datas = DataObjectUtil.query(typeThing.getMetadata().getPath(), UtilMap.toMap("fileExt", type), actionContext);
				if(datas.size() > 0) {
					contentType = datas.get(0).getString("contentType");
				}
			}
		}
		
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=\"" +
				new String(fileName.getBytes(),"ISO8859_1")+ "\"");
		response.setHeader("Content-Length", "" + (until - since + 1));
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Content-Range", "bytes " + since+"-" + until + "/"	+ totalLength);
		
		
		//写入文件
		self.doAction("write", actionContext, "out", response.getOutputStream(), "offset", since, "length", (until - since + 1));
				
		response.getOutputStream().flush();
		response.getOutputStream().close();
	
	}
	
	public static String fileRequestHandler_getFileName(ActionContext actionContext) throws IOException, TemplateException {
		Thing self = actionContext.getObject("self");
		String fileName = self.getStringBlankAsNull("fileName");
		if(fileName != null) {
			return UtilData.getString(fileName, actionContext);
		}
		
		File file = self.doAction("getFile", actionContext);
		if(file != null) {
			return file.getName();
		} else {
			throw new ActionException("Can not determine file name, getFile() return null.");
		}
	}

	public static long fileRequestHandler_getTotalLength(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		if(file != null) {
			if(file.isDirectory()) {
				throw new ActionException("File is directory, path=" + file.getPath());
			}
			
			return file.length();
		} else {
			throw new ActionException("File not found, getFile() return null");
		}
		
	}
	
	public static void fileRequestHandler_write(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		if(file != null) {
			long offset = actionContext.getObject("offset");
			long length = actionContext.getObject("length");
			OutputStream out = actionContext.getObject("out");
			
			RandomAccessFile fr = new RandomAccessFile(file, "r");
			try {
				fr.seek(offset);				
				byte[] buffer=new byte[128*1024];
				int len;
				//boolean full=false;
				//读取，输出流
				while(length > 0 && (len = fr.read(buffer)) > 0){
					if(len > length){
						len = (int) length;
						length = 0;
					} else {
						length = length - len;
					}
					out.write(buffer, 0, len);
				}
			}finally {
				if(fr != null) {
					fr.close();
				}
			}
		}
	}
}

