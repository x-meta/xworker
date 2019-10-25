package xworker.gswt.actors;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Actor;
import xworker.gswt.SimpleGame;

public class StringActor extends Actor{
	String text;	
	Font font;
	
	public StringActor(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
		
		text = thing.getStringBlankAsNull("text");
		if(text == null){
			text = "还没有设置内容";
		}
		
		String f = thing.getStringBlankAsNull("font");
		if(f != null){
			font = actionContext.getObject(f);
		}
	}

	@Override
	public void render(SimpleGame game, PaintEvent event, ActionContext actionContext) {
		thing.doAction("run", actionContext);
		
		Font oldFont = event.gc.getFont();
		if(font != null && !font.isDisposed()){
			event.gc.setFont(font);
		}
		event.gc.drawText(text, x, y);
		event.gc.setFont(oldFont);
	}
	
	public static StringActor create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		StringActor actor = new StringActor(self, actionContext);
		SimpleGame game = actionContext.getObject("game");
		if(game != null){
			game.addActor(actor);
		}
		
		actionContext.g().put(self.getMetadata().getName(), actor);
		return actor;
	}

}
