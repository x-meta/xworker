package xworker.html.module;

import java.io.IOException;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import freemarker.template.TemplateException;
import xworker.html.HtmlActions;

public class ModuleActions {
	public static boolean accept(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String module = actionContext.getObject("module");
		String version = actionContext.getObject("version");
		String myModule = self.doAction("getModule", actionContext);
		String myVersion = self.doAction("getVersion", actionContext);
		
		if(module != null && version != null && myModule != null && myVersion != null) {
			if(module.equals(myModule)) {
				String vs[] = version.split("[.]");
				String myvs[] = myVersion.split("[.]");
				if(vs[0].equals(myvs[0])) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static String getResourceKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String module = self.doAction("getModule", actionContext);
		String version = self.doAction("getVersion", actionContext);
		
		return module + "-" + version;
		
	}
	
	public static int compareVersion(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String version = actionContext.getObject("version");
		String myVersion = self.doAction("getVersion", actionContext);
		
		if(version == null && myVersion == null) {
			return 0;
		}else if(version == null) {
			return 1;
		}else if(myVersion == null) {
			return -1;
		}else {
			String vs[] = version.split("[.]");
			String myvs[] = myVersion.split("[.]");
			for(int i=0; i<vs.length; i++) {
				if(myvs.length <= i) {
					return -1;
				}else {
					try {
						int v = Integer.parseInt(vs[i]);
						int my = Integer.parseInt(myvs[i]);
						int r = my - v;
						if(r != 0) {
							return r;
						}
					}catch(Exception e) {
						int r = vs[i].compareTo(myvs[i]);
						if(r != 0) {
							return r;
						}
					}
				}
			}
			
			return 0;
		}
	}
	
	public static void toHtml(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");		
		List<ModuleProvider> moduleProviders = actionContext.getObject("moduleProviders");
		
		if(moduleProviders != null) {
			boolean have = false;
			for(ModuleProvider mp : moduleProviders) {
				have = mp.equals(self);
				if(have) {
					break;
				}
			}
			
			if(!have) {
				moduleProviders.add(new ModuleProvider(self, actionContext));
			}
		}	
	}
	
	public static void init(ActionContext actionContext) throws IOException, TemplateException {
		Thing self = actionContext.getObject("self");
		String resourceKey = self.doAction("getResourceKey", actionContext);
		if(resourceKey != null) {
			String headers = self.doAction("getHeaders", actionContext);
			String bottoms = self.doAction("getBottoms", actionContext);
			if(headers != null) {
				HtmlActions.addHead(self, resourceKey, headers, actionContext);
			}
			
			if(bottoms != null) {
				HtmlActions.addBottom(resourceKey, bottoms, actionContext);
			}
		}
	}
}
