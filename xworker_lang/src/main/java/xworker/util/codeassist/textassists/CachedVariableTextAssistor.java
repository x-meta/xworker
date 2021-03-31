package xworker.util.codeassist.textassists;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.util.codeassist.CodeAssitContent;
import xworker.util.codeassist.TextAssistor;
import xworker.util.codeassist.variableproviders.CachedVaribleProvider;

public class CachedVariableTextAssistor implements TextAssistor {

	@Override
	public List<CodeAssitContent> getContents(String codeType, String code, int cursorIndex, Thing thing, ActionContext actionContext) {
		List<VariableDesc> vars = CachedVaribleProvider.instance.getVariables(code, cursorIndex, null, thing, actionContext);
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		if(vars != null) {
			for(VariableDesc var : vars) {
				list.add(new CodeAssitContent(var.getName(), null));
			}
		}
		
		return list;
	}

}
