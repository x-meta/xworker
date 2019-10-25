package xworker.util.compress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class FileEntry extends AbstractEntry1{
	String name;
	File file;
	
	public FileEntry(String name, File file, boolean store){
		super(store);
		
		//如果是目录，附加文件名
		if(name.endsWith("/") || name.endsWith("\\")) {
			name = name + file.getName();
		}
		
		this.name = name;		
		this.file = file;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		FileUtils.copyFile(file, output);
	}

	@Override
	public long getLastModified() {
		return file.lastModified();
	}

	@Override
	public Iterator<CompressEntry> getChildsIterator() {
		return null;
	}
	
	@Override
	public long getSize() {
		return file.length();
	}

	@Override
	public void close() {
		
	}
	
	@Override
	public void open() {
		
	}
	
	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public long getCRC32() {
		try {
			return FileUtils.checksumCRC32(file);
		}catch(Exception e) {			
		}
		return 0;
	}
}
