package xworker.swt.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;

/**
 * 组合控件创建的辅助类。组合控件的原型用模型已经创建好，然后包装成模型，通过本帮助类来创建。
 * 
 * @author zyx
 *
 */
public class ThingCompositeCreator {
	Thing self;
	ActionContext actionContext;
	Thing compositeThing;
	ActionContext newActionContext;
	List<String> childFilters = new ArrayList<String>();
	
	public ThingCompositeCreator(Thing self, ActionContext actionContext) {
		this.self = self;
		this.actionContext = actionContext;
		
		newActionContext = new ActionContext();
		newActionContext.put("parentContext", actionContext);
		newActionContext.put("parent", actionContext.get("parent"));
	}
	
	public void setCompositeThing(Thing compositeThing) {
		this.compositeThing = compositeThing;
	}
	
	
	public ActionContext getNewActionContext() {
		return newActionContext;
	}
	
	/**
	 * 添加子节点过滤，创建子节点时，和过滤事物名相同的子节点不执行create方法。
	 * 
	 * @param thingName
	 */
	public void addChildFilter(String thingName) {
		childFilters.add(thingName);
	}
	
	private boolean isFiltered(Thing child) {
		String thingName = child.getThingName();
		
		for(String filter : childFilters) {
			if(thingName.equals(filter)) {
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T create() {
		Object obj = null;
		Designer.pushCreator(self);
		try {
			obj = compositeThing.doAction("create", newActionContext);
			if(obj instanceof Control) {
				Designer.attachCreator((Control) obj, self.getMetadata().getPath(), actionContext);
			}
		}finally {
			Designer.popCreator();
		}
		
		beforeCreateChilds(obj, actionContext, newActionContext);
		
		//创建子节点
		actionContext.peek().put("parent", obj);
		for(Thing child : self.getChilds()){
			if(isFiltered(child)) {
				continue;
			}
						
			child.doAction("create", actionContext);
		}
		
		return (T) obj;
	}
	
	/**
	 * 创建子节点前执行的方法，在创建的控件之后
	 */
	public void beforeCreateChilds(Object parent, ActionContext actionContext, ActionContext newActionContext) {		
	}
}
