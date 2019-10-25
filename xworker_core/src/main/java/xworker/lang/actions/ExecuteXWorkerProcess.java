package xworker.lang.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.system.IProcessManager;
import xworker.service.ServiceManager;
import xworker.util.UtilData;

public class ExecuteXWorkerProcess {
	private static void addLib(File libDir, StringBuilder sb) {
		if(libDir == null || !libDir.exists()) {
			return;
		}
		
		for(File file : libDir.listFiles()) {
			if(file.isFile()) {
				String name = file.getName().toLowerCase();
				if(name.endsWith(".jar") || name.endsWith(".zip")){
					if(sb.indexOf(name) == -1) {
						//不存在则添加
						sb.append(File.pathSeparator);
						sb.append(file.getAbsolutePath());
					}
				}					
			}else if(file.isDirectory()) {
				addLib(file, sb);
			}
			
		}
	}
	public static Process run(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		List<String> args = new ArrayList<String>();
		String javaHome = System.getProperty("sun.boot.library.path");
		String bootClass = System.getProperty("sun.boot.class.path");
		String classPath = System.getProperty("java.class.path");
		String xworkerHome = World.getInstance().getPath();
		
		//java命令
		args.add(javaHome + "/java");
		
		//classPath
		//args.add("-classpath");
		StringBuilder sb = new StringBuilder();//"\"");
		if(bootClass != null) {
			sb.append(bootClass);
		}
		File libDir = self.doAction("getLibDir", actionContext);
		addLib(libDir, sb);
		addLib(new File(xworkerHome + "/lib/"), sb);  //添加XWorker类库
		sb.append(File.pathSeparator);
		sb.append(classPath);
		//sb.append("\"");
		//args.add(sb.toString());
		
		//command
		args.add("org.xmeta.util.ThingRunner");
		
		//xworkerHome
		args.add(xworkerHome);
		
		//事物
		String thingPath = (String) self.doAction("getThingPath", actionContext); 
		args.add(thingPath);
		
		//动作
		args.add((String) self.doAction("getAction", actionContext));
		
		setArgs(self, args, actionContext); 
		 
		ProcessBuilder pb = new ProcessBuilder(args);
		File workingDir = self.doAction("getWorkingDir", actionContext);
		if(workingDir != null){
			pb.directory(workingDir);
		}	
		
		Map<String, String> env = pb.environment();
		env.put("java.class.path", sb.toString());
		if(bootClass != null) {
			env.put("sun.boot.class.path", bootClass);
		}
		
		Process p = pb.start();		
		/*
		InputStream in = p.getInputStream();
		byte[] bytes = new byte[2048];
		int length = -1;
		while((length = in.read(bytes)) != -1){
		 System.out.print(new String(bytes, 0, length));
		}
		*/
		
		if(UtilData.isTrue(self.doAction("isProcessManager", actionContext))) {
    		//是否添加到进程管理器中
    		IProcessManager manager = ServiceManager.getService(IProcessManager.class);
    		if(manager != null && !manager.isDisposed()) {
    			manager.addProcess("Thing: " + thingPath, p);
    		}
    	}
    	
		ProcessConsole.startPrintThread(self, actionContext, p);
		return p;
	}
	
	public static Process runClass(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		List<String> args = new ArrayList<String>();
		String javaHome = System.getProperty("sun.boot.library.path");
		String bootClass = System.getProperty("sun.boot.class.path");
		String classPath = System.getProperty("java.class.path");
		
		//java命令
		args.add(javaHome + "/java");
		
		//classPath
		args.add("-classpath");
		StringBuilder sb = new StringBuilder("\"");
		sb.append(bootClass);
		File libDir = self.doAction("getLibDir", actionContext);
		addLib(libDir, sb);
		sb.append(File.pathSeparator);
		sb.append(classPath);
		sb.append("\"");
		args.add(sb.toString());
		
		//command
		String className = self.getString("class"); 
		args.add(className);
		
		//args
		setArgs(self, args, actionContext);
		
		ProcessBuilder pb = new ProcessBuilder(args);
		File workingDir = self.doAction("getWorkingDir", actionContext);
		if(workingDir != null){
			pb.directory(workingDir);
		}		
		
		Process p = pb.start();
		ProcessConsole.startPrintThread(self, actionContext, p);
		
		if(UtilData.isTrue(self.doAction("isProcessManager", actionContext))) {
    		//是否添加到进程管理器中
    		IProcessManager manager = ServiceManager.getService(IProcessManager.class);
    		if(manager != null && !manager.isDisposed()) {
    			manager.addProcess("Class: " + className, p);
    		}
    	}
		
		 /*
		 InputStream in = p.getInputStream();
		 byte[] bytes = new byte[2048];
		 int length = -1;
		 while((length = in.read(bytes)) != -1){
			 System.out.print(new String(bytes, 0, length));
		 }
		 */
		return p;
	}
	
	public static Process runFile(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		List<String> args = new ArrayList<String>();
		String javaHome = System.getProperty("sun.boot.library.path");
		String bootClass = System.getProperty("sun.boot.class.path");
		String classPath = System.getProperty("java.class.path");
		String xworkerHome = World.getInstance().getPath();
		
		//java命令
		args.add(javaHome + "/java");
		
		//classPath
		args.add("-classpath");
		StringBuilder sb = new StringBuilder("\"");
		sb.append(bootClass);
		File libDir = self.doAction("getLibDir", actionContext);
		addLib(libDir, sb);
		sb.append(File.pathSeparator);
		sb.append(classPath);
		sb.append("\"");
		args.add(sb.toString());
		
		//Java类
		args.add("org.xmeta.util.ThingRunner");
		
		//xworkerHome
		args.add(xworkerHome);
				
		//file
		File file = self.doAction("getFile", actionContext);		
		args.add(file.getAbsolutePath());
		
		//args
		setArgs(self, args, actionContext);
		
		//System.out.println(args);
		ProcessBuilder pb = new ProcessBuilder(args);		
		File workingDir = self.doAction("getWorkingDir", actionContext);
		if(workingDir == null){
			workingDir = file.isDirectory() ? file : file.getParentFile();
		}
		pb.directory(workingDir);
		
		Process p = pb.start();		
		ProcessConsole.startPrintThread(self, actionContext, p);
		
		if(UtilData.isTrue(self.doAction("isProcessManager", actionContext))) {
    		//是否添加到进程管理器中
    		IProcessManager manager = ServiceManager.getService(IProcessManager.class);
    		if(manager != null && !manager.isDisposed()) {
    			manager.addProcess("File: " + file.getAbsolutePath(), p);
    		}
    	}
		 /*
		 InputStream in = p.getInputStream();
		 byte[] bytes = new byte[2048];
		 int length = -1;
		 while((length = in.read(bytes)) != -1){
			 System.out.print(new String(bytes, 0, length));
		 }
		 */
		return p;
	}
	
	private static void setArgs(Thing self, List<String> args, ActionContext actionContext){
		String ps = self.doAction("getArgs", actionContext);
		if(ps != null && !"".equals(ps)){
			for(String arg : ps.split("[ ]")){
				args.add(arg);
			}
		}
	}
}
