package xworker.lang.actions;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class MapIterator {

	@SuppressWarnings("rawtypes")
	public static Object run(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");

		Map map = (Map) self.doAction("getMap", actionContext);		
		try {
			Thing childActions = self.getThing("ChildAction@0");
			if(childActions == null){
				return null;
			}
			
			String keyVarName = (String) self.doAction("getKeyVarName", actionContext);
			String valueVarName = (String) self.doAction("getValueVarName", actionContext);
			Bindings bindings = actionContext.push();
			Object result = null;
			for (Object key : map.keySet()) {
				Object b = map.get(key);
				bindings.put(keyVarName, key);
				bindings.put(valueVarName, b);
				
				for (Thing child : childActions.getChilds()) {
					// System.out.println(child.getMetadata().getPath());
					result = child.getAction().run(actionContext, null, true);

					int sint = actionContext.getStatus();
					if (sint != ActionContext.RUNNING) {
						break;
					}
				}

				int status = actionContext.getStatus();
				if (status == ActionContext.BREAK) {
					actionContext.setStatus(ActionContext.RUNNING);
					break;
				} else if (status == ActionContext.CONTINUE) {
					actionContext.setStatus(ActionContext.RUNNING);
					continue;
				} else if (status == ActionContext.RETURN) {
					break;
				}
			}

			return result;
		} finally {
			actionContext.pop();
		}
	}
}
