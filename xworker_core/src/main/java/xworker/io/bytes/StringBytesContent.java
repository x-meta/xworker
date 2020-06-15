package xworker.io.bytes;

import xworker.io.BytesContent;

public class StringBytesContent extends BytesContent{
	String content;
	
	public StringBytesContent(String content) {
		this.content = content;
	}

	@Override
	protected void doRead() {
		if(listener == null) {
			return;
		}
		
		if(this.content != null) {
			byte[] bytes = content.getBytes();
			listener.onRead(bytes, 0, bytes.length);
		}
	}

	@Override
	protected void close() {
	}

}
