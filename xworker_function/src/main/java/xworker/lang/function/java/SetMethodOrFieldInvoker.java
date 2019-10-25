package xworker.lang.function.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import xworker.java.assist.Javaassist;
import xworker.java.assist.MethodInfo;
import xworker.swt.util.ResourceManager;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;

public class SetMethodOrFieldInvoker {
	/**
	 * 设置字段的值的事件处理。
	 * 
	 * @param actionContext
	 */
	public static void setFieldButtonSelection(ActionContext actionContext){
		Tree methodTree = (Tree) actionContext.get("fieldAndMethodTree");
		Object data = methodTree.getSelection()[0].getData();
		
		//调用函数的原型
		Thing invokerProto = null;
		CtField ctField = (CtField) data;
		Table varTable = (Table) actionContext.get("varTable");
		String varName = (String) actionContext.get("varName");
		Button userVarNameButton = (Button) actionContext.get("userVarNameButton");
		
		//如果是全局变量则可以使用变量名
		boolean useVarName = varTable == null || (userVarNameButton.getSelection() && varTable == actionContext.get("globalTable"));
		if(!useVarName){
			varName =null;
		}
				
		if (Modifier.isStatic(ctField.getModifiers())) {
			// 静态字段			
			invokerProto = new Thing("xworker.lang.function.java.Ognl");
			String exp = "@" + ctField.getDeclaringClass().getName() + "@" + ctField.getName() + "=_value";
			invokerProto.set("expression", exp);
		} else {
			// 普通字段
			invokerProto = new Thing("xworker.lang.function.java.Ognl");
			String exp = null;
			if(varName == null){
				exp = "_object." + ctField.getName() + "=_value";
				invokerProto.set("expression", exp);
				Thing param = Javaassist.createParameterValue("_object");
				invokerProto.addChild(param);
			}else{
				exp = varName + "." + ctField.getName() + "=_value";
				invokerProto.set("expression", exp);
			}
		}
		
		Thing param = new Thing("xworker.lang.function.Parameter");
		param.set("name", "_value");
		invokerProto.addChild(param);
		
		
		//设置回调
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		fnRequest.resetFunctionThing(invokerProto, false);
		if(!Modifier.isStatic(((CtField) data).getModifiers())){
			FunctionParameter objectParam = fnRequest.getParameter("_object");
			if(objectParam != null){			
				//对象变量参数
				if(varTable == actionContext.get("localTable")){
					//局部变量
					objectParam.setVariableValue(varName, null, true);
				}else{
					//全局变量
					objectParam.setVariableValue(varName, null, false);
				}
			}
		}
		
		FunctionParameter valueParam = fnRequest.getParameter("_value");
		valueParam.setValueThing(null, null);
		
		//先刷新函数树
		FunctionManager.sendRequest(fnRequest, actionContext);
		//再执行新的函数
		fnRequest.run(actionContext);
	}
	
	/**
	 * 确定按钮的事件处理。
	 * 
	 * @param actionContext
	 */
	public static void okButtonSelection(ActionContext actionContext){
		Tree methodTree = (Tree) actionContext.get("fieldAndMethodTree");
		Object data = methodTree.getSelection()[0].getData();
		
		Table varTable = (Table) actionContext.get("varTable");
		String varName = (String) actionContext.get("varName");
		Button userVarNameButton = (Button) actionContext.get("userVarNameButton");
		
		//如果是全局变量则可以使用变量名
		boolean useVarName = (varTable == null || (userVarNameButton.getSelection() && varTable == actionContext.get("globalTable")));
		if(!useVarName){
			varName =null;
		}
		
		//调用函数的原型
		Thing invokerProto = null;
		if(data instanceof MethodInfo){
			//方法调用
			MethodInfo methodInfo = (MethodInfo) data; 
			invokerProto = Javaassist.getOgnlFunctionInstance(varName, methodInfo);
			//Javaassist.getJavaMethodFunctionInstance(methodInfo);
		}else{
			//字段
			CtField ctField = (CtField) data;
			invokerProto = Javaassist.getJavaFieldOgnlInstance(varName, ctField);
		}

		//设置回调
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		fnRequest.resetFunctionThing(invokerProto, false);
		
		for(FunctionParameter param : fnRequest.getParameters()){
			if(!"_object".equals(param.getName())){
				param.setValueThing(null, null);
			}
		}
		
		if(data instanceof MethodInfo || (data instanceof CtField && !Modifier.isStatic(((CtField) data).getModifiers()))){
			FunctionParameter objectParam = fnRequest.getParameter("_object");
			if(objectParam != null){			
				//对象变量参数

				if(varTable == actionContext.get("localTable")){
					//局部变量
					objectParam.setVariableValue(varName, null, true);
					objectParam.getValueThing().put("name", "_object");
				}else{
					//全局变量
					objectParam.setVariableValue(varName, null, false);
					objectParam.getValueThing().put("name", "_object");
				}
			}
		}
		
		/*
		if(data instanceof CtField){
			CtField ctField = (CtField) data;
			FunctionParameter nameParam = fnRequest.getParameter("name");
			Thing nameThing = new Thing("xworker.lang.function.values.StringValue");
			nameThing.set("name", "name");
			nameThing.set("value", ctField.getName());
			nameParam.setValueThing(nameThing, null);
			
			if(Modifier.isStatic(ctField.getModifiers())){
				//全局的需要设置className
				FunctionParameter classNameParam = fnRequest.getParameter("className");
				Thing clssThing = new Thing("xworker.lang.function.values.StringValue");
				clssThing.set("name", "name");
				clssThing.set("value", ctField.getDeclaringClass().getName());
				classNameParam.setValueThing(clssThing, null);
			}
		}*/
		//先刷新函数树
		FunctionManager.sendRequest(fnRequest, actionContext);
		//再执行新的函数
		fnRequest.run(actionContext);
	}
	
