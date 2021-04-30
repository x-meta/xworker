package xworker.rap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class XWorkerServiceHandler implements ServiceHandler{
	Thing thing;
	ActionContext actionContext;
	
	public XWorkerServiceHandler(Thing thing) {
		this.thing = thing;
		this.actionContext = new ActionContext();
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			thing.doAction("doService", actionContext, "request", request, "response", response);
		}catch(Exception e) {
			throw new ServletException("doService error, path=" + thing.getMetadata().getPath(), e);
		}
	}

}
