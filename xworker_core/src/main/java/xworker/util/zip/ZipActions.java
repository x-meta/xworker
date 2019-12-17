package xworker.util.zip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.io.file.FileActions;
import xworker.util.UtilData;
import xworker.util.compress.CompressEntry;

public class ZipActions {
	private static Logger logger = LoggerFactory.getLogger(ZipActions.class);
	public static void compressWithEntrys(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File zipFile = FileActions.getFile(self, "getZipFile", actionContext);
		if(!zipFile.exists()){
			zipFile.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(zipFile);
		try{
			ZipOutputStream out = new ZipOutputStream(fout);
			//避免Entry重复的Context
			Map<String, Object> context = new HashMap<String, Object>();
			try{
				for(Thing entrys : self.getChilds("Entrys")){
					for(Thing entryThing : entrys.getChilds()){
						CompressEntry entry = entryThing.doAction("create", actionContext);
						if(entry == null){
							logger.debug("CompressEntry is null, Entry=" + entryThing.getMetadata().getPath());
						}else if(entry.getName() == null){
							logger.debug("Entry name is null, Entry=" + entryThing.getMetadata().getPath());
						}
						
						writeEntry(entry, out, context);
					}
				}
			}finally{
				out.close();
			}
		}finally{
			fout.close();
		}
	}
	
	public static void writeEntry(CompressEntry entry, ZipOutputStream out, Map<String, Object> context) throws IOException{
		if(entry == null){
			return;
		}
		
		entry.open();
		try {
			if(entry.getName() != null){
				String name = entry.getName();
				//去除/或\斜杠
				if(name.startsWith("/") || name.startsWith("\\")){
					name = name.substring(1, name.length());
				}
				name = name.replace('\\', '/');
				if(context.get(name) != null) {
					logger.warn("duplicate entry, ignore it, name=" + name);
				}else {
					context.put(name, name);
					if(entry.isDirectory()) {
						if(!name.endsWith("/")) {
							name = name + "/";
						}
						ZipEntry jentry = new ZipEntry(name);
						jentry.setTime(entry.getLastModified());
						out.putNextEntry(jentry);
					}else {
						
						ZipEntry jentry = new ZipEntry(name);
						if(entry.getSize() >= 0) {
							jentry.setSize(entry.getSize());
						}
						jentry.setTime(entry.getLastModified());		
						out.putNextEntry(jentry);
						entry.write(out);
					}
					out.closeEntry();				
				}
			}
			
			Iterator<CompressEntry> iter = entry.getChildsIterator();
			if(iter != null){
				while(iter.hasNext()) {
				    CompressEntry childEntry = iter.next();
					writeEntry(childEntry, out, context);
				}
			}
		}finally {
			entry.close();
		}
	}

	public static void compress(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = FileActions.getFile(self, "getFile", actionContext);
		File zipFile = FileActions.getFile(self, "getZipFile", actionContext);
		String entryPrefix = (String) self.doAction("getEntryPrefix", actionContext);
		Boolean append = (Boolean) self.doAction("isAppend", actionContext);
		
		byte[] zipBytes = null;
		if(append && zipFile.exists()){
			//文件已存在，且是追加模式，先办原来的zip文件读入内存
			zipBytes = FileUtils.readFileToByteArray(zipFile);
		}
		
		FileOutputStream fout = new FileOutputStream(zipFile);
		try{
			ZipOutputStream zout = new ZipOutputStream(fout);
			
			//写入文件
			Map<String, String> context = new HashMap<String, String>();
			writeFile(file, zout, entryPrefix, context);
			
			//写入原来的文件
			if(zipBytes != null){
				ByteArrayInputStream bin = new ByteArrayInputStream(zipBytes);
				ZipInputStream zin = new ZipInputStream(bin);
				
				ZipEntry entry = null;
				while((entry = zin.getNextEntry()) != null){
					String name = entry.getName();	
					name = name.replace('\\', '/');
					if(context.get(name) == null){
						context.put(name, name);
						
						//放入entry						
						ZipEntry oentry = new ZipEntry(name);
						oentry.setSize(entry.getSize());
						oentry.setTime(entry.getTime());		
						zout.putNextEntry(oentry);
												
						IOUtils.copy(zin, zout);
						
						zout.closeEntry();
					}
					
					zin.closeEntry();
				}
			}
			
			zout.finish();
		}finally{
			fout.close();
		}
		
	}
	
	/**
	 * 向ZipOutputStream中写入文件。
	 * 
	 * @param file 要写入的文件或目录。
	 * @param zout ZipOutputSream
	 * @param entryPrefix entry的前缀
	 * @param context 用于防止重复的entry的上下文，尤其是追加模式时。
	 */
	public static void writeFile(File file, ZipOutputStream zout, String entryPrefix, Map<String, String> context){
		if(file.isDirectory()){
			String prefix = getEntryName(file, entryPrefix);
			for(File child : file.listFiles()){
				writeFile(child, zout, prefix, context);
			}
		}else if(file.isFile()){
			String entryName = getEntryName(file, entryPrefix);
			if(context.get(entryName) != null){
				//已经存在
				return;
			}

			context.put(entryName, entryName);
			try{
				ZipEntry entry = new ZipEntry(entryName);
				entry.setSize(file.length());
				entry.setTime(file.lastModified());		
				zout.putNextEntry(entry);
				
				FileUtils.copyFile(file, zout);
				zout.closeEntry();
			}catch(Exception e){
				logger.warn("Putentry error, " + e.getLocalizedMessage());
			}
			
		}
	}
	
	public static String getEntryName(File file, String entryPrefix){
		if(entryPrefix == null || "".equals(entryPrefix)){
			return file.getName();
		}else{
			return entryPrefix + "/" + file.getName(); 
		}
	}
	
	public static void decompress(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = FileActions.getFile(self, "getDirectory", actionContext);
		File zipFile = FileActions.getFile(self, "getZipFile", actionContext);
		
		FileInputStream fin = new FileInputStream(zipFile);
		try{
			ZipEntry entry = null;
			ZipInputStream in = new ZipInputStream(fin);
			while((entry = in.getNextEntry()) != null){
				//long size = entry.getSize();
				String name = entry.getName();				
				File newFile = new File(directory, name);
				if(entry.isDirectory()){
					newFile.mkdirs();
				}else{
					//写入文件
					if(!newFile.getParentFile().exists()){
						newFile.getParentFile().mkdirs();
					}
					
					FileOutputStream fout = new FileOutputStream(newFile);
					try{
						IOUtils.copy(in, fout);
					}finally{
						fout.close();
					}
					
					if(entry.getTime() > 0){
						newFile.setLastModified(entry.getTime());
					}					
				}
			}
			in.close();
		}finally{
			fin.close();
		}
	}
	
	public static void decompressInputStream(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		InputStream in = self.doAction("getInputStream", actionContext);
		ZipInputStream zin = null;
		try {			
			zin = new ZipInputStream(in);
			ZipEntry entry = null;
			File target = self.doAction("getDirectory", actionContext);
			while((entry = zin.getNextEntry()) != null) {			
				self.doAction("handleEntry", actionContext, "zipInputStream", zin, "entry", entry, "target", target);
				zin.closeEntry();
			}
		}finally {
			if(zin != null) {
				zin.close();
			}
			
			if(in != null && UtilData.isTrue(self.doAction("isCloseInputStream", actionContext))) {
				in.close();
			}	
		}
	}
	
	public static void decompressEntryToDir(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		File target = actionContext.getObject("target");
		if(target == null) {
			logger.info("Target dir is null, can not decompress entry, action=" + self.getMetadata().getPath());
			return;
		}
		
		ZipInputStream zin = actionContext.getObject("zipInputStream");
		ZipEntry entry = actionContext.getObject("entry");
		if(entry.isDirectory() == false) {
			File file = new File(target, entry.getName());
			long size = entry.getSize();
			
			if(file.getParentFile().exists() == false) {
				file.getParentFile().mkdirs();
			}
			
			byte[] buffer = new byte[1024 * 10];			
			FileOutputStream fout = new FileOutputStream(file);
			try {
				while(size > 0){
					if(size > buffer.length) {
						zin.read(buffer);
						fout.write(buffer);
						size = size - buffer.length;
					}else {
						int length = (int) size;
						zin.read(buffer, 0, length);
						fout.write(buffer, 0, length);
						size = 0;
					}
				}
			}finally {
				fout.close();
			}
			if(entry.getLastModifiedTime() != null) {
				file.setLastModified(entry.getLastModifiedTime().toMillis());
			}
		}
	}
}
