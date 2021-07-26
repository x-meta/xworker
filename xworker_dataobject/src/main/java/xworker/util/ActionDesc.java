package xworker.util;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;

/**
 * 动作描述。
 * 
 * @author zhangyuxiang
 *
 */
public class ActionDesc {
	Thing thing;
	
	public ActionDesc(Thing thing) {
		this.thing = thing;
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public String getName() {
		return thing.getMetadata().getName();
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();

		//动作本身的文档
		String desc = thing.getMetadata().getDescription();
		if(desc != null){
			sb.append(desc);
		}

		//查看是否是属性对应的参数动作
		String attributeName = thing.getStringBlankAsNull("attributeName");
		if(attributeName != null){
			Thing parent = thing.getParent();
			if(parent != null){
				parent = parent.getParent();
			}
			if(parent != null){
				for(Thing attr : parent.getChilds("attribute")){
					if(attributeName.equals(attr.getMetadata().getName())){
						desc = attr.getMetadata().getDescription();
						if(desc != null){
							sb.append(desc);
						}

						//动作描述者的文档
						desc = thing.getDescriptor().getMetadata().getDescription();
						if(desc != null){
							sb.append(desc);
						}

						break;
					}
				}
			}
		}
		return sb.toString();
	}
	
	public String getReturnType() {
		Thing outs = thing.getThing("outs@0");
		if(outs != null) {
			Thing param = outs.getThing("param@0");
			if(param != null) {
				String type = param.getStringBlankAsNull("type");
				if(type != null) {
					return type;
				}
			}
		}
		
		return "java.lang.Object";
	}
	
	public List<ParamDesc> getParams(){
		List<ParamDesc> params = new ArrayList<ParamDesc>();
		
		Thing ins = thing.getThing("ins@0");
		if(ins != null) {
			for(Thing param : ins.getChilds("param")) {
				ParamDesc p = new ParamDesc(param);
				params.add(p);
			}
		}
		
		return params;
	}
}
