package xworker.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class ZipCompressEntry extends AbstractBytesEntry{
	ZipInputStream zin;
	ZipEntry entry;
	String path;
	byte[] bytes;
	
	public ZipCompressEntry(ZipInputStream zin, ZipEntry entry, String path, boolean store) {
		super(store);
		
		this.zin = zin;
		this.entry = entry;
		this.path = path;
		
		if(store && !entry.isDirectory()) {
			this.writeToBytes();
		}
	}
	
	@Override
	public String getName() {
		if(path != null && !"".equals(path)) {
			return path + entry.getName();
		}else {
			return entry.getName();
		}
	}

	@Override
	public void write(OutputStream output) throws IOException {
		IOUtils.copy(zin, output);
	}

	@Override
	public long getLastModified() {
		return entry.getTime();
	}

	@Override
	public long getSize() {
		if(bytes == null) {
			return entry.getSize();
		}else {
			return super.getSize();
		}
	}

	@Override
	public Iterator<CompressEntry> getChildsIterator() throws IOException {
		return null;
	}

	@Override
	public void open() throws IOException {
		
	}

	@Override
	public void close() throws IOException {
			
	}
	
	@Override
	public boolean isDirectory() {
		return entry.isDirectory();
	}

	public void writeToBytes() {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.copy(zin, bout);
			setBytes(bout.toByteArray());
		}catch(Exception e) {
			logger.warn("write to bytes exception", e);
		}
	}
}
