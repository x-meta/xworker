package xworker.io.netty.handlers.codec.protobuf;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class SelfProtobufEncoder extends MessageToMessageEncoder<Message> {
	boolean simpleName = false;
	
	public SelfProtobufEncoder(boolean simpleName) {
		this.simpleName = simpleName;
	}
	
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out)
            throws Exception {
    	
    	//动作
    	ByteBuf actionBuf = Unpooled.buffer();
    	String name = null;
    	if(simpleName) {
    		name = msg.getClass().getSimpleName();
    	}else {
    		name = msg.getClass().getName();
    	}
    	
    	byte[] action = name.getBytes();
    	actionBuf.writeByte(action.length);
    	actionBuf.writeBytes(action);
    	
    	//消息体
    	ByteBuf bodyBuf = wrappedBuffer(msg.toByteArray());
    	out.add(wrappedBuffer(actionBuf, bodyBuf));
    }
    
    public static SelfProtobufEncoder create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	boolean simpleName = UtilData.isTrue(self.doAction("isSimpleName", actionContext));
    	
    	return new SelfProtobufEncoder(simpleName);
    }
}
