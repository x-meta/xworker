package xworker.libdgx.scenes.scene2d;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class GroupActions {
	public static Group create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Group group = new Group();
		init(self, group, actionContext);
		//initChilds(self, group, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), group);
		return group;
	}
	
	public static void init(Thing self, Group group, ActionContext actionContext){
		if(self.getStringBlankAsNull("transform") != null){
			group.setTransform(self.getBoolean("transform"));
		}
		
		ActorActions.initActor(self, group, actionContext);
	}
	
	public static void childActorCreated(ActionContext actionContext){
		Group parent = (Group) actionContext.get("parent");
		Actor actor = (Actor) actionContext.get("actor");
		parent.addActor(actor);
	}
	
	public static void initChilds(Thing self, Group group, ActionContext actionContext){

		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", group);
			for(Thing child : self.getChilds()){
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof Actor){
					group.addActor((Actor) obj);
				}
			}
		}finally{
			actionContext.pop();
		}
	}
}
