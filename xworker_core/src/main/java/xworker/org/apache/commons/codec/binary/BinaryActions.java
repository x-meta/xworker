package xworker.org.apache.commons.codec.binary;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BinaryActions {
	public static String encodeBase64String(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBinaryData", actionContext);
		return Base64.encodeBase64String(data);				
	}
	
	public byte[] encodeBase64Chunked(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBinaryData", actionContext);
		return Base64.encodeBase64Chunked(data);				
	}
	
	public byte[] encodeBase64URLSafe(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBinaryData", actionContext);
		return Base64.encodeBase64URLSafe(data);				
	}
	
	public String encodeBase64URLSafeString(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBinaryData", actionContext);
		return Base64.encodeBase64URLSafeString(data);				
	}
	
	public byte[] encodeBase64Integer(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		BigInteger data = self.doAction("getBigInt", actionContext);
		return Base64.encodeInteger(data);				
	}
	
	public byte[] encodeBase64(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBinaryData", actionContext);
		return Base64.encodeBase64(data);				
	}
	
	public byte[] decodeBase64(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBase64Data", actionContext);
		return Base64.decodeBase64(data);				
	}
	
	public byte[] decodeBase64String(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String data = self.doAction("getBase64String", actionContext);
		return Base64.decodeBase64(data);				
	}
	
	public BigInteger decodeBase64Integer(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getBase64Data", actionContext);
		return Base64.decodeInteger(data);				
	}
	
	public String encodeHexString(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] data = self.doAction("getData", actionContext);
		return Hex.encodeHexString(data);				
	}
	
	public byte[] decodeHex(ActionContext actionContext) throws DecoderException {
		Thing self = actionContext.getObject("self");
		
		String data = self.doAction("getHexData", actionContext);
		return Hex.decodeHex(data);		
	}
}
