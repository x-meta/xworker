package xworker.util.codeassist.objectassists;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.util.codeassist.CodeAssitContent;
import xworker.util.codeassist.ObjectAssistor;

public class ActionAssistor implements ObjectAssistor {

	@Override
	public List<CodeAssitContent> getContents(VariableDesc var, Thing thing, ActionContext actionContext) {
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		ClassAssistor.initContents("org.xmeta.Action", list);
		
		if(var.getThing() != null) {
			Thing ac = var.getThing();
			String label = ac.getMetadata().getLabel();
			
			String m = "run(actionContext";
			
			//参数列表
			Thing ins = ac.getThing("ins@0");
			if(ins != null){
				for(Thing p : ins.getChilds()){
					String pname = p.getMetadata().getName();
					m = m + ", \"" + pname + "\", " + pname;
				}
			}
			
			m = m + ");";
			
			CodeAssitContent acvar = new CodeAssitContent(m, m + " - (" + label + ")", "methodImage");
			list.add(acvar);
		}
		
		return list;
	}

}
