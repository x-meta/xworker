package xworker.lang.function.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import xworker.java.assist.Javaassist;
import xworker.java.assist.MethodInfo;

public class JavaStaticFileOrMethodActions {
	private static Logger logger = LoggerFactory.getLogger(JavaStaticFileOrMethodActions.class);
	
	/**
	 * 静态类选择器的选择事件。
	 * 
	 * @param actionContext
	 */
	public static void classSelected(ActionContext actionContext){
		Composite mainComposite = (Composite) actionContext.get("mainComposite");
		
		Thing thing = (Thing) actionContext.get("thing");
		if(thing != null){
			String className = thing.getString("className");
			try{
				Class<?> cls = Class.forName(className);
				List<MethodInfo> methods = Javaassist.getMethods(cls);		
				
				List<Object> fieldMethodList = new ArrayList<Object>();
				
				CtClass ctClass = ClassPool.getDefault().get(cls.getName());
				for(CtField field : ctClass.getFields()){
					if(Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())){
						fieldMethodList.add(field);
					}
				}
				for(MethodInfo fdata : methods){
					//公开的方法才显示
					if(Modifier.isPublic(fdata.getCtMethod().getModifiers()) && Modifier.isStatic(fdata.getCtMethod().getModifiers())){
						fieldMethodList.add(fdata);
					}
				}
				
				actionContext.getScope(0).put("fieldMethodList", fieldMethodList);
				
				SetMethodOrFieldInvoker.filterFieldOrMethod(actionContext);
			}catch(Exception e){
				logger.error("init static class error, path=" + thing.getMetadata().getPath(), e);
				
				Shell shell = mainComposite.getShell();
				MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				box.setText("初始化字段和方法");
				box.setMessage("初始化失败，" + e.getMessage());
			}
		}
	}
}
