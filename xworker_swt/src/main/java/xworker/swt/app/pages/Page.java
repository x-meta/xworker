package xworker.swt.app.pages;

import java.util.Collections;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

/**
 * 页面。
 * 
 * @author zhangyuxiang
 *
 */
public class Page {
	String id;
	Thing thing;
	ActionContext actionContext;
	ActionContext swtContext;
	Control control;
	Map<String, Object> variables = null;
	Page parent;
	Page next;
	PageManager pageManager;
	
	protected Page(PageManager pageManager, String id, Thing thing, ActionContext actionContext) {
		this.pageManager = pageManager;
		this.id = id;
		this.thing = thing;
		this.actionContext = actionContext;
		variables = thing.doAction("getVariables", actionContext);
		variables = Collections.emptyMap();
	}
	
	public String getId() {
		return id;
	}
	
	public String getLabel() {
		return thing.getMetadata().getLabel();
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public boolean isDirty() {
		if(swtContext == null) {
			return false;
		}
		
		return UtilData.isTrue(thing.doAction("isDirty", swtContext));
	}
	
	public void save() {
		if(swtContext != null) {
			thing.doAction("save", swtContext);
		}
	}
	
	public Control getControl(Composite parent) {
		if(control == null || control.isDisposed()) {
			swtContext = new ActionContext();
			swtContext.putAll(variables);
			swtContext.put("parentContext", actionContext);
			swtContext.peek().put("parent", parent);
			
			control = thing.doAction("create", swtContext);
		}
		
		return control;
	}
	
	public void dispose() {
		if(control != null && control.isDisposed() == false) {
			control.dispose();
		}
		
		if(next != null) {
			next.dispose();
		}
	}

	public Page getParent() {
		return parent;
	}

	public Page getNext() {
		return next;
	}

	public void setNext(Page next) {
		if(this.next != null) {
			this.next.dispose();
		}
		
		this.next = next;
		this.next.parent = this;
		
		pageManager.fireChanged();
	}
	
}
