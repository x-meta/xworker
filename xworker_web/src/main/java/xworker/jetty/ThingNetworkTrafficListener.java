package xworker.jetty;

import java.net.Socket;
import java.nio.ByteBuffer;

import org.eclipse.jetty.io.NetworkTrafficListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingNetworkTrafficListener implements NetworkTrafficListener{
	Thing thing;
	ActionContext actionContext;
	
	public ThingNetworkTrafficListener(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	@Override
	public void opened(Socket socket) {
		thing.doAction("opened", actionContext, "socket", socket);
	}

	@Override
	public void incoming(Socket socket, ByteBuffer bytes) {
		thing.doAction("incoming", actionContext, "socket", socket, "bytes", bytes);
	}

	@Override
	public void outgoing(Socket socket, ByteBuffer bytes) {
		thing.doAction("outgoing", actionContext, "socket", socket, "bytes", bytes);
	}

	@Override
	public void closed(Socket socket) {
		thing.doAction("closed", actionContext, "socket", socket);
	}

}
