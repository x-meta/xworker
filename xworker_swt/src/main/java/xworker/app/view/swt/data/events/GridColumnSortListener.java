package xworker.app.view.swt.data.events;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.PageInfo;

public class GridColumnSortListener {
	public static void run(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		
		Grid grid = (Grid) ((GridColumn) event.widget).getParent();
		GridColumn sortColumn = (GridColumn) event.widget;
		GridColumn currentColumn = (GridColumn) event.widget;
		int dir = sortColumn.getSort();
		if (sortColumn == currentColumn) {
			dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;	
		} else {
			//table.setSortColumn(currentColumn);	
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

		sortColumn.setSort(dir);
		//因为Store目前只能有一个排序字段，所以取消其它GridColumn的排序显示
		for(GridColumn column : grid.getColumns()) {
			if(column != currentColumn) {
				column.setSort(SWT.NONE);
			}
		}
		
		//table.setSortDirection(dir);

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
