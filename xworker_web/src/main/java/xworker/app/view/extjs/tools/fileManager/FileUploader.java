package xworker.app.view.extjs.tools.fileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.xmeta.ActionContext;

import xworker.http.MultiPartRequest;
import xworker.http.fileupload.FileuploadAction;

public class FileUploader implements FileuploadAction{

	@Override
	public String doService(MultiPartRequest mrequest,
			HttpServletRequest request, HttpServletResponse response,
			ActionContext actionContext) throws ServletException{
		FileItem fileItem = mrequest.getFileItem("file");
		String dirFile = mrequest.getParameter("dirFile");		
		String randomImage = request.getParameter("randomImage");
		try {
			String filePath = fileItem.getName();
			File newFile = null;
			if("true".equals(randomImage)){
				//xworker orgweb特殊的上传方式
				SimpleDateFormat sf = new SimpleDateFormat("yyyy/MMdd/HHmmss");
				filePath = "/files/" + sf.format(new Date()) + fileItem.getName();
				newFile = new File(request.getServletContext().getRealPath("/") + filePath);		
				if(!newFile.getParentFile().exists()){
					newFile.getParentFile().mkdirs();
				}
			}else{
				newFile = new File(dirFile, fileItem.getName());
			}
			
			String msg = "";
			String success = "false";
			if(newFile.exists()){
				msg = "文件已存在！";
			}else{
				FileOutputStream fout = new FileOutputStream(newFile);
				byte[] bytes = new byte[1024];
				InputStream in = fileItem.getInputStream();
				int length = -1;
				while((length = in.read(bytes)) != -1){
					fout.write(bytes, 0, length);
				}
				fout.close();
				msg = "文件" + fileItem.getName() + "上传成功! ";
				success = "true";
			}
			
			if("true".equals(randomImage)){
				response.setContentType("text/html; charset=utf-8");
			}else{
				response.setContentType("text/plain; charset=utf-8");
			}
		
			response.getWriter().println("{\"success\":" + success + ",\"msg\":\"" + msg + "\"" +
					",\"filePath\":\"" + filePath + "\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}

