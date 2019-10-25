package xworker.app.view.swt.data.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.PageInfo;

public class TableColumnSortListener {
	public static void run(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		
		Table table = (Table) ((TableColumn) event.widget).getParent();
		TableColumn sortColumn = table.getSortColumn();
		TableColumn currentColumn = (TableColumn) event.widget;
		int dir = table.getSortDirection();
		if (sortColumn == currentColumn) {
			dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;	
		} else {
			table.setSortColumn(currentColumn);	
		}

		String name = (String) currentColumn.getData("name");
		Thing store = (Thing) currentColumn.getData("store");
		PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
		pageInfo.setSort(name);
		if(dir == SWT.DOWN){
		    pageInfo.setDir("DESC");
		}else{
		    pageInfo.setDir("ASC");
		}
		store.doAction("reload", actionContext);

		table.setSortDirection(dir);

		/*
		if(currentColumn == lastVisitItem){
		    if(dir == SWT.DOWN){
		        alist = alist.sort{l,r -> return r.lastVisit <=> l.lastVisit};
		    }else{
		        alist = alist.sort{l,r -> return l.lastVisit <=> r.lastVisit};
		    }
		}
		if(currentColumn == visitCountItem){
		    if(dir == SWT.DOWN){
		        alist = alist.sort{l,r -> return r.visitCount <=> l.visitCount};
		    }else{
		        alist = alist.sort{l,r -> return l.visitCount <=> r.visitCount};
		    }
		}
		*/
	}
}
