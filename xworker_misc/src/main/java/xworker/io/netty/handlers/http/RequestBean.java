package xworker.io.netty.handlers.http;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class RequestBean {
	private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed
    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
                                                         // on exit (in normal
                                                         // exit)
        DiskFileUpload.baseDirectory = null;             // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true;  // should delete file on
                                                         // exit (in normal exit)
        DiskAttribute.baseDirectory = null;              // system temp directory
    }
    
	FullHttpRequest request;
	Map<String, Object> params = new HashMap<String, Object>();
	HttpPostRequestDecoder postDecoder;
	Set<Cookie> cookies;	
	String contextPath;
	 
	public void destroy() {
		if(postDecoder != null) {
			postDecoder.destroy();
		}
	}
	
	private RequestBean(FullHttpRequest request, Map<String, Object> params, 
			Set<Cookie> cookies, HttpPostRequestDecoder postDecoder) {
		this.request = request;
		this.params = params;
		this.postDecoder = postDecoder;
		this.cookies = cookies;		
	}
	
	public FullHttpRequest getRequest() {
		return request;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
		if(this.contextPath != null && this.contextPath.endsWith("/")) {
			this.contextPath = this.contextPath.substring(0, this.contextPath.length() - 1);
		}
	}

	public Set<Cookie> getCookies(){
		return cookies;
	}
	
	public Cookie getCookie(String name) {
		for(Cookie cookie : cookies) {
			if(cookie.name().equals(name)) {
				return cookie;
			}
		}
		
		return null;
	}
	
	public String getCookieValue(String name) {
		Cookie cookie = getCookie(name);
		if(cookie != null) {
			return cookie.value();
		}else {
			return null;
		}
	}
	
	public Object get(String name) {
		return params.get(name);
	}
	
	public FileUpload getFileUpload(String name) {
		Object value = params.get(name);
		if(value instanceof FileUpload) {
			return (FileUpload) value;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String getParameter(String name) {
		Object value = params.get(name);
		if(value instanceof String) {
			return (String) value;
		}else if(value instanceof List<?>) {
			List<String> values = (List<String>) value;
			return values.toString();
		}else {
			return null;
		}
	}
	
	public static RequestBean parse(FullHttpRequest request) throws IOException {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		HttpPostRequestDecoder postDecoder = null;
		QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
		decoder.parameters().entrySet().forEach(entry -> {
			List<String> values = entry.getValue();
			if (values.size() == 1) {
				paramMap.put(entry.getKey(), values.get(0));
			} else {
				paramMap.put(entry.getKey(), values);
			}
		});
		
		HttpMethod method = request.method();
		if(HttpMethod.POST == method) {
			//FileUpload小于一定大小保存到内存，大的内容保存到磁盘
            postDecoder = new HttpPostRequestDecoder(factory, request);
	        try {
	            postDecoder.offer(request);
	
	            List<InterfaceHttpData> parmList = postDecoder.getBodyHttpDatas();
	
	            for (InterfaceHttpData parm : parmList) {
	            	HttpDataType type = parm.getHttpDataType();
	            	if(type == HttpDataType.Attribute) {
		                Attribute data = (Attribute) parm;
		                paramMap.put(data.getName(), data.getValue());
	            	}else if(type == HttpDataType.FileUpload) {
	            		FileUpload fileUpload = (FileUpload) parm;
	            		paramMap.put(fileUpload.getName(), fileUpload);
	            	}else if(type == HttpDataType.InternalAttribute) {
	            		Attribute data = (Attribute) parm;
	            		paramMap.put(data.getName(), data.getValue());
	            	}
	            }
            }catch(IOException e) {
            	postDecoder.destroy();
            	
            	throw e;
            }
		}
		
		Set<Cookie> cookies;
        String value = request.headers().get(HttpHeaderNames.COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = ServerCookieDecoder.STRICT.decode(value);
        }
        
		return new RequestBean(request, paramMap, cookies, postDecoder);
	}

}
