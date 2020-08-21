package xworker.doc.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 一个简单的用于生成各种文档的框架。
 * 
 * @author zhangyuxiang
 *
 */
public class DocumentNode {
	Thing thing;
	ActionContext actionContext;
	List<DocumentNode> childs = new ArrayList<DocumentNode>();
	Map<String, Object> data = new HashMap<String, Object>();
	
	public DocumentNode(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void add(DocumentNode node) {
		childs.add(node);
	}
	
	public List<DocumentNode> getChilds(){
		return childs;
	}
	
	public void set(String key, Object value) {
		data.put(key, value);
	}
	
	public Object get(String key) {
		return data.get(key);
	}
	
	public String toString(byte level) {
		return thing.doAction("toString", actionContext, "document_level", level);		
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Map<String, Object> getData() {
		return data;
	}
	
	
}
