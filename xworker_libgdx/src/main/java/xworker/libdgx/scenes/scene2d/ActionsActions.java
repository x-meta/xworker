package xworker.libdgx.scenes.scene2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ActionsActions {
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Object parent = actionContext.get("parent");
		for(Thing child : self.getChilds()){
			Object ac = child.doAction("create", actionContext);
			if(ac instanceof Action){
				Action action = (Action) ac;
				if(parent != null){
					if(parent instanceof Stage){
						((Stage) parent).addAction(action);
					}else if(parent instanceof Group){
						((Group) parent).addAction(action);
					}else if(parent instanceof Actor){
						((Actor) parent).addAction(action);
					}
				}
			}
		}
	}
}
