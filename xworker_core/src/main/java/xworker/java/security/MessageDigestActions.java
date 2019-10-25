package xworker.java.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class MessageDigestActions {
	public static Object run(ActionContext actionContext) throws NoSuchAlgorithmException, IOException{
		Thing self = actionContext.getObject("self");
		
		String algorithm = (String) self.doAction("getAlgorithm", actionContext);
		Object input = self.doAction("getInput", actionContext);
		String resultType = (String) self.doAction("getResultType", actionContext);
		
		MessageDigest digest = MessageDigest.getInstance(algorithm);		
		if(input instanceof Byte[]){
			digest.update((byte[]) input);
		}else if(input instanceof File){
			File file = (File) input;
			FileInputStream fin = new FileInputStream(file);
			try{
				byte[] bytes = new byte[4096];
				int l = -1;
				while((l = fin.read(bytes)) != -1){
					digest.update(bytes, 0, l);
				}
			}finally{
				fin.close();
			}
		}else if(input instanceof InputStream){
			InputStream fin = (InputStream) input;
			try{
				byte[] bytes = new byte[4096];
				int l = -1;
				while((l = fin.read(bytes)) != -1){
					digest.update(bytes, 0, l);
				}
			}finally{
				fin.close();
			}
		}else{
			String value = String.valueOf(input);
			digest.update(value.getBytes());
		}
		
		byte messageDigest[] = digest.digest();	
		if("hexUpperCase".equals(resultType)){
			return UtilString.toHexString(messageDigest).toUpperCase();
		}else if("hexLowerCase".equals(resultType)){
			return UtilString.toHexString(messageDigest).toLowerCase();
		}else{
			return messageDigest;
		}
	}
}
