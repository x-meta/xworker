package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.system.IProcessManager;
import xworker.service.ServiceManager;
import xworker.startup.FileStartup;
import xworker.util.UtilAction;
import xworker.util.UtilData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecuteXWorkerProcess {
	public static List<String> getRunArgs(Thing self, String thingPath, String actionName, String mainClass, ActionContext actionContext) throws IOException{
		List<String> args = new ArrayList<String>();
		String javaHome = System.getProperty("sun.boot.library.path");

		//java命令
		args.add(javaHome + "/java");

		//classPath, 添加一个FileStartup的类库即可
		args.add("-classpath"); //可能会造成路径太长，从而执行错误

		Class<?> cls = FileStartup.class;
		String loc = cls.getProtectionDomain().getCodeSource().getLocation().getFile();
		args.add(loc);

		//要运行的类
		args.add(FileStartup.class.getName());

		File libDir = self.doAction("getLibDir", actionContext);
		File configFile = UtilAction.createFileStartupConfig(self, thingPath, actionName, mainClass, libDir);

		args.add(configFile.getAbsolutePath());

		setArgs(self, args, actionContext);

		return args;
	}

	private static Process run(Thing self, String thingPath, String actionName, String mainClass, ActionContext actionContext) throws IOException{
		List<String> args = getRunArgs(self, thingPath, actionName, mainClass, actionContext);

		ProcessBuilder pb = new ProcessBuilder(args);
		File workingDir = self.doAction("getWorkingDir", actionContext);
		if(workingDir != null){
			pb.directory(workingDir);
		}

		Process p = pb.start();
		ProcessConsole.startProcessConsole(self, actionContext, p);
		return p;
	}

	public static Process run(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");

		//要运行的模型
		String thingPath = (String) self.doAction("getThingPath", actionContext);

		//动作
		String action = self.doAction("getAction", actionContext);
		if(action == null || action.isEmpty()){
			action = "run";
		}

		return run(self, thingPath, action, null, actionContext);

	}
	
	public static Process runClass(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		//command
		String className = self.getString("class");
		return run(self, null, null, className, actionContext);
	}
	
	public static Process runFile(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");

		//file
		File file = self.doAction("getFile", actionContext);		
		return run(self, file.getAbsolutePath(), "run", null, actionContext);
	}
	
	private static void setArgs(Thing self, List<String> args, ActionContext actionContext){
		String ps = self.doAction("getArgs", actionContext);
		if(ps != null && !"".equals(ps)){
			args.addAll(Arrays.asList(ps.split("[ ]")));
		}
	}
}
