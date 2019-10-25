package xworker.lang.command;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CommandDomain {
	public static List<Thing> getCommands(ActionContext actionContext){
		Thing domain = actionContext.getObject("self");
		
		List<Thing> list = new ArrayList<Thing>();
		for(Thing child : domain.getAllChilds("Command")){			
			list.add(child);
		}
		
		for(Thing child : domain.getAllChilds("ReferenceCommand")){			
			list.add(child);
		}
		
		return list;
	}
}
