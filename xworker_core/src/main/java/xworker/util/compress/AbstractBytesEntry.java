package xworker.util.compress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBytesEntry extends AbstractEntry1{
	Logger logger = LoggerFactory.getLogger(AbstractBytesEntry.class);
	
	byte[] bytes;
	long crc32 = 0;
	
	public AbstractBytesEntry(boolean store) {
		super(store);
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
		crc32 = AbstractEntry1.getCRC32(bytes);
	}

	@Override
	public long getSize() {
		if(bytes != null) {
			return bytes.length;
		}
		
		return 0;
	}

	@Override
	public long getCRC32() {		
		return crc32;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
}
