package xworker.gswt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class SimpleGame implements PaintListener, Runnable, DisposeListener{
	private static Logger logger = LoggerFactory.getLogger(SimpleGame.class);
	
	public Thing thing;
	public ActionContext actionContext;
	public Canvas canvas;	
	private boolean started = false;
	public long delta = 0;
	List<Actor> actors = new ArrayList<Actor>();
	
	public SimpleGame(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		create();
		start();
	}
	
	public void addActor(Actor actor){
		if(!actors.contains(actor)){
			actors.add(actor);
		}
	}
	
	public boolean removeActor(Actor actor){
		return actors.remove(actor);
	}
	
	public void create(){
		Composite parent = actionContext.getObject("parent");
		canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
		canvas.addPaintListener(this);
		canvas.addDisposeListener(this);
		
		Bindings bindings = actionContext.push();
		try{
			bindings.put("game", this);
			bindings.put("parent", canvas);
			
			for(Thing child : thing.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
	}
	
	public void start(){
		if(!started){
			started = true;
			new Thread(this).start();
		}
	}
	
	public void stop(){
		started = false;
	}
	
	public void run(){
		long lastTime = 0;		
		
		while(true){
			if(!started){
				break;
			}
			
			try{
				if(lastTime == 0){
					delta = 0;
				}else{
					long d = System.currentTimeMillis() - lastTime;
					long interval = 1000 / 60 - d;
					if(interval > 0){
						Thread.sleep(interval);
					}		
					delta = System.currentTimeMillis() - lastTime;
				}
				lastTime = System.currentTimeMillis();
				
				//重绘Canvas
				if(!canvas.isDisposed()){
					canvas.getDisplay().asyncExec(new Runnable(){
						public void run(){
							if(canvas.isDisposed() == false){
								canvas.redraw();
							}
						}
					});
				}				
			}catch(Exception e){
				logger.error("simple game run error", e);
				break;
			}
		}
	}

	@Override
	public void paintControl(PaintEvent event) {
		try{
			event.gc.fillRectangle(event.x, event.y, event.width, event.height);
			
			for(Actor actor : actors){
				actor.run(this, event, actionContext);
			}
		}catch(Exception e){
			logger.error("simple game redraw error", e);
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		stop();
	}
	
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		SimpleGame game = new SimpleGame(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), game);
		
		return game.canvas;
	}
}
