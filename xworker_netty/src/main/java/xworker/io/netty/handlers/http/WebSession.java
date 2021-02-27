package xworker.io.netty.handlers.http;

import java.util.Locale;


import org.xmeta.ui.session.AbstractSession;
import org.xmeta.ui.session.Session;

public class WebSession extends AbstractSession{
	HttpSession session;
	Session parent;
	
	public WebSession(Session parent) {
		this.parent = parent;;
	}
	
	public WebSession(HttpSession session) {		
		this.session = session;
	}

	@Override
	public Object getAttribute(String name) {
		if(parent != null) {
			return parent.getAttribute(name);
		}else {
			return session.getAttribute(name);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if(parent != null) {
			parent.setAttribute(name, value);
		}else {
			session.setAttribute(name, value);
		}
	}

	@Override
	public Locale getLocale() {
		if(parent != null) {
			return parent.getLocale();
		}
		return super.getLocale();
	}

	@Override
	public void setLocale(Locale locale) {
		if(parent != null) {
			parent.setLocale(locale);
		}
		super.setLocale(locale);
	}


}
