package xworker.lang.function.scripts;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.actions.ActionContainer;
import xworker.swt.xworker.CodeAssistor;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIFactory;
import xworker.ui.function.FunctionRequestUtil;

public class ScriptsFunction {
	public static Object doGroovyFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FunctionRequest fnRequest = (FunctionRequest) actionContext.get("request");
		if(fnRequest == null){
			return self.doAction("run", actionContext);
		}
		
		Thing groovyThing = fnRequest.getThing();
		if(groovyThing.getStringBlankAsNull("code") == null){
			//Groovy脚本还未编辑
			actionContext.peek().put("exception", null);
			sendUIRequest(fnRequest, "xworker.lang.function.scripts.GroovyFunctionComposite/@composite", null, actionContext);
			return null;
		}
		
		//保存参数变量
		Bindings bindings = actionContext.peek();
		for(FunctionParameter param : fnRequest.getParameters()){
			bindings.put(param.getName(), param.getValue());
		}
		
		if(self.getStringBlankAsNull("code") == null){
			actionContext.peek().put("exception", null);
			sendUIRequest(fnRequest, "xworker.lang.function.scripts.GroovyFunctionComposite/@composite", null, actionContext);
			return null;
		}
		
		//执行Groovy脚本
		try{
			Object value = self.doAction("run", actionContext);
			if(fnRequest.getCallback() != null){
				fnRequest.getCallback().ok(value, actionContext);
			}else{
				FunctionManager.finishRequest(fnRequest, value);
			}
			
			return value;
		}catch(Exception e){
			sendUIRequest(fnRequest, "xworker.lang.function.scripts.GroovyFunctionComposite/@composite", e, actionContext);
			return null;
		}
	}
	
	public static void sendUIRequest(FunctionRequest request, String uiThingPath, Throwable exception, ActionContext actionContext){
		UIRequest uiRequest = new UIRequest(World.getInstance().getThing(uiThingPath), actionContext);
		//proxy应该是其他函数的被调用者
		uiRequest.setRequestMessage(request);
		uiRequest.setRequestCallback(true);
		uiRequest.setData("bindings", actionContext.peek()); //保存当前的bindings，已被在ui可以重用
		uiRequest.setData("exception", exception);
		FunctionRequestUIFactory.requestUI(request, FunctionRequestUIFactory.UI_DIALOG_COMPOSITE, uiRequest, actionContext);

		//显示outline的文档
		FunctionRequestUtil.initFunctionRequestHtml(request, null, actionContext);
	}
	
	/**
	 * 在编辑代码的Composite中初始化，就是sendUIRequest那个界面。
	 * 
	 * @param actionContext
	 */
	public static void initEditor(ActionContext actionContext){
		UIRequest uiRequest = actionContext.getObject("request");
		StyledText codeText = actionContext.getObject("codeText");
		CodeAssistor codeAssistor = actionContext.getObject("codeAssistor");
		
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		ActionContext fnActionContext = fnRequest.getActionContext();
		Bindings bindings = (Bindings) uiRequest.getData("bindings");
		fnActionContext.push(bindings) ;
		try{
			codeAssistor.initCache(fnActionContext);
		}finally{
			fnActionContext.pop();
		}
		
		Thing thing = fnRequest.getThing();
		String code = thing.getStringBlankAsNull("code");
		if(code != null){
			codeText.setText(code);			
		}
		
		Throwable exception = (Throwable) uiRequest.getData("exception");
		if(exception != null){
			ActionContainer exceptionText = actionContext.getObject("exceptionText");
			exceptionText.doAction("setThrowable", UtilMap.toMap("throwable", exception));
			
			TabFolder tabFolder = actionContext.getObject("tabFolder");
			TabItem item = actionContext.getObject("exceptionItem");
			
			tabFolder.setSelection(item);
		}
	}
	
	/**
	 * 编辑界面中的执行。
	 * 
	 * @param actionContext
	 */
	public static void executeButtonSelection(ActionContext actionContext){
		StyledText codeText = actionContext.getObject("codeText");
		UIRequest uiRequest = actionContext.getObject("request");
		
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		ActionContext fnActionContext = fnRequest.getActionContext();
		String code = codeText.getText();
		code = code.trim();
		Thing thing = fnRequest.getThing();
		thing.set("code", code);
		
		fnRequest.run(fnActionContext);				
	}
}
