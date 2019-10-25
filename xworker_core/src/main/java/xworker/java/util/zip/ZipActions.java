package xworker.java.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ognl.OgnlException;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ZipActions {
	/**s
	 * ZIP压缩文件。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 * @throws OgnlException 
	 */
	public static void zipCompress(ActionContext actionContext) throws IOException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		File sourceFile = getFile(self.doAction("getSourceFile", actionContext)); 
		if(sourceFile == null || !sourceFile.exists()){
			throw new ActionException("source file is null or not exists, path=" + self.getMetadata().getPath());
		}
		
		File targetZipFile = getFile(self.doAction("getTargetZipFile", actionContext));
		if(targetZipFile == null){
			throw new ActionException("target file is null, path=" + self.getMetadata().getPath());
		}
		
		if(!targetZipFile.exists()){
			targetZipFile.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(targetZipFile);
		ZipOutputStream zout = new ZipOutputStream(fout);
		try{
			if(sourceFile.isDirectory() && !self.getBoolean("includeRootDir")){
				for(File child : sourceFile.listFiles()){
					doZipCompress(zout, child, "");
				}
			}else{
			    doZipCompress(zout, sourceFile, "");
			}
		}finally{
			try {
				zout.finish();
				zout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void doZipCompress(ZipOutputStream zout, File file, String root) throws IOException{
		if(file.isDirectory()){
			String path = root + "/" + file.getName();
			for(File child : file.listFiles()){
				doZipCompress(zout, child, path);
			}
		}else{
			ZipEntry entry = new ZipEntry(root + "/" + file.getName());
			entry.setTime(file.lastModified());
			entry.setSize(file.length());
			
			zout.putNextEntry(entry);
			byte[] bytes = new byte[2048];
			FileInputStream fin = new FileInputStream(file);
			try{
				int length = -1;
				while((length = fin.read(bytes)) != -1){
					zout.write(bytes, 0, length);
				}
				entry.setSize(file.length());
				zout.closeEntry();
			}finally{
				fin.close();
			}
		}
	}	
	
	public static File getFile(Object obj) throws OgnlException{
		if(obj instanceof File){
			return (File) obj;
		}else if(obj instanceof String){
			File file = new File((String) obj);
			return file;
		}
	
		return null;
	}
	
}
