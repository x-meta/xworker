package xworker.io.netty.handlers;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class TimeoutHandler extends ChannelDuplexHandler {
	Thing thing;
	ActionContext actionContext;
	
	public TimeoutHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
        	    thing.doAction("onReaderIdle", actionContext, "ctx", ctx, "evt", evt);
            } else if (e.state() == IdleState.WRITER_IDLE) {
            	thing.doAction("onWriterIdle", actionContext, "ctx", ctx, "evt", evt);
            } else if(e.state() == IdleState.ALL_IDLE) {
            	thing.doAction("onAllIdle", actionContext, "ctx", ctx, "evt", evt);
            }
        }
    }

    public static void onReaderIdle(ActionContext actionContext) {
    	ChannelHandlerContext ctx = actionContext.getObject("ctx");
    	ctx.close();
    }
    
    public static TimeoutHandler create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	
    	return new TimeoutHandler(self, actionContext);
    }
}
