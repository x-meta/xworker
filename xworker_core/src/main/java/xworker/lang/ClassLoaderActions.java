package xworker.lang;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

public class ClassLoaderActions {
	public static ClassLoader getClassLoader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String key = "ClassLoader";
		ClassLoader loader = self.getStaticData(key);
		if(loader != null) {
			return loader;
		}else {
			List<String> paths = self.doAction("getClassPaths", actionContext); 
			List<URL> urls = new ArrayList<URL>();
			if(paths != null) {
				for(String path : paths) {
					path = path.trim();
					if("".equals(path)) {
						continue;
					}
					
					try {
						urls.add(new File(path).toURI().toURL());
					}catch(Exception e) {									
					}
				}
			}
			
			ClassLoader parent = self.doAction("getParentClassLoader", actionContext);
			if(parent == null && UtilData.isTrue(self.doAction("isWorldClassLoader", actionContext))){
				parent = World.getInstance().getClassLoader();				
			}
						
			if(urls.size() > 0) {
				URL[] uls = new URL[urls.size()];
				urls.toArray(uls);
				
				if(parent == null) {
					loader = new URLClassLoader(uls);
				}else {
					loader = new URLClassLoader(uls, parent);
				}
			}else {
				loader = parent;
			}
			
			self.setCachedData(key, loader);
			return loader;
		}
	}
	
	public static void clearClassLoader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		self.setStaticData("ClassLoader", null);
	}
}
