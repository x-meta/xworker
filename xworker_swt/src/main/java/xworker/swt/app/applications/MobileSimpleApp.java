package xworker.swt.app.applications;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.app.Application;
import xworker.swt.reacts.DataReactor;
import xworker.swt.xwidgets.DataItemContainer;

public class MobileSimpleApp {
	public static Composite create1(ActionContext actionContext) {
		//Thing self = actionContext.getObject("self");
		Composite composite = null;
		World world = World.getInstance();
		
		//应用
		Application app = actionContext.getObject("application");
		
		//模板
		Thing prototype = world.getThing("xworker.swt.app.applications.prototypes.MobileSimplePrototype/@mainComposite");
		composite = prototype.doAction("create", actionContext);
		
		//数据相应器
		DataReactor editorDataReactor = actionContext.getObject("editorContainerDataReactor");
		
		//创建主菜单
		Widget shellMenuToolBar = actionContext.getObject("menuItem");
		app.bindItemsToWidget(Application.ITEMS_MAIN, shellMenuToolBar, editorDataReactor);
		
		//创建右上角菜单
		ToolItem rightMenutem = actionContext.getObject("rightMenutem");
		DataItemContainer topRightItems = app.bindItemsToWidget(Application.ITEMS_TOPRIGHT, rightMenutem, editorDataReactor);
		if(topRightItems == null) {
			rightMenutem.setImage(null);
			rightMenutem.setEnabled(false);
		}
				
		//设置标题
		Label titleLabel = actionContext.getObject("titleLabel");
		String title = app.getTitle();
		if(title != null) {
			titleLabel.setText(title);
		}else {
			titleLabel.setText("");
		}
		
		composite.pack();
		
		//初始化条目
		app.bindItemsToWidget(Application.ITEMS_INIT, null, editorDataReactor);
		//composite.layout();
		return composite;
	}
}
