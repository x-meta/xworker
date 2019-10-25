package xworker.lang.function.java;

import java.lang.reflect.Modifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import xworker.dataObject.DataObject;
import xworker.java.assist.MethodInfo;
import xworker.java.assist.ParameterInfo;
import xworker.swt.util.ResourceManager;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionRequest;

public class NewInstanceActions {
	public static void classTableSelection(ActionContext actionContext) throws NotFoundException, CannotCompileException{
		Table classTable = (Table) actionContext.get("classTable");
		TableItem item = classTable.getSelection()[0];
		
		DataObject obj = (DataObject) item.getData();
		String className = (String) obj.get("name");
		
		Tree fieldAndMethodTree = (Tree) actionContext.get("fieldAndMethodTree");
		for(TreeItem titem : fieldAndMethodTree.getItems()){
			titem.dispose();
		}
		
		Button okButton = (Button) actionContext.get("okButton");
		okButton.setEnabled(false);
		
		CtClass ctClass = ClassPool.getDefault().get(className);
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", fieldAndMethodTree);
			
			Image methodImage = (Image) ResourceManager.createIamge("/icons/cog_go.png", actionContext);

			for(CtConstructor ct : ctClass.getDeclaredConstructors()){
				if(Modifier.isPublic(ct.getModifiers())){
					TreeItem mitem = new TreeItem(fieldAndMethodTree, SWT.NONE);
					CtMethod method = ct.toMethod(ctClass.getSimpleName(), ctClass);
					MethodInfo methodInfo = new MethodInfo(method);
					mitem.setText(methodInfo.toString());
					mitem.setData(methodInfo);				
					mitem.setImage(methodImage);	
				}
			}				
		}finally{
			actionContext.pop();
		}
		
	}
	
	public static void okButtonSelection(ActionContext actionContext){
		Tree methodTree = (Tree) actionContext.get("fieldAndMethodTree");
		MethodInfo methodInfo = (MethodInfo) methodTree.getSelection()[0].getData();
		
		Thing methodInvoker = new Thing("xworker.lang.function.java.Ognl");
		String exp = "new " + methodInfo.getCtClass().getName() + "(";

		int index = 0;
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			if(index > 0){
				exp = exp + ",";
			}
			
			exp = exp +  parameterInfo.getName();
			index++;
		}
		
		exp = exp + ")";
		methodInvoker.set("expression", exp);

		// 设置参数
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = new Thing("xworker.lang.function.Parameter");
			param.set("name", parameterInfo.getName());
			methodInvoker.addChild(param);
		}
		
		//设置回调
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		fnRequest.resetFunctionThing(methodInvoker, false);

		//先刷新函数树
		FunctionManager.sendRequest(fnRequest, actionContext);
		//再执行新的函数
		fnRequest.run(actionContext);
	}

}
