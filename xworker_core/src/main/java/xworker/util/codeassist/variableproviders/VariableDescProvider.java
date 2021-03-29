package xworker.util.codeassist.variableproviders;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.codeassist.VariableProvider;

/**
 * 事物模型自定义的变量提供者。
 * 
 * @author zyx
 *
 */
public class VariableDescProvider implements VariableProvider{
	private static final String CACHE = "__VariableDescProvider__";
	
	@Override
	public List<VariableDesc> getVariables(String code, int offset,  List<String> statements, Thing thing, ActionContext actionContext) {	
		String key = VariableDescProvider.CACHE + thing.getMetadata().getPath();
		
		Thing root = thing.getRoot();
		List<VariableDesc> vars = root.getTempData(key);
		if(vars == null) {
			vars = VariableDesc.getVariableDescs(thing, actionContext);
			if(vars != null) {
				root.setTempData(key, vars);
			}
		}
		
		return vars;
	}

}
