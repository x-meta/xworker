package xworker.util.filesync;

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
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.World;

public class FileSync {
	private static Logger logger = LoggerFactory.getLogger(FileSync.class);
	private String serverFileList = null;
	private File serverFileListFile = new File(World.getInstance().getPath() + "/work/update/filelist.txt");
	public static String charset = "UTF-8";
	public FileSync(){		
	}
	
	public FileSync(File serverFileListFile){
		this.serverFileListFile = serverFileListFile;
	}
	
	public synchronized String getServerFileList() throws IOException{
		if(serverFileList == null){
			serverFileList = getFileList(serverFileListFile);		
		}
		
		return serverFileList;
	}
	
	public synchronized void setServerFileList(File fileListFile, String serverFileList) throws IOException{
		FileUtils.write(fileListFile, serverFileList, Charset.forName("UTF-8"));
		
		this.serverFileList = serverFileList;
	}
	
	/**
	 * 把文件对比和版本信息放到指定目录下，更新的文件打包成upload.zip文件。
	 *  
	 * @param targetDir
	 * @param rootDir
	 * @param oldFileList
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public void generateUploadInfo(File targetDir, File rootDir, String oldFileList, FileFilter filter) throws IOException, NoSuchAlgorithmException{
		FileList newList = new FileList(rootDir, filter);
		FileList oldList = new FileList(oldFileList); 		
		CompareResult result = oldList.compare(newList);
		
		if(!result.isChanged()){
			logger.info("no changed");
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
		
		logger.info("generateUploadInfo finished");
	}
	
	/**
	 * 从服务器上获取版本信息文件。
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public String getFileList(String url) throws IOException{
		try{
			URL u = new URL(url);		
			logger.info("open " + url);
			URLConnection uc = u.openConnection();
			uc.setRequestProperty("accept", "*/*"); 
			uc.setRequestProperty("Connection", "Keep-Alive");
			//uc.setRequestProperty("user-agent",              "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
			uc.setDoInput(true);
			
			HttpURLConnection conn = (HttpURLConnection) uc;
			conn.setUseCaches(true);// 不使用Cache
			
			int length = uc.getContentLength();			
			byte[] bytes = getBytes(uc.getInputStream(), length);
			
