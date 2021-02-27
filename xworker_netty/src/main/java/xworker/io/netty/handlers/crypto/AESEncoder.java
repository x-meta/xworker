package xworker.io.netty.handlers.crypto;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class AESEncoder extends MessageToMessageEncoder<ByteBuf> {
	private  byte[] key = null;

	public AESEncoder(byte[] key) {
		this.key = key;
	}
	
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
            throws Exception {    	
    	byte[] bytes = new byte[msg.readableBytes()];
    	msg.readBytes(bytes);
    	//msg.release();
    	
    	javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
	    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, "AES"));
	    byte[] encodedBytes = cipher.doFinal(bytes);
	    
	    
		ByteBuf outBuf = Unpooled.buffer();
    	outBuf.writeBytes(encodedBytes);
    	
    	out.add(outBuf);
    }
    
    public static AESEncoder create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	
    	byte[] key = self.doAction("getKey", actionContext);
    	
    	return new AESEncoder(key);
    }
}