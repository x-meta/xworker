package xworker.ui.function.functionUtils;

import java.util.Map;

import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.UIHandler;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIFactory;
import xworker.ui.function.FunctionRequestUtil;

public class SetParameterWithRegistedFunctions {
	public static void doFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");

		//选择函数的界面
		UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.ui.function.functionUtils.shells.SetParameterWithRegistedFunctionsShell/@composite"), actionContext);
		uiRequest.setRequestMessage(request);
		
		//注册的事物，就是函数本身，这样是为了方便		
		uiRequest.setFinishedMessage(request.getDescriptor().getMetadata().getPath());
		uiRequest.getFinishedMessage();
		
		uiRequest.setRequestCallback(true);
		FunctionRequestUIFactory.requestUI(request, "xworker_session_swt_dialogComposite", uiRequest, actionContext);

		
		
	}
	
	public static Object doCheck(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		if(request.getCallback() == null || request.getCallback().getRequest() == null){
			return "SetParameterWithRegistedFunctions函数只能是一个参数的函数。";
		}else{
			return null;
		}
	}
	
	public static void init(ActionContext actionContext){
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		UIHandler helpBrowserUIHanlder = (UIHandler) actionContext.get("helpBrowserUIHandler");
		//显示outline的文档
		FunctionRequestUtil.initFunctionRequestHtml(fnRequest, helpBrowserUIHanlder, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void okButtonSelection(ActionContext actionContext){
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		Tree functionsTree = (Tree) actionContext.get("functionsTree");
		Map<String, Object> data = (Map<String, Object>) functionsTree.getSelection()[0].getData();
		String id = (String) data.get("id");
		if(id.startsWith("thing:")){
			String thingPath = id.substring(6, id.length());
			FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
			FunctionRequest parentRequest = fnRequest.getCallback().getRequest();
			FunctionParameter parameter = fnRequest.getCallback().getParam();
			parameter.setValueThing(new Thing(thingPath), null);
			parentRequest.setFocusedParam(parameter);
			FunctionParameter unReadyParam = parameter.getValueRequest().getFirstUnReadyParam();
			if(unReadyParam != null){
				//如果新的参数的函数还有参数需要设置，那么设置参数
				FunctionManager.sendParameterRequest(unReadyParam, actionContext);
			}else{
				//先刷新函数树
				FunctionManager.sendRequest(parentRequest, actionContext);
				//再执行新的函数
				parameter.run();
			}
		}
	}
}
