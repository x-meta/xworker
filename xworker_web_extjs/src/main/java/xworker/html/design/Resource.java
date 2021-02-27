package xworker.html.design;

/**
 * 需要动态加载的资源，stylesheet或javascript。
 * 
 * @author Administrator
 *
 */
public class Resource {
	public final String stylesheet = "stylesheet";
	public final String javascript = "javascript";
	
	String type;
	String path;
	
	public Resource(String type, String path){
		this.type = type;
		this.path = path;
	}
}
