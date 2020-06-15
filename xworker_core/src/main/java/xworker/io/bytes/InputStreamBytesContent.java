package xworker.io.bytes;

import java.io.InputStream;

import org.xmeta.ActionException;

import xworker.io.BytesContent;

public class InputStreamBytesContent extends BytesContent{
	InputStream in;
	
	public InputStreamBytesContent(InputStream in) {
		if(in == null) {
			throw new ActionException("InputStream is null.");
		}
		
		this.in = in;
	}
	
	@Override
	protected void doRead() {
		if(listener != null) {
			try {
				byte[] bytes = new byte[4096];
				int l = -1;
				while((l = in.read(bytes)) != -1){
					listener.onRead(bytes, 0, l);
				}
			}catch(Exception e) {
				throw new ActionException("InputStreamBytesContent doRead error", e);
			}
		}
	}

	@Override
	protected void close() {
		//由于InputStream是传入的，故这里不执行释放
	}

}
