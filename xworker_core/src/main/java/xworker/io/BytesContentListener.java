package xworker.io;

public interface BytesContentListener {
	public void onRead(byte[] bytes, int offset, int length);
	
	public void onFinish();
}
