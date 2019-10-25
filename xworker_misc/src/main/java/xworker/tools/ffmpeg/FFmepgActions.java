package xworker.tools.ffmpeg;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.tools.ToolsConfig;

public class FFmepgActions {
	public static void runFFmpeg(ActionContext actionContext) {
		String commands = getCmdArrayString(actionContext);
		
		Action exec = actionContext.getObject("exec");
		exec.run(actionContext, "commands", commands);
	}
	
	public static String getCmdArrayString(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String dir = self.doAction("getFFmepgPath", actionContext);
		if(dir == null || "".equals(dir)) {
			//从配置文件中读取ffmpeg的路径
			dir = ToolsConfig.getAttribute("ffmpegDir");	
		}
		
		if(dir == null || "".equals(dir)) {
			throw new ActionException("The dir of ffmpeg is null, path=" + self.getMetadata().getPath());
		}
		
		//构造ffmpeg命令
		String commands = dir;
		if(!commands.endsWith("/") && !commands.endsWith("\\")) {
			commands = commands + "/";
		}
		commands = commands + "bin/ffmpeg";
		String cmd = self.doAction("getCommands", actionContext);
		if(cmd != null) {
			commands = commands + "\n" + cmd;
		}
		
		return commands;
	}
	
	public static String[] getCmdArray(ActionContext actionContext) {
		String commands = getCmdArrayString(actionContext);
		if(commands != null) {
			return commands.split("[\n]");
		}else {
			return new String[] {};
		}
	}
}
