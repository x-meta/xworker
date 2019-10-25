package xworker.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.CRC32;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.jar.JarActions;

public class JarCompressEntry extends AbstractEntry1{
	Thing self;
	ActionContext actionContext;
	String name;
	byte[] bytes;
	
	public JarCompressEntry(Thing self, String name, boolean store, ActionContext actionContext) throws IOException {
		super(store);
		
		this.self = self;
		this.name = name;
		this.actionContext = actionContext;
		
		if(store) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			JarActions.compressWithEntrysToOutputStream(self, bout, actionContext);
			bytes = bout.toByteArray();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		if(bytes != null) {
			output.write(bytes);
		}else {
			JarActions.compressWithEntrysToOutputStream(self, output, actionContext);
		}
	}

	@Override
	public long getLastModified() {
		return System.currentTimeMillis();
	}

	@Override
	public long getSize() {
		if(bytes != null) {
			return bytes.length;
		}
		return 0;
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
		return false;
	}

	@Override
	public long getCRC32() {
		if(bytes != null) {
			CRC32 crc32 = new CRC32();
			crc32.update(bytes, 0, bytes.length);
			return crc32.getValue();
		}
		return 0;
	}

}
