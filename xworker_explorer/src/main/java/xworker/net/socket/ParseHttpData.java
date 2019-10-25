package xworker.net.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import xworker.httpclient.HttpClientManager;

/**
 * 分析SocketProxy保存的Http信息。
 * 
 * @author zyx
 *
 */
public class ParseHttpData {
	Map<String, byte[]> requests = new HashMap<String, byte[]>();
	
	public void parse(String path) throws IOException{
		List<String> files = new ArrayList<String>();
		File file = new File(path);
		for(String name : file.list()){
			files.add(name);
		}
		
		//文件配对
		for(int i=0; i<files.size(); i++){
			String f1 = files.get(i);
			for(int n=i+1; n < files.size(); n++){
				String f2 = files.get(n);
				if(isPaire(f1, f2)){
					parse(new File(path + "/" + f1), new File(path + "/" + f2));
				}
			}
		}
		
		//启动模拟的服务
		new Thread(new HttpDataServer(9005, requests)).start();
		
		//模拟httpclient请求并保存数据
		for(String key : requests.keySet()){
			requestAndSave("http://localhost:9005/" + key, path + "/" + key);
			System.out.println("parser url: " + key) ;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void requestAndSave(String url, String filePath){
		HttpGet httpGet = new HttpGet(url);

		//httpClient
		HttpClient httpclient = HttpClientManager.getDefaultHttpClient();

		HttpResponse response = null;
		HttpEntity entity = null;
		
		try{
		    response = httpclient.execute(httpGet);
		    entity = response.getEntity();
		    
		    File file = new File(filePath);
		    if(!file.getParentFile().exists()){
		    	file.getParentFile().mkdirs();
		    }
		    FileOutputStream fout = new FileOutputStream(file);
		    
		    InputStream in = entity.getContent();
		    byte[] bytes = new byte[2048];
		    int length = -1;
		    while((length = in.read(bytes)) != -1){
		    	fout.write(bytes, 0, length);
		    }

		    fout.close();		    
		}catch(Exception e){
			
		}finally{
		    if(entity != null){
		        try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		}
	}
	
	
	public  void parse(File inFile, File outFile) throws IOException{
		System.out.println(inFile.getName() + ":" + outFile.getName());
		FileInputStream reqIn = new FileInputStream(outFile);
		FileInputStream resIn = new FileInputStream(inFile);
		try{
			List<String> reqUrls = parseRequestUrl(reqIn);
			byte[] bytes = new byte[resIn.available()];
			resIn.read(bytes);
			
			List<byte[]> resList = splitResponse(bytes);
			for(int i=0; i<reqUrls.size(); i++){
				//放到缓存中
				requests.put(reqUrls.get(i), resList.get(i));
				System.out.println("parser url: " + reqUrls.get(i) + ", response: " + resList.get(i).length) ;
			}
		}finally{
			reqIn.close();
			resIn.close();
		}
	}
	
	public  List<byte[]> splitResponse(byte[] bytes){
		List<byte[]> list = new ArrayList<byte[]>();
		int start = 0;
		int end = 0;		
		for(int i=0; i<bytes.length; i++){
			if(isHttp(bytes, i)){
				if(end != 0){
					byte[] bs = new byte[end - start + 1];
					System.arraycopy(bytes, start, bs, 0, bs.length);
					list.add(bs);
					
					start = i;
					end = i;
				}
			}else{
				end = i;
			}
		}
		
		if(start != end){
			byte[] bs = new byte[bytes.length - start];
			System.arraycopy(bytes, start, bs, 0, bs.length);
			list.add(bs);
		}
		
		return list;
	}
	
	public  boolean isHttp(byte[] bytes, int index){
		if(index < bytes.length - 5){
			String str = new String(bytes, index, 4);
			if("HTTP".equals(str)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * 先支持GET的获取。
	 * 
	 * @param reqIn
	 * @return
	 * @throws IOException
	 */
	public  List<String> parseRequestUrl(FileInputStream reqIn) throws IOException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(reqIn));
		try{
			List<String> reqs = new ArrayList<String>();
			String line = null;
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.startsWith("GET")){
					reqs.add(line.split("[ ]")[1]);
				}
			}
			
			return reqs;
		}finally{
			br.close();
		}		
	}
	
	private  boolean isPaire(String f1, String f2){
		if(f1.startsWith("in_") && f2.startsWith("out_")){
			if(f1.subSequence(3, f1.length()).equals(f2.subSequence(4, f2.length()))){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public static void main(String args[]){
		try{
			ParseHttpData phd = new ParseHttpData();
			phd.parse("f:\\temp\\go10086\\2016-05-30\\");
			
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
