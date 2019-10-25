package xworker.swt.xworker.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.objectassists.ActionAssistor;
import xworker.swt.xworker.codeassist.objectassists.ActionContainerAssistor;
import xworker.swt.xworker.codeassist.objectassists.ClassAssistor;
import xworker.swt.xworker.codeassist.objectassists.ThingAssistor;
import xworker.swt.xworker.codeassist.textassists.CachedVariableTextAssistor;
import xworker.swt.xworker.codeassist.textassists.VariableDescTextAssistor;
import xworker.swt.xworker.codeassist.textassists.WordSpliter;
import xworker.swt.xworker.codeassist.variableproviders.ActionVariableProvider;
import xworker.swt.xworker.codeassist.variableproviders.CachedVaribleProvider;
import xworker.swt.xworker.codeassist.variableproviders.ImportClassProvider;
import xworker.swt.xworker.codeassist.variableproviders.VariableDescProvider;

public class CodeHelper {
	/** 文本辅助类 */
	static List<TextAssistor> textAssists = new ArrayList<TextAssistor>();
	
	/** 对象辅助类 */
	static Map<String, ObjectAssistor> objectAssists = new HashMap<String, ObjectAssistor>();
	
	/** 变量提供者 */
	static List<VariableProvider> variableProviders = new ArrayList<VariableProvider>();
	
	static {
		//分词
		textAssists.add(new VariableDescTextAssistor());
		textAssists.add(new CachedVariableTextAssistor());
		textAssists.add(new WordSpliter());
		
		//对象辅助类
		objectAssists.put("object", new ClassAssistor());
		objectAssists.put("thing", new ThingAssistor());
		objectAssists.put("actionContainer", new ActionContainerAssistor());
		objectAssists.put("action", new ActionAssistor());
		
		//变量提供者
		variableProviders.add(new ImportClassProvider());
		variableProviders.add(new VariableDescProvider());
		variableProviders.add(CachedVaribleProvider.instance);
		variableProviders.add(new ActionVariableProvider());
	}
	
	/**
	 * 获取文本的通用帮助内容。如可以输入的词等。
	 * 
	 * @param text
	 * @param offset
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static List<CodeAssitContent> getHelpContents(String text, int offset, Thing thing, ActionContext actionContext){
		Map<String, String> context = new HashMap<String, String>();
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		for(TextAssistor ta : textAssists) {
			List<CodeAssitContent> contents = ta.getContents(text, offset, thing, actionContext);
			
			if(contents != null) {
				for(CodeAssitContent content : contents) {
					if(context.get(content.value) == null) {
						context.put(content.value, content.value);
						list.add(content);
					}
				}
			}
		}
		
		Collections.sort(list);
		return list;
	}
	
	/**
	 * 返回对象的辅助内容。sentens是调用语句列表。
	 * 
	 * @param statements
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static List<CodeAssitContent> getObjectContents(String code, int offset, List<String> statements, Thing thing, ActionContext actionContext){
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		
		//变量定义
		VariableDesc var = null;
		if(statements.size() > 0) {
			String varName = statements.get(0);
			
			for(VariableProvider vp : variableProviders) {
				List<VariableDesc> vars = vp.getVariables(code, offset, statements, thing, actionContext);
				if(vars != null) {
					for(VariableDesc varDesc : vars) {
						if(varDesc.getName() != null && varDesc.getName().equals(varName)){
							var = varDesc;
							break;
						}
					}
					
					if(var != null) {
						break;
					}
				}
			}
		}
		
		//根据首变量定义，获取调用顺序最后一个变量定义
		if(var != null) {
			VariableParser variableParser = new VariableParser(statements, var);
			var = variableParser.parse();
		}
		
		if(var != null) {
			ObjectAssistor oa = objectAssists.get(var.getType());
			if(oa != null) {
				list = oa.getContents(var, thing, actionContext);
			}
		}
		
		Collections.sort(list);
		return list;
	}
}

