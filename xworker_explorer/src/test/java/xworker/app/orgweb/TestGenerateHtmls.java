package xworker.app.orgweb;

import org.xmeta.World;

public class TestGenerateHtmls{
	public static void main(String args[]){
		try{
			//初始化引擎
			World world = World.getInstance();			
			world.init("./xworker/");
			
			StaticHtmlGenerator g = new StaticHtmlGenerator("http://www.xworker.org", "f:/temp/orgweb", 1, "index.html");
			g.generateHtmls();
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
}