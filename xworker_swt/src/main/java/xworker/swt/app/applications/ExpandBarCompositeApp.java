package xworker.swt.app.applications;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.app.Application;
import xworker.swt.reacts.DataReactor;
import xworker.swt.util.SwtUtils;
import xworker.swt.xwidgets.DataItemContainer;

public class ExpandBarCompositeApp {
	public static Composite create(ActionContext actionContext) {
		//Thing self = actionContext.getObject("self");
		Composite composite = null;
		World world = World.getInstance();
		
		//应用
		Application app = actionContext.getObject("application");
		
		//模板
		Thing prototype = world.getThing("xworker.swt.app.applications.prototypes.ExpandBarCompositeAppPrototype/@mainComposite");
		composite = prototype.doAction("create", actionContext);
		
		//数据相应器
		DataReactor cTabFolderDataReactor = actionContext.getObject("mainCompositeDataReactor");
		
		//创建Shell菜单
		Widget shellMenuToolBar = actionContext.getObject("shellMenuToolBar");
		DataItemContainer shellItems = app.bindItemsToWidget(Application.ITEMS_SHELL, shellMenuToolBar, cTabFolderDataReactor);
		if(shellItems == null){
			shellMenuToolBar.dispose();
		}
		
		//创建右上角的菜单
		Widget toolBar = actionContext.getObject("toolBar");
		app.bindItemsToWidget(Application.ITEMS_TOPRIGHT, toolBar, cTabFolderDataReactor);
		
		//创建主菜单
		ExpandBar expandBar = actionContext.getObject("expandBar");
		app.bindItemsToWidget(Application.ITEMS_MAIN, expandBar, cTabFolderDataReactor);
		
		//设置标题
		Label titleLabel = actionContext.getObject("titleLabel");
		String title = app.getTitle();
		if(title != null) {
			titleLabel.setText(title);
		}
		
		String titleImage = app.getTitleImage();
		Image image = SwtUtils.createImage(composite, titleImage, actionContext);
		CLabel imageLabel = actionContext.getObject("imageLabel");
		if(image != null) {			
			imageLabel.setImage(image);
		
		}else {
			imageLabel.setImage(null);
		}
		composite.pack();
		
		//初始化条目
		app.bindItemsToWidget(Application.ITEMS_INIT, null, cTabFolderDataReactor);
		
		if(expandBar.getItemCount() > 0) {
			expandBar.getItem(0).setExpanded(true);
			int titleHeight = 0;
			for(int i =1; i<expandBar.getItemCount(); i ++) {
				titleHeight = titleHeight + expandBar.getItem(i).getHeaderHeight();
			}
			expandBar.getItem(0).setHeight(expandBar.getClientArea().height - titleHeight);
			expandBar.layout();
		}
		//composite.layout();
		return composite;
	}
}
