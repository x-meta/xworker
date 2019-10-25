package xworker.util.jar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.io.BridgeOutputStream;
import xworker.io.file.FileActions;
import xworker.util.UtilData;
import xworker.util.compress.CompressEntry;
import xworker.util.compress.JarCompressEntry;

public class JarActions {
	private static Logger logger = LoggerFactory.getLogger(JarActions.class);
	
	public static void compressWithEntrysToOutputStream(Thing self, OutputStream out, ActionContext actionContext) throws IOException {
		Object manifest = self.doAction("getManifest", actionContext);
		Manifest man = getManifest(manifest);
					
		//避免Entry重复的Context
		Map<String, Object> context = new HashMap<String, Object>();
		JarOutputStream jout = null;
		if(man != null){
			jout = new JarOutputStream(new BridgeOutputStream(out), man);
		}else{
			jout = new JarOutputStream(new BridgeOutputStream(out));
		}
		try{
			for(Thing entrys : self.getChilds("Entrys")){
				for(Thing entryThing : entrys.getChilds()){
					CompressEntry entry = entryThing.doAction("create", actionContext);
					if(entry == null){
						logger.debug("CompressEntry is null, Entry=" + entryThing.getMetadata().getPath());
					}else if(entry.getName() == null){
						logger.debug("Entry name is null, ignore it but search childs Entry=" + entryThing.getMetadata().getPath());
					}
											
					writeEntry(entry, jout, context);
				}
			}
		}finally{
			jout.close();
		}
	}
	
	public static void compressWithEntrys(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		File jarFile = FileActions.getFile(self, "getJarFile", actionContext);
		if(!jarFile.exists()){
			jarFile.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(jarFile);
		try{
			compressWithEntrysToOutputStream(self, fout, actionContext);
		}finally{
			fout.close();
		}
	}
	
	public static void writeEntry(CompressEntry entry, JarOutputStream out, Map<String, Object> context) throws IOException{
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
				if(entry.isDirectory()) {
					if(!name.endsWith("/")) {
						name = name + "/";
					}
				}
				
				if(context.get(name) != null) {
					logger.warn("duplicate entry, ignore it, name=" + name);
				}else {
					context.put(name, name);
					if(entry.isDirectory()) {
						JarEntry jentry = new JarEntry(name);
						//jentry.setMethod(method);
						jentry.setTime(entry.getLastModified());
						out.putNextEntry(jentry);
					}else {					
						JarEntry jentry = new JarEntry(name);
						if(entry.getMethod() == JarEntry.STORED) {
							jentry.setMethod(JarEntry.STORED);
							jentry.setSize(entry.getSize());
							jentry.setCrc(entry.getCRC32());
						}else if(entry.getSize() >= 0) {
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
		File directory = FileActions.getFile(self, "getDirectory", actionContext);
		File jarFile = FileActions.getFile(self, "getJarFile", actionContext);
		Object manifest = self.doAction("getManifest", actionContext);
		Manifest man = getManifest(manifest);
		
		if(!jarFile.exists()){
			jarFile.getParentFile().mkdirs();
		}
		
		//输出流
		FileOutputStream fout = new FileOutputStream(jarFile);
		try{
			JarOutputStream out = null;
			if(man == null){
				out = new JarOutputStream(fout);			
			}else{
				out = new JarOutputStream(fout, man);
			}
			
			if(directory.isFile()){
				writeFile(directory, directory.getAbsolutePath(), out);
			}else{
				writeFile(directory, directory.getAbsolutePath(), out);
			}
			
			out.close();
		}finally{
			fout.close();
		}
	}
	
	public static void decompress(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = FileActions.getFile(self, "getDirectory", actionContext);
		File jarFile = FileActions.getFile(self, "getJarFile", actionContext);
		
		FileInputStream fin = new FileInputStream(jarFile);
		try{
			JarEntry entry = null;
			JarInputStream in = new JarInputStream(fin);
			while((entry = in.getNextJarEntry()) != null){
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
					fout.close();
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
	
	public static void classPathToJar(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File jarFile = FileActions.getFile(self, "getJarFile", actionContext);
		
		if(!jarFile.exists()){
			jarFile.getParentFile().mkdirs();
		}
		
		//输出流
		FileOutputStream fout = new FileOutputStream(jarFile);
		try{
			JarOutputStream out = null;
			out = new JarOutputStream(fout);			
			
			for(String dir : World.getInstance().getClassLoader().getAlClassPathDirs()){
				File directory = new File(dir);
				for(File child : directory.listFiles()){
					writeFile(child, directory.getAbsolutePath(), out);
				}
			}			
			
			out.close();
		}finally{
			fout.close();
		}
	}
	
	public static void writeFile(File file, String rootPath, JarOutputStream out) throws IOException{
		if(file.isDirectory()){
			for(File child : file.listFiles()){
				writeFile(child, rootPath, out);
			}
		}else{
			String path = file.getAbsolutePath();
			path = path.substring(rootPath.length(), path.length());
			
			try{
				ZipEntry entry = new ZipEntry(path);
				entry.setSize(file.length());
				entry.setTime(file.lastModified());		
				out.putNextEntry(entry);
				
				FileUtils.copyFile(file, out);
				out.closeEntry();
			}catch(Exception e){
				logger.warn("Putentry error, " + e.getLocalizedMessage());
			}
		}
	}
	
	public static Manifest getManifest(Object m) throws IOException{
		if(m instanceof Manifest){
			return (Manifest)m;
		}else if(m instanceof String){
			String str = (String) m;
			ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
			return new Manifest(bin);
		}else if(m instanceof InputStream){
			return new Manifest((InputStream) m);
		}else if(m instanceof File){
			String str = FileUtils.readFileToString((File) m, "utf-8");
			ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
			return new Manifest(bin);
		}else{
			return null;
		}
	}
	
	public static CompressEntry createJarCompressEntry(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		String name = self.doAction("getJarFile", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		
		return new JarCompressEntry(self, name, store, actionContext);
	}
}
