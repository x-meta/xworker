package xworker.swt.xwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.assist.JavaCacheItem;
import xworker.java.assist.JavaClassCache;
import xworker.swt.design.Designer;

public class ClassSelector {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//创建选择器
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("thing", self);
		ac.put("parentContext", actionContext);
		Thing swtThing = World.getInstance().getThing("xworker.swt.xwidgets.ClassSelector/@calssSelector");
		Designer.pushCreator(self);
		Composite composite = null;
		try {
			composite = swtThing.doAction("create", ac);
		}finally {
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", composite);		
		for(Thing child :self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), ac);		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;
	}
	
	public static Object query(ActionContext actionContext){
		//选择类，最多一次返回500个，多了对选择也无意义
		List<SelectContent> contents = new ArrayList<SelectContent>();
			
		String text = actionContext.getObject("text");
		List<JavaCacheItem> cls = JavaClassCache.indexOf(text);
		int count = 0;
		for(JavaCacheItem c : cls){
		    if(c.type == 1){
		        contents.add(new SelectContent(c.path));
		        count++;
		    }
		    
		    if(count >= 500){
		        break;
		    }
		}

		return contents;
	}
	
	public static void selected(ActionContext actionContext){
		Thing thing = actionContext.getObject("thing");
		Object value = actionContext.getObject("value");
		Object content = actionContext.get("content");
		ActionContext ac = actionContext.getObject("parentContext");
		
		actionContext.g().put("content", content);
		actionContext.g().put("value", value);
		thing.doAction("selected", ac, "value", value, "content", content);
		
	}
}
