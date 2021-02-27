package xworker.java.assist;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.xmeta.ThingCoder;
import org.xmeta.World;

import xworker.lang.executor.Executor;

/**
 * 获取在类路径下的所有事物的缓存。
 * 
 * @author zyx
 *
 */
public class ClassThingCache {
private static final String TAG = ClassThingCache.class.getName();
	
	/**
	 * Java的Pacakge和Class的缓存。
	 */
	private static List<JavaCacheItem> items = new ArrayList<JavaCacheItem>();
	private static PackageTree packageTree = new PackageTree("");
	
	/**
	 * 获取包的树形结构。
	 * 
	 * @return
	 */
	public static PackageTree getPackageTree(){
		if(items.size() == 0){
			try {
				init();
			} catch (IOException e) {
				Executor.error(TAG, "init class cache error", e);
			}
		}
		
		return packageTree;
	}
	
	/**
	 * 获取全部缓存。
	 * 
	 * @return
	 */
	public static List<JavaCacheItem> getAll(){
		if(items.size() == 0){
			try {
				init();
			} catch (IOException e) {
				Executor.error(TAG, "init class cache error", e);
			}
		}
		
		return items;
	}
	
	public static List<String> getAllPackages(){
		List<String> pks = new ArrayList<String>();
		
		List<JavaCacheItem> allItems = getAll();
		for(JavaCacheItem item : allItems){
			if(item.type == JavaCacheItem.PACKAGE){
				pks.add(item.path);
			}
		}
		
		return pks;
	}
	
	/**
	 * 通过path查找类和包的缓存，使用indexOf，不区分大小写。
	 * @param path
	 * @return
	 */
	public static List<JavaCacheItem> indexOf(String path){
		List<JavaCacheItem> allItems = getAll();
		
		path = path.toLowerCase();
		String[] paths = path.split("[ ]");
		
		List<JavaCacheItem> list = new ArrayList<JavaCacheItem>();
		for(JavaCacheItem item : allItems){
			boolean ok = true;
			String pth = item.path.toLowerCase(); 
			for(String p : paths){
				if(pth.indexOf(p) == -1){
					ok = false;
					break;
				}
			}
			
			if(ok){
				list.add(item);
			}
		}
		
		return list;
	}
	
	/**
	 * 使用正则表达式搜索。
	 * 
	 * @param regex
	 * @return
	 */
	public static List<JavaCacheItem> matchs(String regex){
		List<JavaCacheItem> allItems = getAll();
				
		List<JavaCacheItem> list = new ArrayList<JavaCacheItem>();
		for(JavaCacheItem item : allItems){
			if(item.path.matches(regex)){
				list.add(item);
			}
		}
		
		return list;
	}
	
	
	/**
	 * 通过path查找类和包的缓存，使用startsWith，不区分大小写。
	 * @param path
	 * @return
	 */
	public static List<JavaCacheItem> startsWith(String path){
		List<JavaCacheItem> allItems = getAll();
		
		path = path.toLowerCase();
		List<JavaCacheItem> list = new ArrayList<JavaCacheItem>();
		for(JavaCacheItem item : allItems){
			if(item.path.toLowerCase().startsWith(path)){
				list.add(item);
			}
		}
		
		return list;
	}
	
	private static void addPackage(String packageName){
		items.add(new JavaCacheItem(packageName, JavaCacheItem.PACKAGE));
	}
	
	private static void addClass(String className){
		items.add(new JavaCacheItem(className, JavaCacheItem.CLASS));
	}
	
	/**
	 * 从文件中读取包。
	 * 
	 * @param pkgContext 包上下文
	 * @param file 文件
	 * @param parentPackage 父上下文
	 * @param classContext 类上下文
	 */
	public static void getFormFile(File file, String parentPackage, Map<String, String> pkgContext, Map<String, String> classContext){
		if(file.isDirectory()){						
			String packageName = null;
			if(parentPackage == null){
				packageName = "";
			}else if(parentPackage.equals("")){
				packageName = file.getName();
			}else{
				packageName = parentPackage + "." + file.getName();
			}
			packageName = packageName.replace('/', '.');
			if(packageName.endsWith(".")){
			    packageName = packageName.substring(0, packageName.length() - 1);
			}
			if(pkgContext.get(packageName) == null){
				pkgContext.put(packageName, packageName);
				addPackage(packageName);
			}
				
			for(File childFile : file.listFiles()){
				getFormFile(childFile, packageName, pkgContext, classContext);
			}
		}else{
			String thingName = getThingName(file.getName());
			if(thingName != null){
				if(parentPackage != null && !"".equals(parentPackage)){
					thingName = parentPackage + "." + thingName;
				}
				if(classContext.get(thingName) == null){
					classContext.put(thingName, thingName);
					addClass(thingName.replace('/', '.'));
				}
			}
		}
	}
	
