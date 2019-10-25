package xworker.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class DragUtil {
	private static Logger logger = LoggerFactory.getLogger(DragUtil.class);
	
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	Thing thing;
	ActionContext actionContext;
	
	boolean dragging  = false;
	
	public DragUtil(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public static DragUtil create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DragUtil util = new DragUtil(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), util);
		
		return util;
	}
	
	public void handleEvent(Event event){
		switch(event.type){
		case SWT.MouseDown:
			if(event.button == 1){
				dragging = true;
				startX = event.x;
				startY = event.y;
				endX = event.x;
				endY = event.y;
				
				try{
					thing.doAction("dragStart", actionContext, UtilMap.toMap("drag", this, "event", event));
				}catch(Exception e){
					logger.error("dragStart error, path=" + thing.getMetadata().getPath(), e);
				}
			}
			
			break;
		case SWT.MouseMove:
			if(dragging){
				endX = event.x;
				endY = event.y;
				
				try{
					thing.doAction("dragMove", actionContext, UtilMap.toMap("drag", this, "event", event));
				}catch(Exception e){
					logger.error("dragMove error, path=" + thing.getMetadata().getPath(), e);
				}
			}
			break;
		case SWT.MouseUp:
			try{
				thing.doAction("dragEnd", actionContext, UtilMap.toMap("drag", this, "event", event));
			}catch(Exception e){
				logger.error("dragEnd error, path=" + thing.getMetadata().getPath(), e);
			}
			dragging = false;
			break;
		}
	}
	
	public int getWidth(){
		if(dragging){
			return endX - startX;
		}else{
			return 0;
		}
	}
	
	public int getHeight(){
		if(dragging){
			return endY - startY;
		}else{
			return 0;
		}
	}
	
	public void reset(){
		startX = endX;
		startY = endY;
	}
}
