package xworker.app.xworker;

import java.io.FileInputStream;
import java.util.Properties;

import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerDeployConfig {
	static Properties p = new Properties();
	static{
		try{
			FileInputStream fin = new FileInputStream("./deploy.properties");
			p.load(fin);
			fin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Thing getThing(){
		return World.getInstance().getThing(p.getProperty("thing"));		
	}
	
	public static String getAction(){
		String ac = p.getProperty("action");
		if(ac ==  null || "".equals(ac)){
			return "run";
		}else{
			return ac;
		}
	}
	
	public static String getUrl(){
		String url = p.getProperty("url");
		if(url == null || "".equals(url)){
			return "http://localhost:9001/";
		}else{
			return url;
		}
	}
}
