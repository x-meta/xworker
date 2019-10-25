package xworker.lang.flow.uiflow.nodes.base;

import java.util.Iterator;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilJava;

import xworker.lang.flow.uiflow.ActionFlow;
import xworker.lang.flow.uiflow.FlowUtils;
import xworker.lang.flow.uiflow.IFlow;

public class IteratorActions {
	public static void flowRun(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		IFlow uiFlow = actionContext.getObject("uiFlow");
		
		//获取要遍历的对象
		Object iterObj = self.doAction("getIterable", actionContext);
		Iterable<Object> iter = UtilJava.getIterable(iterObj);
		Iterator<Object> iterator = iter.iterator();
		int index = 0;
		Thing next = FlowUtils.getNextFlowNode(self, "next");
		while(iterator.hasNext()){
			Object obj = iterator.next();
			boolean hasNext = iterator.hasNext();
			
			if(next != null){
				String name = next.getStringBlankAsNull("itname");
				if(name == null){
					name = "it";
				}
				Bindings bindings = actionContext.push();
				bindings.put(name, obj);
				bindings.put(name + "_index", index);
				bindings.put(name + "_has_next", hasNext);
				try{
					ActionFlow acFlow = new ActionFlow(next, actionContext);
					acFlow.start(next);
				}finally{
					actionContext.pop();
				}
			}
		}
		
		//遍历完毕
		uiFlow.nodeFinished(self, "finish");
	}
}
