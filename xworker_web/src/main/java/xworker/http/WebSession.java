package xworker.http;

import javax.servlet.http.HttpSession;

import org.xmeta.ui.session.AbstractSession;

public class WebSession extends AbstractSession{
	HttpSession session;
	
	public WebSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

}
