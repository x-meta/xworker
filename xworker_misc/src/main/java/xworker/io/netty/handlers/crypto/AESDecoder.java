package xworker.io.netty.handlers.crypto;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

@Sharable
public class AESDecoder  extends MessageToMessageDecoder<ByteBuf> {
	private byte[] key = null;
	
    /**
     * Creates a new instance.
     */
    public AESDecoder(byte[] key) {
    	this.key = key;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
            throws Exception {
    	
    	//加密的内容
    	byte[] bytes = new byte[msg.readableBytes()];
    	msg.readBytes(bytes);
    	//msg.release();
    	
    	javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, "AES"));
		byte[] decodedBytes = cipher.doFinal(bytes);
		
		ByteBuf newMsg = Unpooled.wrappedBuffer(decodedBytes);
		out.add(newMsg);	    
    }

    public static AESDecoder create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	byte[] key = self.doAction("getKey", actionContext);
    	return new AESDecoder(key);
    }
}
