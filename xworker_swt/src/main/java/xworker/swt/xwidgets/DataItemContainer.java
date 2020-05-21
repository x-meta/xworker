package xworker.swt.xwidgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.xwidgets.DataItemContainerDataReactor;
import xworker.swt.util.SwtUtils;
import xworker.swt.widgets.CoolBarCreator;

public class DataItemContainer{
	private static final String TAG = DataItemContainer.class.getName();
	
	List<DataItem> dataItems = new ArrayList<DataItem>();
	Thing thing = null;
	ActionContext actionContext = null;
	List<DataItemListener> listeners = new ArrayList<DataItemListener>();
	String eventType;
	Map<String, DataItem> itemsMap = new HashMap<String, DataItem>();
	DataReactor dataReactor;
	Widget widget;
	
	public DataItemContainer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.eventType = thing.getString("event");
		
		//创建子节点
		for(Thing child : thing.getAllChilds()) {
			if(child.isThing("xworker.swt.xwidgets.DataItems")) {
				//如果子节点也是DataItem，那么创建
				Object obj = child.doAction("create", actionContext, "dataItemContainer", this, "parentItem", null);
				if(obj instanceof DataItem) {
					if(((DataItem) obj).visible) {
						dataItems.add((DataItem) obj);
					}
				}
			}
		}
		
