package xworker.util.codeassist.objectassists;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.ObjectAssistor;

public class ActionContainerAssistor implements ObjectAssistor{

	@Override
	public List<CodeAssitContent> getContents(VariableDesc var, Thing thing, ActionContext actionContext) {
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		ClassAssistor.initContents("org.xmeta.util.ActionContainer", list);
		
		if(var.getThing() != null) {
			ActionContainer ac = new ActionContainer(var.getThing(), actionContext);
			if(ac != null) {
				//获取动作列表		
				for(Thing acthing : ac.getActionThings()){
					list.add(ThingAssistor.getActionAssistString(acthing));
				}
			}
		}
		return list;
	}
}
