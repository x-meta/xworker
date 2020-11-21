package xworker.io.netty.handlers.http;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

@Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	ThingEntry thingEntry;
	ActionContext actionContext;
	List<Handler> handlers = null;

	public HttpServerHandler(Thing thing, ActionContext actionContext) {
		this.thingEntry = new ThingEntry(thing);
		this.actionContext = actionContext;
	}
	
	private void init() {
		if(handlers == null || thingEntry.isChanged()) {
			List<Handler> list = new ArrayList<Handler>();
			
			Thing thing = thingEntry.getThing();
			for(Thing child : thing.getChilds("HttpHandler")) {
				list.add(new Handler(child.getStringBlankAsNull("pathRegex"), child));
			}
			
			handlers = list;
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		//final boolean keepAlive = HttpUtil.isKeepAlive(request);
		final String uri = request.uri();
		final String path = sanitizeUri(uri);
		if (path == null) {
			this.sendError(request, ctx, FORBIDDEN);
			return;
		}
		
		init();
		
		ActionContext actionContext = new ActionContext();
		actionContext.put("parentContext", actionContext);
		//解析参数等放到requestBean中
		RequestBean requestBean = RequestBean.parse(request);
		
		try {
			HttpSession session = HttpSessionManager.getHttpSession(ctx, request, requestBean);
			actionContext.put("requestBean", requestBean);
			actionContext.put("request", request);
			actionContext.put("ctx", ctx);
			actionContext.put("requestMethod", request.method().name());
			actionContext.put("session", session);
			actionContext.put("uri", uri);
			actionContext.put("world", World.getInstance());
			List<Cookie> responseCookies = new ArrayList<Cookie>();
			actionContext.put("responseCookies", responseCookies);
			
			HttpResponse response = null;
			for(Handler handler : handlers) {
				//找到匹配的Handler并处理
				if(handler.accept(path)) {
					response = handler.doRequest(ctx, request, actionContext);
					break;
				}
			}
			
			if(response == null) {
				this.sendError(request, ctx, FORBIDDEN);
				return;
			}
			
			if(response != null) {
				if(response instanceof FullHttpResponse) {
					//加入SESSIONID的cookie
					Cookie cookie = new DefaultCookie(HttpSession.SESSIONID, session.getSessionId());
					cookie.setMaxAge( 15 * 60);
					response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
				    
					for(Cookie cke : responseCookies) {
						response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cke));
					}
					sendAndCleanupConnection(request, ctx, (FullHttpResponse) response);
				}
			}
		}finally {
			requestBean.destroy();
		}
		
	}

	private static String sanitizeUri(String uri) {
		// Decode the path.
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}

		if (uri.isEmpty() || uri.charAt(0) != '/') {
			return null;
		}

		return uri;
	}

	private void sendError(FullHttpRequest request, ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
				Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

		this.sendAndCleanupConnection(request, ctx, response);
	}

	/**
	 * If Keep-Alive is disabled, attaches "Connection: close" header to the
	 * response and closes the connection after the response being sent.
	 */
	private void sendAndCleanupConnection(FullHttpRequest request, ChannelHandlerContext ctx,
			FullHttpResponse response) {
		final boolean keepAlive = HttpUtil.isKeepAlive(request);
		HttpUtil.setContentLength(response, response.content().readableBytes());
		if (!keepAlive) {
			// We're going to close the connection as soon as the response is sent,
			// so we should also make it clear for the client.
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		} else if (request.protocolVersion().equals(HTTP_1_0)) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}

		ChannelFuture flushPromise = ctx.writeAndFlush(response);

		if (!keepAlive) {
			// Close the connection as soon as the response is sent.
			flushPromise.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelUnregistered(ctx);
	}

	public static HttpServerHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new HttpServerHandler(self, actionContext);
	}
	
	static class Handler{
		Pattern pattern;
		Thing thing;
		
		public Handler(String regex, Thing thing) {
			if(regex != null && !"".equals(regex) && !"/".equals(regex)) {
				pattern = Pattern.compile(regex);
			}
			
			this.thing = thing;
		}
		
		public FullHttpResponse doRequest(ChannelHandlerContext ctx, FullHttpRequest request, ActionContext actionContext) {
			return thing.doAction("doRequest", actionContext);
		}
		
		public boolean accept(String path) {
			if(pattern == null) {
				return true;
			}else {
				return pattern.matcher(path).matches();
			}
		}
	}
}
