package xworker.httpclient.actions;

public class HttpTestInfo {
	/** 请求的URL地址 */
	public String url;
	/** 是否成功 */
	public boolean success;
	/** HTTP状态码 */
	public int statusCode;
	/** 内容类型 */
	public String contentType;
	/** 内容长度 */
	public long contentLength;
	/** 总响应时间 */
	public long totalTime;
	/** 平均每秒下载速度 */
	public int speed;
	/** 速度的可读的信息标签  */
	public String speedLabel;
	/** 异常，如果存在 */
	public Exception exception;
	/** HTTP头 */
	public String heads;
	/** HTTP 内容*/
	public String content;
	
	@Override
	public String toString() {
		return "HttpTestInfo [url=" + url + ", success=" + success
				+ ", statusCode=" + statusCode + ", contentType=" + contentType
				+ ", contentLength=" + contentLength + ", totalTime="
				+ totalTime + ", speed=" + speed + ", speedLabel=" + speedLabel
				+ ", exception=" + exception + 
				"]\n    heads=" + heads + "\n    content=" + content;
	}
}
