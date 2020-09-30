package xworker.io.netty;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;

public class EmbeddedChannelActions {
	public static EmbeddedChannel create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		List<ChannelHandler> hls = new ArrayList<ChannelHandler>();
		for(Thing handlers : self.getChilds("Handlers")) {								
			for(Thing handlerThing : handlers.getChilds()) {
				Object handler = handlerThing.doAction("create", actionContext);
				if(handler instanceof ChannelHandler) {					
					hls.add((ChannelHandler) handler);
				}
			}
		}
		
		ChannelHandler hs[] = new ChannelHandler[hls.size()];
		hls.toArray(hs);
		return new EmbeddedChannel(hs);
	}
}
