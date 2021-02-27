package xworker.io.netty.demo;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class NettyDemo {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.netty.demo.prototypes.NettyDemoShell/@mainTabFolder"));
		//设置参数
		cc.getNewActionContext().put("nettyServerThing", self.doAction("getNettyServer", actionContext));
		cc.getNewActionContext().put("nettyClientThing", self.doAction("getNettyClient", actionContext));
		
		return cc.create();
	}
}
