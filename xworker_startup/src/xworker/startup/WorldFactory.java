package xworker.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorldFactory {
	/**
	 * 通过.dml项目文件创建一个World对象。
	 * 
	 * @param dmlPrj
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("resource")
	public static Object create(File prjDir) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Properties p = new Properties();
		p.load(new FileInputStream(new File(prjDir, ".dml")));
		
		String worldPath = p.getProperty("world");
		if(worldPath == null) {
			worldPath = Startup.getHomeFormSytsem();
		}
		
		if(worldPath == null) {
			return null;
		}
		
		//初始化系统框架
		Startup.initOS();
		
		URL[] urls = null;
		List<URL> urlList = new ArrayList<URL>();
		File localConfig = new File(prjDir, "config/");
		if(localConfig.exists() && localConfig.isDirectory()){
			urlList.add(localConfig.toURI().toURL());
		}			
		
		Startup.initJars(new File(prjDir, "os/lib/lib_"  + Startup.OS), urlList);
		Startup.initJars(new File(prjDir, "os/lib/lib_"  + Startup.OS + "_" + Startup.PROCESSOR_ARCHITECTURE), urlList);
		Startup.initJars(new File(prjDir, "lib/"), urlList);
		Startup.initJars(new File(prjDir, "WEB-INF/lib/"), urlList);
		File webClass = new File(prjDir, "WEB-INF/classes/");
		if(webClass.exists()) {
			urlList.add(webClass.toURI().toURL());
		}
		
		//其次是加载XWorker目录下的类
		urlList.add(new File(worldPath+ "/config/").toURI().toURL());			
		Startup.initJars(new File(worldPath + "/os/lib/lib_"  + Startup.OS), urlList);
		Startup.initJars(new File(worldPath + "/os/lib/lib_"  + Startup.OS + "_" + Startup.PROCESSOR_ARCHITECTURE), urlList);
		Startup.initJars(new File(worldPath + "/lib/"), urlList);
		urls = new URL[urlList.size()];
		urlList.toArray(urls);
		
		//URLClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		URLClassLoader classLoader = new URLClassLoader(urls, null);
		
		Class<?> worldClass = classLoader.loadClass("org.xmeta.World");
		Method ins = worldClass.getDeclaredMethod("getInstance", new Class<?>[0]);
		Object world = ins.invoke(null, new Object[0]);
		Method init = worldClass.getDeclaredMethod("init", new Class<?>[]{String.class});
		init.invoke(world, new Object[] {worldPath});
		return world;
	}	
	
	public static void main(String[] args) {
		try {
			Object world = create(new File("d:\\temp\\dmlprj2\\"));
			System.out.println(world);
			
			Object world2 = create(new File("d:\\temp\\dmlprj1\\"));
			System.out.println(world2);
			
			System.out.println(world == world2);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
