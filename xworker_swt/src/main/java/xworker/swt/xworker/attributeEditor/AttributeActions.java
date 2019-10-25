package xworker.swt.xworker.attributeEditor;

import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AttributeActions {
	public static void setThing(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = self.doAction("getThing", actionContext);
		
		if(obj == null || !(obj instanceof Thing)) {
			return;
		}
		
		Thing thing = (Thing) obj;
		String returnType = (String) actionContext.get("returnType");
		String result = null;
		if("attribute".equals(returnType)){			
			if(thing != null){
				String value = thing.getString((String) actionContext.get("attributeName"));
				if(value == null){
					value = "";
				}
				
				
				result = value;
			}
		}else{
			if("name".equals(returnType)){
				result = thing.getMetadata().getName();
			}else if("label".equals(returnType)){
				result = thing.getMetadata().getLabel();
			}else if("path".equals(returnType)){
				result = thing.getMetadata().getPath();
			}			
		}
		
						
		if(result != null){
			//属性模板
			String attributePattern = (String) actionContext.get("attributePattern");
			if(attributePattern != null && !"".equals(attributePattern)){			
				result = attributePattern.replace("{0}", result);
			}	
			
			//前缀
			String prefix = actionContext.getObject("prefix");
			if(prefix != null){
				result = prefix + result;
			}
			String surfix = actionContext.getObject("surfix");
			if(surfix != null) {
				result = result + surfix;
			}
			
			//是否是追加
			if("true".equals(actionContext.get("append"))){
				Text text = (Text) actionContext.get("text");
				if(text != null){
					String value = text.getText();
					if(value.indexOf(result) == -1){
						String seperator = (String) actionContext.get("seperator");
						if(!"".equals(value)){
							result = value + seperator + result;
						}
					}
					
				}
			}
			
			
			actionContext.getScope(0).put("result", result);
		}
	}
}
