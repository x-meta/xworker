package xworker.rap;

import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

//import xworker.swt.util.SwtUtils;

public class RapActions {
	static Boolean isRWT = null;
	/**
	 * 返回是否运行在Eclpise的RWT环境下。
	 * 
	 * @return
	 */
	public static boolean isRWT() {
		if(isRWT == null) {
			try {
				Thing swt = World.getInstance().getThing("xworker.swt.SWT");
				isRWT = swt.doAction("isRWTWebClient", new ActionContext());
				if(isRWT == null) {
					isRWT = false;
				}
			}catch(Throwable t) {
				//log.error("Check isRWT error", t);
				isRWT = false;
			}
		}
		
		return isRWT;
	}
	
	public static void executeJavaScript(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		if (isRWT()) {
			String code = self.doAction("getCode", actionContext);
			if(code != null) {
				exec(code);
			}
		}

	}
	
	public static void requireCss(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		if(isRWT()) {
			ClientFileLoader loader = RWT.getClient().getService(ClientFileLoader.class);
			List<String> urls = self.doAction("getUrls", actionContext);
			for(String url : urls) {
				loader.requireCss(url);
			}
		}
	}

	public static void requireJs(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		if(isRWT()) {
			ClientFileLoader loader = RWT.getClient().getService(ClientFileLoader.class);
			List<String> urls = self.doAction("getUrls", actionContext);
			for(String url : urls) {
				loader.requireJs(url);
			}
		}
	}
	
	private static void exec(String... strings) {
		if(isRWT()) {
			StringBuilder builder = new StringBuilder();
			builder.append("try{");
			for (String str : strings) {
				builder.append(str);
			}
			builder.append("}catch(e){}");
			JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
			executor.execute(builder.toString());
		}
	}
	
	public static void openWindow(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		if(isRWT()) {
			String url = self.doAction("getUrl", actionContext);
			if(url == null) {
				return;
			}
			
			String target = self.doAction("getTarget", actionContext);
			if(target == null || "".equals(target)) {
				target = "";
			}			 
			String specs= self.doAction("getSpecs", actionContext);
			if(specs == null) {
				specs = "";
			}
			
			StringBuilder sb = new StringBuilder("window.open(");
			sb.append("'").append(url).append("', '");
			sb.append(target).append("','").append(specs).append("')");
			String code = sb.toString();
			
			//exec是动作子节点定义的
			Action exec = actionContext.getObject("exec");
			exec.run(actionContext, "code", code);
		}
	}
	
	public static Object setHttpSessionAttribute(ActionContext actionContext) {
		if(isRWT()) {
			Thing self = actionContext.getObject("self");
			
			String attributeName = self.doAction("getAttributeName", actionContext);
			Object attributeValue = self.doAction("getAttributeValue", actionContext);
			
			RWT.getUISession().getHttpSession().setAttribute(attributeName, attributeValue);
			
			return attributeValue;
		}else {
			return null;
		}
	}
	
	public static Object getHttpSessionAttribute(ActionContext actionContext) {
		if(isRWT()) {
			Thing self = actionContext.getObject("self");
			
			String attributeName = self.doAction("getAttributeName", actionContext);
			
			return RWT.getUISession().getHttpSession().getAttribute(attributeName);			
		}else {
			return null;
		}
	}
}
