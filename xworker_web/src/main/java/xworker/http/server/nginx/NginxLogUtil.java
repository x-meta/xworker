package xworker.http.server.nginx;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Nginx日志分析程序。
 * 
 * @author Administrator
 *
 */
public class NginxLogUtil {
	public static List<AppLog> parse(String nginxLog) throws IOException{
		ByteArrayInputStream bin = new ByteArrayInputStream(nginxLog.getBytes());
		return parse(bin);
	}
	
	public static List<AppLog> parse(InputStream in) throws IOException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		
		Map<String, AppLog> logMap = new HashMap<String, AppLog>();
		List<AppLog> logList = new ArrayList<AppLog>();
		while((line = br.readLine()) != null){
			//IP
			int index = line.indexOf(" ");
			if(index == -1){
				continue;
			}
			String ip = line.substring(0, index).trim();
			
			//日期
			index = line.indexOf("[");
			if(index == -1){
				continue;
			}
			int index1 = line.indexOf("]", index);
			if(index1 == -1){
				continue;
			}
			String dateStr = line.substring(index + 1, index1);
			Date date = parseDate(dateStr);
			if(date == null){
				continue;
			}
			
			//url
			index = line.indexOf(" /", index1);
			if(index == -1){
				continue;
			}
			index1 = index;
			index = line.indexOf("/", index1 + 2);
			if(index == -1){
				index = line.indexOf(".", index1);
			}
			if(index == -1){
				continue;
			}
			String app = line.substring(index1 + 2, index);
			
			//appLog
			AppLog log = logMap.get(app);
			if(log == null){
				log = new AppLog();
				log.url = app;
				logMap.put(app, log);
				logList.add(log);
			}
			
			log.count ++;
			log.ipMap.put(ip, ip);
			if(log.minDate == null || log.minDate.compareTo(date) > 0){
				log.minDate = date;
			}
			if(log.maxDate == null || log.maxDate.compareTo(date) < 0){
				log.maxDate = date;
			}
		}
		
		for(AppLog log : logList){
			log.doSum();
		}
		
		return logList;
	}
	
	public static Date parseDate(String dateStr){
		SimpleDateFormat sf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
		try{
			return sf.parse(dateStr);
		}catch(Exception e){
			
		}
		
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return sf.parse(dateStr);
		}catch(Exception e){
			
		}
		
		return null;
	}
	
	public static void main(String args[]){
		try{
			FileInputStream fin = new FileInputStream("e:/temp/1.log");
			List<AppLog> logs = parse(fin);
			fin.close();
			
			for(AppLog log : logs){
				System.out.println(log);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
