package xworker.rap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ui.session.AbstractSession;

import xworker.http.WebSession;
import xworker.http.WebSessionManager;

public class RapSession extends AbstractSession{
	Shell rootShell;
	Composite rootComposite;
	Map<String, Object> attributes = new HashMap<String, Object>();
	Locale locale = null;

	public RapSession() {		
	}
	
	@Override
	public Object getAttribute(String name) {
		WebSession webSession = getWebSession();
		if(webSession != null) {
			return webSession.getAttribute(name);
		}else {
			return attributes.get(name);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		WebSession webSession = getWebSession();
		if(webSession != null) {
			webSession.setAttribute(name, value);
		}
		
		attributes.put(name, value);
	}

	public static WebSession getWebSession() {
		HttpSession httpSession = RWT.getUISession().getHttpSession();
		if(httpSession != null) {
			WebSession session = (WebSession) httpSession.getAttribute(WebSessionManager.KEY);
			if(session == null) {
				session = new WebSession(httpSession);
				httpSession.setAttribute(WebSessionManager.KEY, session);
			}
			
			return session;
		}else {
			return null;
		}
	}
	
	@Override
	public Locale getLocale() {
		WebSession webSession = getWebSession();
		if(webSession != null) {
			return webSession.getLocale();
		}
		
		if(locale == null) {
			try {
				locale =  RWT.getLocale();
			}catch(Exception e) {
				locale = Locale.getDefault();
			}
		}
		
		return locale;
	}

	@Override
	public void setLocale(Locale locale) {
		WebSession webSession = getWebSession();
		if(webSession != null) {
			webSession.setLocale(locale);
		}
		
		this.locale = locale;
		try {
			RWT.setLocale(locale);
		}catch(Exception e) {			
		}
	}
	
	public Shell getRootShell() {
		return rootShell;
	}
	
	/**
	 * 是一个FillLayout的Composite，在XWorkerEntryPoint中设置。
	 * @return
	 */
	public Composite getRootComposite() {
		return rootComposite;
	}

	public void setRootComposite(Composite rootComposite) {
		this.rootComposite = rootComposite;
	}

	public void setRootShell(Shell rootShell) {
		this.rootShell = rootShell;
	}
	
	
}
