package xworker.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class GlobalConfig {
	private static Logger logger = LoggerFactory.getLogger(GlobalConfig.class);
	
	private static int httpPort = 9002;
	private static String webUrl = null;

	public static void setHttpPort(int port) {
		httpPort = port;
	}
	
	public static void setWebUrl(String webUrl) {
		GlobalConfig.webUrl = webUrl;
	}
	
	/**
	 * 如果webUrl还没有设置，则设置。
	 * 
	 * @param webUrl
	 * @param optional
	 */
	public static void setWebUrl(String webUrl, boolean optional) {
		if(GlobalConfig.webUrl == null) {
			GlobalConfig.webUrl = webUrl;
		}
	}
	
	/**
	 * 如果port可以绑定则返回，否则+1再判断，最多循环300次。
	 * 
	 * @param port
	 * @return 返回可用的port，否则返回-1
	 */
	public static int getAvaiablePort(int port) {
		for(int i=0; i<300; i++){
			if(isPortAvailable(port)){   
				return port;
			}else {
				port++;
			}
		}
		
		return -1;
	}
	/** 
	 * 获取XWorker内置的网页路径。
	 * 
	 * @return
	 */
    public static synchronized String getWebUrl(){
    	if(webUrl != null){
			return webUrl;
		}
    	
    	//logger.info(XWorkerUtils.isThingExplorer()  + ":" + XWorkerUtils.getIde());
    	if(!XWorkerUtils.isThingExplorer() && World.getInstance().getData("jettyServer") == null && XWorkerUtils.hasXWorker()){
    		boolean ssl = false;
    		Thing globalConfig = World.getInstance().getThing("_local.xworker.config.GlobalConfig");    	
    		if(globalConfig != null) {
    			ssl = globalConfig.getBoolean("webSSL");
    		}
    		//不是在XWorker的事物管理器运行环境下，此时试图启动Jetty服务器
    		ActionContext ac = new ActionContext();       		
    		//端口可能会被占用，所以尝试启动多个，直到成功
    		for(int i=0; i<300; i++){		
    			logger.info("Check port availabe, port=" + httpPort);
    			if(isPortAvailable(httpPort)){    		
	    			ac.put("port", httpPort);
	    			Thing jetty = World.getInstance().getThing("xworker.ide.worldexplorer.swt.SimpleExplorerRunner/@startJettry2");
	    			if(jetty != null) {
	    				jetty.getAction().run(ac);
	    			}
	    			
	    			if(ssl) {
	    				webUrl = "https://localhost:" + httpPort + "/";
	    			} else {
	    				webUrl = "http://localhost:" + httpPort + "/";
	    			}
	    			
	    			//logger.info("Jetty started at " + httpPort + ".");
	    			break;
	    		}else{
	    			//System.out.println("启动应用Jetty: " + httpPort + "，失败");
	    			httpPort++;
	    		}
    		}
    		
    		return webUrl;
    	}else{
    		Thing globalConfig = World.getInstance().getThing("_local.xworker.config.GlobalConfig");    	
    		if(globalConfig != null) {
    			webUrl =  globalConfig.getString("webUrl");
    		}
    		return webUrl;
    	}    	
    }
    
    private static void bindPort(String host, int port) throws Exception { 
        Socket s = new Socket(); 
        s.bind(new InetSocketAddress(host, port)); 
        s.close(); 
    } 
    public static boolean isPortAvailable(int port) { 
        try { 
            bindPort("0.0.0.0", port); 
            bindPort(InetAddress.getLocalHost().getHostAddress(), port); 
            return true; 
        } catch (Exception e) { 
            return false; 
        } 
    }
    
    /**
     * 获取默认的图片地址。
     * 
     * @return
     */
    public static String getImagePath(){
    	Thing globalConfig = World.getInstance().getThing("_local.xworker.config.GlobalConfig");
		return globalConfig.getString("imagePath");
    }
    
    /**
     * 获取显示事物Description的URL地址。
     * 
     * @param thing 事物
     * @return 事物的文档URL
     */
    public static String getThingDescUrl(Thing thing){
    	if(thing != null){
    		String desc = thing.getMetadata().getDescription();
    		if(desc == null && thing.getBoolean("inheritDescription")){
    			for(Thing ext : thing.getExtends()){
    				thing = ext;
    				break;
    			}
    		}
    	}else{
    		return getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&nohead=true&thing=";
    	}
    	
    	try {
			return getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&nohead=true&thing=" + URLEncoder.encode(thing.getMetadata().getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return getWebUrl() + "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&nohead=true&thing=" + thing.getMetadata().getPath();
		} 
    }
    
    /**
     * 获取显示事物Description的URL地址。
     * 
     * @param thingPath 事物路径
     * @return 事物的文档URL
     */
    public static String getThingDescUrl(String thingPath) {
    	Thing thing = World.getInstance().getThing(thingPath);
    	return getThingDescUrl(thing); 
    }
}
