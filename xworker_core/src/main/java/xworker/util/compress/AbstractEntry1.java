package xworker.util.compress;

import java.util.jar.JarEntry;
import java.util.zip.CRC32;

public abstract class AbstractEntry1 implements CompressEntry{
	boolean store;
	
	public AbstractEntry1(boolean store) {
		this.store = store;
	}
	

	@Override
	public int getMethod() {
		return store ? JarEntry.STORED : -1;
	}
	
	public static long getCRC32(byte[] bytes) {
		if(bytes != null) {
			CRC32 crc32 = new CRC32();
			crc32.update(bytes, 0, bytes.length);
			return crc32.getValue();
		}
		return 0;
	}
}
