package xworker.swt.graphics;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.RegionUtils;
import xworker.swt.util.SwtUtils;

public class PathActions {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Control parent = actionContext.getObject("parent");
		Path parentPath = actionContext.getObject("path");
		
		Path path = new Path(parent.getDisplay());
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext, "path", path);
		}
		
		actionContext.g().put(self.getMetadata().getName(), path);		
		
		if(parentPath != null) {
			parentPath.addPath(path);
		}
		
		Region region = actionContext.getObject("region");
		if(region != null) {
			RegionUtils.addPathToRegion(region, path);
		}
	}
	
	public static void createArc(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);
		Float width = self.doAction("getWidth", actionContext);
		Float height = self.doAction("getHeight", actionContext);
		Float startAngle = self.doAction("getStartAngle", actionContext);
		Float arcAngle = self.doAction("getArcAngle", actionContext);

		path.addArc(x, y, width, height, startAngle, arcAngle);
	}
	
	public static void createRectangle(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);
		Float width = self.doAction("getWidth", actionContext);
		Float height = self.doAction("getHeight", actionContext);


		path.addRectangle(x, y, width, height);
	}
	
	public static void createString(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		String string = self.doAction("getString", actionContext);
		if(string == null || "".equals(string)) {
			return;
		}
		
		Object fontObj = self.doAction("getFont", actionContext);
		Font font = path.getDevice().getSystemFont();
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);
		
		if(fontObj instanceof Font) {
			font = (Font) fontObj;
		}else if(fontObj instanceof String) {
			font = (Font) StyleSetStyleCreator.createResource((String) fontObj, 
	                "xworker.swt.graphics.Font", "fontData", actionContext);
		}
		
		path.addString(string, x, y, font);
	}
	
	public static void createCubicTo(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		
		Float cx1 = self.doAction("getCx1", actionContext);
		Float cy1 = self.doAction("getCy1", actionContext);
		Float cx2 = self.doAction("getCx2", actionContext);
		Float cy2 = self.doAction("getCy2", actionContext);
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);

		path.cubicTo(cx1, cy1, cx2, cy2, x, y);
	}
	
	public static void createLineTo(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);

		path.lineTo(x, y);
	}
	
	public static void createMoveTo(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);

		path.moveTo(x, y);
	}
	
	public static void createQuadTo(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Path path = actionContext.getObject("path");
		
		Float cx = self.doAction("getCx", actionContext);
		Float cy = self.doAction("getCy", actionContext);
		Float x = self.doAction("getX", actionContext);
		Float y = self.doAction("getY", actionContext);
		
		path.quadTo(cx, cy, x, y);
	}
}
