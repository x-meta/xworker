package xworker.swt.xwidgets;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import freemarker.template.TemplateException;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.UtilData;

public class Panel {
	public static Object create(ActionContext actionContext) throws IOException, TemplateException {
		try {
			SwtUtils.pushInitStyle();
			
			Thing self = actionContext.getObject("self");
			
			String prototype = "xworker.swt.xwidgets.prototypes.PanelShell/@ViewForm";
			int style = SWT.NONE;
			if(self.getBoolean("BORDER")) {
				style = style | SWT.BORDER;
			}
			if(self.getBoolean("FLAT")) {
				style = style | SWT.FLAT;
			}
			SwtUtils.setInitStyle(prototype, style);
			
			ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
			cc.setCompositeThing(World.getInstance().getThing(prototype));
			cc.addChildFilter("Toolbar");
			cc.addChildFilter("Content");
			cc.addChildFilter("Footer");
			
			ViewForm viewForm = cc.create();
			ActionContext ac = cc.getNewActionContext();
			viewForm.setData("actionContext", ac);
			
			//标签
			Image image = SwtUtils.createImage(viewForm, self.getString("titleImage"), actionContext);
			if(image != null) {
				((Label) ac.get("titleImageLabel")).setImage(image);
			}else {
				((Label) ac.get("titleImageLabel")).dispose();
			}
			
			String title = UtilData.getString(self.getStringBlankAsNull("title"), actionContext);
			if(title != null) {
				((Label) ac.get("titleTextLabel")).setText(title);
			}
			Font titleFont = SwtUtils.createFont(viewForm, self.getStringBlankAsNull("titleFont"), actionContext);
			if(titleFont != null) {
				((Label) ac.get("titleTextLabel")).setFont(titleFont);
			}			
			
			//Toolbar
			actionContext.peek().put("parent", viewForm);
			Thing toolbar = self.getThing("Toolbar@0");
			if(toolbar != null) {
				Control t = toolbar.doAction("create", actionContext);
				if(t != null) {
					viewForm.setTopRight(t);
				}
			}
			
			//Content
			Thing content = self.getThing("Content@0");
			if(content != null) {
				actionContext.peek().put("parent", ac.get("contentComposite"));
				content.doAction("create", actionContext);
			}
			
			//Footer
			Thing footer = self.getThing("Footer@0");
			if(footer != null) {
				actionContext.peek().put("parent", ac.get("bottomComposite"));
				for(Thing child : footer.getChilds()) {
					child.doAction("create", actionContext);
				}
				
				GridData data = ac.getObject("footerGridData");
				String align = footer.getStringBlankAsNull("align");
				if("left".equals(align)) {
					data.horizontalAlignment = SWT.LEFT;
				}else if("center".equals(align)) {
					data.horizontalAlignment = SWT.CENTER;
				}else if("right".equals(align)) {
					data.horizontalAlignment = SWT.RIGHT;
				}
			}else {
				((Composite) ac.get("bottomComposite")).dispose();
			}
			
			viewForm.layout();
			return viewForm;
		}finally {
			SwtUtils.popInitStyle();
		}
	}
	
	public static void drawFooter(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		Point size = ((Control) event.widget).getSize();
		GC gc = event.gc;

		Composite bottomComposite = actionContext.getObject("bottomComposite");
		if(bottomComposite.isDisposed() == false){
		    int height = bottomComposite.getSize().y + 10;
		    
		    Color gcForeground = gc.getForeground();
			Color border = bottomComposite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			gc.setForeground(border);
				
			gc.drawLine(0, size.y - height, size.x,  size.y - height);
				
			gc.setForeground(gcForeground);
		}
	}
}