			logger.info("file info readed");
			return new String(bytes, charset);
		}catch(Exception e){
			logger.warn(e.getMessage());
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
	public void upload(String versionUrl, String uploadUrl, String user, String password, File rootDir, String filter) throws IOException, NoSuchAlgorithmException{
		FileFilter ffilter = new FileFilter(rootDir, filter);
		
		logger.info("get server file info");
		String oldFileList = getFileList(versionUrl);
		
		logger.info("connect to server for upload");
		URL url = new URL(uploadUrl);
		
		logger.info("connect to server " + url);
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
		
		logger.info("write user & password");
		putString(user, out);
		putString(password, out);
		out.flush();
		
		logger.info("compare and write changed files");
		upload(rootDir, oldFileList, out, ffilter);
		out.flush();		
		
		conn.setRequestProperty("Content-Length", "" + out.size());
		uc.connect();
		OutputStream cout = uc.getOutputStream();
		cout.write(out.toByteArray());
		cout.flush();
		
		logger.info("waitting server response...");
		InputStream in = uc.getInputStream();
		logger.info("server return: " + getString(in));
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
	public void upload(File rootDir, String oldFileList, OutputStream out, FileFilter filter) throws IOException, NoSuchAlgorithmException{
		FileList newList = new FileList(rootDir, filter);
		FileList oldList = new FileList(oldFileList); 		
		CompareResult result = oldList.compare(newList);
		if(!result.isChanged()){
			logger.info("no file changed");
			out.write("00000000".getBytes(charset));			
		}else{
			//写入完整的版本信息
			putString(newList.toString(), out);
			logger.info("version writed");
			
			//写入输出流中
			writeResultToZip(rootDir, result, out);
			logger.info("all file writed");
		}
		
		logger.info("upload finished");
	}
	
	/**
	 * 下载文件，从流里读出版本信息和文件列表。
	 * 
	 * @param targetDir
	 * @param versionFile
	 * @param in
	 * @throws IOException
	 */
	public void download(File targetDir, File versionFile, InputStream in) throws IOException{
		logger.info("get new file list");
		String newFileList = getString(in);
		if(newFileList == null || "".equals(newFileList)){
			logger.info("no new file download");
			return;
		}
		
		FileList newList = new FileList(newFileList);
		
		logger.info("get my file list");
		String myFileList = getFileList(versionFile);
		FileList myList = new FileList(myFileList);		
		CompareResult result = myList.compare(newList);
		
		logger.info("delete removed files");
		for(FileInfo info : result.removeList){
			File file = new File(targetDir, info.path);
			file.delete();
			
			logger.info("file deleted: " + info.path);
		}
		
		logger.info("unzip files");
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
			logger.info("file saved: " + entry.getName());
		}		
		zin.close();
		
		logger.info("write new list");
		FileUtils.write(versionFile, newFileList, Charset.forName("utf-8"));
	}
	
	/**
	 * 服务器端的响应上传的请求。
	 * 
	 * @param targetDir
	 * @param fileListFile
	 * @param in
	 * @param out
	 * @param checker
	 * @throws IOException 
	 */
	public void serverUpload(File targetDir, File fileListFile, InputStream in, OutputStream out, FileUploadChecker checker) throws IOException{
		logger.info("server handle upload");
		String user  = getString(in);
		String password = getString(in);
		
		//检查用户信息
		if(checker != null && checker.checkUser(user, password) == false){
			putString("User and password check failure", out);
			return;
		}
		
		//新版本信息
		String newFileList = getString(in);
		if("".equals(newFileList)){
			putString("no file changed", out);
			return;
		}
		logger.info("fileFIleList size=" + newFileList.length());
		
		String oldFileList = getFileList(fileListFile);
		FileList newList = new FileList(newFileList);
		FileList oldList = new FileList(oldFileList);
		CompareResult result = oldList.compare(newList);
		logger.info("delete removed files");
		for(FileInfo info : result.removeList){
			File file = new File(targetDir, info.path);
			file.delete();
			
			logger.info("file deleted: " + info.path);
		}
		
		logger.info("unzip files");
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
			logger.info("file saved: " + entry.getName());
		}		
		zin.close();
		
		logger.info("write new list");
		setServerFileList(fileListFile, newFileList);
		//FileUtils.write(fileListFile, newFileList);
		
		putString("upload successed", out);
		out.flush();
	}
	
	/**
	 * 服务器端处理用户下载文件的请求。
	 * 
	 * @param rootDir
	 * @param in
	 * @param out
	 * @throws IOException 
	 */
	public void serverDownload(File rootDir, InputStream in, OutputStream out) throws IOException{	
		String oldFileList = getString(in);
		String newFileList = getServerFileList();
		FileList oldList = new FileList(oldFileList);
		FileList newList = new FileList(newFileList);
		
		CompareResult result = oldList.compare(newList);
		if(!result.isChanged()){
			out.write("00000000".getBytes(charset));			
		}else{
			//写入完整的版本信息
			putString(newList.toString(), out);
			
			//写入输出流中
			writeResultToZip(rootDir, result, out);
		}
		out.flush();
	}
	
	/**
	 * 从服务器下载文件列表。
	 * 
	 * @param serverUrl
	 * @param targetDir
	 * @param versionFile
	 * @throws IOException 
	 */
	public void download(String serverUrl, File targetDir, File versionFile) throws IOException{
		logger.info("get my file list");
		String myFileList = getFileList(versionFile);
		
		logger.info("connect to server for upload");
		URL url = new URL(serverUrl);
		
		logger.info("connect to server " + url);
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
		
		logger.info("upload my file list to server");
		putString(myFileList, out);
		out.flush();
		
		logger.info("get server file list");
		InputStream in = uc.getInputStream();
		download(targetDir, versionFile, in);
		
		in.close();
		out.close();
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
			FileUtils.copyFile(file, zout);
			//entry.setSize(file.length());
			zout.flush();
			zout.closeEntry();
			
			logger.info("zip added: " + info.path);
		}
		
		for(FileInfo info : result.newList){
			//把文件输入到zip流中
			File file = new File(rootDir, info.path);
			ZipEntry entry = new ZipEntry(info.path);
			entry.setTime(file.lastModified());
			zout.putNextEntry(entry);
			
			FileUtils.copyFile(file, zout);
			
			zout.closeEntry();
			
			//logger.info("zip added: " + info.path);
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
				return new String(bytes, charset);
			}finally{
				fin.close();
			}
		}
	}
	
	public static void testUploadToServer() throws NoSuchAlgorithmException, IOException{
		File rootDir = new File("e:\\git\\xworker_explorer\\xworker_explorer\\target\\xworker\\");
		String fileListUrl = "http://localhost:9001/do?sc=xworker.tools.update.GetFileList";
		String uploadUrl = "http://localhost:9001/do?sc=xworker.tools.update.Upload";
		String user = "test";
		String password = "123";
		String filter = "actionClasses\n" + 
					"actionSources\n" + 
				"databases\n" +
					"updateindex\n" +
				"work\n" +
					"log\n" +
				"projects/_local";
		FileSync fileSync = new FileSync();		
		fileSync.upload(fileListUrl, uploadUrl, user, password, rootDir, filter);
	}
	
	public static void testDownload() throws IOException{
		File rootDir = new File("e:\\git\\xworker_explorer\\xworker_explorer\\xworker\\work\\xworker");
		File fileListFile = new File("e:\\git\\xworker_explorer\\xworker_explorer\\xworker\\work\\xworker\\conf\\filelist.txt");
		String downloadUrl = "http://localhost:9001/do?sc=xworker.tools.update.Download";
		
		FileSync fileSync = new FileSync();	
		fileSync.download(downloadUrl, rootDir, fileListFile);
	}
		
	
	public static void main(String args[]){
		try{
			//测试上传到服务器
			testUploadToServer();
			
			//测试下载
			//testDownload();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
