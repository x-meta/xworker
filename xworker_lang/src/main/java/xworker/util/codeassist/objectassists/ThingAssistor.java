package xworker.util.codeassist.objectassists;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.util.codeassist.CodeAssitContent;
import xworker.util.codeassist.ObjectAssistor;

public class ThingAssistor implements ObjectAssistor {
	@Override
	public List<CodeAssitContent> getContents(VariableDesc var, Thing thing, ActionContext actionContext) {
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		ClassAssistor.initContents("org.xmeta.Thing", list);
		
		if(var.getThing() != null) {
			//获取属性
			for(Thing attr : var.getThing().getAllAttributesDescriptors()){
				String name = attr.getMetadata().getName();
				String label = attr.getMetadata().getLabel();
				String type = attr.getStringBlankAsNull("type");
				if(type == null){
					type = "string";
				}
				list.add(new CodeAssitContent("put(\"" + name + "\", value);", "put(\"" + name + "\", value):" + type + "(" + label + ")", "fieldImage"));
				list.add(new CodeAssitContent("get(\"" + name + "\", value);", "get(\"" + name + "\", value):" + type + "(" + label + ")", "fieldImage"));
			}
			
			//获取动作列表
			for(Thing ac : var.getThing().getActionsThings()){
				list.add(getActionAssistString(ac));
			}
		}
		
		return list;
	}

	/**
	 * 返回动作的输入辅助字符串。
	 * 
	 * @param ac
	 * @return
	 */
	public static CodeAssitContent getActionAssistString(Thing ac){
		String name = ac.getMetadata().getName();
		String label = ac.getMetadata().getLabel();
		
		String m = "doAction(\"" + name + "\", actionContext";
		
		//参数列表
		Thing ins = ac.getThing("ins@0");
		if(ins != null){
			for(Thing p : ins.getChilds()){
				String pname = p.getMetadata().getName();
				m = m + ", \"" + pname + "\", " + pname;
			}
		}
		
		m = m + ");";
		
		return new CodeAssitContent(m, m + " - (" + label + ")", "methodImage");
	}	
}
