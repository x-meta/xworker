package xworker.lang.flow.uiflow;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public abstract class AbstractUiFlow implements IFlow{
	Map<String, Map<String, Object>> flowNodeData = new HashMap<String, Map<String, Object>>();
	Thing thing;
	ActionContext actionContext;
	
	public AbstractUiFlow(Thing thing, ActionContext actionContext){
		this.thing = thing;
		if(this.thing == null) {
			this.thing = new Thing("xworker.lang.flow.uiflow.UiFlow");
		}
		this.actionContext = actionContext;
	}

	@Override
	public void set(Thing flowNode, String key, Object value) {
		String path = flowNode.getMetadata().getPath();
		Map<String, Object> data = flowNodeData.get(path);
		if(data == null){
			data = new HashMap<String, Object>();
			flowNodeData.put(path, data);
		}
		
		data.put(key, value);
	}

	@Override
	public Object get(Thing flowNode, String key) {
		String path = flowNode.getMetadata().getPath();
		Map<String, Object> data = flowNodeData.get(path);
		if(data != null){
			return data.get(key);
		}else{
			return null;
		}
	}

	public Thing getThing() {
		return thing;
	}
	
	public void save(){
		FlowUtils.saveFlow(thing);
	}
}
