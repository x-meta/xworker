package xworker.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

/**
 * 文件相关的一些动作。
 * 
 * @author Administrator
 *
 */
public class FileActions {
	private static Logger logger = LoggerFactory.getLogger(FileActions.class);
		
	public static void iteratorLines(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		File file = (File) self.doAction("getFile", actionContext);
		if(file == null){
			throw new ActionException("IteratorLines: file is null, path=" + self.getMetadata().getPath());
		}
		
		FileInputStream fin = new FileInputStream(file);
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			
			boolean trim = self.getBoolean("trim");
			boolean continueOnBlank = self.getBoolean("continueOnBlank");
			
			Bindings bindings = actionContext.push();
			bindings.setVarScopeFlag();//isVarScopeFlag = true;
			try{
				bindings.put("file", file);
				while((line = br.readLine()) != null){
					if(trim){
						line = line.trim();
					}
					
					if(continueOnBlank && "".equals(line)){
						continue;
					}
					
					bindings.put("line", line);
					self.doAction("handleLine", actionContext);
				}
			}finally{
				actionContext.pop();
			}
			br.close();
		}finally{
			fin.close();
		}
	}
		
	public static void handleLine(ActionContext actionContext){
		logger.info((String) actionContext.get("line"));
	}
	
	public static File getFile(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Object obj = UtilData.getData(self, "file", actionContext);
		if(obj instanceof File){
			return (File) obj;
		}else if(obj instanceof String){
			return new File((String) obj);
		}else{
			throw new ActionException(obj + "is not a file, path=" + self.getMetadata().getPath());
		}
	}
	
	public static void synchronizeFile(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		String type = self.getString("type");
		
		File sourceDir = (File) self.doAction("getSourceDir", actionContext);
		File targetDir = (File) self.doAction("getTargetDir", actionContext);
		
		if(sourceDir == null || targetDir == null){
			if(self.getBoolean("exceptionOnNoSourceOrTarget")){
				throw new ActionException("source or target dir cann't be null, path=" + self.getMetadata().getPath());
			}else{
				return;
			}
		}
		
		boolean debugLog = UtilAction.getDebugLog(self, actionContext);
		syncDirOrFile(sourceDir, targetDir, type, debugLog);
		if(debugLog){
			logger.info("同步结束：source=" + sourceDir + ", target=" + targetDir);
		}
	}
	
	public static void syncDirOrFile(File source, File target, String type, boolean log) throws IOException{
		if(log && source.isDirectory()){
			logger.info("同步目录：source=" + source + " target=" + target);
		}
		
		if(source == null){
			//源没有，而目标有点的文件
			if("keepSourceSame".equals(type)){
				if(log){
					logger.info("删除目标文件或目录：" + target);
				}
				if(target.isDirectory()){
					FileUtils.deleteDirectory(target);
				}else{
					target.delete();
				}
			}
		}else if(source.isDirectory()){
			if(target.isFile()){
				throw new ActionException("source is directory but target is file, soruce=" + source + ", target=" + target);
			}
			
			if(!target.exists()){
				target.mkdirs();
			}
			
			File sourceFiles[] = source.listFiles();
			File targetFiles[] = target.listFiles();
		
			for(File sfile : sourceFiles){
				boolean have = false;
				for(File tfile : targetFiles){
					if(sfile.getName().equals(tfile.getName())){
						have = true;
						syncDirOrFile(sfile, tfile, type, log);
					}
				}
				
				if(!have){
					if(sfile.isFile()){
						if(log){
							logger.info("覆盖源文件：" + sfile);
						}
						FileUtils.copyFile(sfile, new File(target, sfile.getName()),true);
					}else{
						File tfile = new File(target, sfile.getName());
						tfile.mkdirs();
						
						syncDirOrFile(sfile, tfile, type, log);
					}					
				}
			}
			
			for(File tfile : targetFiles){
				boolean have = false;
				for(File sfile : sourceFiles){				
					if(sfile.getName().equals(tfile.getName())){
						have = true;
						break;
					}
				}
				
				if(!have){
					if("keepSame".equals(type)){
						File sfile = new File(source, tfile.getName());
						if(tfile.isDirectory()){
							sfile.mkdirs();
							
							syncDirOrFile(sfile, tfile, type, log);
						}else{
							
							if(log){
								logger.info("覆盖目标文件：" + target);
							}
							FileUtils.copyFile(tfile, sfile, true);
						}
					}else{
						syncDirOrFile(null, tfile, type, log);
					}
				}
			}
		}else{
			if(target.isDirectory()){
				throw new ActionException("source is file but target is directory, soruce=" + source + ", target=" + target);
			}
			
			//通过校验码检查文件是否一致
			if(FileUtils.checksumCRC32(source) != FileUtils.checksumCRC32(target)){
				if(source.lastModified() < target.lastModified()){
					if("keepSourceSame".equals(type) || "keepSame".equals(type)){
						FileUtils.copyFile(target, source, true);
					}
				}else{
					FileUtils.copyFile(source, target, true);
				}
			}
		}
	}	
	
	public static String byteCountToDisplaySize(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Object size = self.doAction("getSize", actionContext);
		if(size == null){
			return null;
		}else if(size instanceof Number){
			return FileUtils.byteCountToDisplaySize(((Number) size).longValue());
		}else if(size instanceof BigInteger){
			return FileUtils.byteCountToDisplaySize((BigInteger) size);
		}else{
			return FileUtils.byteCountToDisplaySize(Long.parseLong(String.valueOf(size)));
		}
	}
	
	public static long checksumCRC32(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		Object file = self.doAction("getFile", actionContext);
		if(file == null){
			return 0;
		}else if(file instanceof File){
			return FileUtils.checksumCRC32((File) file);
		}else {
			String fileName = String.valueOf(file);
			return FileUtils.checksumCRC32(new File(fileName));
		}
	}
	
	public static void cleanDirectory(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		boolean debug = UtilAction.getDebugLog(self, actionContext);
		File file = getFile(self, "getFile", actionContext);		
		if(!file.exists()){
			return;
		}
		FileUtils.cleanDirectory(file);
	}
	
	public static File getFile(Thing self, String method, ActionContext actionContext){
		Object file = self.doAction(method, actionContext);
		if(file == null){
			return null;
		}else if(file instanceof File){
			return (File) file;
		}else {
			String fileName = String.valueOf(file);
			return new File(fileName);
		}
	}
	
	public static boolean contentEquals(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		File file1 = getFile(self, "getFile1", actionContext);
		File file2 = getFile(self, "getFile2", actionContext);
		
		return FileUtils.contentEquals(file1, file2);
	}
	
	public static boolean contentEqualsIgnoreEOL(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file1 = getFile(self, "getFile1", actionContext);
		File file2 = getFile(self, "getFile2", actionContext);
		String charsetName = (String) self.doAction("getCharsetName", actionContext);
		
		return FileUtils.contentEqualsIgnoreEOL(file1, file2, charsetName);	
	}
	
	public static void copyFileToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcFile = getFile(self, "getSrcFile", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		Boolean preserveFileDate = (Boolean) self.doAction("getPreserveFileDate", actionContext);
		
		FileUtils.copyFileToDirectory(srcFile, destDir, preserveFileDate);
	}
	
	public static void copyDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcDir = getFile(self, "getSrcDir", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		FileFilter filter = (FileFilter) self.doAction("getFilter", actionContext);
		Boolean preserveFileDate = (Boolean) self.doAction("getPreserveFileDate", actionContext);
		
		FileUtils.copyDirectory(srcDir, destDir, filter, preserveFileDate);
	}
	
	public static void copyDirectoryToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcDir = getFile(self, "getSrcDir", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		
		FileUtils.copyDirectoryToDirectory(srcDir, destDir);
	}
	
	public static void copyFile(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcFile = getFile(self, "getSrcFile", actionContext);
		File destFile = getFile(self, "getDestFile", actionContext);
		Boolean preserveFileDate = (Boolean) self.doAction("getPreserveFileDate", actionContext);
		
		FileUtils.copyFile(srcFile, destFile, preserveFileDate);
	}
	
	public static Object copyFileToStream(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File input = getFile(self, "getInput", actionContext);
		OutputStream output = (OutputStream) self.doAction("getOutput", actionContext);
		
		return FileUtils.copyFile(input, output);
	}
	
	public static void copyStreamToFile(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File destination = getFile(self, "getDestination", actionContext);
		InputStream source = (InputStream) self.doAction("getSource", actionContext);
		
		FileUtils.copyInputStreamToFile(source, destination);
	}
	
	public static void copyURLToFile(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File destination = getFile(self, "getDestination", actionContext);
		URL source = (URL) self.doAction("getSource", actionContext);
		int connectionTimeout = (Integer) self.doAction("getConnectionTimeout", actionContext);
		int readTimeout = (Integer) self.doAction("getReadTimeout", actionContext);
		
		FileUtils.copyURLToFile(source, destination, connectionTimeout, readTimeout);
	}
	
	public static void deleteDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = getFile(self, "getDirectory", actionContext);
		
		FileUtils.deleteDirectory(directory);
	}
	
	public static boolean deleteQuietly(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		File directory = getFile(self, "getDirectory", actionContext);
		
		return FileUtils.deleteQuietly(directory);
	}
	
	public static boolean directoryContains(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = getFile(self, "getDirectory", actionContext);
		File child = getFile(self, "getChild", actionContext);
		
		return FileUtils.directoryContains(directory, child);
	}
	
	public static void forceDelete(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		FileUtils.forceDelete(file);
	}
	
	public static void forceDeleteOnExit(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		FileUtils.forceDeleteOnExit(file);
	}
	
	public static void forceMkdir(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = getFile(self, "getDirectory", actionContext);
		
		FileUtils.forceMkdir(directory);
	}
	
	public static void forceMkdirParent(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		FileUtils.forceMkdir(file.getParentFile());
	}
	
	public static File getTempDirectory(ActionContext actionContext){
		return FileUtils.getTempDirectory();
	}
	
	public static String getTempDirectoryPath(ActionContext actionContext){
		return FileUtils.getTempDirectoryPath();
	}
	
	public static File getUserDirectory(ActionContext actionContext){
		return FileUtils.getUserDirectory();
	}
	
	public static String getUserDirectoryPath(ActionContext actionContext){
		return FileUtils.getUserDirectoryPath();
	}
	
	public static void moveDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcDir = getFile(self, "getSrcDir", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		
		FileUtils.moveDirectory(srcDir, destDir);
	}
	
	public static void moveDirectoryToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcDir = getFile(self, "getSrcDir", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		Boolean createDestDir = (Boolean) self.doAction("getCreateDestDir", actionContext);
		
		FileUtils.moveDirectoryToDirectory(srcDir, destDir, createDestDir);
	}
	
	public static void moveFile(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcFile = getFile(self, "getSrcFile", actionContext);
		File destFile = getFile(self, "getDestFile", actionContext);

		FileUtils.moveFile(srcFile, destFile);
	}
	
	public static void MoveFileToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File srcFile = getFile(self, "getSrcFile", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		Boolean createDestDir = (Boolean) self.doAction("getCreateDestDir", actionContext);

		FileUtils.moveFileToDirectory(srcFile, destDir, createDestDir);
	}
	
	public static void moveToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File src = getFile(self, "getSrc", actionContext);
		File destDir = getFile(self, "getDestDir", actionContext);
		Boolean createDestDir = (Boolean) self.doAction("getCreateDestDir", actionContext);

		FileUtils.moveToDirectory(src, destDir, createDestDir);
	}
	
	public static FileInputStream openInputStream(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		return FileUtils.openInputStream(file);
	}
	
	public static FileOutputStream openOutputStream(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		Boolean append = (Boolean) self.doAction("getAppend", actionContext);
		
		return FileUtils.openOutputStream(file, append);
	}
	
	public static byte[] readFileToByteArray(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		return FileUtils.readFileToByteArray(file);
	}
	
	public static String readFileToString(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		Object encoding = self.doAction("getEncoding", actionContext);
		if(encoding == null){
			return FileUtils.readFileToString(file);
		}else if(encoding instanceof Charset){
			return FileUtils.readFileToString(file, (Charset) encoding);
		}else{
			return FileUtils.readFileToString(file, String.valueOf(encoding));
		}
	}
	
	public static List<String> readLines(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		Object encoding = self.doAction("getEncoding", actionContext);
		if(encoding == null){
			return FileUtils.readLines(file);
		}else if(encoding instanceof Charset){
			return FileUtils.readLines(file, (Charset) encoding);
		}else{
			return FileUtils.readLines(file, String.valueOf(encoding));
		}
	}
	
	public static long sizeOf(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		return FileUtils.sizeOf(file);
	}
	
	public static void touch(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		
		FileUtils.touch(file);
	}
	
	public static void writeByteArrayToFile(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		byte[] data = (byte[]) self.doAction("getData", actionContext);
		//Integer off = (Integer) self.doAction("getOff", actionContext);
		//Integer len = (Integer) self.doAction("getLen", actionContext);
		Boolean append = (Boolean) self.doAction("getAppend", actionContext);
		
		//if(off != null && len != null && off >=0 && len >= 0){
			FileUtils.writeByteArrayToFile(file, data, append);
		//}
	}
	
	@SuppressWarnings("deprecation")
	public static void writeStringToFile(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File file = getFile(self, "getFile", actionContext);
		String data = (String) self.doAction("getData", actionContext);
		Boolean append = (Boolean) self.doAction("getAppend", actionContext);
		Object encoding = self.doAction("getEncoding", actionContext);
		
		if(encoding == null){
			FileUtils.writeStringToFile(file, data, append);
		}else if(encoding instanceof Charset){
			FileUtils.writeStringToFile(file, data, (Charset) encoding, append);
		}else{
			FileUtils.writeStringToFile(file, data, String.valueOf(encoding), append);
		}
	}
	
	public static boolean filePathEquals(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		File src = self.doAction("getSrcFile", actionContext);
		File dest = self.doAction("getDestFile", actionContext);
		if(src == null || dest == null) {
			return false;
		}
		
		return src.getCanonicalPath().equals(dest.getCanonicalPath());
	}
	
	@SuppressWarnings("unchecked")
	public static Object sortFiles(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Object files = self.doAction("getFiles", actionContext);
		Comparator<File> comp = (Comparator<File>) self.doAction("getComparator", actionContext);
		if(files instanceof List<?>){
			Collections.sort((List<File>) files, comp);
			return files;
		}else if(files instanceof File[]){
			Arrays.sort((File[]) files, comp);
			
			return files;
		}else if(files instanceof Iterable<?>){
			Iterable<File> itera = (Iterable<File>) files;
			Iterator<File> iter = itera.iterator();
			List<File> list = new ArrayList<File>();
			while(iter.hasNext()){
				list.add(iter.next());
			}
			Collections.sort(list);
			return list;
		}else if(files instanceof Iterator<?>){
			Iterator<File> iter = (Iterator<File>) files;
			List<File> list = new ArrayList<File>();
			while(iter.hasNext()){
				list.add(iter.next());
			}
			Collections.sort(list);
			return list;
		}else{
			throw new ActionException("Not support fiels=" + files + ", support List<File>,File[], Iterable<File>,Iterator<File>.");
		}
	}

	public static boolean exists(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		if(file == null || file.exists() == false){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isFile(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		if(file == null || file.isFile() == false){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isDirectory(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		if(file == null || file.isDirectory() == false){
			return false;
		}else{
			return true;
		}
	}
}

