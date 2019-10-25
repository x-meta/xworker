package xworker.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.World;

public class ThingEntry implements CompressEntry{
	Thing thing;
	String path;
	byte[] bytes = null;
	
	public ThingEntry(Thing thing, ActionContext actionContext){
		this.thing = thing.doAction("getThing", actionContext);
		this.path = thing.doAction("getPath", actionContext);
		if(this.thing == null) {
			throw new ActionException("Thing is null, entryPath=" + thing.getMetadata().getPath());
		}
		
		if(this.path == null || this.path.trim().equals("")) {
			this.path = this.thing.getMetadata().getPath();
		}
		
		ThingCoder coder  = World.getInstance().getThingCoder(this.thing.getRoot().getMetadata().getCoderType());
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		coder.encode(this.thing, bout);
		this.path = this.path.replace('.', '/');
		this.path = this.path + "." + this.thing.getRoot().getMetadata().getCoderFileType();
		
		bytes = bout.toByteArray();
	}
	
	@Override
	public String getName() {
		return path;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		output.write(bytes);
	}

	@Override
	public long getLastModified() {
		return thing.getMetadata().getLastModified();
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
		return AbstractEntry1.getCRC32(bytes);
	}

	@Override
	public int getMethod() {
		// TODO Auto-generated method stub
		return 0;
	}

}
