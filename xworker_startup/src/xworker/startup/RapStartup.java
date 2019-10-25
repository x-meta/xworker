package xworker.startup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RapStartup {
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
	
	public static String getHome(String name){
		name = name.toUpperCase();
		String home = getHomeFromSystem(name);
		if(home == null){
			home = getHomeFromSystem(name.toLowerCase());
		}

		return home;
	}
	
	public static String getHomeFromSystem(String name){
		String home = System.getProperty(name);
		if(home == null){
			home = System.getenv(name);
		}
		
		return home;
	}
	
	public static String testFileHome(String filePath){
		if(new File(filePath).exists()){
			return filePath;
		}else{
			return null;
		}
	}
	
	public static String getHomeFormSytsem(){
		//尝试从系统变量中获取
		String home = getHome("XMETA_HOME");
		if(home == null){
			home =  getHome("XWORKER_HOME");
		}
		if(home == null){
			home = System.getenv("xmeta_home");
		}
		if(home == null){
			home = System.getenv("XMETA_HOME");
		}
		if(home == null){
			home = testFileHome("/usr/local/xworker/");
		}
		
		return home;
	}
	
	public static List<String> getClassThingConfig() throws IOException{
		Properties p = new Properties();
		InputStream fin = Startup.class.getResourceAsStream("/dml.properties");
		if(fin != null){
			p.load(fin);
			
			List<String> list = new ArrayList<String>();
			list.add(p.getProperty("thing"));
			list.add(p.getProperty("action"));
			
			return list;
		}else{
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String args[]){
		try{
			//long start = System.currentTimeMillis();
			URL[] urls = null;
			List<URL> urlList = new ArrayList<URL>();
			String xworkerHome = null;
			if(args.length < 1){//无参数执行，一般是从Jar中执行，不是从xworker的dml.cmd或dml.sh中执行
				//获取系统中设置的XWorker_HOME
				xworkerHome = getHomeFormSytsem();
				if(xworkerHome == null){
					System.out.println("Please set xworker home");
					return;
				}
				
				//获取要执行的事物和法国法
				List<String> dmlCfg = getClassThingConfig();
				if(dmlCfg == null){
					System.out.println("Can not found dml.properties");
					return;
				}
				dmlCfg.add(0, xworkerHome);
				args = new String[dmlCfg.size()];
				dmlCfg.toArray(args);
			}else{
				//System.out.println("XWorker home is: " + args[0]);
				xworkerHome = args[0];
			}
			
			//过滤参数中的引号
			for(int i=0; i<args.length; i++){				
				String arg = args[i];
				if(arg.length() >= 2 && arg.charAt(0) == '"' && arg.charAt(arg.length()) == '"'){
					args[i] = arg.substring(1, arg.length() - 1);
				}
			}
			
			String OS = null;
			String PROCESSOR_ARCHITECTURE = null;
			try{
				OS = System.getenv("OS").toLowerCase();
			}catch(Exception e){
				OS = System.getProperty("os.name").toLowerCase();
			}
			PROCESSOR_ARCHITECTURE = "bit" + System.getProperty("sun.arch.data.model"); //System.getenv("PROCESSOR_ARCHITECTURE").toLowerCase();			
			InputStream osin = Startup.class.getResourceAsStream("/xworker_os.properties");
			if(osin != null){
				Properties p = new Properties();
				p.load(osin);
				osin.close();
				
				String value = p.getProperty(OS);
				if(value != null && !"".equals(value)){
					OS = value;
				}
				value = p.getProperty(PROCESSOR_ARCHITECTURE);
				if(value != null && !"".equals(value)){
					PROCESSOR_ARCHITECTURE = value;
				}
			}			
			//System.out.println("OS=" + OS + ", sun.arch.data.model=" + PROCESSOR_ARCHITECTURE);
			//System.out.println("init os: " + (System.currentTimeMillis() - start));
			//首先默认加载扥地
			File localConfig = new File("./config/");
			if(localConfig.exists() && localConfig.isDirectory()){
				urlList.add(localConfig.toURI().toURL());
			}			
			
			//首先添加rap的类库
			System.out.println("添加RAP类库：" + xworkerHome + "/lib_rap/");
			initJars(new File(args[0] + "/lib_rap"), urlList);
			initJars(new File("./lib_"  + OS), urlList);
			initJars(new File("./lib_"  + OS + "_" + PROCESSOR_ARCHITECTURE), urlList);
			initJars(new File("./lib/"), urlList);
			
			//其次是加载XWorker目录下的类
			urlList.add(new File(args[0] + "/config/").toURI().toURL());
			initJars(new File(args[0] + "/lib_"  + OS), urlList);
			initJars(new File(args[0] + "/lib_"  + OS + "_" + PROCESSOR_ARCHITECTURE), urlList);
			initJars(new File(args[0] + "/lib/"), urlList);
			urls = new URL[urlList.size()];
			urlList.toArray(urls);
			
			//URLClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
			URLClassLoader classLoader = new URLClassLoader(urls);
			/*
			System.out.println("------------------class path-------------------");
			System.out.println(System.getProperty("java.class.path"));
			for(URL url : classLoader.getURLs()){
				System.out.println(url);
			}
			System.out.println("------------------class path-------------------");
				*/	
			
			//System.out.println("init libs: " + (System.currentTimeMillis() - start));

			Thread.currentThread().setContextClassLoader(classLoader);
			Class trCls = classLoader.loadClass("org.xmeta.util.ThingRunner");
			Method method = trCls.getDeclaredMethod("run", new Class[]{String[].class});
			
			//System.out.println("get ThingRunner: " + (System.currentTimeMillis() - start));
			method.invoke(null, new Object[]{args});
			
			//System.out.println("invoke ThingRunner: " + (System.currentTimeMillis() - start));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
