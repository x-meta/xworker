package xworker.startup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * XWorker和类库打包到一个Jar中运行器。
 * @author zyx
 *
 */
public class JarRunner {
	static File tempDir = new File(System.getProperty("user.home"));
	
	public static File getXWorkerHome(String home) {
		if("user.home".equals(home)) {
			return new File(tempDir, "/xworker/"); 
		}else {
			return new File(home);			
		}
	}
	
	public static void extractXWorker(File root, JarFile jarFile, JarEntry xworkerJar) throws IOException {
		System.out.println("xworkerJar=" + xworkerJar);
		if(root.exists() == false) {
			root.mkdirs();
		}
		
		if(xworkerJar == null) {
			return;
		}
		
		JarInputStream jin = new JarInputStream(jarFile.getInputStream(xworkerJar));
		JarEntry entry = null;
		while((entry = jin.getNextJarEntry()) != null){
			if(entry.isDirectory() == false) {
				File target = new File(root, entry.getName());				
				if(target.exists() == false || target.length() != entry.getSize()) {
					if(target.exists() == false) {
						target.getParentFile().mkdirs();
					}
					
					//目标文件不存在或者不一致
					byte[] bytes = new byte[1024 * 8];
					int length = -1;
					FileOutputStream fout = new FileOutputStream(target);
					try {
						while((length = jin.read(bytes)) != -1) {
							fout.write(bytes, 0, length);
						}
					}finally {
						fout.close();
					}
				}
			}
			
			jin.closeEntry();
		}
		
		jin.close();
	}
	
		
	public static void checkClean(String args[]) {
		boolean cleanTemp = false;
		for(int i=0; i<args.length; i++) {
			if("xworker-clean".equals(args[i])) {
				cleanTemp = true;
				break;
			}
		}
		
		if(cleanTemp) {
			File xworkerDir = new File(tempDir, "/xworker/");
			delete(xworkerDir);
		}
	}
	
	public static void delete(File file) {
		if(file.exists()) {
			if(file.isDirectory()) {
				for(File child : file.listFiles()) {
					delete(child);
				}
				
				file.delete();
			}else {
				file.delete();
			}
		}
	}
	
	public static String getJarFileName() {
		URL jarUrl = JarRunner.class.getProtectionDomain().getCodeSource().getLocation();
		String fileName = jarUrl.getFile();
		//System.out.println(fileName);
		int index = fileName.indexOf("!");
		if(index != -1) {
			fileName = fileName.substring(0, index);
		}
		if(fileName.startsWith("file:")) {
			fileName = fileName.substring(5, fileName.length());
		}
				
		//System.out.println(fileName);
		return fileName;
	}
	
	public static void main(String args[]) {
		try {
			JarFile jarFile = new JarFile(getJarFileName());
			
			//获取要运行的事物
			Attributes attributes = jarFile.getManifest().getMainAttributes(); 
			String thing = attributes.getValue("XWorker-Thing"); 
			String action = attributes.getValue("XWorker-Action");
			if(action == null ||"".equals(action)) {
				action = "run";
			}
			String xworker_home = attributes.getValue("XWorker-Home");
			String xworkerJar = attributes.getValue("XWorker-Jar");
			System.out.println("xworker_home=" + xworker_home);
			System.out.println("xworkerJar=" + xworkerJar);
			File xworkerRoot = getXWorkerHome(xworker_home);		
			System.out.println("xworkerRoot=" + xworkerRoot);
			if(xworkerJar != null) {
				extractXWorker(xworkerRoot, jarFile, jarFile.getJarEntry(xworkerJar));
			}
			jarFile.close();

			String[] newArgs = new String[args.length + 3];
			newArgs[0] = xworkerRoot.getAbsolutePath();
			newArgs[1] = thing;
			newArgs[2] = action;
			System.arraycopy(args, 0, newArgs, 3, args.length);
			//System.out.println("XWorker_home: " + newArgs[0]);
			//System.out.println("Thing: " + newArgs[1]);
			//System.out.println("Action: " + newArgs[2]);
			
			//执行main-class
			String mainClass = attributes.getValue("XWorker-Main-Class");
			Class<?> trCls = Thread.currentThread().getContextClassLoader().loadClass(mainClass.trim());
			Method method = trCls.getDeclaredMethod("main", new Class[]{String[].class});
			
			//System.out.println("get ThingRunner: " + (System.currentTimeMillis() - start));
			method.invoke(null, new Object[]{newArgs});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
