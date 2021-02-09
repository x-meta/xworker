package xworker.groovy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.xmeta.Action;
import org.xmeta.ActionContext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyUtils {
	/**
	 * 执行Groovy脚本。
	 * 
	 * @param code
	 * @param actionContext
	 * @return
	 */
	public static Object runGroovy(String code, ActionContext actionContext) {
		Binding binding = new Binding(actionContext);
		GroovyShell shell = new GroovyShell(binding);
		return shell.evaluate(code);
	}
	
	public static Object runGroovyAction(Action action, ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		return GroovyAction.run(action, actionContext);
	}
}
