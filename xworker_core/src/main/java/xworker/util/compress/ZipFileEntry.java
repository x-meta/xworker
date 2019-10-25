package xworker.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ZipFileEntry implements CompressEntry{
	File jarFile;
	FileInputStream fin;
	ZipInputStream jarIn;
	String path;
	boolean store;
	
	public ZipFileEntry(File jarFile, String path, boolean store) {
		this.jarFile = jarFile;
		this.path = path;
		this.store = store;
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
		return new Iterator<CompressEntry>() {
			ZipEntry entry = null;
			
			@Override
			public boolean hasNext() {
				try {
					entry =  jarIn.getNextEntry();
				} catch (IOException e) {
					return false;
				}
				return entry != null;
			}

			@Override
			public CompressEntry next() {
				return new ZipCompressEntry(jarIn, entry, path, store);
			}
			
		};
	}

	@Override
	public void open() throws IOException {
		fin = new FileInputStream(jarFile);
		jarIn = new ZipInputStream(fin);
	}

	@Override
	public void close() throws IOException {
		if(jarIn != null) {
			jarIn.close();
		}
		if(fin != null) {
			fin.close();
		}			
	}
	
	public static ZipFileEntry create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		String path = self.doAction("getPath", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		if(file != null) {
			return new ZipFileEntry(file, path, store);
		}else {
			return null;
		}
	}
	
	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public long getCRC32() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMethod() {
		// TODO Auto-generated method stub
		return 0;
	}

}
