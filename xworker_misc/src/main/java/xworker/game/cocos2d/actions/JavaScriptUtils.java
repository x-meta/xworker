package xworker.game.cocos2d.actions;

import org.xmeta.ActionContext;

public class JavaScriptUtils {
	/**
	 * 有父节点表示代码生成在一个原型下，否则是生成在过程代码里。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static boolean isHaveParent(ActionContext actionContext){
		Boolean haveParent = (Boolean) actionContext.get("haveParent");
	    if(haveParent == null){
	    	haveParent = false;
	    }
	    return haveParent;
	}
	
	public static String getIdentString(String js, String ident){
		if(js == null){
			return null;
		}
		
		String str = null;
		for(String s : js.split("[\n]")){
			if(s.endsWith("\r")){
				s = s.substring(0, s.length() - 1);
			}
			if(str == null){
				str = ident + s;
			}else{
				str = str + "\r\n" + ident + s;
			}
		}
		return str;
	}
}
