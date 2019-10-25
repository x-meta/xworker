package xworker.game.cocos2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class Cocos2dScript {
	public static String toJavaScript(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Bindings bindings = actionContext.push();
		bindings.put("haveParent", false);
		bindings.put("parentName", null);
		String js = null;
		try{
			for(Thing child : self.getChilds()){
				String code = (String) child.doAction("toJavaScript", actionContext);
				if(code != null){
					if(code != null){
						if(js == null){
							js = code;
						}else{
							js = js + "\r\n" + code;
						}
					}
				}
			}
		}finally{
			actionContext.pop();
		}

		if("".equals(js)){
			return null;
		}else{
			return js;
		}
	}
}
