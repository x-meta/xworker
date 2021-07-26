package xworker.net.jsch.jgss;

import com.jcraft.jsch.JSchException;

/**
 * 对于不支持Krb5的，使用它来替换GSSContextKrb5。
 * 
 * @author zhangyuxiang
 *
 */
public class NoneKrb5 implements com.jcraft.jsch.GSSContext{

	@Override
	public void create(String user, String host) throws JSchException {
	}

	@Override
	public boolean isEstablished() {
		return false;
	}

	@Override
	public byte[] init(byte[] token, int s, int l) throws JSchException {
		return null;
	}

	@Override
	public byte[] getMIC(byte[] message, int s, int l) {
		return null;
	}

	@Override
	public void dispose() {
	}
}
