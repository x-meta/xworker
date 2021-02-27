package xworker.lang.actions;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilAction;

/**
 * 作用是把一组动作事物分类，有名字的放到数组中，其它放到list中。
 * 
 * @author zyx
 *
 */
public class ActionsFilter {
	public String[] names;
	public Action[] actionArray;
	public List<Thing> actionList = new ArrayList<Thing>();
	
	public ActionsFilter(List<Thing> actionThings, String ... names) {
		this.names = names;
		this.actionArray = new Action[names.length];
		
		for(Thing actionThing : actionThings) {
			String actionName = actionThing.getMetadata().getName();
			boolean isNamed = false;
			for(int i=0; i<names.length; i++) {
				if(actionName.equals(names[i])) {
					this.actionArray[i] = actionThing.getAction();
					isNamed = true;
					break;
				}
			}
			
			if(isNamed == false) {
				actionList.add(actionThing);
			}
		}		
	}
	
	/**
	 * 执行指定名字的动作。
	 * 
	 * @param name
	 * @param actionContext
	 * @param isSubAction
	 * @return
	 */
	public Object run(String name, ActionContext actionContext, boolean isSubAction) {
		int index = getNameIndex(name);
		if(index == -1) {
			return null;
		}
		
		Action action = actionArray[index];
		if(action == null) {
			return null;
		}else {
			return action.run(actionContext, null, isSubAction);
		}
	}
	
	/**
	 * 返回一个指定名字的动作是否存在。
	 * 
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		return getNameIndex(name) != -1;
	}
	
	/**
	 * 执行没有通过名字分类的其它动作。
	 * 
	 * @param actionContext
	 * @param isSubAction
	 * @return
	 */
	public Object runList(ActionContext actionContext, boolean isSubAction) {
		return UtilAction.runChildActions(actionList, actionContext, isSubAction);
	}
	
	/**
	 * 返回名字的索引。
	 * 
	 * @param name
	 * @return
	 */
	private int getNameIndex(String name) {
		for(int i=0; i<names.length; i++) {
			String n = names[i];
			if(n != null && n.equals(name)) {
				if(actionArray.length > i && actionArray[i] != null) {
					return i;
				} else {
					return -1;
				}
			}
		}
		
		return -1;
	}
}