	public static String getThingName(String fileName){
		for(ThingCoder coder : World.getInstance().getThingCoders()){
			String type = coder.getType();
			if(fileName.endsWith(type)){
				return fileName.substring(0, fileName.length() - type.length() - 1);
			}
		}
		
		return null;
	}
	/**
	 * 从jar或者zip中读取。
	 * 
	 * @param file 文件
	 * @param parentPackage 父目录
	 * @param pkgContext 包上下文
	 * @param classContext 类上下文
	 * 
	 * @throws IOException IO异常 
	 */
	public static void getFromZip(File file, String parentPackage, Map<String, String> pkgContext, Map<String, String> classContext) throws IOException{
		JarFile jarFile = new JarFile(file);   
        Enumeration<JarEntry> enumeration = jarFile.entries();   
        while (enumeration.hasMoreElements()) {   
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            if(jarEntry.isDirectory()){
            	String jarEntryName = jarEntry.getName();
            	String packageName = null;
            	if(jarEntryName.lastIndexOf("/") != -1){
            		packageName = jarEntryName.substring(0, jarEntryName.lastIndexOf("/"));
	            	packageName = packageName.replace('/', '.');
	            	if(packageName.endsWith(".")){
        			    packageName = packageName.substring(0, packageName.length() - 1);
        			}
	            	if(pkgContext.get(packageName) == null){
	    				pkgContext.put(packageName, packageName);
	    				addPackage(packageName);
	    			}
            	}else{
            		packageName = parentPackage == null ? jarEntry.getName() : parentPackage + "." + jarEntry.getName();
	            	packageName = packageName.replace('/', '.');
	            	if(packageName.endsWith(".")){
	    			    packageName = packageName.substring(0, packageName.length() - 1);
	    			}
	            	if(pkgContext.get(packageName) == null){
	    				pkgContext.put(packageName, packageName);
	    				addPackage(packageName);
	    			}
            	}
            }else{
            	String jarEntryName = jarEntry.getName();    
            	String packageName = null;
            	if(jarEntryName.lastIndexOf("/") != -1){
            		packageName = jarEntryName.substring(0, jarEntryName.lastIndexOf("/"));
	            	packageName = packageName.replace('/', '.');
	            	if(packageName.endsWith(".")){
        			    packageName = packageName.substring(0, packageName.length() - 1);
        			}
	            	if(pkgContext.get(packageName) == null){
	    				pkgContext.put(packageName, packageName);
	    				addPackage(packageName);
	    			}
            	}
            	
            	String thingName = getThingName(jarEntryName.toLowerCase());
            	if(thingName != null){
            		if(classContext.get(thingName) == null){
    					classContext.put(thingName, thingName);
    					if(thingName.endsWith(".")){
    						thingName = thingName.substring(0, thingName.length() - 1);
            			}
    					
    					addClass(thingName.replace('/', '.'));
    				}                   
            	}
            }
        }
        jarFile.close();
	}
	
	/**
	 * 读取World下所有的包。
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized void init() throws IOException{		
		if(items.size() > 0){
			return;
		}
		
		World world = World.getInstance();
		String classpath = world.getClassLoader().getClassPath();
		String[] classpaths = classpath.split("[" + File.pathSeparator + "]"); 
		Map<String, String> pkgContext = new HashMap<String, String>();
		Map<String, String> classContext = new HashMap<String, String>();
		
		
		for(String path : classpaths){
			File file = new File(path);
			if(file.exists()){
				if(file.isDirectory()){
					getFormFile(file, null, pkgContext, classContext);
				}else{
					String fileName = file.getName().toLowerCase();
					if(fileName.endsWith(".jar") || fileName.endsWith(".zip")){
						getFromZip(file, null, pkgContext, classContext);
					}
				}
			}
		}
		
		//获取java的包
		URL javaUrl = world.getClassLoader().getResource("java/util/Map.class");
		String protocol = javaUrl.getProtocol();
		if("jar".equals(protocol)){
			URL jarUrl = new URL(javaUrl.getPath());   
            String path = jarUrl.getPath();
            path = path.substring(0, path.indexOf("!"));
            getFromZip(new File(URLDecoder.decode(path, "utf-8")), null, pkgContext, classContext);
		}else if("file".equals(protocol)){
			String path = javaUrl.getPath();
			path = path.substring(0, path.indexOf("!"));
			File file = new File(path);
			if(file.exists()){
				if(file.isDirectory()){
					getFormFile(file, null, pkgContext, classContext);
				}else{
					String fileName = file.getName().toLowerCase();
					if(fileName.endsWith(".jar") || fileName.endsWith(".zip")){
						getFromZip(file, null, pkgContext, classContext);
					}
				}
			}
		}		
		
		Collections.sort(items);
		
		for(JavaCacheItem item :items){
			if(item.type == JavaCacheItem.PACKAGE){
				packageTree.addPackage(item.path);
			}
		}
	}	
}
