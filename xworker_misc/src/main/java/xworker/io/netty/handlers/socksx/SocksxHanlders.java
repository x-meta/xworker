package xworker.io.netty.handlers.socksx;

import org.xmeta.ActionContext;

import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;

public class SocksxHanlders {
	public static SocksPortUnificationServerHandler createSocksPortUnificationServerHandler(ActionContext actionContext) {
		return new SocksPortUnificationServerHandler();
	}
}
