package xworker.java.security;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.io.BytesContent;
import xworker.io.BytesContentListener;

public class CipherActions {
	public static Object run(ActionContext actionContext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Thing self = actionContext.getObject("self");
		String transformation = self.doAction("getTransformation", actionContext);
		byte[] key = self.doAction("getKey", actionContext);
		String algorithm = self.doAction("getAlgorithm", actionContext);
		Object params = self.doAction("getParams", actionContext);
		SecureRandom random = self.doAction("getRandom", actionContext);
		Object input = self.doAction("getInput", actionContext);
		int mode = "decrypt".equals(self.doAction("getMode", actionContext)) ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE;
		
		if(key == null) {
			throw new ActionException("Key is null, action=" + self.getMetadata().getPath());
		}
		if(algorithm == null) {
			throw new ActionException("Algorithm is null, action=" + self.getMetadata().getPath());			
		}
		if(input == null) {
			throw new ActionException("Input is null, action=" + self.getMetadata().getPath());			
		}
		if(transformation == null) {
			throw new ActionException("Transformation is null, action=" + self.getMetadata().getPath());
		}
		
		
		BytesContent bc = BytesContent.create(input);
		if(bc == null) {
			throw new ActionException("Not support input type, input type is " +input.getClass());
		}
		
		final Cipher cipher = Cipher.getInstance(transformation);
		SecretKeySpec keyspec = new SecretKeySpec(key, algorithm);
		cipher.init(mode, keyspec);
		
		CipherListener listener = new CipherListener(cipher);
		bc.setListener(listener);
		bc.start();
		
		return listener.result;
	}
	
	public static Object createSecureRandom(ActionContext actionContext) throws NoSuchAlgorithmException {
		Thing self = actionContext.getObject("self");
		String algorithm = self.doAction("getAlgorithm", actionContext);
		return SecureRandom.getInstance(algorithm);
	}
	
	public static Object generateKey(ActionContext actionContext) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		Thing self = actionContext.getObject("self");
		String algorithm = self.doAction("getAlgorithm", actionContext);
		KeyGenerator kg = KeyGenerator.getInstance(algorithm);
		SecureRandom random = self.doAction("getRandom", actionContext);
		int keysize = self.doAction("getKeysize", actionContext);
		if(keysize > 0) {
			if(random != null) {
				kg.init(keysize, random);				
			}else {
				kg.init(keysize);
			}
		}else {
			AlgorithmParameterSpec params = self.doAction("getParams", actionContext);
			if(params != null) {
				if(random != null) {
					kg.init(params, random);
				}else {
					kg.init(params);
				}
			}else if(random != null){
				kg.init(random);
			}
		}
		
		return kg.generateKey();
	}
	
	public static byte[] encrypt(ActionContext actionContext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		Thing self = actionContext.getObject("self");
		String transformation = self.doAction("getTransformation", actionContext);
		Cipher cipher = Cipher.getInstance(transformation);
		Key key = null;
		AlgorithmParameters params = null;
		AlgorithmParameterSpec spec = null;
		SecureRandom random = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Key) {
				key = (Key) obj;
			}else if(obj instanceof AlgorithmParameters) {
				params = (AlgorithmParameters) obj;
			}else if(obj instanceof AlgorithmParameterSpec) {
				spec = (AlgorithmParameterSpec) obj;
			}else if(obj instanceof SecureRandom) {
				random = (SecureRandom) obj;
			}
		}
		
		if(key == null) {
			throw new ActionException("Key is null, action=" + self.getMetadata().getPath());
		}
		if(params != null && random != null) {
			cipher.init(Cipher.ENCRYPT_MODE, key, params, random);
		}else if(params != null) {
			cipher.init(Cipher.ENCRYPT_MODE, key, params);
		}else if(spec != null && random != null) {
			cipher.init(Cipher.ENCRYPT_MODE,  key, spec, random);
		}else if(spec != null) {
			cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		}else if(random != null) {
			cipher.init(Cipher.ENCRYPT_MODE, key, random);
		}else {
			cipher.init(Cipher.ENCRYPT_MODE, key);			
		}
		
		
		Object input = self.doAction("getInput", actionContext);
		BytesContent bc = BytesContent.create(input);
		if(bc == null) {
			throw new ActionException("Not support input type, input type is " +input.getClass());
		}
		
		CipherListener listener = new CipherListener(cipher);
		bc.setListener(listener);
		bc.start();
		
		return listener.result;
	}
	
	public static byte[] decrypt(ActionContext actionContext) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		Thing self = actionContext.getObject("self");
		String transformation = self.doAction("getTransformation", actionContext);
		Cipher cipher = Cipher.getInstance(transformation);
		Key key = null;
		AlgorithmParameters params = null;
		AlgorithmParameterSpec spec = null;
		SecureRandom random = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Key) {
				key = (Key) obj;
			}else if(obj instanceof AlgorithmParameters) {
				params = (AlgorithmParameters) obj;
			}else if(obj instanceof AlgorithmParameterSpec) {
				spec = (AlgorithmParameterSpec) obj;
			}else if(obj instanceof SecureRandom) {
				random = (SecureRandom) obj;
			}
		}
		
		if(key == null) {
			throw new ActionException("Key is null, action=" + self.getMetadata().getPath());
		}
		if(params != null && random != null) {
			cipher.init(Cipher.DECRYPT_MODE, key, params, random);
		}else if(params != null) {
			cipher.init(Cipher.DECRYPT_MODE, key, params);
		}else if(spec != null && random != null) {
			cipher.init(Cipher.DECRYPT_MODE,  key, spec, random);
		}else if(spec != null) {
			cipher.init(Cipher.DECRYPT_MODE, key, spec);
		}else if(random != null) {
			cipher.init(Cipher.DECRYPT_MODE, key, random);
		}else {
			cipher.init(Cipher.DECRYPT_MODE, key);			
		}
		
		
		Object input = self.doAction("getInput", actionContext);
		BytesContent bc = BytesContent.create(input);
		if(bc == null) {
			throw new ActionException("Not support input type, input type is " +input.getClass());
		}
		
		CipherListener listener = new CipherListener(cipher);
		bc.setListener(listener);
		bc.start();
		
		return listener.result;
	}
	
	static class CipherListener implements  BytesContentListener{
		Cipher cipher;
		byte[] result;
		
		public CipherListener(Cipher cipher) {
			this.cipher = cipher;
		}
		
		@Override
		public void onRead(byte[] bytes, int offset, int length) {
			cipher.update(bytes, offset, length);
		}

		@Override
		public void onFinish() {
			try {			
				result = cipher.doFinal();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
		}
	}
}
