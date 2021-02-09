package xworker.swt.xworker.codeassist.textassists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.TextAssistor;
import xworker.swt.xworker.codeassist.variableproviders.ActionVariableProvider;

public class VariableDescTextAssistor implements TextAssistor{
	private static final String CACHE = "__VariableDescTextAssistor__";
	
	@Override
	public List<CodeAssitContent> getContents(String codeType, String code, int cursorIndex, Thing thing, ActionContext actionContext) {
		String key = VariableDescTextAssistor.CACHE + thing.getMetadata().getPath();
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		Map<String, String> context = new HashMap<String, String>();
		
		//先初始化动作自身定义的
		List<VariableDesc> vars =  ActionVariableProvider.getActionVariables(code, cursorIndex, null, thing, actionContext);
		initVars(vars, list, context);
		
		//然后初始化事物本身定义的
		Thing root = thing.getRoot();
		vars = root.getTempData(key);
		if(vars == null) {
			vars = VariableDesc.getVariableDescs(thing, actionContext);
			
			if(vars != null) {
				root.setTempData(key, vars);
				initVars(vars, list, context);
			}
		}else {
			initVars(vars, list, context);
		}
		
		
		return list;
	}

	public void initVars(List<VariableDesc> vars, List<CodeAssitContent> list, Map<String, String> context) {
		if(vars == null) {
			return;
		}
		
		for(VariableDesc var : vars) {
			String name = var.getName();
			if(context.get(name) == null) {
				list.add(new CodeAssitContent(name, null));
				context.put(name, name);
			}
		}
	}
}
