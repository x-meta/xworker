package xworker.gswt.actors;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Actor;
import xworker.gswt.SimpleGame;

public class ImageActor extends Actor{
	Image image;	
	
	public ImageActor(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
		
		image = actionContext.getObject(thing.getString("image"));				
	}

	@Override
	public void render(SimpleGame game, PaintEvent event, ActionContext actionContext) {
		thing.doAction("run", actionContext);
		
		if(image != null && !image.isDisposed()){
			event.gc.drawImage(image, x, y);
		}
	}
	
	public static ImageActor create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ImageActor actor = new ImageActor(self, actionContext);
		SimpleGame game = actionContext.getObject("game");
		if(game != null){
			game.addActor(actor);
		}
		
		actionContext.g().put(self.getMetadata().getName(), actor);
		return actor;
	}
}
