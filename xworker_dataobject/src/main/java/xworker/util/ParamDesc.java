package xworker.util;

import org.xmeta.Thing;

/**
 * 参数描述。
 * 
 * @author zhangyuxiang
 *
 */
public class ParamDesc {
	Thing thing;
	public ParamDesc(Thing thing) {
		this.thing = thing;		 
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public String getName() {
		return thing.getMetadata().getName();
	}
	
	public String getType() {
		String type = thing.getStringBlankAsNull("type");
		if(type != null) {
			return type;
		}else {
			return "java.lang.Object";
		}
	}
	
	public String getDescription() {
		return thing.getMetadata().getDescription();
	}
}
