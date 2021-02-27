package xworker.libdgx.input;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import xworker.lang.executor.Executor;

public class InputActions {
	private static final String TAG = InputActions.class.getName(); 
	
	public static void keySwitch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Object event = actionContext.get("event");
		if(event instanceof InputEvent){
			InputEvent e = (InputEvent) event;
			String keyName = Input.Keys.toString(e.getKeyCode());
			if(UtilAction.getDebugLog(self, actionContext)){
				Executor.info(TAG, "KeyCode=" + e.getKeyCode() + ": KeyName=" + keyName);
			}
			
			if(keyName != null){
				for(Thing child : self.getChilds("Case")){
					if(keyName.equals(child.getStringBlankAsNull("key"))){
						for(Thing c : child.getChilds()){
							c.getAction().run(actionContext);
						}
					}
				}
			}
		}else{
			Executor.warn(TAG, "Event is not InputEvent, path=" + self.getMetadata().getPath());
		}
	}
}
