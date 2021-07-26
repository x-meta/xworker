package xworker.startup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Update {
	/**
	 * 向服务器发送请求，检查是否有更新。
	 * 
	 * @param url
	 * @throws IOException 
	 */
	public static String checkUpdate(String url) throws IOException{
		System.out.println("Check update from: " + url);
		URL u = new URL(url);
		URLConnection con = u.openConnection();

		InputStream in = con.getInputStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		byte[] bs = new byte[1024];
		int length = -1;
		while((length = in.read(bs)) != -1){
			bout.write(bs, 0, length);
		}
		String content = new String(bout.toByteArray());
		if(content.startsWith("|")){
			String ss[] = content.split("[|]");
			if(ss.length == 3){
				String version = ss[1];
				String downUrl = ss[2];
				
				System.out.println("Found new version: " + version + ", url=" + downUrl);
				doUpdate(version, downUrl);
				return version;
			}
		}
		
		System.out.println("No update, Server return " + content);
		
		return null;
	}
	
	/**
	 * 执行更新操作。
	 * 
	 * @param version
	 * @param downUrl
	 * @throws IOException 
	 */
	public static void doUpdate(String version, String downUrl) throws IOException{
		URL u = new URL(downUrl);
		URLConnection con = u.openConnection();
		
		int totalLength = con.getContentLength();
		
		InputStream in = con.getInputStream();
		FileOutputStream fout = new FileOutputStream(version + ".zip");
		long start = System.currentTimeMillis();
		try{
			int downloadSize = 0;
			int speedSize = 0;
			byte[] bytes = new byte[4096];
			int length = -1;
			while((length = in.read(bytes)) != -1){
				fout.write(bytes, 0, length);
				downloadSize += length;
				speedSize += length;
				
				if(System.currentTimeMillis() - start > 3000){
					System.out.println("总大小：" + getSizeInfo(totalLength) + "，已下载：" + getSizeInfo(downloadSize) 
							+ "，速度：" + getSizeInfo(speedSize / 5) + "/秒");
				}
			}
		}finally{
			fout.close();
		}
		
		//解压文件
		File zipFile = new File(version + ".zip");
		ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
		try{
			ZipEntry entry = null;
			while((entry = zin.getNextEntry()) != null){
				if(!entry.isDirectory()){
					String fileName = entry.getName();
					if(fileName.startsWith("/") || fileName.startsWith("\\")){
						fileName = fileName.substring(1, fileName.length());
					}
					System.out.println("解压文件：" + fileName);
					File outFile = new File(fileName);
					if(!outFile.exists()){
						if(outFile.getParentFile() != null){
						    outFile.getParentFile().mkdirs();
						}
					}
					
					FileOutputStream fout1 = new FileOutputStream(outFile);
					try{
						byte[] bytes = new byte[4096];
						int length = -1;
						while((length = zin.read(bytes)) != -1){
							fout1.write(bytes, 0, length);
						}
					}finally{
						fout1.close();
						outFile.setLastModified(entry.getTime());
					}
				}
			}			
		}finally{
			zin.close();
		
			//最后删除zip
			zipFile.delete();
		}
	}	
	
	public static String getSizeInfo(double size) {
		DecimalFormat sf = new DecimalFormat("#.##");
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else if (size < 1024 * 1024) {
			return sf.format(size / 1024) + "KB";
		} else {
			return sf.format(size / 1024 / 1024) + "MB";
		}
	}
	
	public static void main(String args[]){
		try{
			Properties p = new Properties();
			FileInputStream fin = new FileInputStream("update.inf");
			p.load(fin);
			fin.close();
			
			String downUrl = p.getProperty("url");
			String version = p.getProperty("version");
			
			String newVersion = checkUpdate(downUrl + "&version=" + version);
			if(newVersion != null){
				p.put("version", newVersion);
				FileOutputStream fout = new FileOutputStream("update.inf");
				p.store(fout, "");
				fout.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
