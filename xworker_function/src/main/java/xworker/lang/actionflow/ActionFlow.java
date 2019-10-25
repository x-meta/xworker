package xworker.lang.actionflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ActionFlow {
	/**
	 * 获取动作流程的可选后续动作。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> getNextActions(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//获取NextActions子节点
		Thing nextActions = self.getThing("NextActions@0");
		World world = World.getInstance();
		
		if(nextActions != null){
			List<Thing> actions = new ArrayList<Thing>();
			for(Thing child : nextActions.getChilds()){
				if(child.getThingName().equals("ActionFlowReference")){
					Thing thing = world.getThing(child.getString("actionFlowPath"));
					if(thing != null){
						actions.add(thing);
					}
				}else{
					actions.add(child);
				}
			}
			
			return actions;
		}else{
			return Collections.emptyList();
		}
	}
	
	public static void start(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionFlowRunner runner = new ActionFlowRunner(actionContext);
		runner.addAction(self);
	}
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionFlowRunner runner = new ActionFlowRunner(actionContext);
		runner.actionFinished(self, null);
	}
	
	public static String accept(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Object object = actionContext.get("it");
		if(self.getBoolean("rejectNull")){
			if(object == null){
				return self.getString("rejectMessage");
			}			
		}
		
		return null;
	}
}
