package xworker.util.monitor.http;

/**
 * HTTP监控结果。
 * 
 * @author Administrator
 *
 */
public class HttpResult {
	/** 服务器返回的状态值 */
	public int statusCode;
	
	/** 花费的时间 */
	public long time;
	
	/** 返回的内容 */
	public String content;
}
