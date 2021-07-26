package xworker.net.ssh;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SshCommand {
	public static String run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String script = null;
		for(Thing commands : self.getChilds("Commands")){
			for(Thing command : commands.getChilds()){
				String com = (String) command.getAction().run(actionContext);
				if(com != null){
					if(script != null){
						script = com;
					}else{
						script = script + "\n" + com;
					}
				}
			}
		}
		
		return script;
	}
}
