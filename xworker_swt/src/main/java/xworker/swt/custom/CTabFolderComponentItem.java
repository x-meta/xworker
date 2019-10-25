package xworker.swt.custom;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.UtilData;

public class CTabFolderComponentItem {
	private static String TOPRIGHT = "__CTabFolderComponentItem_TOPRIGHT__";

	public static Object create(ActionContext actionContext) throws IOException, TemplateException {
		Thing self = actionContext.getObject("self");
		int style = SWT.NONE;
		if (self.getBoolean("CLOSE"))
			style = SWT.CLOSE | style;

		CTabFolder parent = (CTabFolder) actionContext.get("parent");
		CTabItem item = new CTabItem(parent, style);

		if (self.getString("text") != null)
			item.setText(UtilString.getString(self.getString("text"), actionContext));
		if (self.getString("toolTipText") != null)
			item.setToolTipText(UtilString.getString(self.getString("toolTipText"), actionContext));

		// 保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), item);

		// 应用模板，如果存在
		Thing StyleManager = (Thing) actionContext.get("StyleManager");
		if (StyleManager != null) {
			StyleManager.doAction("apply", actionContext,
					UtilMap.toParams(new Object[] { "widget", item, "widgetThing", self }));
		}

		Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), "xworker.swt.graphics.Image",
				"imageFile", actionContext);
		if (image != null) {
			item.setImage(image);
		}

		item.setData(Designer.DATA_THING, self.getMetadata().getPath());
		item.setData(Designer.DATA_ACTIONCONTEXT, actionContext);
		
		//创建Component
		Thing component = self.doAction("getComponent", actionContext);
		if(component == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(UtilData.getString("lang:d=没有设置组件！&en=Component not exists.", actionContext));
			item.setControl(label);
		}else {
			//创建组件
			ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
			cc.setCompositeThing(component);
			
			//组件的返回值是ActionContainer
			ActionContainer actions = cc.create();

			//获取组件的根控件
			Control control = actions.doAction("getControl");
			if(control != null) {
				item.setControl(control);
			}
			item.setData("componentContext", cc.getNewActionContext());
			item.setData("component", actions);
			
			String componentName = self.getStringBlankAsNull("componentVarName");
			if(componentName == null) {
				componentName = self.getMetadata().getName() + "Component";				
			}
			actionContext.g().put(componentName, actions);			
		}
		
		return item;
	}

	public static Control getTopRightControl(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");

		Event event = actionContext.getObject("event");
		Control control = (Control) event.item.getData(TOPRIGHT);
		if (control != null && !control.isDisposed()) {
			return control;
		}

		Thing component = self.doAction("getComponent", actionContext);
		if(component == null) {
			return null;
		}
		
		Thing controlThing = component.doAction("getToolbar", actionContext);
		if (controlThing != null) {
			//使用组件的变量上下文
			ActionContainer actions = (ActionContainer) event.item.getData("component");
			ActionContext componentContext = actions.getActionContext();
			componentContext.peek().put("parent", actionContext.getObject("parent"));
			
			// parent有CTabFilder那边的Selection中的代码预先设定了
			control = controlThing.doAction("create", componentContext);
			if (control != null) {
				event.item.setData(TOPRIGHT, control);
			}

			return control;
		}

		return null;
	}
}
