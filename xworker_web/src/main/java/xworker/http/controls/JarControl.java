package xworker.http.controls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JarControl {
	public static void httpDo(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		String contentPath = null;
		String uri = request.getRequestURI();
		int index = uri.indexOf("!");
		if(index != -1) {
			contentPath = uri.substring(index, uri.length());
		}
		if(index == -1 || contentPath.equals("")) {
			//非法的路径地址，跳转到默认页
			uri = "/do/" + self.getMetadata().getPath().replace('.', '/') + "!/";
			uri = uri + self.doAction("getDefault", actionContext);
			response.sendRedirect(uri);
		}else {
			if(contentPath.equals("!/")) {
				contentPath = contentPath + self.doAction("getDefault", actionContext);
			}
			String jarUrl = self.doAction("getJarUrl", actionContext);
			URL url = new URL("jar:" + jarUrl + contentPath);
			URLConnection uc = url.openConnection();
			String contentType = uc.getContentType();
			int contentLength = uc.getContentLength();
			response.setContentLength(contentLength);
			if(contentType != null) {
				response.setContentType(contentType);
			}
			
			InputStream in = uc.getInputStream();
			OutputStream out = response.getOutputStream();
			byte[] bytes = new byte[1024 * 10];
			int length = -1;
			while((length = in.read(bytes)) > 0) {
				out.write(bytes, 0, length);
			}
		}
	}	
}
