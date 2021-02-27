package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import xworker.javafx.util.JavaFXUtils;

public class ScrollPaneActions {
	public static void init(ScrollPane pane, Thing thing, ActionContext actionContext) {
		ControlActions.init(pane, thing, actionContext);
		
        if(thing.valueExists("content")){
        	Node content = (Node) UtilData.getData(thing.getString("content"), actionContext);
        	if(content != null) {
        		pane.setContent(content);
        	}
        }
        if(thing.valueExists("fitToHeight")){
            pane.setFitToHeight(thing.getBoolean("fitToHeight"));
        }
        if(thing.valueExists("fitToWidth")){
            pane.setFitToWidth(thing.getBoolean("fitToWidth"));
        }
        if(thing.valueExists("hbarPolicy")){
            pane.setHbarPolicy(ScrollBarPolicy.valueOf(thing.getString("hbarPolicy")));
        }
        if(thing.valueExists("hmax")){
            pane.setHmax(thing.getDouble("hmax"));
        }
        if(thing.valueExists("hmin")){
            pane.setHmin(thing.getDouble("hmin"));
        }
        if(thing.valueExists("hvalue")){
            pane.setHvalue(thing.getDouble("hvalue"));
        }
        if(thing.valueExists("minViewportHeight")){
            pane.setMinViewportHeight(thing.getDouble("minViewportHeight"));
        }
        if(thing.valueExists("minViewportWidth")){
            pane.setMinViewportWidth(thing.getDouble("minViewportWidth"));
        }
        if(thing.valueExists("pannable")){
            pane.setPannable(thing.getBoolean("pannable"));
        }
        if(thing.valueExists("prefViewportHeight")){
            pane.setPrefViewportHeight(thing.getDouble("prefViewportHeight"));
        }
        if(thing.valueExists("prefViewportWidth")){
            pane.setPrefViewportWidth(thing.getDouble("prefViewportWidth"));
        }
        if(thing.valueExists("vbarPolicy")){
            pane.setVbarPolicy(ScrollBarPolicy.valueOf(thing.getString("vbarPolicy")));
        }
        if(thing.valueExists("viewportBounds")){
        	Bounds viewportBounds = JavaFXUtils.getObject(thing, "viewportBounds", actionContext);
        	if(viewportBounds != null) {
        		pane.setViewportBounds(viewportBounds);
        	}
        }
        if(thing.valueExists("vmax")){
            pane.setVmax(thing.getDouble("vmax"));
        }
        if(thing.valueExists("vmin")){
            pane.setVmin(thing.getDouble("vmin"));
        }
        if(thing.valueExists("vvalue")){
            pane.setVvalue(thing.getDouble("vvalue"));
        }
	}
	
	public static ScrollPane create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ScrollPane item = new ScrollPane();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Node) {
				item.setContent((Node) obj);
			}
		}
		
		return item;
	}
}
