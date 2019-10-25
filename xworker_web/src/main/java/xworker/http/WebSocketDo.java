package xworker.http;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class WebSocketDo extends WebSocketServlet{

	@Override
	public void configure(WebSocketServletFactory factory) {
		// set a 120 second timeout
        factory.getPolicy().setIdleTimeout(120000);
        
        factory.register(WebSocketHandler.class);
	}

}
