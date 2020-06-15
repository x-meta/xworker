package xworker.io;

import java.io.File;
import java.io.InputStream;

import xworker.io.bytes.BytesBytesContent;
import xworker.io.bytes.FileBytesContent;
import xworker.io.bytes.InputStreamBytesContent;
import xworker.io.bytes.StringBytesContent;

public abstract class BytesContent {
	protected BytesContentListener listener;
	
	public void setListener(BytesContentListener listener) {
		this.listener = listener;
	}
		
	protected abstract void doRead();
	
	protected abstract void close();
	
	public void start() {
		try {
			doRead();
			
			if(listener != null) {
				listener.onFinish();
			}
		}finally {
			close();
		}
	}
	
	public static BytesContent create(Object object) {
		if(object instanceof File) {
			return new FileBytesContent((File) object);
		}else if(object instanceof byte[]) {
			return new BytesBytesContent((byte[]) object);
		}else if(object instanceof InputStream) {
			return new InputStreamBytesContent((InputStream) object);
		}else if(object instanceof String) {
			return new StringBytesContent((String) object);
		}else {
			return null;
		}
	}
}
