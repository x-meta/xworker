/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

/**
 * 与动作相关的工具类。
 * 
 * @author zyx
 *
 */
public class UtilAction {
	static Method runGroovy;
	static Method runGroovyAction;
	
	private static void initGroovyUtils() {
		if(runGroovy != null) {
			return;
		}
		
		try {
			Class<?> cls = World.getInstance().getClassLoader().loadClass("xworker.groovy.GroovyUtils");
			runGroovy = cls.getDeclaredMethod("runGroovy", new Class<?>[] {String.class, ActionContext.class});
			runGroovyAction = cls.getDeclaredMethod("runGroovyAction", new Class<?>[] {Action.class, ActionContext.class});
		}catch(Exception e) {			
		}
	}
	/**
	 * 返回异常的最初的异常，平常里异常可能会被包装了多次，比如脚本、XWorker的动作异常、Hibernate异常等，
	 * 此方法返回最初的异常。
	 * 
	 * @param t
	 * @return
	 */
	public static Throwable getCause(Throwable t){
		Throwable cause = t;
		Throwable cause1 = null;

		while((cause1 = cause.getCause()) != null){
			cause = cause1;
		}
		
		return cause;
	}
	
	public static String getCauseStackTrace(Throwable t) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Throwable cause = getCause(t);
		cause.printStackTrace(new PrintStream(bout));
		return bout.toString();
	}
	
	public static Object runAsGroovy(Thing thing, String codeName, ActionContext actionContext, String varScope){
		checkGroovy();
		
		String cacheName = "_action_as_groovy_" + codeName;
		//thing.setData(cacheName, null);
		Thing actionThing = (Thing) thing.getData(cacheName);
		if(actionThing == null || actionThing.getMetadata().getLastModified() < thing.getMetadata().getLastModified()){
			//克隆一个路径和原有事物一致的事物
			actionThing = new Thing("xworker.lang.actions.GroovyAction");
			actionThing.getAttributes().putAll(thing.getAttributes());
			String name = thing.getMetadata().getName() + codeName;
			if(name.startsWith("#")){
				name = name.substring(1, name.length());
			}
			actionThing.set("name", name);
			
			//actionThing.set("descriptors", "xworker.lang.actions.GroovyAction");
			actionThing.set("code", thing.getString(codeName));
			if(varScope != null){
				actionThing.set("varScope", varScope);
			}
			actionThing.getMetadata().setCategory(thing.getMetadata().getCategory());
			actionThing.getMetadata().setLastModified(thing.getMetadata().getLastModified());
			actionThing.setParent(thing);
			thing.setData(cacheName, actionThing);
		}else {
			String code =  thing.getString(codeName);
			if(code != null && !code.equals(actionThing.getString("code"))){
				actionThing.set("code", code);
			}
		}
		
		return actionThing.getAction().run(actionContext);

	}
	public static Object runAsGroovy(Thing thing, String codeName, ActionContext actionContext){
		return runAsGroovy(thing, codeName, actionContext, null);
	}
	
	/**
	 * 把指定的事物当作GroovyAction来执行。
	 * 
	 * @param thing
	 * @return
	 */
	public static Object runAsGroovy(Thing thing, ActionContext actionContext){
		return runAsGroovy(thing, "code", actionContext, null);
	}
	
	/**
	 * 如果一个模型继承了GroovyAction,但它又重写了run方法，此时还想作为GroovyAction运行，那么可以在重写的run方法中调用本方法。
	 * @param thing
	 * @param actionContext
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static Object runGroovyAction(Thing thing, ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		checkGroovy();
		
		initGroovyUtils();
		if(runGroovyAction != null) {
			try {
				Bindings bindings = actionContext.push();
				for(Thing vars : thing.getChilds("Variables")){
		        	for(Thing var : vars.getChilds()){
		        		String key = var.getMetadata().getName();
		        		Object value = var.getAction().run(actionContext, null, true);
		        		bindings.put(key, value);
		        	}
		        }
				
				for(Thing acs : thing.getChilds("ActionDefined")) {
					for(Thing ac : acs.getChilds()) {
						bindings.put(ac.getMetadata().getName(), ac.getAction());
					}
				}
				
				Action action = thing.getAction();
				action.checkChanged();
				return runGroovyAction.invoke(null, new Object[] {action, actionContext});
			}finally {				
				actionContext.pop();
			}
		}else {
			return null;
		}
	}
	
	/**
	 * 执行Groovy脚本。
	 * 
	 * @param code
	 * @param actionContext
	 * @return
	 */
	public static Object runGroovy(String code, ActionContext actionContext) {
		checkGroovy();
		
		initGroovyUtils();
		if(runGroovy != null) {
			try {
				return runGroovy.invoke(null, new Object[] {code, actionContext});
			} catch (Exception e) {
				throw new ActionException("Run groovy error", e);
			}
		}else {
			return null;
		}
	}
	
	/**
	 * 把一组动作当作子动作执行，判断RETURN、BREAK、EXCEPTION、CONTINUE等状态，执行结束时设置状态为RUNNING。
	 * 
	 * @param actionThings 动作列表
	 * @param actionContext 变量上下文
	 * @return 执行的结果
	 */
	public static Object runChildActions(List<Thing> actionThings, ActionContext actionContext){
		try {
			return runChildActions(actionThings, actionContext, true);
		}finally {
			actionContext.setStatus(ActionContext.RUNNING);
		}
	}
	
	/**
	 * 检测是否引用了xworker_groovy，如果没有抛出异常。
	 */
	public static void checkGroovy() {
		if(World.getInstance().getThing("xworker.lang.actions.GroovyAction") == null) {
			throw new ActionException("GroovyAction not exists, please import xworker_groovy!");
		}
	}
	
	/**
	 * 执行一组动作，判断RETURN、BREAK、EXCEPTION、CONTINUE等时会中断。
	 * 
	 * @param actionThings 动作列表
	 * @param actionContext 变量上下文
	 * @param isSubAction 是否是子动作，如果为false会重置状态到RUNNING
	 * @return 执行的结果
	 */
	public static Object runChildActions(List<Thing> actionThings, ActionContext actionContext, boolean isSubAction){
		Object result = null;
		try {
	        for(Thing action : actionThings){      
	            result = action.getAction().run(actionContext, null, isSubAction);
	    
	            if(ActionContext.RETURN == actionContext.getStatus() || 
	                ActionContext.CANCEL == actionContext.getStatus() || 
	                ActionContext.BREAK == actionContext.getStatus() || 
	                ActionContext.EXCEPTION == actionContext.getStatus() ||
	                ActionContext.CONTINUE == actionContext.getStatus()){
	                break;
	            }
	        }
		}finally {
			if(isSubAction == false) {
				actionContext.setStatus(ActionContext.RUNNING);
			}
		}
        return result;
	}
	
	/**
	 * 执行一组动作，并且把它们的值放入到Map中，其中key是动作的名字。
	 * 
	 * @param actionThings 要执行的动作列表
	 * @param actionContext 变量上下文
	 * @return 结果集
	 */
	public static Map<String ,Object> getActionsResultMap(List<Thing> actionThings, ActionContext actionContext){
		Map<String, Object> vars = new HashMap<String, Object>();
		
		for(Thing thing : actionThings){
			Object result = thing.getAction().run(actionContext, null, true);
			vars.put(thing.getMetadata().getName(), result);
		}
		return vars;
	}
	
	/**
	 * 执行self事物的子节点的子节点，并把结果放入到Map中。
	 * 
	 * @param self 事物
	 * @param childName 子节点名称
	 * @param actionContext 变量上下文
	 * @return 结果集
	 */
	public static Map<String ,Object> getChildChildsResultMap(Thing self, String childName, ActionContext actionContext){
		Map<String, Object> vars = new HashMap<String, Object>();
		
		for(Thing child : self.getChilds(childName)){
			for(Thing thing : child.getChilds()){
				Object result = thing.getAction().run(actionContext, null, true);
				vars.put(thing.getMetadata().getName(), result);
			}
		}
		
		return vars;
	}
	
	/**
	 * 执行动作当作子动作，事物列表中的子节点列表是要执行的子动作。
	 * 
	 * @param actionThings 要执行包含动作的事物
	 * @param actionContext 变量上下文
	 * @return 执行的结果
	 */
	public static Object runChildChildActions(List<Thing> actionThings, ActionContext actionContext){
		Object result = null;
		 //执行子动作
        for(Thing actions : actionThings){
            for(Thing action : actions.getChilds()){      
                result = action.getAction().run(actionContext, null, true);
        
                if(ActionContext.RETURN == actionContext.getStatus() || 
                    ActionContext.CANCEL == actionContext.getStatus() || 
                    ActionContext.BREAK == actionContext.getStatus() || 
                    ActionContext.EXCEPTION == actionContext.getStatus() ||
                    ActionContext.CONTINUE == actionContext.getStatus()){
                    break;
                }
            } 
            if(ActionContext.RETURN == actionContext.getStatus() || 
                ActionContext.CANCEL == actionContext.getStatus() || 
                ActionContext.BREAK == actionContext.getStatus() || 
                ActionContext.EXCEPTION == actionContext.getStatus() ||
                ActionContext.CONTINUE == actionContext.getStatus()){
                break;
            }
        }
        
        return result;
	}
	
	/**
	 * 根据对象的类型返回true/false。
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean returnTrueFalse(Object obj){
		if(obj != null){
            if(obj instanceof Boolean){
                return (Boolean) obj;
            }else if(obj instanceof String){
            	if("success".equals(obj) || "true".equals(obj)){
            		return true;
            	}else{
            		return false;
            	}
            }else{
            	return false;
            }
        }else{
            return false;
        }
	}
	
	/**
	 * 从给定的ActionContext读取字符串。
	 * 
	 * 如果name为""包含的字符串，那么返回定值字符串，否则从actionContext读取。
	 * 
	 * @param value 值
	 * @param actionContext 变量上下文
	 * @return 值
	 */
	public static String getString(String value, ActionContext actionContext){
		return UtilString.getString(value, actionContext);
	}
	
	/**
	 * 获取一个动作的代码文件名。
	 * 
	 * @param actionThing
	 * @param ext 代码文件的后缀
	 * @return
	 */
	public static String getActionCodeFilePath(Thing actionThing, String ext){
		String className = "";
		
		Thing parent = actionThing.getParent();			
		Thing rootParent = actionThing.getRoot();
		if(parent == null){
			parent = actionThing;
		}
		
		if(rootParent != null){
			className = className + ".p" + rootParent.getMetadata().getPath().hashCode();
		}
		
		if(parent != null && parent != rootParent){
			className = className + ".p" + parent.getMetadata().getPath().hashCode();
		}
		
		String cName = actionThing.getString("className");
		if(cName == null || "".equals(cName)){
			className = className + "." + actionThing.getMetadata().getName();
		}else{
			className = className + "." + cName;
		}
		className = Action.getClassName(className);

		String fileName = className.replace('.', '/');
		//fileName += ".java";
					
		return World.getInstance().getPath() + "/actionSources/" + fileName + "." + ext;
	}
	
	/**
	 * 根据动作的类名获取动作。
	 * 
	 * @param actionClassName 动作类名
	 * @return 对应的事物
	 */
	public static Thing getActionThing(String actionClassName){
		File rootDir = new File(World.getInstance().getPath() + "/work/actionSources/");
		
		String packageName = "";
		String className = actionClassName;
		int lastIndex = actionClassName.lastIndexOf(".");
		if(lastIndex != -1){
			packageName = actionClassName.substring(0, lastIndex);
			className = actionClassName.substring(lastIndex + 1, actionClassName.length());
		}
		
		for(File thingManagerDir : rootDir.listFiles()){
			if(thingManagerDir.isDirectory()){
				String fileName = packageName.replace('.', '/');
				File f = new File(thingManagerDir, fileName);
				if(f.isDirectory()){
					for(File childFile : f.listFiles()){
						if(childFile.isFile() && childFile.getName().startsWith(className + ".")){
							//取第一行
							try {
								BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(childFile)));
								String line = br.readLine();
								br.close();
								String thingPath = line.substring(7, line.length() - 2);						
								return World.getInstance().getThing(thingPath);
							} catch (IOException e) {						
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 基于thing1，合并thing2生成一个新的事物路径。
	 * 主要是想生存一个唯一的路径，而路径没有特别的意义，仅是标识。
	 * 
	 * @param thing1
	 * @param thing2
	 * @param type
	 * @return
	 */
	public static String generatePath(Thing thing1, Thing thing2, String type) {		
		String name = thing1.getRoot().getMetadata().getPath() + "_" + type + ".p" 
				+ thing2.getMetadata().getPath().hashCode()  + 
				".p" + thing2.getMetadata().getPath().hashCode();
		
		name = name.replace('-', '_');
		return name;
	}
	
	public static interface IGroovyUtils{
		public Object runGroovyAction(Thing thing, ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException;
		
		public Object runGroovy(String code, ActionContext actionContext);
	}
}