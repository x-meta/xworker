package xworker.lang.command;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;

import xworker.swt.xwidgets.SelectContent;

public class BaseDomain {
	/**
	 * 取变量的可选列表。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static List<SelectContent> varContents(ActionContext actionContext){
		List<SelectContent> contents = new ArrayList<SelectContent>();
		for(String key : actionContext.keySet()){
			Object obj = actionContext.get(key);
			String label = key + " : " + (obj == null ? null : obj.getClass().getName());
			contents.add(new SelectContent(key, label, obj));
		}
		
		return contents;
	}
	
	public static Object varRun(ActionContext actionContext){
		Command command = (Command) actionContext.get("command");		
		return command.getResult();
	}
	
	public static void groovyRun(ActionContext actionConext){
		
	}
	
}
