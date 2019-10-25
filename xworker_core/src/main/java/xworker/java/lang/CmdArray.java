package xworker.java.lang;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class CmdArray {
	public static String[] getCmdArray(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		List<String> list = getCmdList(self, actionContext);
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}
	
	public static String getCmdString(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		List<String> list = getCmdList(self, actionContext);
		StringBuffer sb = new StringBuffer();
		for(String str : list) {
			sb.append(str);
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	public static String getCmdArrayString(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		List<String> list = getCmdList(self, actionContext);
		StringBuffer sb = new StringBuffer();
		for(String str : list) {
			sb.append(str);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static String getAttributeValue(ActionContext actionContext) {
		String value = actionContext.getObject("value");
		return value;
	}
	
	public static List<String> getCmdList(Thing self, ActionContext actionContext){
		List<String> list = new ArrayList<String>();
		List<Thing> attributes = new ArrayList<Thing>();
		Thing cmdArray = World.getInstance().getThing("xworker.java.lang.Cmdarray");
		for(Thing d : self.getAllDescriptors()) {
			if(d.isThing("xworker.java.lang.Cmdarray") || d.getExtends().contains(cmdArray)) {
			//if("xworker.java.lang.Cmdarray".equals(d.getDescriptor().getMetadata().getPath())) {
				attributes.addAll(d.getChilds("attribute"));
			}
		}
		for(Thing attribute : attributes) {
			String value = self.getStringBlankAsNull(attribute.getMetadata().getName());
			value = self.doAction("getAttributeValue", actionContext, "value", value, "attribute", attribute);
			if(value == null) {
				continue;
			}
			
			//System.out.println(attribute);
			String prefix = attribute.getStringBlankAsNull("reserve");
			String inputType = attribute.getStringBlankAsNull("inputtype");
			if(prefix != null) {
				if("truefalse".equals(inputType)) {
					if("true".equals(value.toLowerCase())) {
						list.add(prefix);						
					}
					continue;
				}else {
					list.add(prefix);
				}
			}
			
			list.add(value);
		}
		
		return list;
	}
}
