package xworker.startup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void initJars(File file, List<URL> urlList){
		if(!file.exists()){
			return;
		}
		
		if(file.isDirectory()){
			for(File childFile : file.listFiles()){
				initJars(childFile, urlList);
			}
		}else if(file.getName().toLowerCase().endsWith(".jar") || file.getName().toLowerCase().endsWith(".zip")){
			try {
				urlList.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public static void main(String[] args) {
		try {
			if(args.length < 1) {
				System.out.println("Please input main class");
				return;
			}
			
			List<URL> urlList = new ArrayList<URL>();
			initJars(new File("./lib/"), urlList);
			URL[] urls = new URL[urlList.size()];
			urlList.toArray(urls);
			System.out.println(urlList);
			
			URLClassLoader classLoader = new URLClassLoader(urls);
			Class trCls = classLoader.loadClass(args[0]);
			Method method = trCls.getDeclaredMethod("main", new Class[]{String[].class});
			
			//System.out.println("get ThingRunner: " + (System.currentTimeMillis() - start));
			method.invoke(null, new Object[]{args});
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
	}
}
