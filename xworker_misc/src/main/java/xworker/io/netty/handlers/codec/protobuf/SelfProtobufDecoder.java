package xworker.io.netty.handlers.codec.protobuf;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import xworker.com.google.proto.ProtobufMessageFactory;
import xworker.lang.executor.Executor;

@Sharable
public class SelfProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
	private static final String TAG = SelfProtobufDecoder.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	
	ProtobufMessageFactory factory;
	
    /**
     * Creates a new instance.
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    public SelfProtobufDecoder(Thing thing, ActionContext actionContext) throws NoSuchMethodException, SecurityException {
    	this.thing = thing;
    	this.actionContext = actionContext;
    	
    	factory = new ProtobufMessageFactory(thing, actionContext);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
            throws Exception {
    	//动作
    	int actionLength = msg.readByte() & 0xFF;
    	byte[] actionBytes = new byte[actionLength];
    	msg.readBytes(actionBytes);    	
    	String messageType = new String(actionBytes); 
    	
    	//消息体
        final byte[] array;
        final int offset;
        final int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
            offset = 0;
        }
        
        Object messageObj = factory.decoce(messageType, array, offset, length);
        if(messageObj != null) {
        	out.add(messageObj);
        }
    }

    public static void undecoded(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	
    	String messageType = actionContext.getObject("messageType");
    	Throwable cause = actionContext.getObject("cause");
    	
    	Executor.warn(TAG, "Message undecoded, thing=" + self.getMetadata().getPath() + "type=" +  messageType,  cause); 
    }
    
    public static SelfProtobufDecoder create(ActionContext actionContext) throws NoSuchMethodException, SecurityException {
    	Thing self = actionContext.getObject("self");
    	Class<?>[] classes = self.doAction("getMessageClasses", actionContext);
    	
    	SelfProtobufDecoder decoder = new SelfProtobufDecoder(self, actionContext);
    
    	return decoder;
    }
}
