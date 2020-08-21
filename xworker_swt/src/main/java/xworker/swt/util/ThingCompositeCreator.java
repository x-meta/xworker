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
	Thing replaceComposite;
	ActionContext newActionContext;
	List<String> childFilters = new ArrayList<String>();
	
	public ThingCompositeCreator(Thing self, ActionContext actionContext) {
		this.self = self;
		this.actionContext = actionContext;
		
		newActionContext = new ActionContext();
		newActionContext.put("parentContext", actionContext);
		newActionContext.put("parentActionContext", actionContext);
		newActionContext.put("parent", actionContext.get("parent"));
	}
	
	/**
	 * 设置用于创建界面的Composite模型。
	 * 
	 * @param compositeThing
	 */
	public void setCompositeThing(Thing compositeThing) {
		this.compositeThing = compositeThing;
	}
	
	/**
	 * ThingCompositeCreator使用独立的变量上下文，在这里可以定义这个变量 
	 * 
	 * @param newActionContext
	 */
	public void setNewActionContext(ActionContext newActionContext) {
		this.newActionContext = newActionContext;
	}
	
	/**
	 * 用于替换setCompositeThing(Thing Composite)方法设置的界面模型的根节点，使得用户可以自定义界面的根节点。
	 * 
	 * @param replaceComposite
	 */
	public void setReplaceCompositeThing(Thing replaceComposite) {
		this.replaceComposite = replaceComposite;
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
			Thing thing = compositeThing;
			if(replaceComposite != null) {
				//替换第一个子节点,
				thing = replaceComposite.detach();
				thing.put("name", compositeThing.getMetadata().getName());
				thing.getMetadata().setPath(compositeThing.getMetadata().getPath());
				
				//复制子节点，但不改变子节点的所属
				for(Thing child : compositeThing.getChilds()) {
					thing.addChild(child, false);
				}
			}
			
			obj = thing.doAction("create", newActionContext);
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
