package xworker.game.cocos2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class Cocos2dFunction {
	public static String toJavaScript(ActionContext actionContext){
		String js = null;
		Thing self = (Thing) actionContext.get("self");
		
		boolean haveParent = JavaScriptUtils.isHaveParent(actionContext);
		String parentName = (String) actionContext.get("parentName");
		if(haveParent){
			if(parentName != null && !"this".equals(parentName)){
				js = parentName + "." + self.getString("name") + " = function(" + getParameters(self) + "){";
			}else{
				js = self.getString("name") + ":function(" + getParameters(self) + "){";
			}
		}else{
			js = "var " + self.getString("name") + " = function(" + getParameters(self) + "){";
		}
		if(!self.getBoolean("useChildsAsCode")){
			String code = JavaScriptUtils.getIdentString(self.getString("code"), "    ");
			if(code != null){
				js = js + "\r\n" + code;
			}
		}else{
			Bindings bindings = actionContext.push();
			bindings.put("haveParent", false);
			try{
				for(Thing child : self.getChilds()){
					String code = (String) child.doAction("toJavaScript", actionContext);
					code = JavaScriptUtils.getIdentString(code, "    ");
					if(code != null){
						if(code != null){
							js = js + "\r\n" + code;
						}
					}
				}
			}finally{
				actionContext.pop();
			}
		}
		if(haveParent){
			if(parentName != null && !"this".equals(parentName)){
				js = js + "\r\n};";
			}else{
				js = js + "\r\n}";
			}
		}else{
			js = js + "\r\n};";
		}
		
		return js;
	}
	
	public static String getParameters(Thing self){
		String params = self.getStringBlankAsNull("params");
		if(params != null){
			return params;
		}else{
			return "";
		}
	}
}
