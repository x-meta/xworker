package xworker.util.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.CRC32;

import org.apache.commons.io.IOUtils;
import org.xmeta.ActionException;
import org.xmeta.World;

public class ClassFileEntry extends AbstractEntry1{
	String className;
	long lastModified;
	byte[] bytes;
	
	public ClassFileEntry(String className, long lastModified, boolean store) throws IOException{
		super(store);
		
		this.className = className.replace('.', '/') + ".class";
		this.lastModified = lastModified;
		
		InputStream fin = World.getInstance().getResourceAsStream("/" + this.className);
		if(fin != null){
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.copy(fin, bout);
			bytes = bout.toByteArray();
		}else{
			throw new ActionException("Class not found, className=" + className );
		}
	}
	
	@Override
	public String getName() {
		return className;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		InputStream fin = ClassFileEntry.class.getResourceAsStream("/" + className);
		if(fin != null){
			try{
				IOUtils.copy(fin, output);
			}finally{
				fin.close();
			}			
		}
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

	@Override
	public long getCRC32() {
		InputStream fin = ClassFileEntry.class.getResourceAsStream("/" + className);
		if(fin != null){			
			CRC32 crc32 = new CRC32();
			byte[] bytes = new byte[1024 * 8];
			int length = -1;
			
			try{
				while((length = fin.read(bytes)) != -1) {
					crc32.update(bytes, 0, length);
				}				
			}catch(Exception e) {
			}finally{
				try {
					fin.close();
				} catch (IOException e) {
				}
			}	
			
			return crc32.getValue();
		}
		return 0;
	}
}
