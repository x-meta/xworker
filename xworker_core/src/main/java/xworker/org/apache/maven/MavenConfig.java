package xworker.org.apache.maven;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

public class MavenConfig {
	/**
	 * 返回通过Runtime可以执行的Mvn程序的路径。
	 * 
	 * @return
	 */
	public static String getMavenCommand() {
		Thing config = XWorkerUtils.getPreference("xworker.org.apache.maven.MavenConfig");
		if(config != null) {
			return config.getString("mavenCommand");
		}else {
			if("win32".equals(World.getInstance().getOS())) {
				return "mvn.cmd";
			}else {
				return "mvn";
			}
		}
	}
}
