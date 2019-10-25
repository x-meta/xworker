package xworker.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 在JarOutputStream里调用close会同时把文件流等关闭，使用BridgetOutputStream是为了避免此种情况。
 * 
 * @author zyx
 *
 */
public class BridgeOutputStream extends OutputStream{
	OutputStream out;
	
	public BridgeOutputStream(OutputStream out) {
		this.out = out;
	}
	
	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}
	
}
