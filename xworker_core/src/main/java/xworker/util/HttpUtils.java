package xworker.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
	 * 通过Servlet返回真实的IP地址。
	 * 
	 * 代理服务器转发后会改变地址，并且一般转发后在Http的头中包含了原有的地址，可以根据Header获取真实的地址。
	 * 
	 * @param request
	 * @return
	 */
	public static String getRealRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		if (ip.indexOf(",") > 0) {
			// 如果是中间经历多级反向代理
			logger.info("ips=" + ip);
			return ip.substring(0, ip.indexOf(","));
		} else {
			return ip;
		}
	}
	
	/**
	 * 判断Hettp请求是否是本机发起的。
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isLocalHost(HttpServletRequest request) {
		return isLocalHost(getRealRemoteAddr(request));
	}
	
	/**
	 * 返回是否是本机的IP地址。
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isLocalHost(String ip) {
		if("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			return true;
		}
		
		//可能存在其它情况，因此保留从NetworkInterface校验的代码
		try{
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()){
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()){
					InetAddress lip = (InetAddress) addresses.nextElement();
					if(lip.isLoopbackAddress() && lip.getHostAddress().equals(ip)) {
						return true;
					}
				}
			}
		}catch(Exception e){			
		}

		return false;
	}
}