	/**
	 * 选择局部变量或全局变量的事件处理。
	 * 
	 * @param actionContext
	 * @throws NotFoundException
	 */
	public static void varTableSelection(ActionContext actionContext) throws NotFoundException{
		Event event = (Event) actionContext.get("event");
		
		//本地变量或全局变量表格选中的对象
		Object[] data = (Object[]) event.item.getData();
		Object object = data[1];
		
		List<MethodInfo> methods = Javaassist.getMethods(object.getClass());

		actionContext.getScope(0).put("varTable", event.widget);
		actionContext.getScope(0).put("varName", data[0]);
		
		//在树上显示字段和方法
		Button okButton = (Button) actionContext.get("okButton");
		okButton.setEnabled(false);
		
		actionContext.getScope(0).put("varTable", event.widget);
		actionContext.getScope(0).put("varName", data[0]);
	
		List<Object> fieldMethodList = new ArrayList<Object>();
		
		CtClass ctClass = ClassPool.getDefault().get(object.getClass().getName());
		for(CtField field : ctClass.getFields()){
			if(Modifier.isPublic(field.getModifiers())){
				fieldMethodList.add(field);
			}
		}
		for(MethodInfo fdata : methods){
			//公开的方法才显示
			if(Modifier.isPublic(fdata.getCtMethod().getModifiers())){
				fieldMethodList.add(fdata);
			}
		}
		
		actionContext.getScope(0).put("fieldMethodList", fieldMethodList);
		
		filterFieldOrMethod(actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void filterFieldOrMethod(ActionContext actionContext){
		//初始化字段和方法
		Tree fieldAndMethodTree = (Tree) actionContext.get("fieldAndMethodTree");
		for(TreeItem item : fieldAndMethodTree.getItems()){
			item.dispose();
		}
		
		List<Object> fieldMethodList = (List<Object>) actionContext.get("fieldMethodList");
		if(fieldMethodList == null){
			return;
		}
		
		//在树上显示字段和方法
		try{
			Button okButton = (Button) actionContext.get("okButton");
			okButton.setEnabled(false);
			
			Button setFieldButton = (Button) actionContext.get("setFieldButton");
			setFieldButton.setEnabled(false);
			
			Bindings bindings = actionContext.push();
			bindings.put("parent", fieldAndMethodTree);
			
			Image filedImage = (Image) ResourceManager.createIamge("/icons/bullet_green.png", actionContext);
			Image methodImage = (Image) ResourceManager.createIamge("/icons/cog_go.png", actionContext);

			String filter = ((Text) actionContext.get("objSerachText")).getText().toLowerCase().trim();
			for(Object obj : fieldMethodList){
				if(obj instanceof CtField){
					CtField field = (CtField) obj;
					if("".equals(filter) || field.getName().toLowerCase().indexOf(filter) != -1){
						TreeItem item = new TreeItem(fieldAndMethodTree, SWT.NONE);
						item.setText(field.getName());
						item.setData(field);				
						item.setImage(filedImage);	
					}
				}else if(obj instanceof MethodInfo){
					MethodInfo fdata = (MethodInfo) obj;
					if("".equals(filter) || fdata.getName().toLowerCase().indexOf(filter) != -1){
						TreeItem item = new TreeItem(fieldAndMethodTree, SWT.NONE);
						item.setText(fdata.toString());
						item.setData(fdata);				
						item.setImage(methodImage);		
					}
				}
			}		
		}finally{
			actionContext.pop();
		}
	}
	
	public static void filterVar(ActionContext actionContext){
		if(actionContext.get("request") != null){
			UIRequest request = (UIRequest) actionContext.get("request");
		    FunctionRequest fnRequest = (FunctionRequest) request.getRequestMessage();
		    Object[][] variables = fnRequest.getLocalVariables();
		    Table localTable = (Table) actionContext.get("localTable");
		    for(TableItem item : localTable.getItems()){
		    	item.dispose();
		    }
		    String filter = ((Text) actionContext.get("varSerachText")).getText().toLowerCase().trim();
		    for(int i=0; i<variables.length; i++){
		    	String name = (String) variables[i][0];
		    	String value = String.valueOf(variables[i][1]);
		    	if("".equals(filter) || name.indexOf(filter) != -1){
			        TableItem item = new TableItem(localTable, SWT.NONE);
			        item.setText(new String[]{name, value});
			        item.setData(variables[i]);
		    	}
		    }
		    
		    variables = fnRequest.getGlobalVariables();
		    Table globalTable = (Table) actionContext.get("globalTable");
		    for(TableItem item : globalTable.getItems()){
		    	item.dispose();
		    }
		    for(int i=0; i<variables.length; i++){
		        String name = (String) variables[i][0];
		    	String value = String.valueOf(variables[i][1]);
		    	if("".equals(filter) || name.indexOf(filter) != -1){
			        TableItem item = new TableItem(globalTable, SWT.NONE);
			        item.setText(new String[]{name, value});
			        item.setData(variables[i]);
		    	}
		    }
		}
	}
}
