package xworker.filesync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileSync {
	public static String charset = "UTF-8";
	/**
	 * 把文件对比和版本信息放到指定目录下，更新的文件打包成upload.zip文件。
	 *  
	 * @param targetDir
	 * @param rootDir
	 * @param oldFileList
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void generateUploadInfo(File targetDir, File rootDir, String oldFileList, FileFilter filter) throws IOException, NoSuchAlgorithmException{
		FileList newList = new FileList(rootDir, filter);
		FileList oldList = new FileList(oldFileList); 		
		CompareResult result = oldList.compare(newList);
		
		if(!result.isChanged()){
			System.out.println("no changed");
		}else{
			//写入完整的版本信息
			FileOutputStream fout = new FileOutputStream(new File(targetDir, "version.txt"));
			fout.write(newList.toString().getBytes(charset));
			fout.close();
						
			
			//写入输出流中
			fout = new FileOutputStream(new File(targetDir, "files.zip"));
			writeResultToZip(rootDir, result, fout);
			fout.close();
		}
		
		System.out.println("generateUploadInfo finished");
	}
	
	/**
	 * 从服务器上获取版本信息文件。
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static String getFileList(String url) throws IOException{
		try{
			URL u = new URL(url);		
			System.out.println("open " + url);
			URLConnection uc = u.openConnection();
			uc.setRequestProperty("accept", "*/*"); 
			uc.setRequestProperty("Connection", "Keep-Alive");
			//uc.setRequestProperty("user-agent",              "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
			uc.setDoInput(true);
			
			HttpURLConnection conn = (HttpURLConnection) uc;
			conn.setUseCaches(true);// 不使用Cache
			
			int length = uc.getContentLength();			
			byte[] bytes = getBytes(uc.getInputStream(), length);
			
			System.out.println("file info readed");
			return new String(bytes, charset);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return "";
		}
	}
	
	/**
	 * 向服务器上传版本。
	 * 
	 * @param versionUrl
	 * @param uploadUrl
	 * @param user
	 * @param password
	 * @param rootDir
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void upload(String versionUrl, String uploadUrl, String user, String password, File rootDir, String filter) throws IOException, NoSuchAlgorithmException{
		FileFilter ffilter = new FileFilter(rootDir, filter);
		
		System.out.println("get server file info");
		String oldFileList = getFileList(versionUrl);
		
		System.out.println("connect to server for upload");
		URL url = new URL(uploadUrl);
		
		System.out.println("connect to server " + url);
		URLConnection uc = url.openConnection();		
		uc.setRequestProperty("accept", "*/*");  
		//uc.setRequestProperty("user-agent",              "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
		uc.setDoOutput(true);
		uc.setDoInput(true);
		HttpURLConnection conn = (HttpURLConnection) uc;
		conn.setUseCaches(true);// 不使用Cache
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		//conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type","application/octet-stream");				
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		System.out.println("write user & password");
		putString(user, out);
		putString(password, out);
		out.flush();
		
		System.out.println("compare and write changed files");
		upload(rootDir, oldFileList, out, ffilter);
		out.flush();		
		
		conn.setRequestProperty("Content-Length", "" + out.size());
		uc.connect();
		OutputStream cout = uc.getOutputStream();
		cout.write(out.toByteArray());
		cout.flush();
		
		System.out.println("waitting server response...");
		InputStream in = uc.getInputStream();
		System.out.println("server return: " + getString(in));
		out.close();
		in.close();				
	}
	
	/**
	 * 由于Jetty会断流，数据不能全部读取，所以编写次方法。
	 * 
	 * @param in
	 * @param length
	 * @return
	 * @throws IOException 
	 */
	public static ByteArrayInputStream readComplete(InputStream in , int length) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
		int l = 0;
		while(length > 0){
			l = in.read(bytes);
			if(l > 0){
				out.write(bytes, 0, l);
				length = length - l;
			}
		}
		
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	/**
	 * 把版本更新修改和新的文件打包到输出流中。
	 * 
	 * @param rootDir
	 * @param oldFileList
	 * @param out
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void upload(File rootDir, String oldFileList, OutputStream out, FileFilter filter) throws IOException, NoSuchAlgorithmException{
		FileList newList = new FileList(rootDir, filter);
		FileList oldList = new FileList(oldFileList); 		
		CompareResult result = oldList.compare(newList);
		if(!result.isChanged()){
			System.out.println("no file changed");
			out.write("00000000".getBytes(charset));			
		}else{
			//写入完整的版本信息
			putString(newList.toString(), out);
			System.out.println("version writed");
			
			//写入输出流中
			writeResultToZip(rootDir, result, out);
			System.out.println("all file writed");
		}
		
		System.out.println("upload finished");
	}
	
	/**
	 * 下载文件，从流里读出版本信息和文件列表。
	 * 
	 * @param targetDir
	 * @param versionFile
	 * @param in
	 * @throws IOException
	 */
	public static void download(File targetDir, File versionFile, InputStream in) throws IOException{
		System.out.println("get new file list");
		String newFileList = getString(in);
		if(newFileList == null || "".equals(newFileList)){
			System.out.println("no new file download");
			return;
		}
		
		FileList newList = new FileList(newFileList);
		
		System.out.println("get my file list");
		String myFileList = getFileList(versionFile);
		FileList myList = new FileList(myFileList);		
		CompareResult result = myList.compare(newList);
				
		System.out.println("unzip files");
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry entry = null;		
		while((entry = zin.getNextEntry()) != null){
			File newFile = new File(targetDir, entry.getName());
			
			if(newFile.exists() == false){
				newFile.getParentFile().mkdirs();
			}
			FileOutputStream fout = new FileOutputStream(newFile);
			try{
				byte[] bytes = new byte[4096];
				int l = -1;
				while((l = zin.read(bytes)) != -1){
					fout.write(bytes, 0, l);
				}			
			}finally{
				fout.close();
				newFile.setLastModified(entry.getTime());
			}
			zin.closeEntry();
			System.out.println("file saved: " + entry.getName());
		}		
		zin.close();
		
		System.out.println("delete removed files");
		for(FileInfo info : result.removeList){
			File file = new File(targetDir, info.path);
			if(file.exists()){
				File parentFile = file.getParentFile();
				if(!file.delete()){
					System.out.println("can not delete file: " + info.path + ", exit update");
					return;
				}
				
				//如果父目录为空，那么一并删掉
				if(parentFile.listFiles() == null || parentFile.listFiles().length == 0) {
					parentFile.delete();
				}
			}
			
			System.out.println("file deleted: " + info.path);
		}
		
		System.out.println("write new list");
		writeToFile(versionFile, newFileList);
	}
	
	/**
	 * 从服务器下载文件列表。
	 * 
	 * @param serverUrl
	 * @param targetDir
	 * @param versionFile
	 * @throws IOException 
	 */
	public static void download(String serverUrl, File targetDir, File versionFile) throws IOException{
		System.out.println("get my file list");
		String myFileList = getFileList(versionFile);
		if(myFileList == null){
			System.out.println("My file list is null, versionFile=" + versionFile);
			return;
		}
		
		System.out.println("connect to server for upload");
		URL url = new URL(serverUrl);
		
		System.out.println("connect to server " + url);
		URLConnection uc = url.openConnection();
		uc.setDoOutput(true);
		uc.setDoInput(true);
		HttpURLConnection conn = (HttpURLConnection) uc;
		conn.setUseCaches(true);// 不使用Cache
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		//conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type","application/octet-stream");
		
		OutputStream out = uc.getOutputStream();
		
		System.out.println("upload my file list to server");
		putString(myFileList, out);
		out.flush();
		
		System.out.println("get server file list");
		InputStream in = uc.getInputStream();
		download(targetDir, versionFile, in);
		
		in.close();
		out.close();
	}
	
	public static void copyFile(File file, OutputStream out) throws IOException{
		FileInputStream fin = new FileInputStream(file);
		try{
			byte[] bytes = new byte[fin.available()];
			fin.read(bytes);
			out.write(bytes);
			out.flush();
		}finally{
			fin.close();
		}
	}
	
	public static void writeResultToZip(File rootDir, CompareResult result, OutputStream out) throws IOException{
		//压缩文件
		ZipOutputStream zout = new ZipOutputStream(out);
		for(FileInfo info : result.changedList){
			//把文件输入到zip流中
			File file = new File(rootDir, info.path);
			ZipEntry entry = new ZipEntry(info.path);
			
			entry.setTime(file.lastModified());
			zout.putNextEntry(entry);			
			copyFile(file, zout);
			//entry.setSize(file.length());
			zout.flush();
			zout.closeEntry();
			
			System.out.println("zip added: " + info.path);
		}
		
		for(FileInfo info : result.newList){
			//把文件输入到zip流中
			File file = new File(rootDir, info.path);
			ZipEntry entry = new ZipEntry(info.path);
			entry.setTime(file.lastModified());
			zout.putNextEntry(entry);
			
			copyFile(file, zout);
			
			zout.closeEntry();
			
			//System.out.println("zip added: " + info.path);
		}
		zout.close();
		out.flush();
	}
	
	public static byte[] getBytes(InputStream in, int length) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
		int l = 0;
		while(length > 0){
			if(length < bytes.length){
				l = in.read(bytes, 0, length);
			}else{
				l = in.read(bytes);
			}
			if(l > 0){
				out.write(bytes, 0, l);
				length = length - l;
			}else if(l == -1){				
				break;
			}
		}
		return out.toByteArray();
	}
	
	public static String getString(InputStream in) throws IOException{
		byte[] bytes = getBytes(in, getLength(in));
		return new String(bytes, charset);
	}
	
	public static void putString(String str, OutputStream out) throws IOException{
		byte[] bytes = str.getBytes(charset);
		writeLength(bytes.length, out);
		out.write(bytes);
	}
	
	public static int getLength(InputStream in) throws IOException{
		byte[] bytes = getBytes(in, 8);
		if(bytes.length == 0){
			return 0;
		}
		
		return Integer.parseInt(new String(bytes, charset));
	}
	
	public static void writeLength(int length, OutputStream out) throws IOException{
		String l = String.valueOf(length);
		while(l.length() < 8){
			l = "0" + l;
		}
		
		out.write(l.getBytes(charset));
	}
	
	public static String getFileList(File file) throws IOException{
		if(!file.exists() || !file.isFile()){
			return "";
		}else{
			FileInputStream fin = new FileInputStream(file);
			try{
				byte[] bytes = new byte[fin.available()];
				fin.read(bytes);
				//System.out.println("文件已读!");
				//检查文件是否存在
				String list = new String(bytes, charset);
				StringBuffer newList = new StringBuffer();
				
				String lines[] = list.split("[\n]");
				//System.out.println("文件行已分割!");
				//int count = 0;
				for(String line : lines){
					
					line = line.trim();
					if("".equals(line)){
						continue;
					}
					
					String[] fileInfo = line.split("[|]");
					File file1 = new File("." + fileInfo[0]);
					if(file1.exists() == false){
						continue;
					}else{
						if(newList.length() > 0) {
							newList.append("\n");
						}
						newList.append(line);
						
					}
						
					/*
					count++;
					if(count % 100 == 0) {
						System.out.println("已处理" + count + "行了。");
					}*/
				}
				
				return newList.toString();
			}finally{
				fin.close();
			}
		}
	}
		
	public static byte[] readFileToByteArray(File file) throws IOException{
		FileInputStream fin = new FileInputStream(file);
		try{
			byte[] bytes = new byte[fin.available()];
			fin.read(bytes);
			return bytes;
		}finally{
			fin.close();
		}
	}
	
	public static void writeToFile(File file, String text) throws IOException{
		if(file.exists() == false){
			file.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(file);
		try{
			fout.write(text.getBytes(charset));
		}finally{
			fout.close();
		}
	}
	
	public static void main(String args[]){
		try{
			String project = null;
			if(args.length > 0) {
				project = args[0];
			}
			
			File rootDir = new File("./");
		
			File fileListFile = null;
			if(project == null) {
				fileListFile = new File("./config/filelist.txt");
			}else {
				fileListFile = new File("./config/" + project + "filelist.txt");
			}
			if(fileListFile.exists() == false){
				fileListFile.getParentFile().mkdirs();
			}
						
			String downloadUrl = "https://www.xworker.org/do?sc=xworker.tools.update.Download";
			if(project != null) {
				downloadUrl = downloadUrl + "&project=" + project;
			}
			
			FileSync.download(downloadUrl, rootDir, fileListFile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
