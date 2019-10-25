package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;

public class ActionStackTraceText {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		Thing thing = null;
		if(SwtUtils.isRWT()) {
			thing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ActionStackTraceTextShell/@text1");
		}else {
			thing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ActionStackTraceTextShell/@text");
		}
		Object obj = thing.doAction("create", ac);
		
		actionContext.peek().put("parent", obj);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), ac.get("actions"));
		return obj;
	}
	
	public static void setStackTrace(ActionContext actionContext){
		Text text = (Text) actionContext.get("text");
		//text.setText("");
		SwtTextUtils.setText(text, "");
		
		String stackTrace = (String) actionContext.get("stackTrace");
		if(stackTrace == null){
			return;
		}
		
		for(String line : stackTrace.split("[\n]")){
			String l = line.trim();
			if(l.startsWith("caller:")){
				String ls[] = l.split("[,]");
				String ls1[] = ls[0].split("[:]");
				if(ls1.length == 3){
					SwtTextUtils.append(text, "\t");
					SwtTextUtils.append(text, ls1[0] + ":");
					SwtTextUtils.append(text, ls1[1] + ":");
					Thing thing = World.getInstance().getThing(ls1[2].trim());
					if(thing != null){
						int start = SwtTextUtils.getText(text).length();			
						String link = thing.getMetadata().getPath();
						SwtTextUtils.append(text, link);						
					}else{
						SwtTextUtils.append(text, ls1[2]);
					}
					
					for(int i=1;i<ls.length; i++){
						SwtTextUtils.append(text, "," + ls[i]);
					}
					SwtTextUtils.append(text, "\n");
				}else{
					SwtTextUtils.append(text, line + "\n");
				}
			}else{
				SwtTextUtils.append(text, line + "\n");
			}
		}
	}
	

}
