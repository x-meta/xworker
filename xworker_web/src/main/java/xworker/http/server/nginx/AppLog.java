package xworker.http.server.nginx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppLog {
	/** URL地址，分析出第一级目录的 */
	public String url;
	/** 访问的不同IP总数 */
	public int ipTotal;
	/** 访问总数 */
	public int count;
	/** 访问最早日期 */
	public Date minDate;
	/** 访问最后日期 */
	public Date maxDate;
	/** 每小时访问量 */
	public int countPerHour;
	/** 每个IP平均访问次数 */
	public int countPerIp;
	/** 每秒访问量 */
	public float countPerSecond;
	protected Map<String, String> ipMap = new HashMap<String, String>();
	/** 
	 * 最后时调用，计算访问量等。
	 */
	public void doSum(){
		if(minDate.equals(maxDate)){
			countPerIp = 1;
			countPerSecond = 1;
			countPerHour = 1;
			ipTotal = 1;
		}else{
			countPerSecond  = 1000f * count / (maxDate.getTime() - minDate.getTime());
			countPerHour = (int) (3600000f * count / (maxDate.getTime() - minDate.getTime()));
			ipTotal = ipMap.size();
			countPerIp = count / ipTotal;
		}
	}
	
	@Override
	public String toString() {
		return "AppLog [url=" + url + ", ipTotal=" + ipTotal + ", count="
				+ count + ", minDate=" + minDate + ", maxDate=" + maxDate
				+ ", countPerHour=" + countPerHour + ", countPerIp="
				+ countPerIp + ", countPerSecond=" + countPerSecond
				+  "]";
	}
}
