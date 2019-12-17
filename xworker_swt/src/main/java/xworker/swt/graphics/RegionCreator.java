package xworker.swt.graphics;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;

public class RegionCreator {
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Widget parent = (Widget) actionContext.get("parent");
		
		//创建Region对象
		final Region region =new Region();
		parent.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				region.dispose();
			}
		});
		
		//添加自身定义的区域
		String regions = self.getStringBlankAsNull("regions");
		if(regions != null){
			String[] ss = regions.split("[,]");
			int[] rs = new int[ss.length];
			for(int i=0;i<ss.length; i++){
				rs[i] = Integer.parseInt(ss[i].trim());
			}
			region.add(rs);
		}
		
		//执行子节点
		actionContext.peek().put("region", region);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		//绑定到Shell
		if(parent instanceof Shell && SwtUtils.isRWT() == false){
			((Shell) parent).setRegion(region);
		}

	}
	
	public static void add(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Region region = actionContext.getObject("region");
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof int[]) {
				region.add((int[]) obj);
			}else if(obj instanceof Rectangle) {
				region.add((Rectangle) obj);
			}else if(obj instanceof Region) {
				region.add((Region) obj);
			}
		}
	}
	
	public static void intersect(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Region region = actionContext.getObject("region");
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof int[]) {
				//region.intersect((int[]) obj);
			}else if(obj instanceof Rectangle) {
				region.intersect((Rectangle) obj);
			}else if(obj instanceof Region) {
				region.intersect((Region) obj);
			}
		}
	}
	
	public static void subtract(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Region region = actionContext.getObject("region");
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof int[]) {
				region.subtract((int[]) obj);
			}else if(obj instanceof Rectangle) {
				region.subtract((Rectangle) obj);
			}else if(obj instanceof Region) {
				region.subtract((Region) obj);
			}
		}
	}
	
	public static int[] createPoints(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String points = self.doAction("getPoints", actionContext);
		if(points != null) {
			String[] ss = points.split("[,]");
			int[] rs = new int[ss.length];
			for(int i=0;i<ss.length; i++){
				rs[i] = Integer.parseInt(ss[i].trim());
			}
			
			return rs;
		}
		
		return null;
	}
	
	public static int[] createCircle(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int r = self.doAction("getRadius", actionContext);
		int offsetX = self.doAction("getOffsetX", actionContext);
		int offsetY = self.doAction("getOffsetY", actionContext);
		
		int[] polygon = new int[8 * r + 4];
	    // x^2 + y^2 = r^2
	    for (int i = 0; i < 2 * r + 1; i++) {
	      int x = i - r;
	      int y = (int) Math.sqrt(r * r - x * x);
	      polygon[2 * i] = offsetX + x;
	      polygon[2 * i + 1] = offsetY + y;
	      polygon[8 * r - 2 * i - 2] = offsetX + x;
	      polygon[8 * r - 2 * i - 1] = offsetY - y;
	    }
	    return polygon;
	}
	
	public static Rectangle createRectangle(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int x = self.doAction("getX", actionContext);
		int y = self.doAction("getY", actionContext);
		int width = self.doAction("getWidth", actionContext);
		int height = self.doAction("getHeight", actionContext);
		
		return new Rectangle(x, y, width, height);
	}
}
