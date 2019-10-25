package xworker.gswt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

/**
 * 角色。
 * 
 * @author zyx
 *
 */
public abstract class Actor {
	public int x;
	public int y;
	public int orignX;
	public int orignY;
	public Thing thing;
	public boolean visible;	
	Map<String, Object> actionDatas = new HashMap<String, Object>();
	protected List<Action> actions = new ArrayList<Action>();
	
	public Actor(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.x = this.orignX = thing.getInt("x");
		this.y = this.orignY = thing.getInt("y");
		this.visible = thing.getBoolean("visible");
		
		for(Thing actions : thing.getChilds("Actions")){
			for(Thing acthing : actions.getChilds()){
				Action action = (Action) acthing.doAction("create", actionContext);
				if(action != null){
					this.addAction(action);
				}
			}
		}
	}
	
	public void run(SimpleGame game, PaintEvent event, ActionContext actionContext){
		Bindings bindings = actionContext.push();
		try{
			bindings.put("game", game);
			bindings.put("actor", this);
			
			//执行动作
			for(int i = 0; i<actions.size(); i++){
				Action action = actions.get(i);
				action.run(game, actionContext);
				
				if(action.finished){
					this.removeAction(action);
					i--;
				}
			}
			
			//执行绘制
			render(game, event, actionContext);
		}finally{
			actionContext.pop();
		}
	}
	
	public void addAction(Action action){
		if(!actions.contains(action)){
			action.setActor(this);
			actions.add(action);
		}
	}
	
	public void removeAction(Action action){
		actions.remove(action);
	}
	
	public abstract void render(SimpleGame game, PaintEvent event, ActionContext actionContext);
	
	@SuppressWarnings("unchecked")
	public <T> T getActionData(Thing action){
		return (T) actionDatas.get(action.getMetadata().getPath());
	}
	
	public Object removeActionData(Thing action){
		return actionDatas.remove(action.getMetadata().getPath());
	}
	
	public void putActionData(Thing action, Object value){
		actionDatas.put(action.getMetadata().getPath(), value);
	}
}
