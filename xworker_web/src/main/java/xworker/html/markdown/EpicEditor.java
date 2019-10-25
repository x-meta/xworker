package xworker.html.markdown;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.utils.JsonFormator;

public class EpicEditor {
	public static String toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		if(actionContext.get("heads") != null){
			importExtjs(actionContext);
		}
		
		String opt = JsonFormator.formatThingAttributeAsString(self, "", true);
		
		String event = "";
		for(Thing child : self.getChilds("Event")){
			String name = child.getStringBlankAsNull("event");
			String code = child.getStringBlankAsNull("code");
			if(name != null && code != null){
				event = event + "\n" + self.getMetadata().getName() + ".on(\"" + name + "\", " + code + ");";
			}
		}
		return "<div id=\"epiceditor\" style=\"height:500px\"></div><script language=\"javascript\">var " + self.getMetadata().getName() + " = new EpicEditor(" + opt + ").load();" + event + "</script>";
	}
	
	@SuppressWarnings("unchecked")
	public static void importExtjs(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");	
    	
        if(actionContext.get("heads") != null){
            boolean haveEditor = false;
            List<Map<String, String>> heads = (List<Map<String, String>>) actionContext.get("heads");
            for(Map<String, String> head : heads){
                if("EpicEditor".equals(head.get("name"))){
                    haveEditor = true;
                    break;
                }        
            }
            
            if(!haveEditor){
                String html = null;
                String epicPath = self.getString("epicPath");
                if(self.getBoolean("useMinJs")){
	                html = "        <script type=\"text/javascript\" src=\"" + epicPath + "js/epiceditor.min.js\"></script>\n";
                }else{
                	html = "        <script type=\"text/javascript\" src=\"" + epicPath + "js/epiceditor.js\"></script>\n";
                }
          
                Map<String, String> head = new HashMap<String, String>(); 
                head.put("name", "EpicEditor");
                head.put("value", html);
                heads.add(head);
            }
        }
	}
}
