package xworker.game.cocos2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JavaScript {
	public static String toJavaScript(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String js = null;
		if(!self.getBoolean("useChildsAsCode")){
			String code = self.getString("code");
			if(code != null){
				js = code;
			}
		}else{			
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
		}

		if("".equals(js)){
			return null;
		}else{
			return js;
		}
	}
}
