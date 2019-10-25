package xworker.lang.flow.uiflow.nodes;

import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.lang.flow.uiflow.IFlow;
import xworker.lang.flow.uiflow.UiFlowActions;

/**
 * 流程节点或子流程节点。
 * 
 * @author zyx
 *
 */
public class Flow {
	public static void flowRun(ActionContext actionContext){
		Thing self = actionContext.getObject("self");		
		Thing flow = (Thing) self.doAction("getFlow", actionContext);
		
		//父流程节点
		IFlow uiFlow = actionContext.getObject("uiFlow");
		uiFlow.startChildFlow(flow);
	}
	
	public static Object getFlow(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
		if(self.getStringBlankAsNull("flowPath") == null){
			return self;
		}		
		
		Object obj = UtilData.getData(self, "flowPath", actionContext);
		if(obj instanceof Thing){
			return obj;
		}else{
			String path = String.valueOf(obj);
			return World.getInstance().getThing(path);
		}
	}
	
	public static void intiParams(ActionContext actionContext){
		IFlow uiFlow = actionContext.getObject("uiFlow");
		List<Map<String, String>> params = UiFlowActions.parseParams(uiFlow.getThing().getStringBlankAsNull("params"));
		if(params != null){
			IFlow parent = uiFlow.getParent();
			for(Map<String, String> param : params){
				uiFlow.set(param.get("label"), parent.get(param.get("name")));
			}
		}
	}
	
	public static void setReturnValues(ActionContext actionContext){
		IFlow uiFlow = actionContext.getObject("uiFlow");
		List<Map<String, String>> params = UiFlowActions.parseParams(uiFlow.getThing().getStringBlankAsNull("returnParams"));
		if(params != null){
			IFlow parent = uiFlow.getParent();
			for(Map<String, String> param : params){
				parent.set(param.get("label"), uiFlow.get(param.get("name")));
			}
		}
	}
}
