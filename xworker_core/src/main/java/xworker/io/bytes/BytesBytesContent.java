package xworker.io.bytes;

import xworker.io.BytesContent;

public class BytesBytesContent extends BytesContent{
	byte[] bytes;
	public BytesBytesContent(byte[] bytes) {
		this.bytes = bytes;
	}
	
	@Override
	protected void doRead() {
		if(listener != null) {
			listener.onRead(bytes, 0, bytes.length);
		}
	}

	@Override
	protected void close() {
		
	}

}