		List<String> bindToList = thing.doAction("getBindTo", actionContext);
		if(bindToList != null) {
			for(String bindTo : bindToList) {
				bindTo = bindTo.trim();
				String name = bindTo;
				int event = SWT.Selection;
				
				int index = bindTo.indexOf("?");
				if(index != -1) {
					name = bindTo.substring(0, index).trim();
					String e = bindTo.substring(index + 1, bindTo.length()).trim();
					if("defaultSelection".equals(e)) {
						event = SWT.DefaultSelection;
					}
				}
				
				Object widget = actionContext.get(name);
				if(widget instanceof Widget) {
					bind((Widget) widget, event);
				}
			}
		}
	}
		
	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
		
	public List<DataItem> getDataItems() {
		return dataItems;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public List<DataItemListener> getListeners() {
		return listeners;
	}

	public void addListener(DataItemListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
		
	public void removeListener(DataItemListener listener) {
		listeners.remove(listener);
	}
	
	public DataItem getItem(String name) {
		return itemsMap.get(name);
	}
	
	public DataItem findByThing(Thing thing) {
		for(DataItem item : dataItems) {
			DataItem it = item.findByThing(thing);
			if(it != null) {
				return it;
			}
		}
		
		return null;
	}
	
	public Widget getBindItem(String name, Widget parent) {
		DataItem item = getItem(name);
		if(item != null) {
			return item.getBindItem(parent);
		}
		
		return null;
	}
	
	public Control getBindControl(String name, Widget parent) {
		DataItem item = getItem(name);
		if(item != null) {
			return item.getBindControl(parent);
		}
		
		return null;
	}
	
	public void addItem(DataItem dataItem) {
		if(dataItem != null) {
			dataItems.add(dataItem);
		}
	}
	
	public void bind(Widget widget) {
		int event = "defaultSelection".equals(eventType) ? SWT.DefaultSelection : SWT.Selection;
		bind(widget, event);
	}
	
	/**
	 * 如果已经绑定了控件上，那么会把先前绑定的条目移除，然后重新创建。
	 */
	public void refresh() {
		if(widget == null) {
			return;
		}
		
		int event = "defaultSelection".equals(eventType) ? SWT.DefaultSelection : SWT.Selection;
		
		//添加事件监听
		if(widget instanceof Decorations) {
			Decorations decorations = (Decorations) widget;
			remove(decorations.getMenuBar());
			createItem(dataItems, (Decorations) widget, event);
		}if(widget instanceof Tree) {
			for(TreeItem item : ((Tree) widget).getItems()) {
				remove(item);
			}
			
			for(DataItem dataItem : dataItems) {
				if((dataItem.getStyle(widget) & SWT.SEPARATOR) == SWT.SEPARATOR) {
					//tree不支持分割符
					continue;
				}
				
				createItem(dataItem, (Tree) widget);
			}
		}else if(widget instanceof TreeItem) {
			TreeItem treeItem = (TreeItem) widget;			
			for(TreeItem item : ((TreeItem) widget).getItems()) {
				remove(item);
			}
			
			for(DataItem dataItem : dataItems) {
				if((dataItem.getStyle(widget) & SWT.SEPARATOR) == SWT.SEPARATOR) {
					//tree不支持分割符
					continue;
				}
				
				createItem(dataItem, treeItem);
			}
		}else if(widget instanceof Menu) {
			for(MenuItem item : ((Menu) widget).getItems()) {
				remove(item);
			}
			
			for(DataItem dataItem : dataItems) {
				createItem(dataItem, (Menu) widget, event, widget);
			}
		}else if(widget instanceof ToolBar) {
			for(ToolItem item : ((ToolBar) widget).getItems()) {
				remove(item);
			}
			
			for(DataItem dataItem : dataItems) {
				createItem(dataItem, (ToolBar) widget, event);
			}
			
			//ToolBarCreator.initToolBar((ToolBar) widget); 
		}else if(widget instanceof ToolItem){
			Widget menu = (Widget) widget.getData("menu");
			remove(menu);
			createItem(dataItems, (ToolItem) widget, event);
		}else if(widget instanceof CoolBar) {
			for(CoolItem item : ((CoolBar) widget).getItems()) {
				remove(item);
			}
			createItem(dataItems, (CoolBar) widget, event);
		}else if(widget instanceof ExpandBar) {		
			for(ExpandItem item : ((ExpandBar) widget).getItems()) {
				remove(item);
			}
			
			for(DataItem dataItem : dataItems) {
				createItem(dataItem, (ExpandBar) widget, event);
			}
		}else if(widget instanceof Control) {
			//默认是Control的右键菜单
			Menu menu = ((Control) widget).getMenu();
			remove(menu);
			createItem(dataItems, (Control) widget, event);
		}
	}
	
	private void remove(Widget widget) {
		if(widget != null && widget.getData(TAG) == this) {
			Control control = null;
			if(widget instanceof CoolItem) {
				control = ((CoolItem) widget).getControl();
			}else if(widget instanceof ExpandItem) {
				control = ((ExpandItem) widget).getControl();				
			}else if(widget instanceof ToolItem) {
				control = ((ToolItem) widget).getControl();
			}else if(widget instanceof CTabItem) {
				control = ((CTabItem) widget).getControl();
			}
			
			if(control != null) {
				control.dispose();
			}
			widget.dispose();
		}
	}
	
	/**
	 * 绑定到控件上。
	 * 
	 * @param widget
	 * @param event
	 */
	public void bind(Widget widget, int event) {
		//添加事件监听
		if(widget instanceof Decorations) {
			createItem(dataItems, (Decorations) widget, event);
		}if(widget instanceof Tree) {
			addListener(widget, event);
			
			for(DataItem dataItem : dataItems) {
				if((dataItem.getStyle(widget) & SWT.SEPARATOR) == SWT.SEPARATOR) {
					//tree不支持分割符
					continue;
				}
				
				createItem(dataItem, (Tree) widget);
			}
		}else if(widget instanceof TreeItem) {
			TreeItem treeItem = (TreeItem) widget;
			addListener(treeItem.getParent(), event);
			
			for(DataItem dataItem : dataItems) {
				if((dataItem.getStyle(widget) & SWT.SEPARATOR) == SWT.SEPARATOR) {
					//tree不支持分割符
					continue;
				}
				
				createItem(dataItem, treeItem);
			}
		}else if(widget instanceof Menu) {
			for(DataItem dataItem : dataItems) {
				createItem(dataItem, (Menu) widget, event, widget);
			}
		}else if(widget instanceof ToolBar) {
			for(DataItem dataItem : dataItems) {
				createItem(dataItem, (ToolBar) widget, event);
			}
			
			//ToolBarCreator.initToolBar((ToolBar) widget); 
		}else if(widget instanceof ToolItem){
			addListener((ToolItem) widget, event);
			createItem(dataItems, (ToolItem) widget, event);
		}else if(widget instanceof CoolBar) {
			createItem(dataItems, (CoolBar) widget, event);
		}else if(widget instanceof ExpandBar) {			
			for(DataItem dataItem : dataItems) {
				createItem(dataItem, (ExpandBar) widget, event);
			}
		}else if(widget instanceof Control) {
			//默认是Control的右键菜单
			createItem(dataItems, (Control) widget, event);
		}else {
			Executor.warn(TAG, "Can not bind items, widget not supported, widget=" + widget);
		}
	}
	
	private void createItem(List<DataItem> dataItems, Control control, int event) {
		Menu menu = new Menu(control);
		menu.setData(TAG, this);
		control.setMenu(menu);
		for(DataItem dataItem : dataItems) {
			createItem(dataItem, menu, event, control);
		}
	}
	
	private void createItem(DataItem dataItem, ExpandBar expandBar, int event) {
		ExpandItem item = new ExpandItem(expandBar, SWT.NONE);
		item.setData(TAG, this);
		item.setText(dataItem.getLabel());
		item.setData(dataItem);
		Image image = dataItem.getIcon(expandBar);
		if(image != null) {
			item.setImage(image);
		}
		
		//创建Tree
		Tree tree = new Tree(expandBar, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		for(DataItem childItem : dataItem.getChilds()) {
			if((childItem.getStyle(tree) & SWT.SEPARATOR) == SWT.SEPARATOR) {
				//tree不支持分割符
				continue;
			}
			
			createItem(childItem, tree);
		}
		item.setControl(tree);
		addListener(tree, event);
				
	}
	
	private void createItem(List<DataItem> dataItems, Decorations decorations, int event) {
		Menu menu = new Menu(decorations, SWT.BAR);
		menu.setData(TAG, this);
		decorations.setMenuBar(menu);
		for(DataItem dataItem : dataItems) {
			createItem(dataItem, menu, event, decorations);
		}
	}
	
	private void createItem(List<DataItem> dataItems, CoolBar coolBar, int event) {
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setData(TAG, this);
		ToolBar toolBar = new ToolBar(coolBar, SWT.HORIZONTAL | SWT.FLAT);
		item.setControl(toolBar);
		
		for(DataItem dataItem : dataItems) {
			createItem(dataItem, toolBar, event);
		}
		
		CoolBarCreator.initCoolBar(coolBar);
		
	}
	
	private void createItem(DataItem dataItem, ToolBar toolBar, int event) {
		int style = dataItem.getStyle(toolBar);
		if(dataItem.getChilds().size() > 0 && dataItem.isNoDropDown() == false) {
			//DROP_DOWN的优先级最高，可以覆盖默认的样式
			style = SWT.DROP_DOWN;
		}
		ToolItem  item = new ToolItem(toolBar, style);
		item.setData(TAG, this);
		addListener(item, event);
		item.setData(dataItem);
		if(dataItem.showLabel()) {
			item.setText(dataItem.getLabel());	
		}
		String toolTip = dataItem.getToolTip();
		if(toolTip != null) {
			item.setToolTipText(toolTip);
		}
		Image image = dataItem.getIcon(toolBar);
		if(image != null) {
			item.setImage(image);
		}
		
		//创建子节点
		if(dataItem.getChilds().size() > 0) {
			Menu childMenu = new Menu(toolBar);
			item.setData("menu", childMenu);
			for(DataItem childItem : dataItem.getChilds()) {
				createItem(childItem, childMenu, event, toolBar);
			}
		}
		
		dataItem.onBind(toolBar, item);
	}
	
	private void createItem(List<DataItem> dataItems, ToolItem toolItem, int event) {		
		Thing thing = new Thing("xworker.swt.xwidgets.DataItems/@Folder");
		thing.put("noDropDown", true);
		DataItem root = new DataItem(this, null, false, thing, new ActionContext()) {
			@Override
			public Object getData() {
				return null;
			}
		};
		root.getChilds().addAll(dataItems);
		toolItem.setData(root);
		
		Menu childMenu = new Menu(toolItem.getParent());
		childMenu.setData(TAG, this);
		toolItem.setData("menu", childMenu);
		for(DataItem childItem : dataItems) {
			createItem(childItem, childMenu, event, toolItem.getParent());
		}
	}
	
	private void createItem(DataItem dataItem, Menu menu, int event, Widget parent) {
		int style = dataItem.getStyle(menu);
		if(dataItem.getChilds().size() > 0) {
			//CASCADE的优先级最高，可以覆盖默认的样式
			style = SWT.CASCADE;
		}
		
		MenuItem item = new MenuItem(menu, style);
		item.setData(TAG, this);
		addListener(item, event);
		item.setData(dataItem);
		item.setText(dataItem.getLabel());
		String toolTip = dataItem.getToolTip();
		if(toolTip != null) {
			item.setToolTipText(toolTip);
		}
		Control control = menu.getParent();
		if(control == null) {
			control = menu.getShell();
		}
		Image image = dataItem.getIcon(control);
		if(image != null) {
			item.setImage(image);
		}
		
		//创建子节点
		if(dataItem.getChilds().size() > 0) {			
			Menu childMenu = new Menu(item);
			item.setMenu(childMenu);
			for(DataItem childItem : dataItem.getChilds()) {
				createItem(childItem, childMenu, event, parent);
			}
		}
		
		dataItem.onBind(parent, item);
	}
	
	private void addListener(Widget widget, int event) {
		for(Listener listener : widget.getListeners(event)) {
			if(listener == this) {
				//已经添加过了，避免重复添加
				return;
			}
		}
		
		if(event == SWT.DefaultSelection) {
			addListener(widget, SWT.Selection, false);
		}else {
			addListener(widget, SWT.Selection, true);
		}
		
		addListener(widget, SWT.DefaultSelection, true);
	}
	
	private void addListener(Widget widget, int event, boolean invokeListener) {
		for(Listener listener : widget.getListeners(event)) {
			if(listener == this) {
				//已经添加过了，避免重复添加
				return;
			}
		}
		
		widget.addListener(event, new Selection(this, invokeListener));
	}
	
	private void createItem(DataItem dataItem, Tree tree) {
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setData(TAG, this);
		item.setText(dataItem.getLabel());
		Image image = dataItem.getIcon(tree);
		if(image != null) {
			item.setImage(image);
		}
		item.setData(dataItem);
		
		if(dataItem.isDynamic() == false) {
			for(DataItem childItem : dataItem.getChilds()) {
				if((childItem.getStyle(tree) & SWT.SEPARATOR) == SWT.SEPARATOR) {
					//tree不支持分割符
					continue;
				}
				
				createItem(childItem, item);
			}
		}
		
		dataItem.onBind(tree, item);
	}
	
	private void createItem(DataItem dataItem, TreeItem treeItem) {
		TreeItem item = new TreeItem(treeItem, SWT.NONE);
		item.setData(TAG, this);
		item.setText(dataItem.getLabel());
		Image image = dataItem.getIcon(treeItem.getParent());
		if(image != null) {
			item.setImage(image);
		}
		item.setData(dataItem);
		
		if(dataItem.isDynamic() == false) {
			for(DataItem childItem : dataItem.getChilds()) {
				if((childItem.getStyle(treeItem.getParent()) & SWT.SEPARATOR) == SWT.SEPARATOR) {
					//tree不支持分割符
					continue;
				}
				
				createItem(childItem, item);
			}
		}
		
		dataItem.onBind(item.getParent(), item);
	}

	public DataReactor getDataReactor() {
		if(dataReactor == null) {
			Thing self = new Thing("xworker.swt.reactors.xwidgets.DataItemContainerDataReactor");
			
			dataReactor = new DataItemContainerDataReactor(this, self, actionContext);
		}
		return dataReactor;
	}

	public static DataItemContainer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataItemContainer container = new DataItemContainer(self, actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), container);
		return container;
	}
	
	/**
	 * 触发所有子节点的选择事件。
	 */
	public void fireSelectionAll() {
		for(DataItem dataItem : dataItems) {
			fireSelectionAll(dataItem);
		}
	}
	
	/**
	 * 触发子节点的onSelection事件。
	 * 
	 * @param dataItem
	 */
	private void fireSelectionAll(DataItem dataItem) {
		//触发DataItem的onSelection事件
		dataItem.onSelection(this);
		
		//触发Container的onSeleton事件
		this.thing.doAction("onSelection", actionContext,  "dataItemContainer", this, "dataItem", this);
		
		//触发Listeners							
		for(DataItemListener listener : listeners) {
			listener.onSelection(this, dataItem);
		}
		
		for(DataItem childItem : dataItem.getChilds()) {
			fireSelectionAll(childItem);
		}
	}
	
	public static class Selection implements Listener{
		DataItemContainer container;
		boolean invokeListener = true;
		
		public Selection(DataItemContainer container, boolean invokeListener) {
			this.container = container;
			this.invokeListener = invokeListener;
		}
		
		@Override
		public void handleEvent(Event event) {
			Widget item = event.item;
			if(item == null) {
				item = event.widget;
			}
			if(item != null) {
				Object data = item.getData();
				if(data instanceof DataItem) {
					DataItem dataItem = (DataItem) data;
					if(dataItem.isDynamic() && item instanceof TreeItem){
						//动态加载树的子节点
						TreeItem treeItem = (TreeItem) item;					
						if(treeItem.getItemCount() == 0) {
							for(DataItem childDataItem : dataItem.getChilds()) {
								container.createItem(childDataItem, treeItem);
							}
							treeItem.setExpanded(true);
						}
					}else if(item instanceof ToolItem) {
						if(dataItem.getChilds().size() > 0) {
							if(dataItem.isNoDropDown() == true ||  (event.detail & SWT.ARROW) == SWT.ARROW) {
								Menu menu = (Menu) item.getData("menu");
								if(menu != null) {
									SwtUtils.showMenuByWidget(item, menu);
									return;
								}						
							}
						}
					}
					
					//触发事件
					try {
						if(SWT.DefaultSelection == event.type && dataItem.getDefaultSelection()) {
							//触发DataItem的onSelection事件
							dataItem.onDefaultSelection(container);
							
							//触发Container的onSeleton事件
							container.thing.doAction("onDefaultSelection", container.actionContext,  "dataItemContainer", this, "dataItem", this);
							
							//触发Listeners							
							for(DataItemListener listener : container.listeners) {
								listener.onDefaultSelection(container, dataItem);
							}
						}else if(dataItem.getDefaultSelection() == false){
							//触发DataItem的onSelection事件
							dataItem.onSelection(container);
							
							//触发Container的onSeleton事件
							container.thing.doAction("onSelection", container.actionContext,  "dataItemContainer", this, "dataItem", this);
							
							//触发Listeners							
							for(DataItemListener listener : container.listeners) {
								listener.onSelection(container, dataItem);
							}
						}
					}catch(Exception e) {
						Executor.warn(TAG, "Invoke onselection exception, container=" + container.thing.getMetadata().getPath() + 
								",dataItem=" + dataItem.getThing().getMetadata().getPath(), e);
					}
				}
			}
		}
	}
}
