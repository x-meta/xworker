package xworker.ide;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.io.SystemIoRedirector;

/**
 * 临时用的，可以随时删除。
 * 
 * @author zhangyuxiang
 *
 */
public class Temp {
	static java.util.Hashtable<String, String> config=new java.util.Hashtable<String, String>();
	  static{
	    config.put("cipher.s2c", 
	               "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
	    config.put("cipher.c2s",
	               "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");

	    config.put("mac.s2c", "hmac-md5,hmac-sha1,hmac-sha2-256,hmac-sha1-96,hmac-md5-96");
	    config.put("mac.c2s", "hmac-md5,hmac-sha1,hmac-sha2-256,hmac-sha1-96,hmac-md5-96");
	    config.put("compression.s2c", "none");
	    config.put("compression.c2s", "none");

	    config.put("lang.s2c", "");
	    config.put("lang.c2s", "");

	    config.put("compression_level", "6");

	    config.put("diffie-hellman-group-exchange-sha1", 
	                                "com.jcraft.jsch.DHGEX");
	    config.put("diffie-hellman-group1-sha1", 
		                        "com.jcraft.jsch.DHG1");
	    config.put("diffie-hellman-group14-sha1", 
	               "com.jcraft.jsch.DHG14");    // available since JDK8.
	    config.put("diffie-hellman-group-exchange-sha256", 
	               "com.jcraft.jsch.DHGEX256"); // available since JDK1.4.2.
	                                            // On JDK8, 2048bits will be used.
	    config.put("ecdsa-sha2-nistp256", "com.jcraft.jsch.jce.SignatureECDSA256");
	    config.put("ecdsa-sha2-nistp384", "com.jcraft.jsch.jce.SignatureECDSA384");
	    config.put("ecdsa-sha2-nistp521", "com.jcraft.jsch.jce.SignatureECDSA521");

	    config.put("ecdh-sha2-nistp256", "com.jcraft.jsch.DHEC256");
	    config.put("ecdh-sha2-nistp384", "com.jcraft.jsch.DHEC384");
	    config.put("ecdh-sha2-nistp521", "com.jcraft.jsch.DHEC521");

	    config.put("ecdh-sha2-nistp", "com.jcraft.jsch.jce.ECDHN");

	    config.put("dh",            "com.jcraft.jsch.jce.DH");
	    config.put("3des-cbc",      "com.jcraft.jsch.jce.TripleDESCBC");
	    config.put("blowfish-cbc",  "com.jcraft.jsch.jce.BlowfishCBC");
	    config.put("hmac-sha1",     "com.jcraft.jsch.jce.HMACSHA1");
	    config.put("hmac-sha1-96",  "com.jcraft.jsch.jce.HMACSHA196");
	    config.put("hmac-sha2-256",  "com.jcraft.jsch.jce.HMACSHA256");
	    // The "hmac-sha2-512" will require the key-length 2048 for DH,
	    // but Sun's JCE has not allowed to use such a long key.
	    //config.put("hmac-sha2-512",  "com.jcraft.jsch.jce.HMACSHA512");
	    config.put("hmac-md5",      "com.jcraft.jsch.jce.HMACMD5");
	    config.put("hmac-md5-96",   "com.jcraft.jsch.jce.HMACMD596");
	    config.put("sha-1",         "com.jcraft.jsch.jce.SHA1");
	    config.put("sha-256",         "com.jcraft.jsch.jce.SHA256");
	    config.put("sha-384",         "com.jcraft.jsch.jce.SHA384");
	    config.put("sha-512",         "com.jcraft.jsch.jce.SHA512");
	    config.put("md5",           "com.jcraft.jsch.jce.MD5");
	    config.put("signature.dss", "com.jcraft.jsch.jce.SignatureDSA");
	    config.put("signature.rsa", "com.jcraft.jsch.jce.SignatureRSA");
	    config.put("keypairgen.dsa",   "com.jcraft.jsch.jce.KeyPairGenDSA");
	    config.put("keypairgen.rsa",   "com.jcraft.jsch.jce.KeyPairGenRSA");
	    config.put("keypairgen.ecdsa", "com.jcraft.jsch.jce.KeyPairGenECDSA");
	    config.put("random",        "com.jcraft.jsch.jce.Random");

	    config.put("none",           "com.jcraft.jsch.CipherNone");

	    config.put("aes128-cbc",    "com.jcraft.jsch.jce.AES128CBC");
	    config.put("aes192-cbc",    "com.jcraft.jsch.jce.AES192CBC");
	    config.put("aes256-cbc",    "com.jcraft.jsch.jce.AES256CBC");

	    config.put("aes128-ctr",    "com.jcraft.jsch.jce.AES128CTR");
	    config.put("aes192-ctr",    "com.jcraft.jsch.jce.AES192CTR");
	    config.put("aes256-ctr",    "com.jcraft.jsch.jce.AES256CTR");
	    config.put("3des-ctr",      "com.jcraft.jsch.jce.TripleDESCTR");
	    config.put("arcfour",      "com.jcraft.jsch.jce.ARCFOUR");
	    config.put("arcfour128",      "com.jcraft.jsch.jce.ARCFOUR128");
	    config.put("arcfour256",      "com.jcraft.jsch.jce.ARCFOUR256");

	    config.put("userauth.none",    "com.jcraft.jsch.UserAuthNone");
	    config.put("userauth.password",    "com.jcraft.jsch.UserAuthPassword");
	    config.put("userauth.keyboard-interactive",    "com.jcraft.jsch.UserAuthKeyboardInteractive");
	    config.put("userauth.publickey",    "com.jcraft.jsch.UserAuthPublicKey");
	    config.put("userauth.gssapi-with-mic",    "com.jcraft.jsch.UserAuthGSSAPIWithMIC");
	    config.put("gssapi-with-mic.krb5",    "com.jcraft.jsch.jgss.GSSContextKrb5");

	    config.put("zlib",             "com.jcraft.jsch.jcraft.Compression");
	    config.put("zlib@openssh.com", "com.jcraft.jsch.jcraft.Compression");

	    config.put("pbkdf", "com.jcraft.jsch.jce.PBKDF");

	    config.put("StrictHostKeyChecking",  "ask");
	    config.put("HashKnownHosts",  "no");

	    config.put("PreferredAuthentications", "gssapi-with-mic,publickey,keyboard-interactive,password");

	    config.put("CheckCiphers", "aes256-ctr,aes192-ctr,aes128-ctr,aes256-cbc,aes192-cbc,aes128-cbc,3des-ctr,arcfour,arcfour128,arcfour256");
	    config.put("CheckKexes", "diffie-hellman-group14-sha1,ecdh-sha2-nistp256,ecdh-sha2-nistp384,ecdh-sha2-nistp521");
	    config.put("CheckSignatures", "ecdsa-sha2-nistp256,ecdsa-sha2-nistp384,ecdsa-sha2-nistp521");

	    config.put("MaxAuthTries", "6");
	    config.put("ClearAllForwardings", "no");
	  }
	  
	  public static void main(String[] args) {
		  try {
			//初始化引擎
				World world = World.getInstance();			
				world.init("./xworker/");
				Thread.currentThread().setContextClassLoader(world.getClassLoader());
				SystemIoRedirector.init();
				
				Thing template = world.getThing("xworker.net.jsch.Jsch/@Configs/@kex");
				Thing configs = world.getThing("xworker.net.jsch.Jsch/@Configs");
				
				for(String key : config.keySet()) {
					String value = config.get(key);
					
					Thing config = template.detach();
					config.set("name", key);
					for(int i =0; i<config.getChilds().size(); i++) {
						Thing child = config.getChilds().get(i);
						if(i == 0) {
							child.set("default", key);
						}else if(i==2) {
							child.set("default", value);
						}
					}
					configs.addChild(config);
				}
				
				configs.save();
				
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	  }
}
