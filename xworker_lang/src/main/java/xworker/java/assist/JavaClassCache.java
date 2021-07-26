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

import org.xmeta.World;

import xworker.lang.executor.Executor;

public class JavaClassCache {
	private static final String TAG = JavaClassCache.class.getName();
	
	/**
	 * Java的Pacakge和Class的缓存，按照List存储。
	 */
	private static List<JavaCacheItem> items = new ArrayList<>();
	/**
	 * Package和Class的缓存，按照包名存储。
	 */
	private static Map<String, JavaCacheItem> itemMap = new HashMap<>();
	private static PackageTree packageTree = new PackageTree("");
	private static JavaCacheItem rootItem = new JavaCacheItem("/", JavaCacheItem.ROOT);
	
	/**
	 * 获取包的树形结构。
	 * 
	 * @return
	 */
	public static PackageTree getPackageTree(){
		checkInit();
		
		return packageTree;
	}

	private static void checkInit(){
		if(items.size() == 0){
			try {
				init();
			} catch (IOException e) {
				Executor.error(TAG, "init class cache error", e);
			}
		}
	}
	/**
	 * 获取全部缓存。
	 * 
	 * @return
	 */
	public static List<JavaCacheItem> getAll(){
		checkInit();
		
		return items;
	}
	
	public static List<String> getAllPackages(){
		List<String> pks = new ArrayList<>();
		
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
		
		//过滤重复类，还不知为何重复，其实已经在早期处理过，但重复还在在
		Map<String, String> context = new HashMap<>();
		List<JavaCacheItem> list = new ArrayList<>();
		for(JavaCacheItem item : allItems){
			boolean ok = true;
			String pth = item.path.toLowerCase(); 
			for(String p : paths){
				if(!pth.contains(p)){
					ok = false;
					break;
				}
			}
			
			if(ok){
				if(context.get(item.path) == null){
					context.put(item.path, item.path);
					list.add(item);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 使用正则表达式搜索。
	 * 
	 * @param regex 正则表达式
	 * @return 查询结果
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
	 * @param path 路径
	 * @return 匹配的结果
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

	public static JavaCacheItem getJavaCacheItem(String path){
		return itemMap.get(path);
	}

	public static JavaCacheItem getRootItem(){
		checkInit();

		return rootItem;
	}

	private static void addPackage(String packageName){
		JavaCacheItem item = itemMap.get(packageName);
		if(item != null){
			return;
		}

		item = new JavaCacheItem(packageName, JavaCacheItem.PACKAGE);
		items.add(item);
		itemMap.put(packageName, item);
		addToParent(item);
	}
	
	private static void addClass(String className){
		JavaCacheItem item = itemMap.get(className);
		if(item != null){
			return;
		}

		item = new JavaCacheItem(className, JavaCacheItem.CLASS);
		items.add(item);
		itemMap.put(className, item);
		addToParent(item);
	}

	private static void addToParent(JavaCacheItem item){
		String path = item.getName();
		int index = path.lastIndexOf(".");
		if(index == -1){
			rootItem.addItem(item);
			item.setParentItem(rootItem);
		}else{
			String parentPath = path.substring(0, index);
			JavaCacheItem parentItem = itemMap.get(parentPath);
			if(parentItem == null){
				//parent的是包
				parentItem = new JavaCacheItem(parentPath, JavaCacheItem.PACKAGE);
				items.add(parentItem);
				itemMap.put(parentPath, parentItem);

				//由于父节点是新建的，因此也要加入到它的父节点中
				addToParent(parentItem);
			}

			parentItem.addItem(item);
			item.setParentItem(parentItem);
		}
	}
	
	/**
	 * 从文件中读取包。
	 * 
	 * @param pkgContext 包上下文
	 * @param file 文件
	 * @param parentPackage 父上下文
	 * @param classContext 类上下文
	 */
	private static void getFormFile(File file, String parentPackage, Map<String, String> pkgContext, Map<String, String> classContext){
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
			if(file.getName().toLowerCase().endsWith(".class")){
				String clsName = file.getName();
				clsName = clsName.substring(0, clsName.lastIndexOf("."));
				if(parentPackage != null && !"".equals(parentPackage)){
        			clsName = parentPackage + "." + clsName;
				}
				clsName = clsName.replace('/', '.');
				if(classContext.get(clsName) == null){
					classContext.put(clsName, clsName);
					addClass(clsName);
				}
			}
		}
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
	private static void getFromZip(File file, String parentPackage, Map<String, String> pkgContext, Map<String, String> classContext) throws IOException{
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
            	
            	if(jarEntryName.toLowerCase().endsWith(".class")){
        			String clsName = jarEntryName;
            		clsName = clsName.substring(0, clsName.lastIndexOf("."));          
            		if(classContext.get(clsName) == null){
    					classContext.put(clsName, clsName);
    					if(clsName.endsWith(".")){
            			    clsName = clsName.substring(0, clsName.length() - 1);
            			}
    					
    					addClass(clsName.replace('/', '.'));
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
			item.sort();
		}
	}	

	
	public static void main(String args[]){
		try{
			World world = World.getInstance();			
			world.init("xworker");
			
			long start = System.currentTimeMillis();
			List<JavaCacheItem> items = JavaClassCache.indexOf("java.lang.String");
			System.out.println("init time: " + (System.currentTimeMillis() - start));
			System.out.println("items size: " + items.size());
			for(JavaCacheItem item : items){
				System.out.println(item.path);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
