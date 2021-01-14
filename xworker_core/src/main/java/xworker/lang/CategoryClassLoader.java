package xworker.lang;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingClassLoader;

import xworker.lang.executor.Executor;

public class CategoryClassLoader {
	private static final String TAG = CategoryClassLoader.class.getName();
	
	ThingClassLoader classLoader;
	Thing thing;
	ActionContext actionContext;
	
	public CategoryClassLoader(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	
	public ThingClassLoader getClassLoader() {
		if(classLoader == null) {
			try {
				init(false);
			} catch (MalformedURLException e) {
			}
		}
		
		return classLoader;
	}


	public void setClassLoader(ThingClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	public Thing getThing() {
		return thing;
	}


	public ActionContext getActionContext() {
		return actionContext;
	}

	public void apply() {
		Executor.info(TAG, "Apply CategoryClassLoader " + thing.getMetadata().getPath());
		
		//通过模型获取要设置的Category
		List<Thing> things = thing.doAction("getThingsForCategory", actionContext);
		for(Thing th : things) {
			Category category = th.getMetadata().getCategory();
			if(category != null) {
				Executor.info(TAG, "Set classLoder to " + category.getName());
				category.setClassLoader(classLoader);
			}
		}
		
		//应用到其它ClassLoader
		List<Thing> targetClassLoaders = thing.doAction("getTargetClassLoaders", actionContext);
		for(Thing target : targetClassLoaders) {
			CategoryClassLoader cl = target.doAction("getCategoryClassLoader", actionContext);
			if(cl != null) {
				Executor.info(TAG, "Apply classLoder to classLoader " + cl.thing.getMetadata().getPath());
				cl.setClassLoader(classLoader);
				cl.apply();
			}
		}		
		Executor.info(TAG, "ClassLoader apply finished");
	}
	
	public void init(boolean force) throws MalformedURLException {
		Executor.info(TAG, "Init CategoryClassLoader, force=" + force + ", path=" + thing.getMetadata().getPath());
		if(classLoader != null && !force) {
			Executor.info(TAG, "ClassLoader has inited");
			return;
		}
		
		ClassLoader parent = thing.doAction("getParentClassLoader", actionContext);		
		List<String> classPaths = thing.doAction("getClassPaths", actionContext);
		Executor.info(TAG, "Parent=" + parent);
		if(classPaths != null && classPaths.size() > 0) {
			URL[] urls = new URL[classPaths.size()];
			for(int i=0; i<classPaths.size(); i++) {
				urls[i] = new File(classPaths.get(i)).toURI().toURL();
			}
			
			if(parent == null) {
				classLoader = new ThingClassLoader(urls);
			}else {
				classLoader = new ThingClassLoader(urls, thing.getMetadata().getThingManager().getClassLoader());
			}
		}else {
			if(parent == null) {
				classLoader = new ThingClassLoader(new URL[] {});
			}else {
				classLoader = new ThingClassLoader(new URL[] {}, thing.getMetadata().getThingManager().getClassLoader());
			}
		}
		
		for(Thing child : thing.getChilds()) {
			Executor.info(TAG, "Init child, path=" + child.getMetadata().getPath());
			child.doAction("run", actionContext, "classLoader", classLoader);
		}			
		
		Executor.info(TAG, "ClassLoader has inited");
	}
	
	public void copyToXWorker() throws IOException {
		init(false);
		
		String path = World.getInstance().getPath() + "/lib/ccl/" + thing.getMetadata().getPath();
		Executor.info(TAG, "Copy liberaries to " + path);
		for(URL url : classLoader.getURLs()) {
			String fileName = url.getFile();
			if(fileName != null) {
				File srcFile = new File(fileName);
				if(srcFile.exists()) {
					if(srcFile.isFile()) {
						File target = new File(path + "/" + srcFile.getName());
						Executor.info(TAG, "Copping file " + srcFile.getAbsolutePath() + " to " + target.getAbsolutePath());
						FileUtils.copyFile(srcFile, target);
					}else if(srcFile.isDirectory()) {
						Executor.warn(TAG, "Copping to xworker not surrport directory, " + srcFile.getAbsolutePath());
					}
				}else {
					Executor.warn(TAG, "File not exists, " + fileName);
				}
			}else {
				Executor.warn(TAG, "URL is not a file, " + url);
			}
		}
		Executor.warn(TAG, "ClassLoader copy to XWorker finished");
	}
		
	public static void run(ActionContext actionContext) throws MalformedURLException {		
		apply(actionContext);
	}
	
	public static void copyToXWorker(ActionContext actionContext) throws IOException {
		CategoryClassLoader cl = getCategoryClassLoader(actionContext);
		cl.copyToXWorker();
	}
	
	public static ClassLoader getParentClassLoader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		if(self.getBoolean("noParent")) {
			return null;
		}
		
		Thing classLoaderThing = self.doAction("getParentClassLoaderThing", actionContext);
		if(classLoaderThing != null) {
			CategoryClassLoader ccl = classLoaderThing.doAction("getCategoryClassLoader", actionContext);
			if(ccl != null) {
				return ccl.getClassLoader();
			}
		}
		
		return self.getMetadata().getThingManager().getClassLoader();
	}
	
	public static CategoryClassLoader getCategoryClassLoader(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		synchronized(self) {
			String key = "ClassLoader";
			CategoryClassLoader cl = self.getStaticData(key);
			if(cl == null) {
				cl = new CategoryClassLoader(self, actionContext);
				self.setStaticData(key, cl);
			}
			
			return cl;
		}
	}
	
	public static ThingClassLoader getClassLoader(ActionContext actionContext) throws MalformedURLException {
		CategoryClassLoader cl = getCategoryClassLoader(actionContext);
		ThingClassLoader loader = cl.getClassLoader();
		if(loader == null) {
			cl.init(false);
			loader = cl.getClassLoader();
		}
		
		return loader;
	}
	
	public static void apply(ActionContext actionContext) throws MalformedURLException {	
		CategoryClassLoader cl = getCategoryClassLoader(actionContext);
		cl.init(false);
		cl.apply();
	}
	
	public static void applyForce(ActionContext actionContext) throws MalformedURLException {	
		CategoryClassLoader cl = getCategoryClassLoader(actionContext);
		cl.init(true);
		cl.apply();
	}
	
	public static void mavenPomDependencies(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String pomFile = self.doAction("getPomFile", actionContext);
		String mvnCommand = self.doAction("getMvnCommand", actionContext);
		ThingClassLoader classLoader = actionContext.getObject("classLoader");		
		
		File output = new File(World.getInstance().getPath() + "/work/libs/" + self.getMetadata().getCategory().getName().replace('.','/'));
		if(output.exists()) {
			for(File file : output.listFiles()) {
				file.delete();
			}
		}else {
			output.getParentFile().mkdirs();
		}
		
		//执行拷贝
		self.doAction("copy", actionContext, "mvnCommand", mvnCommand, "pomFile", pomFile, "output", output.getAbsolutePath());
		
		//初始化库
		classLoader.addJarOrZip(output);
		
	}
	
	public static void mavenDependencies(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String dependencies = self.doAction("getDependencies", actionContext);
		String mvnCommand = self.doAction("getMvnCommand", actionContext);
		ThingClassLoader classLoader = actionContext.getObject("classLoader");		
		
		File output = new File(World.getInstance().getPath() + "/work/libs/" + self.getMetadata().getCategory().getName().replace('.','/'));
		if(output.exists()) {
			for(File file : output.listFiles()) {
				file.delete();
			}
		}else {
			output.getParentFile().mkdirs();
		}
		
		//执行拷贝
		self.doAction("copy", actionContext, "mvnCommand", mvnCommand, "dependencies", dependencies, "output", output.getAbsolutePath());
		
		//初始化库
		classLoader.addJarOrZip(output);
		
	}
}
