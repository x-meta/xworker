package xworker.swt.xwidgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;

public abstract class DataItem {
	protected Thing thing;
	protected ActionContext actionContext;
	protected List<DataItem> childs = new ArrayList<DataItem>();
	protected DataItem parentItem;
	protected DataItemContainer dataItemContainer;
	protected Map<Widget, Widget> bindItems = new HashMap<Widget, Widget>();
	protected Map<Widget, Control> bindControls = null;
	//如果为false，那么不加入到父节点的子节点列表中
	protected boolean visible = true;
	
	public DataItem(DataItemContainer dataItemContainer, DataItem parentItem, boolean createChilds, Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.dataItemContainer = dataItemContainer;
		this.parentItem = parentItem;
		dataItemContainer.itemsMap.put(thing.getMetadata().getName(), this);
		
		if(createChilds) {
			//创建子节点
			for(Thing child : thing.getAllChilds()) {
				if(child.isThing("xworker.swt.xwidgets.DataItems")) {
					//如果子节点也是DataItem，那么创建
					Object obj = child.doAction("create", actionContext, "dataItemContainer", dataItemContainer, "parentItem", this);
					if(obj instanceof DataItem) {
						DataItem childItem = (DataItem) obj;
						if(childItem.visible) {
							childs.add(childItem);
						}
					}
				}
			}
		}
	}
	
	public void addChild(DataItem dataItem) {
		if(dataItem != null) {
			childs.add(dataItem);
		}
	}
		
	public DataItem getParentItem() {
		return parentItem;
	}

	public DataItemContainer getDataItemContainer() {
		return dataItemContainer;
	}

	/**
	 * 获取DataItem所包含的数据。
	 * 
	 * @return
	 */
	public abstract Object getData();
	
	/**
	 * 返回图标，默认返回事物自身对应的图标。
	 * 
	 * @param control 用于绑定Image的，当control销毁时计算图标是否需要销毁
	 * @return
	 */
	public Image getIcon(Control control) {
		if("none".equals(thing.getStringBlankAsNull("icon"))) {
			return null;
		}
		
		return SwtUtils.getIcon(thing, control, actionContext);
	}
	
	/**
	 * 返回Item的标签。
	 * 
	 * @return
	 */
	public String getLabel() {
		return thing.getMetadata().getLabel();
	}
	
	/**
	 * 返回是否显示标签。
	 * 
	 * @return
	 */
	public boolean showLabel() {
		return thing.getBoolean("showLabel");
	}
	
	public String getToolTip() {
		return thing.doAction("getToolTip", actionContext);
	}
	
	public boolean isNoDropDown() {
		return thing.getBoolean("noDropDown");
	}
	
	public boolean getDefaultSelection() {
		return thing.getBoolean("defaultSelection");
	}
	
	/**
	 * 返回子节点列表。
	 * 
	 * @return
	 */
	public List<DataItem> getChilds(){
		return childs;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
	
	/**
	 * 是否是动态的，即在点击父控件时才创建子节点。
	 * 
	 * @return 默认faalse
	 */
	public boolean isDynamic() {
		return false;
	}
	
	/**
	 * 移除自己。
	 */
	public void remove() {
		if(parentItem != null) {
			parentItem.remove(this);
		}
	}
	
	public void remove(DataItem item) {
		childs.remove(item);
	}
	
	public DataItem findByThing(Thing thing) {
		if(this.thing == thing) {
			return this;
		}
		
		for(DataItem item : childs) {
			DataItem it = item.findByThing(thing);
			if(it != null) {
				return it;
			}
		}
		
		return null;
	}
	
	/**
	 * 创建条目时，返回style，默认为SWT.NONE。
	 * 
	 * @return
	 */
	public int getStyle(Widget parent) {
		return SWT.NONE;
	}
	
	public void onSelection(DataItemContainer container) {		
		thing.doAction("onSelection", actionContext, "dataItemContainer", container, "dataItem", this);
	}
	
	public void onDefaultSelection(DataItemContainer container) {		
		thing.doAction("onDefaultSelection", actionContext, "dataItemContainer", container, "dataItem", this);
	}
	
	/**
	 * 在绑定到一个Item上时触发。
	 * 
	 * @param widgt
	 */
	protected final void fireOnBind(Widget parent, Widget item) {
		bindItems.put(parent, item);
		
		onBind(parent, item);
	}

	public Widget getBindItem(Widget parent) {
		return bindItems.get(parent);
	}
	
	public void onBind(Widget parent, Widget item) {
		
	}
	
	public Control getBindControl(Widget parent) {
		if(bindControls != null) {
			return bindControls.get(parent);
		}
		
		return null;
	}
	
	protected void putBindControl(Widget parent, Control control) {
		if(bindControls == null) {
			bindControls = new HashMap<Widget, Control>();
		}
		
		bindControls.put(parent, control);
	}
}
