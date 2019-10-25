package xworker.libdgx.scenes.scene2d;


import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StageActions {
	public static Stage create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Stage stage = null;
		String constructor = self.getString("constructor");
		if("viewport".equals(constructor)){
			Viewport viewport = (Viewport) actionContext.get(self.getString("viewport"));
			stage = new Stage(viewport);
		}else if("viewport_batch".equals(constructor)){
			Viewport viewport = (Viewport) actionContext.get(self.getString("viewport"));
			Batch batch = (Batch) actionContext.get("batch");
			stage = new Stage(viewport, batch);
		}else{
			stage = new Stage();
		}
		
		Bindings bindings = actionContext.push();
		bindings.put("parent", stage);
		try{
			for(Thing child : self.getChilds()){				
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof Actor){
					stage.addActor((Actor) obj);
				}
			}
		}finally{
			actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), stage);
		
		return stage;
	}
}
