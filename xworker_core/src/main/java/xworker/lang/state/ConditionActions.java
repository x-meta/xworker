package xworker.lang.state;

import java.util.List;
import java.util.Random;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class ConditionActions {
	public static void execAbstractCondition(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		if(UtilData.isTrue(self.doAction("validate", actionContext))) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
	
	public static void execActionCondition(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		if(UtilData.isTrue(self.getAction().run(actionContext))) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
	
	public static void execRandom(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		Integer n = self.doAction("getN", actionContext);
		if(n == 0 || n <= 0) {
			n = 2;
		}
		Random r = new Random();		
		if(r.nextInt(n) == 0) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
	
	public static void execIs(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		List<String> keys = self.doAction("getKeys", actionContext);
		Boolean exists = self.doAction("isExists", actionContext);
		boolean ok = false;
		for(String key : keys) {
			ok = state.is(key);
			
			if(exists != null && exists) {
				if(ok) {
					break;
				}
			}else {
				if(ok == false) {
					break;
				}
			}
		}
		
		if(ok) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
	
	public static void execNot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		List<String> keys = self.doAction("getKeys", actionContext);
		Boolean exists = self.doAction("isExists", actionContext);
		boolean ok = false;
		for(String key : keys) {
			ok = state.not(key);
			
			if(exists != null && exists) {
				if(ok) {
					break;
				}
			}else {
				if(ok == false) {
					break;
				}
			}
		}
		
		if(!ok) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
	
	public static void execExists(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		List<String> keys = self.doAction("getKeys", actionContext);
		Boolean exists = self.doAction("isExists", actionContext);
		boolean ok = false;
		for(String key : keys) {
			ok = state.exists(key);
			
			if(exists != null && exists) {
				if(ok) {
					break;
				}
			}else {
				if(ok == false) {
					break;
				}
			}
		}
		
		if(ok) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
	
	public static void execNotExists(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		List<String> keys = self.doAction("getKeys", actionContext);
		Boolean exists = self.doAction("isExists", actionContext);
		boolean ok = false;
		for(String key : keys) {
			ok = !state.exists(key);
			
			if(exists != null && exists) {
				if(ok) {
					break;
				}
			}else {
				if(ok == false) {
					break;
				}
			}
		}
		
		if(ok) {
			for(Thing child : self.getChilds()) {
				Object result = child.doAction("exec", actionContext);
				state.set(child.getMetadata().getName(), result);
				if(state.doit == false) {
					return;
				}
			}
		}
	}
}
