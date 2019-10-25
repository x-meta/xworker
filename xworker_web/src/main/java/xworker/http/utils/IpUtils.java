package xworker.http.utils;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {
	//private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
	public static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");
	public static final String[] ipHeaders = new String[] {
			"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", 	"WL-Proxy-Client-IP", 
			"HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
	};

	public static String longToIpV4(long longIp) {
		int octet3 = (int) ((longIp >> 24) % 256);
		int octet2 = (int) ((longIp >> 16) % 256);
		int octet1 = (int) ((longIp >> 8) % 256);
		int octet0 = (int) ((longIp) % 256);
		return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
	}

	public static long ipV4ToLong(String ip) {

		String[] octets = ip.split("\\.");

		return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
				+ (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
	}

	public static boolean isIPv4Private(String ip) {

		long longIp = ipV4ToLong(ip);

		return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
				|| (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
				|| longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
	}

	public static boolean isIPv4Valid(String ip) {

		return pattern.matcher(ip).matches();
	}

	private static String getHeaderIp(HttpServletRequest request, String header) {
		String ip = request.getHeader(header);
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			return null;
		}else {
			return ip;
		}
	}
	
	public final static String getIpAddress(HttpServletRequest request) {
		return getIpAddress(request, null);
	}
	
	/**
	 * 
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 *      
	 * @param request
	 * @return
	 */

	public final static String getIpAddress(HttpServletRequest request, String headers[]) {
		//logger.info("IpUtils.getIpAddress is start");
		if (request == null) {
			//logger.info("IpUtils.getIpAddress request is null");
			return null;

		}

		//String headerType = "X-Forwarded-For";
		String ip = null;
		if(headers != null) {
			for(String header : headers) {
				//headerType = header;
				ip = getHeaderIp(request, header);
				if(ip != null) {
					break;
				}
			}
		}
		
		if(ip == null) {
			for(String header : ipHeaders) {
				//headerType = header;
				ip = getHeaderIp(request, header);
				if(ip != null) {
					break;
				}
			}
		}
		
		if(ip == null) {
			//headerType = "getRemoteAddr";
			ip = request.getRemoteAddr();
		}
		
		if (ip != null && ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}

		//logger.info("IpUtils.getIpAddress {} ip = {}", headerType, ip);
		if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
			return ip;
		}

		return null;
	}
}
