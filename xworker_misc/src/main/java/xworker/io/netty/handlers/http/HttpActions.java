package xworker.io.netty.handlers.http;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public class HttpActions {
	public static FullHttpResponse sendRedirect(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String url = self.doAction("getUrl", actionContext);
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND, Unpooled.EMPTY_BUFFER);
        response.headers().set(HttpHeaderNames.LOCATION, url);
        
        return response;
	}
	
	public static void addCookie(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String name = self.doAction("getCookieName", actionContext);
		String value = self.doAction("getValue", actionContext);
		if(name != null && value != null) {
			List<Cookie> cookies = actionContext.getObject("responseCookies");
			if(cookies != null) {
				cookies.add(new DefaultCookie(name, value));
			}
		}
	}
}
