package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.CRC32;

public class BytesEntry extends AbstractEntry1{
	byte[] bytes;
	String name;
	long lastModified;
	
	
	public BytesEntry(String name, long lastModified, byte[] bytes, boolean store){
		super(store);
		
		this.name = name;
		this.lastModified = lastModified;
		this.bytes = bytes;		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void write(OutputStream output) throws IOException {
		output.write(bytes);
	}
	
	@Override
	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public Iterator<CompressEntry> getChildsIterator() {
		return null;
	}

	@Override
	public long getSize() {
		return bytes.length;
	}

	@Override
	public void close() {
		
	}

	@Override
	public void open() {
		
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public long getCRC32() {
		CRC32 crc32 = new CRC32();
		crc32.update(bytes, 0, bytes.length);
		return crc32.getValue();
	}

	
}
