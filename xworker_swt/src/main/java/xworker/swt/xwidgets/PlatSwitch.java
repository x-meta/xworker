package xworker.swt.xwidgets;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;

public class PlatSwitch {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		World world = World.getInstance();
		String type = SwtUtils.isRWT() ? "RWT" : "SWT";
		String os = world.getOS();
		String arc = world.getJVMBit();
		String os_arc = os + "_" + arc;
		
		Object result = null;
		for(Thing child : self.getChilds("Case")) {
			String name = child.getMetadata().getName();
			boolean brk = false;
			if(type.equals(name) || os.equals(name) || os_arc.equals(name)) {
				for(Thing c : child.getChilds()) {
					if("BREAK".equals(c.getThingName())) {
						brk = true;
						break;
					}
					
					result = c.doAction("create", actionContext);
				}
			}
			
			if(brk) {
				break;
			}
		}
		
		return result;
	}
}
