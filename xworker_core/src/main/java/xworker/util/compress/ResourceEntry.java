package xworker.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionException;
import org.xmeta.World;

public class ResourceEntry extends AbstractBytesEntry{
	String resource;
	long lastModified;
	byte[] bytes;
	
	public ResourceEntry(String resource, long lastModified, boolean store) throws IOException{
		super(store);
		
		this.resource = resource;
		if(!this.resource.startsWith("/")) {
			this.resource = "/" + this.resource;
		}
		this.lastModified = lastModified;
		
		InputStream fin = World.getInstance().getResourceAsStream(this.resource);
		if(fin != null){
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.copy(fin, bout);
			bytes = bout.toByteArray();
			this.setBytes(bytes);
		}else{
			throw new ActionException("Resource not found, " + resource );
		}
	}
	
	@Override
	public String getName() {
		return resource;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		output.write(bytes);
	}

	@Override
	public long getLastModified() {
		return System.currentTimeMillis();
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

}
