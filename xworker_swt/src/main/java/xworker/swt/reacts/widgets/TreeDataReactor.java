package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.WidgetDataReactor;

public class TreeDataReactor extends WidgetDataReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(TableDataReactor.class);
		
	Tree tree;
	
	public TreeDataReactor(Tree tree, Thing self, ActionContext actionContext) {
		super(tree, self, actionContext);
		
		this.tree = tree;
		String type = self.getString("type");
		if("selection".equals(type)) {
			this.tree.addListener(SWT.Selection, this);
		}else if("defaultSelection".equals(type)) {
			this.tree.addListener(SWT.DefaultSelection, this);
		}
	}

	@Override
	public void handleEvent(Event event) {
		boolean check = self.getBoolean("check") & ((tree.getStyle() & SWT.CHECK) == SWT.CHECK);
		List<Object> datas = new ArrayList<Object>();
		if(!check) {
			for(TreeItem item : tree.getSelection()) {
				datas.add(item.getData());
			}
		}else {
			for(TreeItem item : tree.getItems()) {
				getCheckItem(item, datas);
			}
		}
		
		if(datas.size() > 0) {
			this.fireSelected(datas);
		}else {
			this.fireUnselected();
		}
	}
	
	private void getCheckItem(TreeItem item, List<Object> datas) {
		if(item.getChecked()) {
			datas.add(item.getData());
		}
		
		for(TreeItem childItem : item.getItems()) {
			getCheckItem(childItem, datas);
		}
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas) {	
	}

	@Override
	protected void widgetDoOnUnselected() {
	}
	
	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas) {
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas) {
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas) {
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas) {
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Tree widget = self.doAction("getBindTo", actionContext);
		if(widget != null) {
			TreeDataReactor reactor = new TreeDataReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("Tree is null, can not create TreeDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
