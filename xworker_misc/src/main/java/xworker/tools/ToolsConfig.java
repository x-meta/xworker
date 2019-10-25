package xworker.tools;

import org.xmeta.Thing;
import org.xmeta.util.UtilThing;

public class ToolsConfig {
	/**
	 * 返回事物_local.xworker.config.tools.ToolsConfig的指定属性。
	 * 
	 * @param name
	 * @return
	 */
	public static String getAttribute(String name) {
		try {
			Thing config = UtilThing.getThingIfNotExistsCreate("_local.xworker.config.tools.ToolsConfig", 
					"_local", "xworker.tools.ToolsConfig");
			if(config != null) {
				return config.getStringBlankAsNull(name);
			}
		}catch(Exception e) {				
		}		
		
		return null;
	}
}
