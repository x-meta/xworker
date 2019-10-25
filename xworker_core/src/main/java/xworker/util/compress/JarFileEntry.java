package xworker.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JarFileEntry implements CompressEntry{
	File jarFile;
	FileInputStream fin;
	JarInputStream jarIn;
	String path;
	boolean store;
	
	public JarFileEntry(File jarFile, String path, boolean store) {
		this.jarFile = jarFile;
		this.path = path;
		this.store = store;
		
		if(store) {
			if(path != null) {
				this.path = path + jarFile.getName();
			}else {
				this.path = jarFile.getName();
			}
		}
	}

	@Override
	public String getName() {
		return null;
		/*
		if(store) {
			return path;
		}else {
			return null;
		}*/
	}

	@Override
	public void write(OutputStream output) throws IOException {
		/*
		if(!store) {
			return;
		}
		
		//是store的方式
		JarOutputStream jarOut = new JarOutputStream(output, jarIn.getManifest());
		JarEntry oldEntry = null;
		while((oldEntry = jarIn.getNextJarEntry()) != null) {
			JarEntry newEntry = new JarEntry(oldEntry.getName());
			byte[] bytes = null;
			if(oldEntry.isDirectory() == false) {
				newEntry.setMethod(JarEntry.STORED);
				if(oldEntry.getExtra() != null) {
					newEntry.setExtra(oldEntry.getExtra());
				}
				
				if(oldEntry.getSize() == -1 || oldEntry.getCrc() != -1) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					IOUtils.copy(jarIn, bout);
					bytes = bout.toByteArray();
					CRC32 crc32 = new CRC32();
					crc32.update(bytes, 0, bytes.length);
					newEntry.setSize(bytes.length);
					newEntry.setCrc(crc32.getValue());
				} else{
					newEntry.setSize(oldEntry.getSize());
					newEntry.setCrc(oldEntry.getCrc());
				}
			}
			newEntry.setLastModifiedTime(oldEntry.getLastModifiedTime());
			newEntry.setTime(oldEntry.getTime());
			try {
				System.out.println("getName=" + newEntry.getName());
				System.out.println("isDirectory=" + newEntry.isDirectory());
				System.out.println("getSize=" + newEntry.getSize());
				System.out.println("getCrc=" + newEntry.getCrc());
				
				jarOut.putNextEntry(newEntry);
				if(!oldEntry.isDirectory()) {
					if(bytes == null) {
						IOUtils.copy(jarIn, jarOut);
					}else {
						jarOut.write(bytes);
					}
				}
			}finally {
				jarIn.closeEntry();
				jarOut.closeEntry();
			}
		}
		jarOut.close();*/
	}	

	@Override
	public long getLastModified() {
		return jarFile.lastModified();
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public Iterator<CompressEntry> getChildsIterator() throws IOException {
		return new Iterator<CompressEntry>() {
			JarEntry entry = null;
			
			@Override
			public boolean hasNext() {
				try {
					entry =  jarIn.getNextJarEntry();
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
		jarIn = new JarInputStream(fin);
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
	
	public static JarFileEntry create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		File file = self.doAction("getFile", actionContext);
		String path = self.doAction("getPath", actionContext);
		Boolean store = self.doAction("isStore", actionContext);
		if(file != null) {
			return new JarFileEntry(file, path, store);
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

