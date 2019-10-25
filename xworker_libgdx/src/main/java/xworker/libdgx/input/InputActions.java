package xworker.libdgx.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class InputActions {
	private static Logger logger = LoggerFactory.getLogger(InputActions.class);
	
	public static void keySwitch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Object event = actionContext.get("event");
		if(event instanceof InputEvent){
			InputEvent e = (InputEvent) event;
			String keyName = Input.Keys.toString(e.getKeyCode());
			if(UtilAction.getDebugLog(self, actionContext)){
				logger.info("KeyCode=" + e.getKeyCode() + ": KeyName=" + keyName);
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
			logger.warn("Event is not InputEvent, path=" + self.getMetadata().getPath());
		}
	}
}
