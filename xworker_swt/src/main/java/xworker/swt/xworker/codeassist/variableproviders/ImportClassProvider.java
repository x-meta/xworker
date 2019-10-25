package xworker.swt.xworker.codeassist.variableproviders;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.codeassist.VariableProvider;

public class ImportClassProvider implements VariableProvider{

	@Override
	public List<VariableDesc> getVariables(String code, int offset, List<String> statements, Thing thing,
			ActionContext actionContext) {
		if(code == null || "".equals(code)) {
			return null;
		}
		
		List<VariableDesc> vars = new ArrayList<VariableDesc>();
		for(String line : code.split("[\n]")) {
			line = line.trim();
			if(line.startsWith("import ")) {
				int lastIndex = line.lastIndexOf(";");
				String className = line.substring(6, lastIndex).trim();
				int index = className.lastIndexOf(".");			
				String name = index == -1 ? className : className.substring(index + 1, className.length());
				
				vars.add(new VariableDesc(name, VariableDesc.OBJECT, className, null));
			}
		}
		
		return vars;
	}

}
