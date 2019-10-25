package xworker.swt.xworker.codeassist.textassists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.TextAssistor;
import xworker.util.StringUtils;

public class WordSpliter implements TextAssistor{

	@Override
	public List<CodeAssitContent> getContents(String code, int cursorIndex, Thing thing, ActionContext actionContext) {
		Map<String, String> context = new HashMap<String, String>();
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		if(code != null) {
			String[] strs = StringUtils.split(code, " .,={}()[]\n:;'\"\\<>&$#@!|/+-");
			if(strs != null){
				for(String s : strs){
					if(s != null){
						s = s.trim();
						if(!s.equals("") && context.get(s) == null){
							list.add(new CodeAssitContent(s, null));
							context.put(s, s);
						}
					}
				}
			}
		}
		
		return list;
	}

}
