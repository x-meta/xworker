package xworker.lang.flow.uiflow.nodes;

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.flow.uiflow.ActionFlow;
import xworker.lang.flow.uiflow.UiFlow;
import xworker.lang.flow.uiflow.UiFlowActions;

public class NoUiFlow {
	public static void flowRun(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Boolean newActionContext = (Boolean) self.doAction("isNewActionContext", actionContext);
		ActionContext ac = actionContext;
		if(newActionContext){
			ac = new ActionContext();
			ac.put("parentContext", actionContext);
			
		}
		
		//创建无界面的动作流程，启动自己
		ActionFlow acFlow = new ActionFlow(self, ac);
		acFlow.nodeFinished(self, "NOUIFLOW");
		
		//原有界面流程的流向
		UiFlow uiFlow = actionContext.getObject("uiFlow");
		uiFlow.nodeFinished(self, "OLDFLOW");
	}
	
	public static void initParams(ActionContext actionContext){
		ActionContext parentContext = actionContext.getObject("parentContext");
		
		UiFlow uiFlow = actionContext.getObject("uiFlow");
		List<Map<String, String>> params = UiFlowActions.parseParams(uiFlow.getThing().getStringBlankAsNull("params"));
		if(params != null){
			for(Map<String, String> param : params){
				uiFlow.set(param.get("label"), parentContext.get(param.get("name")));
			}
		}
	}
}
