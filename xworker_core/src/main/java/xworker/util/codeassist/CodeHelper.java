package xworker.util.codeassist;

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
import xworker.swt.xworker.codeassist.objectassists.DataObjectAssistor;
import xworker.swt.xworker.codeassist.objectassists.SqlAssistor;
import xworker.swt.xworker.codeassist.objectassists.ThingAssistor;
import xworker.swt.xworker.codeassist.textassists.CachedVariableTextAssistor;
import xworker.swt.xworker.codeassist.textassists.DataBinderTextAssistor;
import xworker.swt.xworker.codeassist.textassists.DataReactorTextAssistor;
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
		textAssists.add(new SqlAssistor());
		textAssists.add(new DataBinderTextAssistor());
		textAssists.add(new DataReactorTextAssistor());
		
		//对象辅助类
		objectAssists.put("object", new ClassAssistor());
		objectAssists.put("thing", new ThingAssistor());
		objectAssists.put("actionContainer", new ActionContainerAssistor());
		objectAssists.put("action", new ActionAssistor());
		objectAssists.put("dataObject", new DataObjectAssistor());
		
		//变量提供者
		variableProviders.add(new ImportClassProvider());
		variableProviders.add(new VariableDescProvider());
		variableProviders.add(CachedVaribleProvider.instance);
		variableProviders.add(new ActionVariableProvider());
	}
	
	/**
	 * 获取文本的通用帮助内容。如可以输入的词等。
	 * 
	 * @param textAssistor 代码服务器的名称
	 * @param text
	 * @param offset
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static List<CodeAssitContent> getHelpContents(String textAssistor, String text, int offset, Thing thing, ActionContext actionContext){
		Map<String, String> context = new HashMap<String, String>();
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		for(TextAssistor ta : textAssists) {
			List<CodeAssitContent> contents = ta.getContents(textAssistor, text, offset, thing, actionContext);
			
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
		return getObjectContents(null, code, offset, statements, thing, actionContext);
	}
	
	/**
	 * 返回对象的辅助内容。sentens是调用语句列表。
	 * 
	 * @param varProvider
	 * @param statements
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static List<CodeAssitContent> getObjectContents(VariableProvider varProvider, String code, int offset, List<String> statements, Thing thing, ActionContext actionContext){
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		
		//变量定义
		VariableDesc var = null;
		if(statements.size() > 0) {
			String varName = statements.get(0);
			if(varProvider != null) {
				//先从传入的VariableProvider上获取				
				var = getVar(varProvider, varName, code, offset, statements, thing, actionContext);
			}
			if(var == null) {
				//再从注册的VariableProvider上获取
				for(VariableProvider vp : variableProviders) {
					var = getVar(vp, varName, code, offset, statements, thing, actionContext);
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
	
	private static VariableDesc getVar(VariableProvider varProvider, String varName, String code, int offset, List<String> statements, Thing thing, ActionContext actionContext) {
		if(varProvider == null || thing == null) {
			return null;
		}
		
		VariableDesc var = null;
		List<VariableDesc> vars = varProvider.getVariables(code, offset, statements, thing, actionContext);
		if(vars != null) {
			for(VariableDesc varDesc : vars) {
				if(varDesc.getName() != null && varDesc.getName().equals(varName)){
					var = varDesc;
					break;
				}
			}
			
			if(var != null) {
				return var;
			}
		}
		
		return null;
	}
	
	public static List<VariableDesc> getVariableDescs(Thing thing, ActionContext actionContext){
		List<VariableDesc> descs = new ArrayList<VariableDesc>();
		for(VariableProvider provider : variableProviders) {
			List<VariableDesc> ds = provider.getVariables(null, 0, Collections.emptyList(), thing, actionContext);
			if(ds != null) {
				descs.addAll(ds);
			}
		}
		
		//去重
		Map<String, VariableDesc> context = new HashMap<String, VariableDesc>();
		for(int i=0; i<descs.size(); i++) {
			VariableDesc desc = descs.get(i);
			VariableDesc ctx = context.get(desc.getName());
			if(ctx == null) {
				context.put(desc.getName(), desc);
			}else if(desc.equals(ctx)) {
				//重复，去掉后者
				descs.remove(i);
				i--;
			}
		}
		
		return descs;
	}
}

