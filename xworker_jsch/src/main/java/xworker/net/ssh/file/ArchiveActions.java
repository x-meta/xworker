package xworker.net.ssh.file;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ArchiveActions {
	
	public static String zip(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String param = "";
		if(self.getBoolean("delete")){
			param = param + "d";
		}
		if(self.getBoolean("modify")){
			param = param + "m";
		}
		if(self.getBoolean("recursion")){
			param = param + "r";
		}
		if(self.getBoolean("S")){
			param = param + "S";
		}
		if(self.getBoolean("u")){
			param = param + "u";
		}
		
		return "zip -" + param + " + " + self.doAction("getZipFile", actionContext) 
				+ " " + self.doAction("getFileSet", actionContext); 
	}
}
