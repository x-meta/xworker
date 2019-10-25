package xworker.swt.nebula;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.app.view.swt.data.DataStoreDisposeListener;
import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;

public class GridActions {
	public static Object create(ActionContext actionContext) {
	Thing self = actionContext.getObject("self");
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		Composite parent = actionContext.getObject("parent");
		
		style = style | SwtUtils.getSWT(self.getString("style"));
		if(self.getBoolean("NO_FOCUS")) {
			style = style | SWT.NO_FOCUS;
		}
		if(self.getBoolean("CHECK")) {
			style = style | SWT.CHECK;
		}
		if(self.getBoolean("VIRTUAL")) {
			style = style | SWT.VIRTUAL;
		}		
		Grid grid = new Grid(parent, style);
		Designer.attach(grid, self.getMetadata().getPath(), actionContext);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", grid);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		grid.setAutoHeight(self.getBoolean("autoHeight​"));
		if(SwtUtils.isRWT() == false) {
			grid.setAutoWidth(self.getBoolean("autoWidth"));
			grid.setCellSelectionEnabled(self.getBoolean("cellSelectionEnabled​"));
			if(self.get("columnScrolling") != null && !"".equals(self.get("columnScrolling"))) {
				grid.setColumnScrolling(self.getBoolean("columnScrolling"));
			}
			
			if(self.getStringBlankAsNull("itemHeaderWidth​") != null) {
				grid.setItemHeaderWidth(self.getInt("itemHeaderWidth"));
			}
			
			Color lineColor = (Color) StyleSetStyleCreator.createResource(self.getString("lineColor"), 
	                "xworker.swt.graphics.Color", "rgb", actionContext);
			if(lineColor != null) {
				grid.setLineColor(lineColor);
			}
			
			if(self.getStringBlankAsNull("rowHeaderVisible​​​") != null) {
				grid.setRowHeaderVisible(self.getBoolean("rowHeaderVisible​"));
			}
			
			if(self.getStringBlankAsNull("rowsResizeable​​​") != null) {
				grid.setRowsResizeable(self.getBoolean("rowsResizeable​"));
			}
			
			if(self.getStringBlankAsNull("treeLinesVisible​​​") != null) {
				grid.setTreeLinesVisible(self.getBoolean("treeLinesVisible​"));
			}
			
			if(self.getStringBlankAsNull("visibleLinesBasedColumnPack​​​") != null) {
				grid.setVisibleLinesColumnPack(self.getBoolean("visibleLinesBasedColumnPack​"));
			}
			
			if(self.getStringBlankAsNull("wordWrapHeader​​​​") != null) {
				grid.setWordWrapHeader(self.getBoolean("wordWrapHeader​​"));
			}
		}
		
		grid.setFooterVisible(self.getBoolean("footerVisible​"));
		grid.setHeaderVisible(self.getBoolean("headerVisible"));
		
		if(self.getStringBlankAsNull("itemHeight​​") != null) {
			grid.setItemHeight(self.getInt("itemHeight​"));
		}
		
		grid.setLinesVisible(self.getBoolean("linesVisible"));
		
		
		//创建子节点
		actionContext.peek().put("parent", grid);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), grid);
		
		return grid;
	}
	
	public static Object createGridColumnGroup(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int style = SWT.NONE;
		if(self.getBoolean("TOGGLE")) {
			style = style | SWT.TOGGLE;
		}
		Grid parent = actionContext.getObject("parent");
		GridColumnGroup column = new GridColumnGroup(parent, style);
		column.setExpanded(self.getBoolean("expanded"));
		Font font = (Font) StyleSetStyleCreator.createResource(self.getString("headerFont​"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            column.setHeaderFont(font);
        }
        column.setHeaderWordWrap(self.getBoolean("headerWordWrap​"));
        
        actionContext.peek().put("parent", column);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
        actionContext.g().put(self.getMetadata().getName(), column);
        return column;
	}
	
	public static Object createGridColumn(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		int style = SWT.NONE;
		style = style | SwtUtils.getSWT(self.getString("style"));
		if(self.getBoolean("CHECK")) {
			style = style | SWT.CHECK;
		}
		Object parent = actionContext.getObject("parent");
		GridColumn column = null;
		if(parent instanceof Grid) {
			column = new GridColumn((Grid) parent, style);
		}else {
			column = new GridColumn((GridColumnGroup) parent, style);
		}
		column.setCellSelectionEnabled(self.getBoolean("cellSelectionEnabled"));
		column.setDetail(self.getBoolean("detail"));
		Font footerFont = (Font) StyleSetStyleCreator.createResource(self.getString("footerFont​​"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(footerFont != null){
            column.setFooterFont(footerFont);
        }
        Image footerImage = (Image) StyleSetStyleCreator.createResource(self.getString("footerImage"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(footerImage != null){
            column.setFooterImage(footerImage);
        }
        String footerText = self.getStringBlankAsNull("footerText​");
        if(footerText != null) {
        	column.setFooterText(footerText);
        }
        Font headerFont = (Font) StyleSetStyleCreator.createResource(self.getString("headerFont​​"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(headerFont != null){
            column.setHeaderFont(headerFont);
        }
        String headerTooltip = self.getStringBlankAsNull("headerTooltip​");
        if(headerTooltip != null) {
        	column.setHeaderTooltip(headerTooltip);
        }
        if(self.getStringBlankAsNull("minimumWidth") != null) {
        	column.setMinimumWidth(self.getInt("minimumWidth"));
        }
        column.setMoveable(self.getBoolean("moveable"));
        column.setResizeable(self.getBoolean("resizeable"));
        column.setSummary(self.getBoolean("summary"));
        column.setTree(self.getBoolean("tree"));
        column.setVisible(self.getBoolean("visible"));
        if(self.getStringBlankAsNull("width") != null) {
        	column.setWidth(self.getInt("width"));
        }
        column.setWordWrap(self.getBoolean("wordWrap"));
        actionContext.g().put(self.getMetadata().getName(), column);
        return column;
    }
	
	public static void attachDataStore(ActionContext actionContext) {
		Thing dataStore = (Thing) actionContext.get("dataStore");
		Object object = actionContext.get("control");
		
		//添加一个表格数据仓库监听
		Grid grid = (Grid) object;
		Thing listener = new Thing("xworker.app.view.swt.data.events.GridDataStoreListener");
		listener.put("grid", object);

		//先调用监听初始化
		listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", dataStore));

		//加入到监听器中
		dataStore.doAction("addListenerToFirst", actionContext, UtilMap.toMap("listener", listener));

		//把监听和自身添加到table中以备后用
		grid.setData("storeListener", listener);
		grid.setData("store", dataStore);

		DataStoreDisposeListener.attach(grid);
	}
}
