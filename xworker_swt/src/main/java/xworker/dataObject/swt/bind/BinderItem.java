package xworker.dataObject.swt.bind;

import java.io.IOException;

import org.eclipse.swt.events.DisposeListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.util.UtilData;

/**
 * 绑定器条目，是DataObjectBinder的子节点，实现具体的绑定功能。
 * 
 * @author zyx
 *
 */
public abstract class BinderItem implements DisposeListener{
	protected Thing thing;
	protected ActionContext actionContext;
	protected Object oldValue = null;
	boolean firstUpdate = true;	
	
	public BinderItem(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	
	}
		
	public abstract boolean isDisposed();
	
	/**
	 * 从数据对象获取值的方法。
	 * @return
	 */
	public Object getValue(DataObjectBinder binder, DataObject dataObject) {
		return thing.doAction("getValue", actionContext, "binder", binder, "dataObject", dataObject, "item", this);		
	}
	
	/** 
	 * 从被绑定的控件上读取数据。
	 * 
	 * @return
	 */
	public abstract Object getValue();
	
	/**
	 * 数据对象发生变化时会触发该事件。默认什么也不做，为了避免数据对象快速变动而频繁
	 * 更新UI，在doUpdate中处理界面更新。
	 * 
	 * @param binder
	 * @param dataObject
	 */
	public void changed(DataObjectBinder binder, DataObject dataObject) {		
	}
	
	public void update(DataObjectBinder binder, DataObject dataObject) {
		Object value = getValue(binder, dataObject);
		//如果新的值和旧的值一样，说明没有变化，则不更新，第一次是必须要更新的
		if(value != oldValue || firstUpdate) {
			oldValue = value;
			firstUpdate = false;
			doUpdate(binder, dataObject, value);
		}
	}
	
	
	public abstract void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value);
	
	public static Object getValueAction(ActionContext actionContext) throws OgnlException, IOException {
		//获取传入的参数
		BinderItem item = actionContext.getObject("item");
		DataObject dataObject = actionContext.getObject("dataObject");
		Thing thing = item.thing;
		
		//获取值路径
		String valuePath = thing.getString("valuePath");
		//System.out.println(valuePath);
		if(valuePath == null || "".equals(valuePath)) {
			String actionName = thing.getStringBlankAsNull("actionName");
			if(actionName != null) {
				return dataObject.doAction(actionName, actionContext);
			}else {
				valuePath = thing.getMetadata().getName();
			}
		}
		
		String valuePaths[] = valuePath.split("[,]");
		for(String valuePath1 : valuePaths) {
			int index = valuePath1.indexOf(":");
			if(index != -1) {
				//如果字符串含有:号，表示它是一个表达式，不是从当前数据对象获取的
				return UtilData.getData(valuePath1, actionContext);
			}else {
				
				if(dataObject == null) {
					continue;
				}
				
				String[] paths = valuePath1.split("[.]");
				Object root = dataObject;
				for(int i=0; i<paths.length; i++) {
					String path = paths[i];
					root = OgnlUtil.getValue(path, root);
					if(root == null && i < paths.length - 1) {
						//按照路径的顺序，中间就已经有null，后续不能再通过Ognl获取值了
						continue;
					}
				}
				
				if(root != null) {
					return root;
				}
			}
		}
		
		return null;
	}
}
