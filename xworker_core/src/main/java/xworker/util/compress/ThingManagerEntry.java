package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.xmeta.Thing;
import org.xmeta.ThingManager;

public class ThingManagerEntry extends AbstractEntry1{
	ThingManager thingManager;
	String path;
	
	public ThingManagerEntry(ThingManager thingManager, String path, boolean store) {
		super(store);
		
		this.thingManager = thingManager;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public void write(OutputStream output) throws IOException {
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public Iterator<CompressEntry> getChildsIterator() throws IOException {
		final Iterator<Thing> iter = thingManager.iterator(null, true);
		return new Iterator<CompressEntry>(){

			@Override
			public boolean hasNext() {				
				return iter.hasNext();
			}

			@Override
			public CompressEntry next() {
				return new ThingEntry(iter.next(), path, store);
			}
			
		};
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
		return 0;
	}

}
